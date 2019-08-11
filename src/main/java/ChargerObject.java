import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.util.*;

public class ChargerObject {

    private String id,address, name;
    private Double price, powerOutput;
    private Integer designator;
    private Boolean isRapid;
    private Map<Long,Boolean> chargingLog;
    private Set<Long> generatedLogs;

    ChargerObject(String id, Integer designator){
        this.id = id;
        this.designator = designator;
        this.address = "";
        this.name = "";
        this.price = 0.0;
        this.powerOutput = 0.0;
        this.isRapid = false;
        this.chargingLog = new HashMap<>();
        this.generatedLogs = new HashSet<>();
    }

    public HtmlPage getHtmlPage(WebClient client) throws IOException {
        String url = "https://polar-network.com/charge-point-information/" + id;
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(true);
        client.getOptions().setDownloadImages(false);
        client.getOptions().setThrowExceptionOnScriptError(false);
        client.getOptions().setThrowExceptionOnFailingStatusCode(false);
        return client.getPage(url);
    }

    public void fetchDetailsFromPage(HtmlPage doc) {
        DomNode addressNode = doc.getElementById("cp-address").getFirstChild();
        if(addressNode != null){
            this.address = addressNode.getNodeValue();
        }

        DomNode nameNode = doc.getElementById("cp-name").getFirstChild();
        if(nameNode == null){
            this.name = this.address.split(",")[0];
        } else {
            this.name = nameNode.getNodeValue();
        }

        DomNode postcodeNode = doc.getElementById("cp-postcode").getFirstChild();
        if(postcodeNode != null){
            this.address += "," + postcodeNode.getNodeValue();
        }

        DomNode priceNode = doc.getElementById("cp-price").getFirstChild();
        if(priceNode != null){
            if(!(priceNode.getNodeValue().equals("No Cost") || priceNode.getNodeValue().equals("?"))){
                this.price = Double.parseDouble(priceNode.getNodeValue());
            }
        }

        DomNode typeNode;
        DomNode powerNode;
        if(designator == 1){
            typeNode = doc.getElementById("cp-type").getFirstChild();
            powerNode = doc.getElementById("cp-power").getFirstChild();
        } else {
            typeNode = doc.getElementById("cp-type" + designator).getFirstChild();
            powerNode = doc.getElementById("cp-power" + designator).getFirstChild();
        }

        if(powerNode != null){
            this.powerOutput = Double.parseDouble(powerNode.getNodeValue().replace("Kw","").trim());
        }

        if(typeNode != null){
            for(String chargerType : Settings.FAST_CHARGERS){
                if(typeNode.getNodeValue().equals(chargerType)){
                    this.isRapid = true;
                }
            }
        }
    }

    //returns the total number of ms in use.
    public Long getTotalUsage(){
        List<Long> times = new ArrayList<>(getLogTimes());
        Collections.sort(times);
        Long totalUsage = 0L;
        for(int i = 1;i < times.size(); i++){
            if(getEntryInLog(times.get(i)) && !isGenerated(times.get(i))){
                totalUsage += times.get(i) - times.get(i-1);
            }
        }
        return totalUsage;
    }

    public Long getTotalLogTime(){
        Long runtTime = (Collections.max(getLogTimes()) - Collections.min(getLogTimes()));
        List<Long> times = new ArrayList<>(getLogTimes());
        Collections.sort(times);
        for(int i = 1;i < times.size(); i++){
            if(isGenerated(times.get(i))){
                runtTime -= times.get(i) - times.get(i-1);
            }
        }
        return runtTime;
    }

    //returns the average daily usage
    public Long getAverageDailyUsage(){
        Long totalUsage = getTotalUsage();
        Double numOfDays = getTotalLogTime()/86400000.0;
        return (long)(totalUsage/numOfDays);
    }

    public Long getAverageChargeTime(){
        List<Long> chargeDurations = getChargeDurations();

        if(chargeDurations.size() >0 ){
            Long sum = 0L;
            for(Long duration : chargeDurations){
                sum += duration;
            }
            return sum/chargeDurations.size();
        }else{
            return 0L;
        }
    }

    public List<Long> getChargeDurations(){
        List<Long> times = new ArrayList<>(getLogTimes());
        Collections.sort(times);

        List<Long> chargeDurations = new ArrayList<>();
        if(times.size() >0){
            boolean isCharging = false;
            Long chargeStart = 0L;
            for(int i = 1; i < times.size(); i++){
                if(isGenerated(times.get(i))){
                    isCharging = false;
                } else if(!isCharging && getEntryInLog(times.get(i)) && !getEntryInLog(times.get(i-1))){
                    isCharging = true;
                    chargeStart = times.get(i);
                } else if (isCharging && !getEntryInLog(times.get(i))){
                    isCharging = false;
                    chargeDurations.add( times.get(i) - chargeStart);
                }
            }
        }
        return chargeDurations;
    }

    public Double getEstimatedTotalRevenue(){
        Long totalUsage = getTotalUsage();
        Double pricePerkWh = getPrice();
        Double power = getPowerOutput();
        return pricePerkWh*power*(totalUsage/3600000.0);
    }

    public Double getEstimatedDailyRevenue(){
        Double totalRevenue = getEstimatedTotalRevenue();
        Long totalLogTime = getTotalLogTime();
        return totalRevenue/(totalLogTime/86400000.0);
    }

    public String getDetailsString(){
        StringBuilder detailsString = new StringBuilder("");
        Long totalLogTime = getTotalLogTime();
        Long totalUsage = getTotalUsage();
        Long averageDailyUsage = getAverageDailyUsage();
        Long averageChargeTime = getAverageChargeTime();
        Integer totalCharges = getChargeDurations().size();
        Double estimatedTotalRevenue = getEstimatedTotalRevenue();
        Double estimatedDailyRevenue = getEstimatedDailyRevenue();

        detailsString.append("Total Time Logged: ").append(totalLogTime/86400000).append("d ");
        detailsString.append((totalLogTime%86400000)/3600000).append("h");

        detailsString.append("\n\nTotal Usage: ").append(totalUsage/86400000).append("d ");
        detailsString.append((totalUsage%86400000)/3600000).append("h");

        detailsString.append("\nAverage Daily Usage: ").append(averageDailyUsage/3600000).append("h ");
        detailsString.append((averageDailyUsage%3600000)/60000).append("m");

        detailsString.append("\nAverage Charge Duration: ").append(averageChargeTime/3600000).append("h ");
        detailsString.append((averageChargeTime%3600000)/60000).append("m");

        detailsString.append("\n\nTotal Charges: ").append(totalCharges);

        detailsString.append("\nAverage Daily Charges: ");
        detailsString.append(String.format("%.2f",totalCharges/(totalLogTime/86400000.0)));

        detailsString.append("\n\nEstimated Total Revenue: £");
        detailsString.append(String.format("%.2f",estimatedTotalRevenue/100));

        detailsString.append("\nEstimated Daily Revenue: £");
        detailsString.append(String.format("%.2f",estimatedDailyRevenue/100));
        return detailsString.toString();
    }

    public String getInfoString(){
        StringBuilder infoString = new StringBuilder();
        infoString.append("NAME: ").append(getName());
        infoString.append("\nPRICE: ").append(getPrice()).append("p");
        infoString.append("\nOUTPUT: ").append(getPowerOutput()).append("kW");
        if(isRapid()){
            infoString.append("\nRAPID: Yes");
        } else {
            infoString.append("\nRAPID: No");
        }
        infoString.append("\nADDRESS: ").append(getAddress());
        return infoString.toString();
    }

    public void logCurrent(HtmlPage doc, Long time){
        DomNode statusNode = doc.getElementById("connector" + designator + "Status").getFirstChild();
        if(statusNode != null){
            if(statusNode.getNodeValue().equals("Charging")){
                chargingLog.put(time,true);
            } else {
                chargingLog.put(time,false);
            }
        } else {
            NotificationLogger.logger.addToLog("[ERROR] Could not make log entry for '" + this.id+":"+this.designator+"'");
        }
    }

    public Set<Long> getLogTimes(){
        return chargingLog.keySet();
    }

    public Boolean getEntryInLog(Long key){
        return chargingLog.get(key);
    }

    public void addLogEntry(Long time, Boolean value, Boolean isGenerated){
        chargingLog.put(time,value);
        if(isGenerated){
            generatedLogs.add(time);
        }
    }

    public void removeLogEntry(Long time){
        chargingLog.remove(time);
        generatedLogs.remove(time);
    }

    public Boolean isRapid(){
        return isRapid;
    }

    public String getId() {
        return id;
    }

    public Integer getDesignator() {
        return designator;
    }

    public String getAddress() {
        return address;
    }

    public Double getPrice() {
        return price;
    }

    public Double getPowerOutput() {
        return powerOutput;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setPowerOutput(Double powerOutput) {
        this.powerOutput = powerOutput;
    }

    public void setDesignator(Integer designator) {
        this.designator = designator;
    }

    public void setRapid(boolean rapid) {
        isRapid = rapid;
    }

    public boolean isGenerated(Long time){
        return generatedLogs.contains(time);
    }
}

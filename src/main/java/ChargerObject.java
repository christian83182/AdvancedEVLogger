import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ChargerObject {

    private String id,address, additionalInfo, name;
    private Double price, powerOutput;
    private Integer designator;
    private boolean isRapid;
    private Map<Long,Boolean> chargingLog;

    ChargerObject(String id, Integer designator){
        this.id = id;
        this.designator = designator;
        this.address = "";
        this.additionalInfo = "";
        this.name = "";
        this.price = 0.0;
        this.powerOutput = 0.0;
        this.isRapid = false;
        this.chargingLog = new HashMap<>();
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

        DomNode locationNode = doc.getElementById("location").getFirstChild();
        if(locationNode != null){
            this.additionalInfo = locationNode.getNodeValue();
        }

        DomNode additionalNode = doc.getElementById("additional").getFirstChild();
        if(additionalNode != null){
            if(this.additionalInfo.equals("")){
                this.additionalInfo += additionalNode.getNodeValue();
            } else {
                this.additionalInfo += "\n" + additionalNode.getNodeValue();
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

    public void logCurrent(HtmlPage doc) {
        DomNode statusNode = doc.getElementById("connector" + designator + "Status").getFirstChild();
        if(statusNode != null){
            if(statusNode.getNodeValue().equals("Charging")){
                chargingLog.put(System.currentTimeMillis(),true);
            } else {
                chargingLog.put(System.currentTimeMillis(),false);
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

    public void addLogEntry(Long time, Boolean value){
        chargingLog.put(time,value);
    }

    public boolean isRapid(){
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

    public String getAdditionalInfo() {
        return additionalInfo;
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

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
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
}

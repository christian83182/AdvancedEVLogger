
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

class DataModel {

    private Map<String,ChargerObject> chargers;
    private Application app;
    private Map<Long,Double> generalChargingLog;
    private Set<Long> isGenerated;
    private Map<String,String> analysisMap;

    DataModel(Application app){
        this.chargers = new HashMap<>();
        this.app = app;
        this.generalChargingLog = new HashMap<>();
        this.isGenerated = new HashSet<>();
        this.analysisMap = new HashMap<>();
    }

    public synchronized void rebuildAnalysis(){
        if(chargers.values().isEmpty()){
            return;
        }

        List<Long> times = new ArrayList<>(getGeneralLogKey());
        Collections.sort(times);

        Long runtime = Collections.max(times) - Collections.min(times);
        List<Long> averageChargeDurations = new ArrayList<>();
        List<Long> averageDailyUsage = new ArrayList<>();
        List<Double> averageDailyUses = new ArrayList<>();
        List<Double> averageDailyRevenues = new ArrayList<>();

        for(int i =1; i< times.size(); i++){
            if(isGenerated(times.get(i))){
                runtime -= times.get(i) - times.get(i-1);
            }
        }

        for(ChargerObject charger : chargers.values()){
            if(isValidCharger(charger)){
                Long chargerAverageChargeTime = charger.getAverageChargeTime();
                if( chargerAverageChargeTime > 0){
                    averageChargeDurations.add(chargerAverageChargeTime);
                }
                Long chargerAverageUse = charger.getAverageDailyUsage();
                if(chargerAverageUse >0){
                    averageDailyUsage.add(chargerAverageUse);
                }
                Integer chargerTotalUses = charger.getChargeDurations().size();
                if(chargerTotalUses >0){
                    averageDailyUses.add(chargerTotalUses/(runtime/86400000.0));
                }
                Double chargerDailyRevenue = charger.getEstimatedDailyRevenue();
                if(chargerDailyRevenue > 0){
                    averageDailyRevenues.add(chargerDailyRevenue);
                }
            }
        }

        DateFormat format = new SimpleDateFormat("dd/MM/yy  HH:mm:ss");
        analysisMap.put("TotalChargers",chargers.values().size()+"");
        analysisMap.put("FirstLogTime",format.format(Collections.min(generalChargingLog.keySet())));
        analysisMap.put("LastLogTime",format.format(Collections.max(generalChargingLog.keySet())));
        analysisMap.put("ActiveChargers",String.format("%.2f",getGeneralLogEntry(Collections.max(generalChargingLog.keySet()))));

        String totalLogTimeString = (runtime/86400000) + "d " + ((runtime%86400000)/3600000) +"h";
        analysisMap.put("TotalLogTime",totalLogTimeString);

        Long averageChargeDuration = (long)averageChargeDurations.stream().mapToLong(e -> e).average().getAsDouble();
        String avgChargeDurationString = (averageChargeDuration/3600000) +"h " + (averageChargeDuration%3600000)/60000 +"m";
        analysisMap.put("AverageChargeDuration",avgChargeDurationString);

        Long averageChargerUsage = (long)averageDailyUsage.stream().mapToLong(e->e).average().getAsDouble();
        String averageChargerUsageString = (averageChargerUsage/3600000)+"h " + (averageChargerUsage%3600000)/60000 +"m";
        analysisMap.put("AverageDailyUsage",averageChargerUsageString);

        Double averageDailyUse = averageDailyUses.stream().mapToDouble(e->e).average().getAsDouble();
        String averageDailyUseString = String.format("%.2f",averageDailyUse);
        analysisMap.put("AverageDailyUses",averageDailyUseString);

        Double averageDailyRevenue = averageDailyRevenues.stream().mapToDouble(e->e/100).average().getAsDouble();
        String averageDailyRevenueString = "£" + String.format("%.2f",averageDailyRevenue);
        analysisMap.put("AverageDailyRevenue",averageDailyRevenueString);

        if(app.getMenuPanel().getSelectedOption().equals("Show All") || app.getMenuPanel().getSelectedOption().equals("Show Moving Average")){
            app.getDetailsPanel().setInfoText(getInfoString());
            app.getDetailsPanel().setAnalysisText(getAnalysisString());
        }
    }

    public synchronized String getAnalysisString(){
        StringBuilder detailsString = new StringBuilder();

        detailsString.append("Total Time Logged: ").append(analysisMap.get("TotalLogTime"));
        detailsString.append("\n\nAverage Charge Duration: ").append(analysisMap.get("AverageChargeDuration"));
        detailsString.append("\nAverage Daily Charger Usage: ").append(analysisMap.get("AverageDailyUsage"));
        detailsString.append("\nAverage Daily Charger Uses: ").append(analysisMap.get("AverageDailyUses"));
        detailsString.append("\nAverage Daily Charger Revenue: ").append(analysisMap.get("AverageDailyRevenue"));

        return detailsString.toString();
    }

    public synchronized String getInfoString(){
        StringBuilder detailsString = new StringBuilder();

        detailsString.append("Total Chargers Tracked: ").append(analysisMap.get("TotalChargers"));
        //detailsString.append("\nCurrent Active Chargers: ").append(analysisMap.get("ActiveChargers"));
        detailsString.append("\nFirst Log Recorded: ").append(analysisMap.get("FirstLogTime"));
        detailsString.append("\nLast Log Recorded: ").append(analysisMap.get("LastLogTime"));

        return detailsString.toString();
    }

    public synchronized void rebuiltGeneralModel(){
        this.generalChargingLog.clear();
        this.isGenerated.clear();
        for(ChargerObject charger : chargers.values()){
            if(isValidCharger(charger)){
                for(Long time : charger.getLogTimes()){
                    if(charger.getEntryInLog(time)){
                        if(generalChargingLog.containsKey(time)){
                            generalChargingLog.put(time, generalChargingLog.get(time)+1);
                        } else {
                            generalChargingLog.put(time,1.0);
                        }
                    } else {
                        if(!generalChargingLog.containsKey(time)){
                            generalChargingLog.put(time, 0.0);
                        }
                    }
                    if(charger.isGenerated(time)){
                        isGenerated.add(time);
                    }
                }
            }
        }

        if(app.getMenuPanel().getSelectedOption().equals("Show Moving Average")
                && !getGeneralLogKey().isEmpty()){
            List<Long> times = new ArrayList<>(getGeneralLogKey());
            Map<Long,Double> newMap = new HashMap<>();
            Collections.sort(times);
            Integer movingAverageWidth = app.getMenuPanel().getMovingAverageWidth()/2;
            for(int i =movingAverageWidth; i < times.size()-movingAverageWidth; i++){
                double sumCount = 0.0;
                double sumTimes = 0.0;
                for(int j = -movingAverageWidth; j <= movingAverageWidth; j++){
                    sumCount += getGeneralLogEntry(times.get(i+j));
                    sumTimes += times.get(i+j);
                }
                double averageCount = sumCount/(movingAverageWidth*2 +1);
                long averageTime = (long)(sumTimes/(movingAverageWidth*2 +1));
                newMap.put(averageTime,averageCount);
                if(isGenerated(times.get(i))){
                    isGenerated.add(averageTime);
                }
            }
            this.generalChargingLog = newMap;
        }
        rebuildAnalysis();
        app.repaint();
    }

    public void repairDataModel(){
        if(!chargers.isEmpty()){
            for(ChargerObject charger : chargers.values()){
                List<Long> times = new ArrayList<>(charger.getLogTimes());
                Collections.sort(times);

                //remove any points which aren't present on all chargers
                for(ChargerObject checkingCharger : chargers.values()){
                    for(Long time : times){
                        if(!checkingCharger.getLogTimes().contains(time)){
                            charger.removeLogEntry(time);
                        }
                    }
                }

                //recompute times now that some points of data might have been removed
                times = new ArrayList<>(charger.getLogTimes());
                Collections.sort(times);
                //Iterate over all chargers where there are 3 chargers or more
                Long previousInterval = times.get(1) - times.get(0);
                Random rand = new Random();
                for(int i = 2; i < times.size(); i++){
                    Long currentInterval = times.get(i) - times.get(i-1);
                    Long intervalChange = currentInterval - previousInterval;

                    //if the current interval is not within 10s of the previous interval, then correct.
                    if(intervalChange > 5000 || intervalChange < -5000){
                        Long numOfNewPoints = Math.round((double)currentInterval/(double)previousInterval)-2;
                        if(numOfNewPoints >0){
                            Long newInterval = currentInterval/numOfNewPoints;

                            //iterate over the number of new points, and add them as log entries
                            Long leftTime = times.get(i-1);
                            Long rightTime = times.get(i);
                            Integer randomDivider = (int)(1.0/rand.nextDouble());
                            for(int j = 1; j <= numOfNewPoints; j++) {
                                //make half of them the first value, and the other half the second value
                                boolean value;
                                if (j < numOfNewPoints / randomDivider) {
                                    value = charger.getEntryInLog(leftTime);
                                } else {
                                    value = charger.getEntryInLog(rightTime);
                                }

                                if(currentInterval > 1800000){
                                    charger.addLogEntry(leftTime + j*newInterval,value,true);
                                } else {
                                    charger.addLogEntry(leftTime + j*newInterval, value, false);
                                }
                            }
                        }
                    } else {
                        previousInterval = times.get(i) - times.get(i-1);
                    }
                }
            }
            rebuiltGeneralModel();
        }
        app.repaint();
    }

    public boolean isValidCharger(ChargerObject charger){
        if(app.getMenuPanel().isShowRapid() && charger.isRapid()){
            return true;
        } else if(app.getMenuPanel().isShowFast() && !charger.isRapid()){
            return true;
        }
        return false;
    }

    public synchronized Set<String> getIds(){
        return new TreeSet<>(chargers.keySet());
    }

    public synchronized ChargerObject getCharger(String id){
        return chargers.get(id);
    }

    public synchronized void clearChargers(){
        chargers.clear();
    }

    public synchronized void addId(String newId){
        chargers.put(newId,null);
    }

    public synchronized void addCharger(String chargerId, ChargerObject charger){
        chargers.put(chargerId,charger);
    }

    public synchronized Set<Long> getGeneralLogKey(){
        return generalChargingLog.keySet();
    }

    public synchronized Double getGeneralLogEntry(Long time){
        return generalChargingLog.get(time);
    }

    public synchronized void clearGeneralLogEntries(){
        generalChargingLog.clear();
    }

    public synchronized void addToGeneralLog(Long time, Double quantity){
        generalChargingLog.put(time,quantity);
    }

    public synchronized boolean isGenerated(Long time){
        return isGenerated.contains(time);
    }
}

package models;

import org.codehaus.jackson.JsonNode;

import java.util.Iterator;
import org.w3c.dom.Document;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import play.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: fredrik hansen
 * Date: 11.04.13
 * Time: 20:35
 * To change this template use File | Settings | File Templates.
 */
public class Tog {
//    private static String RETNINGAVGANG =  "\"2\"";
//    private static String RETNINGANKOMST= "\"1\"";

    private static int RETNINGAVGANGv2 =  2;
    private static int RETNINGANKOMSTv2= 1;


    public String avgang;
    public String ankomst;
    public String temptekst;
//    public JsonNode sanntidJson;
    public JsonNode sanntidJsonNy;
//
//    public Tog(JsonNode sanntid){
//        sanntidJson = sanntid;
//
//        avgang ="";
//        ankomst = "";
//
//        if (sanntid == null){
//            avgang ="NA";
//            return;
//        }
//
////        Logger.debug("sanntid:" + sanntid.toString());
//
//        Iterator<JsonNode> ite = sanntid.getElements();
//
//        while (ite.hasNext()) {
//            JsonNode temp = ite.next();
//
//            if(avgang.equals("") && temp.path("DirectionRef").toString().equals(RETNINGAVGANG)){
//                avgang = getMinutterTilAvgang(temp);
//            } else if(ankomst.equals("")) {
//                ankomst = getMinutterTilAvgang(temp);
//            }
//
//         }
//    }

    public Tog(JsonNode sanntid){
        sanntidJsonNy = sanntid;

        avgang ="";
        ankomst = "";

        if (sanntid == null){
            avgang ="NA";
            ankomst ="NA";
            return;
        }

        JsonNode togankomster = sanntid.path("MonitoredStopVisit") ;
        Iterator<JsonNode> ite = sanntid.getElements();
        int avgangtid =  10000000;
        int ankomsttid = 10000000;

        DateTime naa = new org.joda.time.DateTime() ;

        while (ite.hasNext()) {
            JsonNode temp = ite.next();
            DateTime parsed = new DateTime(temp.path("MonitoredVehicleJourney").path("MonitoredCall").path("ExpectedDepartureTime").asText());

            Logger.debug(parsed.toString());

            try {
                int togtid = 0;
                if (parsed.compareTo(naa) >= 0) {
                    togtid = getIntervalIMinutter(new Interval(naa, parsed));

                    if (isAvgang(temp)) {
                        avgangtid = avgangtid > togtid ? togtid : avgangtid;
                        Logger.debug("avgangstid: " + avgangtid + " togtid:" + togtid + " dest:" + temp.path("MonitoredVehicleJourney").path("DestinationName").asText());
                    } else {
                        ankomsttid = ankomsttid > togtid ? togtid : ankomsttid;
                        Logger.debug("ankommstid: " + ankomsttid + " togtid:" + togtid + " dest:" + temp.path("MonitoredVehicleJourney").path("DestinationName").asText());
                    }
                }
            }catch (Exception e)
            {
                Logger.error(e.toString());
                e.printStackTrace();
            }

        }
        avgang ="" + avgangtid;
        ankomst ="" + ankomsttid;

    }

    private boolean isAvgang(JsonNode temp) {

        if(temp.path("MonitoredVehicleJourney").path("DirectionRef").asInt() == RETNINGAVGANGv2){
          return true;
        }else if(temp.path("MonitoredVehicleJourney").path("DirectionRef").asInt() == RETNINGANKOMSTv2){
           return false;
        }

        Logger.debug("DirectionRef er ikke satt korrekt: destinataion = " +temp.path("MonitoredVehicleJourney").path("DestinationName").asText() );

        if(",DAL,HAUERSETER,NORDBY,".indexOf(","+ temp.path("MonitoredVehicleJourney").path("DestinationName").asText().toUpperCase()+ ",")>=0 ){
            return false;
        }

        return  true;

    }

//    private String getMinutterTilAvgang(JsonNode temp) {
//        try{
//            DateTime naa = new org.joda.time.DateTime() ;
//            String date = temp.path("ExpectedArrivalTime").asText();
//            DateTime parsed = new DateTime(Long.parseLong(date.substring(6,date.length() - 7)));
//            Logger.debug("toget går:" + parsed.toString() + "klokka er: " + naa.toString());
//            return "" + getIntervalIMinutter(new Interval(naa,parsed));
//
//        }   catch (Exception e){
//
//            Logger.debug("getMinutterTilAvgang" + e.toString());
//            return "" + getIntervalIMinutter(new Interval(0,0));
//        }
//
//    }

    private int getIntervalIMinutter(Interval interval){
        return interval.toPeriod().toStandardMinutes().getMinutes()  ;

    }

}

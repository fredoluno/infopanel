package models;

import org.codehaus.jackson.JsonNode;

import java.util.Iterator;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import play.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: fredrik hansen
 * Date: 11.04.13
 * Time: 20:35
 * To change this template use File | Settings | File Templates.
 */
public class Tog {
    private static String RETNINGAVGANG =  "\"2\"";
    private static String RETNINGANKOMST= "\"1\"";

    public String avgang;
    public String ankomst;
    public String temptekst;
    public JsonNode sanntidJson;

    public Tog(JsonNode sanntid){
        sanntidJson = sanntid;

        avgang ="";
        ankomst = "";

        if (sanntid == null){
            avgang ="NA";
            return;
        }

        Iterator<JsonNode> ite = sanntid.getElements();

        while (ite.hasNext()) {
            JsonNode temp = ite.next();

            if(avgang.equals("") && temp.path("DirectionRef").toString().equals(RETNINGAVGANG)){
                avgang = getMinutterTilAvgang(temp);
            } else if(ankomst.equals("")) {
                ankomst = getMinutterTilAvgang(temp);
            }

         }
    }

    private String getMinutterTilAvgang(JsonNode temp) {
        try{
            DateTime naa = new org.joda.time.DateTime() ;
            String date = temp.path("ExpectedArrivalTime").asText();
            DateTime parsed = new DateTime(Long.parseLong(date.substring(6,date.length() - 7)));
            Logger.debug("toget går:" + parsed.toString() + "klokka er: " + naa.toString());
            return "" + getIntervalIMinutter(new Interval(naa,parsed));

        }   catch (Exception e){

            Logger.debug("getMinutterTilAvgang" + e.toString());
            return "" + getIntervalIMinutter(new Interval(0,0));
        }

    }

    private int getIntervalIMinutter(Interval interval){
        return interval.toPeriod().toStandardMinutes().getMinutes()  ;

    }

}

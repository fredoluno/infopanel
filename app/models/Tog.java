package models;

import org.codehaus.jackson.JsonNode;

import java.util.Date;
import java.util.Iterator;

import org.joda.time.DateTime;
import org.joda.time.Interval;

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
    public Tog(JsonNode sanntid){

        Iterator<JsonNode> ite = sanntid.getElements();
          avgang ="";
          ankomst = "";
        while (ite.hasNext()) {
            JsonNode temp = ite.next();
            temptekst +=  " " + temp.path("DirectionRef").toString();
            if(avgang.equals("") && temp.path("DirectionRef").toString().equals(RETNINGAVGANG)){
                avgang = "" + getInterval(temp).toPeriod().getMinutes();
            } else if(ankomst.equals("")) {
                ankomst ="" +  getInterval(temp).toPeriod().getMinutes();
            }
         }
    }

    private Interval getInterval(JsonNode temp) {
        DateTime naa = new org.joda.time.DateTime() ;
        String date = temp.path("ExpectedArrivalTime").asText();

        Date parsed = new Date(Long.parseLong(date.substring(6,date.length() - 7)));
        DateTime ola = new DateTime(parsed);
        return new Interval(naa,ola);
    }

}

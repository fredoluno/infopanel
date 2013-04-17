package models;

import logic.DiverseUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import play.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: fredrik hansen
 * Date: 14.04.13
 * Time: 17:38
 * To change this template use File | Settings | File Templates.
 */
public class KalenderEvent implements Comparable {

    public DateTime eventStart;
    public DateTime eventSlutt;
    public String tittel;

    public int compareTo(Object c){
        if (eventStart == null){
            return 1;
        }

        return eventStart.compareTo(((KalenderEvent)c).eventStart) ;
    }

    public String printDato(){

        if(DiverseUtils.erIdag(eventStart)&&DiverseUtils.erIdag(eventSlutt)){
            DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm");
            return fmt.print(eventStart) + "-" + fmt.print(eventSlutt);
        }
        else if (DiverseUtils.erInnen7dager(eventSlutt)) {
            String dato = "";
            DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEE");

            if (DiverseUtils.datoPassert(eventStart)) {
                dato = "nå-" + fmt.print(eventSlutt);
            } else if (DiverseUtils.erSammeDag(eventStart, eventSlutt)) {
                dato = fmt.print(eventStart);
            } else {
                dato = fmt.print(eventStart) + "-" + fmt.print(eventSlutt);
            }
            return dato;

        } else if (DiverseUtils.erSammeDag(eventStart, eventSlutt)) {
            DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM");
            return fmt.print(eventStart);
        }

        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM");


        return  fmt.print(eventStart) + "-" +fmt.print(eventSlutt);
    }


}

package models;

import logic.DiverseUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import play.Logger;

import java.util.Locale;

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
        int comp = eventStart.compareTo(((KalenderEvent)c).eventStart) ;
        if(comp == 0)
            comp =  eventStart.compareTo(((KalenderEvent)c).eventSlutt);
        if(comp == 0)
            return -1;
        else
        return comp;
    }


    public String printDato(){

        if(DiverseUtils.erIdag(eventStart)&&DiverseUtils.erIdag(eventSlutt)){
            DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm").withLocale(new Locale("no","NO"));

            return fmt.print(eventStart) + "-" + fmt.print(eventSlutt);
        }
        else if (DiverseUtils.erInnen7dager(eventSlutt)) {
            String dato = "";
            DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEE").withLocale(new Locale("no","NO"));

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

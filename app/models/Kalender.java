package models;

import logic.DiverseUtils;
import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import play.Logger;

import java.util.Iterator;
import java.util.TreeSet;

/**
 * Created with IntelliJ IDEA.
 * User: fredrik hansen
 * Date: 13.04.13
 * Time: 19:48
 * To change this template use File | Settings | File Templates.
 */
public class Kalender {

    public TreeSet<KalenderEvent> eventer;
    public TreeSet<KalenderEvent> eventerIdag;
    KalenderEvent kalenderEvent;

    public Kalender(Document document){

        eventer = new TreeSet<KalenderEvent>();
        eventerIdag = new TreeSet<KalenderEvent>();


        NodeList kalenderEntry =document.getElementsByTagName("entry");


        Logger.debug("Antall "  +kalenderEntry.getLength());
        for (int i = 0; i < kalenderEntry.getLength(); i++)   {
            Node node = kalenderEntry.item(i)  ;
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                KalenderEvent event = new KalenderEvent();
                Element element = (Element)node;
                event.tittel =  DiverseUtils.hentVerdi("title", element);

                NodeList list1 = element.getElementsByTagName("gd:when") ;
                Element naar = (Element)element.getElementsByTagName("gd:when").item(0) ;
                if (naar != null){
                    event.eventStart = new DateTime(naar.getAttribute("startTime"));
                    event.eventSlutt = new DateTime(naar.getAttribute("endTime"));
                }

                Logger.debug(event.tittel +"s " + event.eventStart + "sl " + event.eventSlutt);
                if(!DiverseUtils.datoPassert(event.eventSlutt)){
                    if(DiverseUtils.erIdag(event.eventStart,event.eventSlutt))  {
                        eventerIdag.add(event);
                    }
                    else{
                        eventer.add(event);
                    }

                }


            }
        }

        Iterator iterator = eventer.iterator()        ;

        while(iterator.hasNext()) {
            Logger.debug("" + ((KalenderEvent)iterator.next()).eventStart   );

        }
    }
    public  String printEventer() {
        Iterator iterator = eventer.iterator()        ;
        String text = "";
        while(iterator.hasNext()) {
            KalenderEvent event = (KalenderEvent)iterator.next();

            text += "<li>" + event.printDato() + ": "+ event.tittel + "</li>";

        }
        return    text;
    }
}

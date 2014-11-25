package models;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.CalendarRequestInitializer;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import logic.DiverseUtils;
import logic.Tjenester;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import play.Logger;

import java.io.IOException;
import java.util.Iterator;
import java.util.TimeZone;
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
    public TreeSet<KalenderEvent> eventerKomplett;
    KalenderEvent kalenderEvent;

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static com.google.api.services.calendar.Calendar client;

    private static String API_KEY = "AIzaSyDmwtaFR3AbFS4_LqeB4VJ9r1J52v4Eng0";
    private static String CALENDAR_ID = "fredoluno@gmail.com";

//    public Kalender(Document document){
//
//        eventer = new TreeSet<KalenderEvent>();
//        eventerIdag = new TreeSet<KalenderEvent>();
//        eventerKomplett = new TreeSet<KalenderEvent>();
//
//
//        NodeList kalenderEntry =document.getElementsByTagName("entry");
//
//
//        Logger.debug("Antall "  +kalenderEntry.getLength());
//        for (int i = 0; i < kalenderEntry.getLength(); i++)   {
//            Node node = kalenderEntry.item(i)  ;
//            if(node.getNodeType() == Node.ELEMENT_NODE) {
//                KalenderEvent event = new KalenderEvent();
//                Element element = (Element)node;
//                event.tittel =  DiverseUtils.hentVerdi("title", element);
//
//                NodeList list1 = element.getElementsByTagName("gd:when") ;
//                Element naar = (Element)element.getElementsByTagName("gd:when").item(0) ;
//                if (naar != null){
//                    event.eventStart = new DateTime(naar.getAttribute("startTime"));
//                    event.eventSlutt = new DateTime(naar.getAttribute("endTime"));
//                }
//
//                Logger.debug(event.tittel +"s " + event.eventStart + "sl " + event.eventSlutt);
//                if(!DiverseUtils.datoPassert(event.eventSlutt)){
//                    eventerKomplett.add(event);
//                    if(DiverseUtils.erIdag(event.eventStart,event.eventSlutt))  {
//                        eventerIdag.add(event);
//                    }
//                    else{
//                        eventer.add(event);
//                    }
//
//
//                }
//
//
//            }
//        }
//
//        Iterator iterator = eventer.iterator()        ;
//
//        while(iterator.hasNext()) {
//            Logger.debug("" + ((KalenderEvent)iterator.next()).eventStart   );
//
//        }
//    }

    public Kalender(){

        eventer = new TreeSet<KalenderEvent>();
        eventerIdag = new TreeSet<KalenderEvent>();
        eventerKomplett = new TreeSet<KalenderEvent>();

        Events events  = getKalenderEvent();
        Logger.debug("Det er " + events.size() + " elementer i kalenderen");

        for( Event googleEvent: events.getItems()){

            KalenderEvent event = new KalenderEvent();
            Logger.debug("Event:" + googleEvent.getSummary());
            Logger.debug("EventEnd:" + googleEvent.getEnd().getDateTime());
            Logger.debug("EventStart:" + googleEvent.getStart().getDate());



            event.tittel = googleEvent.getSummary();



            event.eventStart = getDateTime(googleEvent.getStart());
            event.eventSlutt = getDateTime(googleEvent.getEnd());

            Logger.debug(event.tittel +"s " + event.eventStart + "sl " + event.eventSlutt);
                if(!DiverseUtils.datoPassert(event.eventSlutt)){
                    eventerKomplett.add(event);
                    if(DiverseUtils.erIdag(event.eventStart,event.eventSlutt))  {
                        eventerIdag.add(event);
                    }
                    else{
                        eventer.add(event);
                    }


                }




        }



    }

    private DateTime getDateTime(EventDateTime eventTime) {

         if(eventTime.getDateTime() != null)
             return new DateTime(eventTime.getDateTime().getValue());

         return  new DateTime(eventTime.getDate().getValue(), DateTimeZone.UTC);
    }

    public static Events getKalenderEvent(){
        GoogleCredential credentials = new GoogleCredential().setAccessToken(Tjenester.getGoogleToken());
        CalendarRequestInitializer credential = new    CalendarRequestInitializer(API_KEY);

        try {

            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            client = new com.google.api.services.calendar.Calendar.Builder(
                    httpTransport, JSON_FACTORY,credentials).setApplicationName("Infoskjerm").setCalendarRequestInitializer(credential).build();

            Events events = client.events().list(CALENDAR_ID)
                    .setTimeMin(new com.google.api.client.util.DateTime(new org.joda.time.DateTime().toDate()))
                    .setTimeMax(new com.google.api.client.util.DateTime(new org.joda.time.DateTime().plusYears(1).toDate()))
                    .setSingleEvents(true)
                    .execute();
            httpTransport.shutdown();

            return events;

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return null;
    }




    public  String printEventer() {
        Iterator iterator = eventerKomplett.iterator()        ;
        String text = "";
        while(iterator.hasNext()) {
            KalenderEvent event = (KalenderEvent)iterator.next();

            text += "<li>" + event.printDato() + ": "+ event.tittel + "</li>";

        }
        return    text;
    }
}

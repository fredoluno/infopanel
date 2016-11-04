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
    public TreeSet<KalenderEvent> dailyIdag;
    public TreeSet<KalenderEvent> dailyImorgen;

    public TreeSet<KalenderEvent> eventer2;
    KalenderEvent kalenderEvent;

    public String theDailyEvent;

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static com.google.api.services.calendar.Calendar client;

    private static String API_KEY = "AIzaSyDmwtaFR3AbFS4_LqeB4VJ9r1J52v4Eng0";
    private static String CALENDAR_ID = "fredoluno@gmail.com";
    private static String DAILYCAL_ID = "8kqi1qjeko3b9n0ujp6712j220@group.calendar.google.com";

    public Kalender(){

        eventer             = new TreeSet<KalenderEvent>();
        eventerIdag         = new TreeSet<KalenderEvent>();
        eventerKomplett     = new TreeSet<KalenderEvent>();
        dailyIdag           = new TreeSet<KalenderEvent>();
        dailyImorgen        = new TreeSet<KalenderEvent>();


        GoogleCredential credentials = new GoogleCredential().setAccessToken(Tjenester.getGoogleToken());
        CalendarRequestInitializer credential = new    CalendarRequestInitializer(API_KEY);

        Logger.debug("--------------------------------------------------------------------");
        SetUpCalendar(credentials, credential);
        Logger.debug("--------------------------------------------------------------------");
        DiverseUtils.erDetTidligPaaDagen();
        SetupDailyCalendar(credentials, credential);
        Logger.debug("--------------------------------------------------------------------");

    }

    private void SetupDailyCalendar(GoogleCredential credentials, CalendarRequestInitializer credential) {
        Events events  = getKalenderEvent(credentials, credential,DAILYCAL_ID );
        Logger.debug("Det er " + events.size() + " elementer i den daglige kalenderen");
        for( Event googleEvent: events.getItems()) {

            KalenderEvent event = new KalenderEvent();
//            Logger.debug("DagligEvent:" + googleEvent.getSummary());
//            Logger.debug("DagligEventEnd:" + googleEvent.getEnd().getDateTime());
//            Logger.debug("DagligEventStart:" + googleEvent.getStart().getDate());

            event = populateEvent(googleEvent);

            if(!DiverseUtils.datoPassert(event.eventSlutt)){
                if(DiverseUtils.erIdag(event.eventStart,event.eventSlutt))  {
                    dailyIdag.add(event);
                    Logger.debug(theDailyEvent);
                } else if (DiverseUtils.erImorgen(event.eventStart, event.eventSlutt)) {
                    dailyImorgen.add(event);
                    Logger.debug("I morgen:" + googleEvent.getSummary());
                }
                else
                {
                    Logger.debug("En annen dag:" + googleEvent.getSummary());
                }
            }

        }
        Logger.debug("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
            Logger.debug(printDaily());
        Logger.debug("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");

    }

    public  String printDaily() {
        Iterator iterator = dailyImorgen.iterator()        ;
        if(DiverseUtils.erDetTidligPaaDagen())  {
            iterator=   dailyIdag.iterator();
        }

        String text = "";
        int count=0;

        while(iterator.hasNext()) {
            KalenderEvent event = (KalenderEvent)iterator.next();
            if (count > 0 ) {
                text += ", ";
            }
            text+=event.tittel;
            count++;
        }
        if (DiverseUtils.erDetTidligPaaDagen()){
            return    "I dag må du huske " +  text;
        }
        return "I morgen er det " + text;

    }

    private void SetUpCalendar(GoogleCredential credentials, CalendarRequestInitializer credential) {
        Events events  = getKalenderEvent(credentials, credential,CALENDAR_ID );
        Logger.debug("Det er " + events.size() + " elementer i kalenderen");

        for( Event googleEvent: events.getItems()){

            KalenderEvent event = new KalenderEvent();
            Logger.debug("Event:" + googleEvent.getSummary());
            Logger.debug("EventEnd:" + googleEvent.getEnd().getDateTime());
            Logger.debug("EventStart:" + googleEvent.getStart().getDate());


            event = populateEvent(googleEvent);

            Logger.debug(event.tittel +" start " + event.eventStart + "slutt " + event.eventSlutt);
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

    private KalenderEvent populateEvent(Event googleEvent ) {
        KalenderEvent eventen = new KalenderEvent();
        eventen.tittel = googleEvent.getSummary();
        eventen.eventStart = getDateTime(googleEvent.getStart());
        eventen.eventSlutt = getDateTime(googleEvent.getEnd());
        return eventen;
    }

    private DateTime getDateTime(EventDateTime eventTime) {

         if(eventTime.getDateTime() != null)
             return new DateTime(eventTime.getDateTime().getValue());

         return  new DateTime(eventTime.getDate().getValue(), DateTimeZone.UTC);
    }

    private static Events getKalenderEvent(GoogleCredential credentials, CalendarRequestInitializer credential, String CalendarID) {
        try {

            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            client = new com.google.api.services.calendar.Calendar.Builder(
                    httpTransport, JSON_FACTORY,credentials).setApplicationName("Infoskjerm").setCalendarRequestInitializer(credential).build();

            Events events = client.events().list(CalendarID)
                    .setTimeMin(new com.google.api.client.util.DateTime(new DateTime().toDate()))
                    .setTimeMax(new com.google.api.client.util.DateTime(new DateTime().plusYears(1).toDate()))
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

}

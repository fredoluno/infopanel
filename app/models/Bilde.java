package models;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.Lists;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.google.api.services.calendar.CalendarRequestInitializer;
import logic.Tjenester;
import org.mortbay.log.Log;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Created with IntelliJ IDEA.
 * User: fredrik hansen
 * Date: 12.11.13
 * Time: 21:46
 * To change this template use File | Settings | File Templates.
 */
public class Bilde {
    private static HttpTransport httpTransport;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static com.google.api.services.calendar.Calendar client;

    private static String API_KEY = "AIzaSyDmwtaFR3AbFS4_LqeB4VJ9r1J52v4Eng0";
    private static String CALENDAR_ID = "qokp9blsibr3ka32kjkcg5vmng@group.calendar.google.com";




    public static String getBilde(){

        return Tjenester.getSVG(getBildeIDfraGoogle());

    }

    public static String getBildeIDfraGoogle(){

        CalendarRequestInitializer credential = new    CalendarRequestInitializer(API_KEY);
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            client = new com.google.api.services.calendar.Calendar.Builder(
                    httpTransport, JSON_FACTORY,null).setApplicationName("Infoskjerm").setCalendarRequestInitializer(credential).build();

            Events events = client.events().list(CALENDAR_ID)
                    .setTimeMin(new DateTime(new org.joda.time.DateTime().toDate()))
                    .setTimeMax(new DateTime(new org.joda.time.DateTime().plusMinutes(1).toDate()))
                    .setSingleEvents(true)
                    .execute();
            if(!events.getItems().isEmpty()){
                return events.getItems().get(0).getSummary();
            }

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return "default";
    }
}

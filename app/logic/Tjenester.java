package logic;

import org.codehaus.jackson.JsonNode;
import org.w3c.dom.Document;
import play.Logger;
import play.cache.Cache;
import play.libs.WS;

/**
 * Created with IntelliJ IDEA.
 * User: fredrik hansen
 * Date: 11.04.13
 * Time: 16:17
 * To change this template use File | Settings | File Templates.
 */
public class Tjenester {


    public static String RUTER_URL = "http://reis.trafikanten.no/reisrest/realtime/getrealtimedata/2350040";
    public static String YR_URL = "http://www.yr.no/sted/Norge/Akershus/Ullensaker/Jessheim/varsel.xml";


    private static String NETATMO_TOKEN_URL = "http://api.netatmo.net/oauth2/token";
    private static String NETATMO_MEASURE_URL = "http://api.netatmo.net/api/getmeasure";
    private static String NETATMO_CLIENT_SECRET ="D2EBEDjEneXQRjGhn5QWlzz1ZMgi84OGUn";
    private static String NETATMO_CLIENT_ID = "5168809619775993e4000026";
    private static String DEVICE_ID = "70:ee:50:00:b1:60";
    private static String OUTDOOR_ID = "02:00:00:00:ab:86";


    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String TOKEN = "token";

    private static String KALENDER_URL = "https://www.google.com/calendar/feeds/fredoluno%40gmail.com/private-e461fa862b97c7b0b8553ed553a7414e/full";







    public static JsonNode hentSanntidsinformasjon(){
        return WS.url(RUTER_URL).get().get().asJson();
    }
    public static Document hentVaermelding(){



        Document vaermelding = (Document) Cache.get("vaermelding");
        if(vaermelding == null) {
            Logger.debug("Henter YR værrapport");
            vaermelding = WS.url(YR_URL).get().get().asXml();
            Cache.set("vaermelding", vaermelding, 10*60);


        }else{
            Logger.debug("Cachet YR værrapport");
        }
        return vaermelding;
    }

    public static Document hentKalender(){
        return WS.url(KALENDER_URL).get().get().asXml();

    }

    public static JsonNode hentNetatmoInne(){

     String token = getToken();
     Logger.debug("hentNetatmo" +  getIndoorPostMsg(token));
     JsonNode node = WS.url(NETATMO_MEASURE_URL).setHeader("Content-Type", "application/x-www-form-urlencoded").post(getIndoorPostMsg(token)).get().asJson();

     Logger.debug("Measure: " + node.toString());
     return  node;
    }
    public static JsonNode hentNetatmoUte(){

        String token = getToken();
        Logger.debug("hentNetatmo" +  getIndoorPostMsg(token));
        JsonNode node = WS.url(NETATMO_MEASURE_URL).setHeader("Content-Type", "application/x-www-form-urlencoded").post(getOutDoorPostMsg(token)).get().asJson();

        Logger.debug("Measure: " + node.toString());
        return  node;
    }

    private static String getToken() {

        String token =(String) Cache.get(TOKEN);
        Logger.debug("Token: " + token);
        if (token == null){
            JsonNode node;
            String postMsg ="";
            String refreshToken = (String)Cache.get(REFRESH_TOKEN);
            if (refreshToken != null){
                Logger.debug("Forsøker med Refresh Token");
                 node = WS.url(NETATMO_TOKEN_URL).setHeader("Content-Type", "application/x-www-form-urlencoded").post(getRefreshPostMsg(refreshToken)).get().asJson();
                if(node.get("error") != null){
                    Logger.debug("REFRESH_TOKEN_FEILER:  " + node.get("error").toString() );
                    Cache.set(REFRESH_TOKEN,null);
                    getToken();
                }
            }else{
                Logger.debug("Går for vanlig innlogging");
                node = WS.url(NETATMO_TOKEN_URL).setHeader("Content-Type", "application/x-www-form-urlencoded").post(getLoginPostMsg()).get().asJson();
                if(node.get("error") != null){
                    Logger.debug("Vanlig innlogging feiler:  " + node.get("error").toString() );
                    return null;
                }
            }
            token =  setTokenCache(node);
        }
        return token;  //To change body of created methods use File | Settings | File Templates.
    }

    private static String setTokenCache(JsonNode node) {

        String token =    replaceFnutter(node.get("access_token").toString());
        Logger.debug("setter Token til:" + token);
        Logger.debug("noden ser slik ut:" + node.toString());
        Cache.set(TOKEN, token,(new Integer(node.get("expires_in").toString()).intValue()));
        Cache.set(REFRESH_TOKEN,replaceFnutter(node.get("refresh_token").toString()));
        return token;
    }

    private static String getLoginPostMsg() {
        String postMsg = "grant_type=password" ;
        postMsg += "&client_id=" + NETATMO_CLIENT_ID;
        postMsg += "&client_secret="+ NETATMO_CLIENT_SECRET;
        postMsg += "&username=fredoluno@gmail.com";
        postMsg += "&password=peacerich";
        return postMsg;
    }
    private static String getRefreshPostMsg(String token) {
        String postMsg = "grant_type=refresh_token" ;
        postMsg += "&refresh_token=" + token;
        postMsg += "&client_id=" + NETATMO_CLIENT_ID;
        postMsg += "&client_secret="+ NETATMO_CLIENT_SECRET;

        return postMsg;
    }

    private static String getIndoorPostMsg(String token) {
        String postMsg = "access_token="+ token ;
        postMsg += "&device_id=" + DEVICE_ID;
        postMsg += "&scale=max";
        postMsg += "&type=Temperature,CO2,Humidity,Pressure,Noise";
        postMsg += "&date_end=last";
        return   postMsg;
    }

    private static String getOutDoorPostMsg(String token) {
        String postMsg = "access_token="+ token ;
        postMsg += "&device_id=" + DEVICE_ID;
        postMsg += "&module_id=" + OUTDOOR_ID;
        postMsg += "&scale=max";
        postMsg += "&type=Temperature,Humidity";
        postMsg += "&date_end=last";
        return   postMsg;
    }

    private static String replaceFnutter(String text){
        text = text.replaceAll("\"","");
        Logger.debug(text);
        return text;
    }



}

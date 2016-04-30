package logic;

import org.codehaus.jackson.JsonNode;
import org.w3c.dom.Document;
import play.Logger;
import play.cache.Cache;
import play.libs.WS;
import scala.util.control.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.Exception;


/**
 * Created with IntelliJ IDEA.
 * User: fredrik hansen
 * Date: 11.04.13
 * Time: 16:17
 * To change this template use File | Settings | File Templates.
 */
public class Tjenester {


    public static String RUTER_URL = "http://reis.trafikanten.no/reisrest/realtime/getrealtimedata/2350040";
    public static String RUTER_URLV2 = "http://reisapi.ruter.no/stopvisit/getdepartures/2350040";
    public static String YR_URL = "http://www.yr.no/sted/Norge/Akershus/Ullensaker/Jessheim/varsel.xml";


    private static String NETATMO_TOKEN_URL = "http://api.netatmo.net/oauth2/token";
    private static String NETATMO_MEASURE_URL = "http://api.netatmo.net/api/getmeasure";
    private static String NETATMO_CLIENT_SECRET ="D2EBEDjEneXQRjGhn5QWlzz1ZMgi84OGUn";
    private static String NETATMO_CLIENT_ID = "5168809619775993e4000026";
    private static String DEVICE_ID = "70:ee:50:00:b1:60";
    private static String OUTDOOR_ID = "02:00:00:00:ab:86";



    private static String GOOGLE_TOKEN_URL="https://accounts.google.com/o/oauth2/token";
    private static String GOOGLE_CLIENT_SECRET="p8H71sBAUW2NkUWXuEXieeUh";
    private static String GOOGLE_CLIENT_ID="205034400856-18dht3k2fp8erfom72nescvv1hpjs6m3.apps.googleusercontent.com";
    private static String GOOGLE_REFRESH_TOKEN ="1/EAcOCH_CR6Emvgkuswr4r7yEVi8c-tFiq4dcjHCFCQM";

    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String TOKEN = "token";
    public static final String G_REFRESH_TOKEN = "g_refresh_token";
    public static final String G_TOKEN = "g_token";

    private static String KALENDER_URL = "https://www.google.com/calendar/feeds/fredoluno%40gmail.com/private-e461fa862b97c7b0b8553ed553a7414e/full";


    private static String PUBLIC_SVG="https://dl.dropboxusercontent.com/u/53169381/skjerm.svg";
    private static String PUBLIC_SVG_FOLDER="https://dl.dropboxusercontent.com/u/53169381/infoskjerm/";

    private static String PUBLIC_VAER="https://dl.dropboxusercontent.com/u/53169381/vaer/";



    public static JsonNode hentSanntidsinformasjon(){
        return WS.url(RUTER_URL).get().get().asJson();
    }

    public static JsonNode hentSanntidsinformasjon_v2(){
       // return WS.url(RUTER_URLV2).get().get().asXml();
        try {
            return WS.url(RUTER_URLV2).setHeader("Content-Type", "application/json").get().get().asJson();
        }catch (Exception e)
        {
            e.printStackTrace();
            Logger.error(e.toString());
            return null;
        }

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




    public static String getSVG(){

        return WS.url(PUBLIC_SVG).get().get().getBody();

    }

    public static String getSVG(String bilde){
        Logger.debug(PUBLIC_SVG_FOLDER + bilde + ".svg");
        return WS.url(PUBLIC_SVG_FOLDER + bilde + ".svg").get().get().getBody();


    }
    public static String getVaerSymbol(String symbol){

        return WS.url(PUBLIC_VAER + symbol +".svg").get().get().getBody();

    }

    public static JsonNode hentNetatmoInne(){
        JsonNode node =  (JsonNode) Cache.get("netatmoinne");
        if(node == null){
         String token = getToken();
         Logger.debug("hentNetatmo" +  getIndoorPostMsg(token));
         node = WS.url(NETATMO_MEASURE_URL).setHeader("Content-Type", "application/x-www-form-urlencoded").post(getIndoorPostMsg(token)).get().asJson();

         Logger.debug("Measure: " + node.toString());
         Cache.set("netatmoinne", node, 5*60);
        }else{
            Logger.debug("henter cahcet versjon av NetatmoInne");
        }

         return  node;
    }
    public static JsonNode hentNetatmoUte(){
        JsonNode node =  (JsonNode) Cache.get("netatmoute");
        if(node == null){
            String token = getToken();
            Logger.debug("hentNetatmo" +  getIndoorPostMsg(token));
            node = WS.url(NETATMO_MEASURE_URL).setHeader("Content-Type", "application/x-www-form-urlencoded").post(getOutDoorPostMsg(token)).get().asJson();

            Logger.debug("Measure: " + node.toString());
            Cache.set("netatmoute", node, 5*60);
        }else{
            Logger.debug("henter cahcet versjon av NetatmoUt");
        }
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


    public static String getGoogleToken() {

        Cache.set(G_REFRESH_TOKEN,GOOGLE_REFRESH_TOKEN);

        String token =(String) Cache.get(G_TOKEN);
        Logger.debug("Google_Token: " + token);
     //   token = null;
        if (token == null){
            JsonNode node;
            String postMsg ="";
            String refreshToken = (String)Cache.get(G_REFRESH_TOKEN);

            Logger.debug("GOOGLE Forsøker med Refresh Token");
            node = WS.url(GOOGLE_TOKEN_URL).setHeader("Content-Type", "application/x-www-form-urlencoded").post(getGoogleRefreshPostMsg(refreshToken)).get().asJson();
            if(node.get("error") != null){
                 Logger.debug("GOOGLE REFRESH_TOKEN_FEILER:  " + node.get("error").toString() );
                 //Cache.set(G_REFRESH_TOKEN,null);
                 getToken();
             }

            token =  setGoogleTokenCache(node);
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

    private static String setGoogleTokenCache(JsonNode node) {
        String token =    replaceFnutter(node.get("access_token").toString());
        Logger.debug("setter Token til:" + token);
        Logger.debug("noden ser slik ut:" + node.toString());
        Cache.set(G_TOKEN, token,(new Integer(node.get("expires_in").toString()).intValue()));
        //Cache.set(REFRESH_TOKEN,replaceFnutter(node.get("refresh_token").toString()));
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

    private static String getGoogleRefreshPostMsg(String token) {
        String postMsg = "grant_type=refresh_token" ;
        postMsg += "&refresh_token=" + token;
        postMsg += "&client_id=" + GOOGLE_CLIENT_ID;
        postMsg += "&client_secret="+ GOOGLE_CLIENT_SECRET;
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

    public static String  getAccessTokenGooglePostMsg()
    {
        String postMsg ="code=4%2FcDSv2ESOIaEcl0GRbD8uFA6IMwjc.0sQLZhqWi_MZOl05ti8ZT3aTQ-mhiAI&";
        postMsg += "client_id=205034400856-oimgsumc6uik4paem53h4mosmgal8jc5.apps.googleusercontent.com&";
        postMsg += "scope=&client_secret=rM54dwVG2T1T9TemfM82tF9G&";
        postMsg += "redirect_uri=http%3A%2F%2Fwww.vg.no&";
        postMsg +="grant_type=authorization_code";

       return postMsg;
    }

}






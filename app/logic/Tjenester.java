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
    public static String NETATMO_TOKEN_URL = "http://api.netatmo.net/oauth2/token";

    private static String NETATMO_CLIENT_SECRET ="D2EBEDjEneXQRjGhn5QWlzz1ZMgi84OGUn";
    private static String NETATMO_CLIENT_ID = "5168809619775993e4000026";



    public static JsonNode hentSanntidsinformasjon()
    {
        return WS.url(RUTER_URL).get().get().asJson();
    }
    public static Document hentVaermelding()
    {

        Document vaermelding = (Document) Cache.get("vaermelding");
        if(vaermelding == null) {
            Logger.debug("Henter YR værrapport");
            vaermelding = WS.url(YR_URL).get().get().asXml();
            Cache.set("vaermelding", vaermelding, 10*60);
            Logger.debug(WS.url(YR_URL).get().get().getBody());

        }else{
            Logger.debug("Cachet YR værrapport");
        }
        return vaermelding;
    }


}

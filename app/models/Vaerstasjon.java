package models;

import org.codehaus.jackson.JsonNode;
import play.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: fredrik hansen
 * Date: 17.04.13
 * Time: 22:03
 * To change this template use File | Settings | File Templates.
 */
public class Vaerstasjon {


    public String inneTemperatur;
    public String co2;
    public String inneFuktighet;
    public String trykk;
    public String lyd;
    public String uteTemperatur;
    public String uteFuktighet;


    public  Vaerstasjon(JsonNode inne, JsonNode ute){
        Logger.debug("body" + inne.get("body").toString());
        Logger.debug("body2" + inne.get("body").getElements().next().get("value").getElements().next().toString());
        String inneliste = removeBrackets(inne.get("body").getElements().next().get("value").getElements().next().toString());
        Logger.debug("inneliste:" + inneliste);

        String[] liste = inneliste.split(",");
        inneTemperatur = liste[0];
        co2 = liste[1];
        inneFuktighet = liste[2];
        trykk = liste[3];
        lyd = liste[4];
        String uteliste = removeBrackets(ute.get("body").getElements().next().get("value").getElements().next().toString());
        String[] liste2 = uteliste.split(",");

        uteTemperatur = liste2[0];
        uteFuktighet = liste2[1];
    }

    private String removeBrackets(String text){

        return        text.replaceAll("\\]","").replaceAll("\\[","") ;
    }
}

package controllers;

import logic.Tjenester;
import models.*;
import org.codehaus.jackson.JsonNode;
import org.w3c.dom.Document;
import play.libs.WS;
import play.mvc.*;

public class Application extends Controller {


    public static Result index() {

        Tog tog = new Tog(Tjenester.hentSanntidsinformasjon());
        Vaermelding vaermelding =   new Vaermelding(Tjenester.hentVaermelding());
        Kalender kalender = new Kalender(Tjenester.hentKalender());
        Vaerstasjon vaerstasjon = new Vaerstasjon(Tjenester.hentNetatmoInne(),Tjenester.hentNetatmoUte());
        Infoskjerm infoskjerm =   new Infoskjerm(tog,vaermelding ,kalender,vaerstasjon);
        return ok(views.html.index.render(infoskjerm));

    }



  
}

package controllers;

import logic.Tjenester;
import models.*;
import play.libs.WS;
import play.mvc.*;

public class Application extends Controller {


    public static Result index() {

        Infoskjerm infoskjerm =   new Infoskjerm(new Tog(Tjenester.hentSanntidsinformasjon()), new Vaermelding(Tjenester.hentVaermelding()),new Kalender(Tjenester.hentKalender()));
        return ok(views.html.index.render(infoskjerm));

    }



  
}

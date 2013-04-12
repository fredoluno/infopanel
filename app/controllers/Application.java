package controllers;

import logic.Tjenester;
import models.*;
import play.libs.WS;
import play.mvc.*;

public class Application extends Controller {


    public static Result index() {

        String ole = "";
        return ok(views.html.index.render(new Infoskjerm(new Tog(Tjenester.hentSanntidsinformasjon()), new Vaermelding(Tjenester.hentVaermelding()))));

    }



  
}

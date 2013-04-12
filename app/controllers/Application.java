package controllers;

import logic.Ruter;
import play.*;
import play.data.*;
import models.*;
import play.libs.F.Promise;
import play.libs.WS;
import play.mvc.*;
import play.libs.F.Function;



import views.html.*;

public class Application extends Controller {


    public static Result index() {
        WS.url(Ruter.URL).get().get();
        return ok(views.html.index.render(new Infoskjerm(new Tog(WS.url(Ruter.URL).get().get().asJson()))));

    }



  
}

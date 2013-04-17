package models;

import org.codehaus.jackson.JsonNode;

/**
 * Created with IntelliJ IDEA.
 * User: fredrik hansen
 * Date: 11.04.13
 * Time: 19:20
 * To change this template use File | Settings | File Templates.
 */
public class Infoskjerm {


    public Tog tog;
    public Vaermelding vaermelding;
    public Kalender kalender;
    public Vaerstasjon vaerstasjon;


    public Infoskjerm(Tog tog, Vaermelding vaermelding, Kalender kalender, Vaerstasjon vaerstasjon ){
        this.tog = tog;
        this.vaermelding = vaermelding;
        this.kalender = kalender;
        this.vaerstasjon = vaerstasjon;

    }



}

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


    public Infoskjerm(Tog tog, Vaermelding vaermelding ){
        this.tog = tog;
        this.vaermelding = vaermelding;

    }



}

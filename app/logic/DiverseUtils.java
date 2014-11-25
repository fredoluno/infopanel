package logic;

import models.Infoskjerm;
import models.KalenderEvent;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import play.Logger;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeSet;

public class DiverseUtils {
    public static String hentVerdi(String tag, Element element) {
        try{
        NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodes.item(0);
        return node.getNodeValue();
        }catch(Exception e)
        {
            return "";
        }
    }

    public static boolean erIdag(DateTime dateToCheck) {
        DateTime now = (new DateTime()).withTimeAtStartOfDay();
        DateTime tomorrow = now.plusDays(1);

        if (dateToCheck.compareTo(now)>0 && dateToCheck.compareTo(tomorrow) < 0 ){
            return true;
        }
        return false;
    }

    public static boolean erIdag(DateTime fra, DateTime til) {
        DateTime now = (new DateTime()).withTimeAtStartOfDay();
        DateTime tomorrow = now.plusDays(1);

        if (fra.compareTo(tomorrow)< 0 && til.compareTo(now)>0 ){
            return true;
        }
        return false;
    }

     public static boolean datoPassert(DateTime dateToCheck){
         DateTime today = (new DateTime()).withTimeAtStartOfDay();
         if(dateToCheck.compareTo(today) <= 0){
             return true;
         }
         return false;

     }

    public static boolean erSammeDag(DateTime dato, DateTime dato2) {

//        Logger.debug("dato: " + dato.toString()+ " dato2: " + dato2);
//        Logger.debug("datom: " + dato.toDateMidnight()+ " dato2m: " + dato2.minusMinutes(1).toDateMidnight());
//
//        if(dato.toDateMidnight().compareTo(dato2.minusMinutes(1).toDateMidnight())== 0){
//            Logger.debug("Er samme dag2");
//            return true;
//        }


        DateTime now = dato.withTimeAtStartOfDay();
        DateTime tomorrow = now.plusDays(1);
        tomorrow = tomorrow.plusMinutes(1);
        Logger.debug("now: " + now.toString()+ " tomorrow: " + tomorrow);
        if (dato2.compareTo(now)>0 && dato2.compareTo(tomorrow) < 0 ){
//            Logger.debug("Er samme dag");
            return true;
        }
        return false;
    }
    public static boolean erInnen7dager(DateTime dato) {
        DateTime now = (new DateTime()).withTimeAtStartOfDay();
        DateTime tomorrow = now.plusDays(7);

        if (dato.compareTo(now)>0 && dato.compareTo(tomorrow) <= 0 ){
            return true;
        }
        return false;
    }

    public  static String naa(){
           DateTime now = new DateTime();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm");
        return fmt.print(now);
    }
    public  static String dag(DateTime dag){
        DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEE").withLocale(new Locale("no","NO"));
        return fmt.print(dag);
    }


    public static InputStream fyllBilde(String bilde, Infoskjerm infoskjerm)
    {
        try{
 //           Integer teller = (Integer) Cache.get("vaer");
 //           if(teller == null || teller.intValue()>24)
 //           {
 //               teller = new Integer(1);
 //           }
            bilde = bilde.replaceAll("@@OPPDATERT@@", DiverseUtils.naa());
            bilde = bilde.replaceAll("@@INNETEMP@@",left(infoskjerm.vaerstasjon.inneTemperatur, ".")) ;
            bilde = bilde.replaceAll("@@UTETEMP@@", left(infoskjerm.vaerstasjon.uteTemperatur,"."));
            bilde = bilde.replaceAll("@@INNEFUKTIGHET@@", infoskjerm.vaerstasjon.inneFuktighet);
            bilde = bilde.replaceAll("@@UTEFUKTIGHETT@@", infoskjerm.vaerstasjon.uteFuktighet);
            bilde = bilde.replaceAll("@@LYD@@",         infoskjerm.vaerstasjon.lyd);
            bilde = bilde.replaceAll("@@TRYKK@@",       infoskjerm.vaerstasjon.trykk);
            bilde = bilde.replaceAll("@@CO2@@",         infoskjerm.vaerstasjon.co2);
            bilde = bilde.replaceAll("@@AVGANG@@",      infoskjerm.tog.avgang);
            bilde = bilde.replaceAll("@@ANKOMST@@",      infoskjerm.tog.ankomst);
   //         bilde = bilde.replaceAll("@@VARSELDAG@@",""+ teller.intValue()/*infoskjerm.vaermelding.dagTemperatur*/);
            bilde = bilde.replaceAll("@@VARSELDAG@@",   infoskjerm.vaermelding.dagTemperatur);
            bilde = bilde.replaceAll("@@VARSELNATT@@",  infoskjerm.vaermelding.nattTemperatur);
            bilde = bilde.replaceAll("@@VARSELMORGEN@@",infoskjerm.vaermelding.morgenTemperatur);
            bilde = bilde.replaceAll("@@VARSELKVELD@@", infoskjerm.vaermelding.kveldsTemperatur);
            bilde = bilde.replaceAll("@@VARSETITTEL@@", infoskjerm.vaermelding.getVarselTittel() );


 //           bilde = bilde.replaceAll("@@SYMBOL@@","v" + teller.intValue()/* infoskjerm.vaermelding.dagSymbol */);
            bilde = bilde.replaceAll("@@SYMBOL@@","v" + infoskjerm.vaermelding.dagSymbol);
            bilde = bilde.replaceAll("@@VARSELNATTSYMBOL@@", "v" +  infoskjerm.vaermelding.nattSymbol);
            bilde = bilde.replaceAll("@@VARSELMORGENSYMBOL@@","v" + infoskjerm.vaermelding.morgenSymbol);
            bilde = bilde.replaceAll("@@VARSELKVELDSYMBOL@@","v" +  infoskjerm.vaermelding.kveldsSymbol);


            bilde = settInnEventer(bilde,infoskjerm);
            bilde = settInnLangtidsvarsel(bilde,infoskjerm)  ;
//            Logger.debug(bilde);
 //           teller = new Integer(teller.intValue()+1);
 //           Cache.set("vaer", teller);
            return  new ByteArrayInputStream(bilde.getBytes("UTF-8"));
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    public static String settInnEventer(String bilde, Infoskjerm infoskjerm){
        TreeSet<KalenderEvent> eventer = infoskjerm.kalender.eventerKomplett;
        Iterator iterator = eventer.iterator()        ;

        int teller = 1;
        while(iterator.hasNext()) {
            KalenderEvent event = (KalenderEvent)iterator.next();

            bilde = bilde.replaceAll("@@EVENT"+ teller +"@@",""+event.printDato() + " - "+ event.tittel );
            teller++;
        }

        while(teller < 10){
            bilde = bilde.replaceAll("@@EVENT"+ teller +"@@","" );
            teller ++;
        }
        return    bilde;
    }

    public static String settInnLangtidsvarsel(String bilde, Infoskjerm infoskjerm)
    {
        for(String val :  infoskjerm.vaermelding.langtidsvarselet.keySet()){
            bilde = bilde.replaceAll("@@"+val+"@@",infoskjerm.vaermelding.langtidsvarselet.get(val) );
        }
        return bilde;
    }
    public static String left(String text, String divider){
        int  teller = text.indexOf(divider);
        if (teller < 0) return text;

        return text.substring(0,teller) ;
    }
}

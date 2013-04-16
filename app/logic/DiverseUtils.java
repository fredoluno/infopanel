package logic;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
         if(dateToCheck.compareTo(today)< 0){
             return true;
         }
         return false;

     }

    public static boolean erSammeDag(DateTime dato, DateTime dato2) {
        DateTime now = dato.withTimeAtStartOfDay();
        DateTime tomorrow = now.plusDays(1);
        tomorrow = tomorrow.plusMinutes(1);

        if (dato2.compareTo(now)>0 && dato2.compareTo(tomorrow) < 0 ){
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
}

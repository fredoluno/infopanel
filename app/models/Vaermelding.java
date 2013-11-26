package models;

import logic.DiverseUtils;
import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import play.Logger;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: fredrik hansen
 * Date: 12.04.13
 * Time: 20:45
 * To change this template use File | Settings | File Templates.
 */
public class Vaermelding {
    public  boolean varselFraIdag = true;

    public String nattTemperatur ="";
    public String nattSymbol ="";
    public String morgenTemperatur ="";
    public String morgenSymbol = "";
    public String dagTemperatur ="";
    public String kveldsTemperatur ="";
    public String kveldsSymbol = "";

    public String dagSymbol;
    public int langtidsvarsel = 0;
    public Document vaerXML;
    public String temptekst;
    public HashMap <String,String>langtidsvarselet;


    public Vaermelding(Document document){
        vaerXML = document;
        langtidsvarselet = new HashMap<String,String>();
        Node tabular =document.getElementsByTagName("tabular").item(0);

        NodeList nodeList =  tabular.getChildNodes();
        boolean  foersteElement = true;
        int  dagPeriode = 2;
        boolean hovedFerdig=false;

        for (int i = 0; i < nodeList.getLength(); i++)   {
            Node node = nodeList.item(i)  ;
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element)node;
                String period =  element.getAttribute("period");
                if(foersteElement && new Integer(period).intValue()> dagPeriode)
                {
                    //det er da kveld og vi henter heller morgendagens varsel.
                    foersteElement = false;
                    varselFraIdag = false;
                    continue ;

                }
                foersteElement = false;
                if(!hovedFerdig){
                    if(period.equals("0")){
                        nattSymbol = ((Element)element.getElementsByTagName("symbol").item(0)).getAttribute("number");
                        nattTemperatur  = ((Element)element.getElementsByTagName("temperature").item(0)).getAttribute("value");
                    }else if(period.equals("1")){
                        morgenSymbol = ((Element)element.getElementsByTagName("symbol").item(0)).getAttribute("number");
                        morgenTemperatur  =  ((Element)element.getElementsByTagName("temperature").item(0)).getAttribute("value");
                    }else if(period.equals("2")){
                        dagSymbol = ((Element)element.getElementsByTagName("symbol").item(0)).getAttribute("number");
                        dagTemperatur  =  ((Element)element.getElementsByTagName("temperature").item(0)).getAttribute("value");
                    }
                    else if(period.equals("3")){
                        kveldsSymbol = ((Element)element.getElementsByTagName("symbol").item(0)).getAttribute("number");
                        kveldsTemperatur  =   ((Element)element.getElementsByTagName("temperature").item(0)).getAttribute("value");
                        hovedFerdig=true;
                    }
                }
                else{
                      leggTilLangtidsvarsel(element);
                }


            }
        }
        printLangtidsvarsel();
    }

    private void leggTilLangtidsvarsel(Element element) {
       langtidsvarselet.put("langtidsvarsel." + langtidsvarsel+".temperatur",((Element)element.getElementsByTagName("temperature").item(0)).getAttribute("value"));
       langtidsvarselet.put("langtidsvarsel." + langtidsvarsel+".symbol",((Element)element.getElementsByTagName("symbol").item(0)).getAttribute("number"));
       langtidsvarselet.put("langtidsvarsel." + langtidsvarsel + ".dag", DiverseUtils.dag(new DateTime(element.getAttribute("from"))));
       langtidsvarselet.put("langtidsvarsel." + langtidsvarsel+".temperatur",((Element)element.getElementsByTagName("temperature").item(0)).getAttribute("value"));
        langtidsvarsel++;

    }

    public void printLangtidsvarsel(){
        Logger.debug("YEAH");
      for(String val : langtidsvarselet.keySet()){
          Logger.debug(val + " - " +langtidsvarselet.get(val));
      }
    }

    public String getVarselTittel(){
        if(varselFraIdag) return "Været i dag";
        return "Været i morgen";

    }


}

package models;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
    public String morgenTemperatur ="";
    public String dagTemperatur ="";
    public String kveldsTemperatur ="";

    public String vaerSymbol;

    public Document vaerXML;
    public String temptekst;

    public Vaermelding(Document document){
        vaerXML = document;

        Node tabular =document.getElementsByTagName("tabular").item(0);

        NodeList nodeList =  tabular.getChildNodes();
        boolean  foersteElement = true;
        int  dagPeriode = 2;

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
                if(period.equals("0")){
                    nattTemperatur  = ((Element)element.getElementsByTagName("temperature").item(0)).getAttribute("value");
                }else if(period.equals("1")){
                    morgenTemperatur  =  ((Element)element.getElementsByTagName("temperature").item(0)).getAttribute("value");
                }else if(period.equals("2")){
                    vaerSymbol = ((Element)element.getElementsByTagName("symbol").item(0)).getAttribute("number");
                    dagTemperatur  =  ((Element)element.getElementsByTagName("temperature").item(0)).getAttribute("value");
                }
                else if(period.equals("3")){

                    kveldsTemperatur  =   ((Element)element.getElementsByTagName("temperature").item(0)).getAttribute("value");
                    break;
                }



            }
        }
    }

    public String getVarselTittel(){
        if(varselFraIdag) return "varsel i dag";
        return "varsel i morgen";

    }


}

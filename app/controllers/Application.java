package controllers;


import logic.DiverseUtils;
import logic.Tjenester;
import models.*;
import org.apache.batik.ext.awt.image.codec.imageio.PNGTranscoderImageIOWriteAdapter;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.io.IOUtils;

import play.Logger;
import play.data.format.Formats;
import play.mvc.*;
import play.api.Play;
import scala.reflect.io.VirtualFile;
import java.awt.Color;

import java.io.*;
import java.net.MalformedURLException;
import java.awt.image.BufferedImage;
import java.awt.color.ColorSpace;

import java.awt.image.ColorConvertOp;
import java.net.URL;
import java.text.DateFormat;
import java.util.Locale;


import javax.imageio.ImageIO;


public class Application extends Controller {




    public static Result index() {

        Infoskjerm infoskjerm = getInfoskjerm();
        return ok(views.html.index.render(infoskjerm));

    }



    private static Infoskjerm getInfoskjerm() {
        Vaermelding vaermelding =   new Vaermelding(Tjenester.hentVaermelding());
        Kalender kalender = new Kalender();
        Vaerstasjon vaerstasjon = new Vaerstasjon(Tjenester.hentNetatmoInne(),Tjenester.hentNetatmoUte());
        Tog tog = new Tog(Tjenester.hentSanntidsinformasjon_v2());
        return new Infoskjerm(tog,vaermelding ,kalender,vaerstasjon);
    }

    public static Result locale() throws IOException{
        String lo = "";
        Locale list[] = DateFormat.getAvailableLocales();
        for (Locale aLocale : list) {
            lo = lo +"\n" + aLocale.toString();
        }
        return ok(lo);
    }



    public static Result google() throws IOException{
//        Kalender kalender = new Kalender();
//        return ok(kalender.printEventer());

        Tog toget = new Tog(Tjenester.hentSanntidsinformasjon_v2());
        return ok("ssss");
    }

    public static Result setBilde(String id) throws IOException{

        Tjenester.skrivFil(id);
        Logger.error("skrivfil=" + id);
        return ok("OK="+id);
    }
    public static Result hentBilde() throws IOException{
        String filtekst=Tjenester.lesFil();
        Logger.error("filtekst=" + filtekst);
        if(!filtekst.trim().equals(""))   {
            Logger.error("adadas");
            Tjenester.skrivFil("");
            return ok( IOUtils.toByteArray(Tjenester.getCloudinary(filtekst))).as("image/png");
        } else{
            return ok("Ikke noe bilde");
        }


    }

    public static Result bilde() throws IOException {

        try
        {
            String filtekst=Tjenester.lesFil();
            Logger.error("filtekst=" + filtekst);
            if(!filtekst.trim().equals(""))   {
                Logger.error("adadas");
                Tjenester.skrivFil("");
                return ok( IOUtils.toByteArray(Tjenester.getCloudinary(filtekst))).as("image/png");
            } else{

                long startTime = System.nanoTime();

                //        MÅ GJØRES INNTIL JEG VEIT HVA SOM SKAPER ISSUES PÅ HEROKU. SER UT TIL AT DE IKKE GREIER Å SETTE MAX HEAP
                System.gc();

                PNGTranscoder t = new PNGTranscoder();
                ByteArrayOutputStream ostream ;

                ostream = new ByteArrayOutputStream();    //("public/images/out.png");
                TranscoderOutput output = new TranscoderOutput(ostream );
                TranscoderInput input = new TranscoderInput(DiverseUtils.fyllBilde(Bilde.getBilde(), getInfoskjerm()));   //new FileInputStream("public/images/skjermplain.svg"))  ;
                t.addTranscodingHint(PNGTranscoder.KEY_BACKGROUND_COLOR, Color.white);

                t.transcode(input, output);

                BufferedImage image = ImageIO.read(new ByteArrayInputStream(ostream.toByteArray()));
                ColorConvertOp colorConvert = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
                BufferedImage image2 = new BufferedImage(600,800, BufferedImage.TYPE_BYTE_GRAY );
                colorConvert.filter(image, image2);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image2, "png",baos );
                ostream.close();
                long endTime = System.nanoTime();

                long duration = (endTime - startTime);
                Logger.info("Tidbrukt=" + duration);
                return ok(baos.toByteArray()).as("image/png");
            }

        } catch (Exception e) {
            Logger.error(e.toString());
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }



        return ok("failed");

    }
}


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
        Kalender kalender = new Kalender(Tjenester.hentKalender());
        Vaerstasjon vaerstasjon = new Vaerstasjon(Tjenester.hentNetatmoInne(),Tjenester.hentNetatmoUte());
        Tog tog = new Tog(Tjenester.hentSanntidsinformasjon());
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
        Bilde bilde = new Bilde();
        return ok(bilde.getBilde());
    }

    public static Result bilde() throws IOException {

        try {
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

            return ok(baos.toByteArray()).as("image/png");

        } catch (Exception e) {
            Logger.debug(e.toString());
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }



        return ok("failed");

    }
}


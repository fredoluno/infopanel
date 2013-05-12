package controllers;

import logic.Tjenester;
import models.*;
import org.apache.batik.ext.awt.image.codec.imageio.PNGTranscoderImageIOWriteAdapter;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.commons.io.output.ByteArrayOutputStream;
import play.Logger;
import play.mvc.*;
import play.api.Play;
import scala.reflect.io.VirtualFile;
import java.awt.Color;

import java.io.*;
import java.net.MalformedURLException;
import java.awt.image.BufferedImage;
import java.awt.color.ColorSpace;

import java.awt.image.ColorConvertOp;


import javax.imageio.ImageIO;


public class Application extends Controller {


    public static Result index() {

        Tog tog = new Tog(Tjenester.hentSanntidsinformasjon());
        Vaermelding vaermelding =   new Vaermelding(Tjenester.hentVaermelding());
        Kalender kalender = new Kalender(Tjenester.hentKalender());
        Vaerstasjon vaerstasjon = new Vaerstasjon(Tjenester.hentNetatmoInne(),Tjenester.hentNetatmoUte());
        Infoskjerm infoskjerm =   new Infoskjerm(tog,vaermelding ,kalender,vaerstasjon);
        return ok(views.html.index.render(infoskjerm));

    }


    public static Result bilde() throws IOException {
        // Create a JPEG transcoder


        // Set the transcoding hints.
       // t.addTranscodingHint(PN);
        //t.addTranscodingHint(PNGTranscoder.KEY_QUALITY,
          //      new Float(.8));

        // Create the transcoder input.


        String svgURI = null;
        try {
            PNGTranscoder t = new PNGTranscoder();
            ByteArrayOutputStream ostream ;

            ostream = new ByteArrayOutputStream();    //("public/images/out.png");
            TranscoderOutput output = new TranscoderOutput(ostream );
            TranscoderInput input = new TranscoderInput(Tjenester.getSVG());   //new FileInputStream("public/images/skjermplain.svg"))  ;
            t.addTranscodingHint(PNGTranscoder.KEY_BACKGROUND_COLOR, Color.white);

            t.transcode(input, output);



            BufferedImage image = ImageIO.read(new ByteArrayInputStream(ostream.toByteArray()));
            ColorConvertOp colorConvert = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
            BufferedImage image2 = new BufferedImage(600,800, BufferedImage.TYPE_BYTE_GRAY );
            colorConvert.filter(image, image2);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image2, "png",baos );
            //InputStream is = new ByteArrayInputStream(baos.toByteArray());
              //return ok("asdad");
            return ok(baos.toByteArray()).as("image/png");
            //ostream.flush();
            //ostream.close();

        } catch (Exception e) {
            Logger.debug(e.toString());
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }



        return ok("failed");

    }



  
}

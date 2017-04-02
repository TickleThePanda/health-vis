package uk.co.ticklethepanda.utility.image;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by panda on 06/12/2016.
 */
public class PngToByteArray {

    public static byte[] convert(BufferedImage image) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ColorModel cm = image.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = image.copyData(null);
        BufferedImage imageClone = new BufferedImage(cm, raster, isAlphaPremultiplied, null);

        Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("png");
        ImageWriter writer = iter.next();

        writer.setOutput(ImageIO.createImageOutputStream(stream));
        IIOImage ioImage = new IIOImage(imageClone, null, null);
        writer.write(null, ioImage, null);
        writer.dispose();

        return stream.toByteArray();
    }
}

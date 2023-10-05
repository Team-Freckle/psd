package pcot.psd.entity.buffered;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

public class PcotBufferedImage extends BufferedImage {
    public PcotBufferedImage(int width, int height, int imageType) {
        super(width, height, imageType);
    }

    public PcotBufferedImage(int width, int height, int imageType, IndexColorModel cm) {
        super(width, height, imageType, cm);
    }

    public PcotBufferedImage(ColorModel cm, WritableRaster raster, boolean isRasterPremultiplied, Hashtable<?, ?> properties) {
        super(cm, raster, isRasterPremultiplied, properties);
    }

    public void saveToImage(String path, String fileName, ImageType type) throws IOException {
        String format;
        if (type == ImageType.PNG) {
            format = "png";
        } else if (type == ImageType.JPEG) {
            format = "jpg";
        } else {
            throw new IOException("Unknown Type");
        }
        try {
            File outputFile =
                    new File(path
                            + fileName
                            + "." + format);

            if (outputFile.createNewFile()) {
                System.out.println("File created");
            } else {
                throw new IOException("File already exists");
            }

            ImageIO.write(this, format, outputFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

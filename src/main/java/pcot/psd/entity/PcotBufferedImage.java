package pcot.psd.entity;

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

            if (format.equals("jpg")) {
                int width = this.getWidth();
                int height = this.getHeight();
                BufferedImage rgb = new BufferedImage(
                        width
                        , height
                        , BufferedImage.TYPE_INT_RGB
                );
                rgb.createGraphics().drawImage(
                        this
                        , 0
                        , 0
                        , width
                        , height
                        , null
                );
                ImageIO.write(rgb, format, outputFile);
            }
            else {
                ImageIO.write(this, format, outputFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (! (o instanceof BufferedImage imgB)) return false;
        BufferedImage imgA = this;
        if (imgA.getWidth() != imgB.getWidth() || imgA.getHeight() != imgB.getHeight()) {
            return false;
        }

        int width  = imgA.getWidth();
        int height = imgA.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (imgA.getRGB(x, y) != imgB.getRGB(x, y)) {
                    return false;
                }
            }
        }

        return true;
    }
}

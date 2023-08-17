import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class ImageService {
    public void saveToImage(BufferedImage image, String fileName) {
        try {
            File outputFile =
                    new File("C:/Users/cksgu/git/psd/src/main/resources/png/"
                            + fileName
                            + ".png");

            if (outputFile.createNewFile()) {
                System.out.println("File created");
            } else {
                throw new IOException("File already exists");
            }

            ImageIO.write(image, "png", outputFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

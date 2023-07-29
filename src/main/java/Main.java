import com.idrsolutions.image.png.PngEncoder;
import com.idrsolutions.image.utility.ImageInfo;
import psd.BinaryReader;
import psd.PsdReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class Main {
    public static void main(String[]args) throws IOException {
        PsdReader psdReader = new PsdReader();
        BinaryReader binaryReader = new BinaryReader();
        ImageService imageService = new ImageService();

        FileInputStream fis = new FileInputStream("C:/Users/cksgu/git/psd/src/main/resources/alt.psd");
        BufferedInputStream bis = new BufferedInputStream(fis);

        binaryReader.setInput(bis);
        System.out.println(binaryReader.getBinary());
        bis = new BufferedInputStream(new FileInputStream("C:/Users/cksgu/git/psd/src/main/resources/alt.psd"));

        psdReader.open(bis);
        psdReader.run();

        imageService.saveToImage(psdReader.getPreview(), "output");

        System.out.println(psdReader.toString());
    }


}
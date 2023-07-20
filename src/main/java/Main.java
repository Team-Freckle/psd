import psd.BinaryReader;
import psd.PsdReader;

import java.io.*;

public class Main {
    public static void main(String[]args) throws IOException {
        PsdReader psdReader = new PsdReader();
        BinaryReader binaryReader = new BinaryReader();

        FileInputStream fis = new FileInputStream("C:/Users/cksgu/git/psd/src/main/resources/psdss.psd");
        BufferedInputStream bis = new BufferedInputStream(fis);

        binaryReader.setInput(bis);
        System.out.println(binaryReader.getBinary());
        bis = new BufferedInputStream(new FileInputStream("C:/Users/cksgu/git/psd/src/main/resources/psdss.psd"));

        psdReader.open(bis);
        psdReader.run();

        System.out.println(psdReader.toString());
    }
}
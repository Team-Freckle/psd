import psd.BinaryReader;
import psd.PsdReader;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    public static void main(String[]args) throws IOException {
        PsdReader psdReader = new PsdReader();
        BinaryReader binaryReader = new BinaryReader();

        FileInputStream fis = new FileInputStream("C:/Users/cksgu/git/psd/src/main/resources/psds.psd");
        BufferedInputStream bis = new BufferedInputStream(fis);

        binaryReader.setInput(bis);
        System.out.println(binaryReader.getBinary());
        bis = new BufferedInputStream(new FileInputStream("C:/Users/cksgu/git/psd/src/main/resources/psds.psd"));

        psdReader.open(bis);
        psdReader.run();
    }
}
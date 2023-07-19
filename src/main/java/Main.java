import psd.PsdReader;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[]args) throws FileNotFoundException {
        PsdReader psdReader = new PsdReader();

        FileInputStream fis = new FileInputStream("c:/Users/user/git/psd/src/main/resources/psds.psd");
        BufferedInputStream bis = new BufferedInputStream(fis);

        psdReader.open(bis);
        psdReade
    }
}
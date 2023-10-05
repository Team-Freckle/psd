package pcot.psd;

import pcot.psd.entity.Psd;
import pcot.psd.entity.PsdReader;
import java.io.*;

public class PSDPcot {
    private final PsdReader psdManipulator;
    public PSDPcot() {
        psdManipulator = new PsdReader();
    }

    public Psd parsingPSD(String path) throws IOException {
        FileInputStream fis = new FileInputStream(path);
        BufferedInputStream bis = new BufferedInputStream(fis);

        psdManipulator.open(bis);
        psdManipulator.run();
        return psdManipulator.getPsd();
    }
}
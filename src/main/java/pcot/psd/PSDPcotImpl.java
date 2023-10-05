package pcot.psd;

import pcot.psd.entity.Psd;
import pcot.psd.entity.PsdReader;
import java.io.*;

public class PSDPcotImpl implements PSDPcot {
    private final PsdReader psdManipulator;
    public PSDPcotImpl() {
        psdManipulator = new PsdReader();
    }

    @Override
    public Psd parsingPSD(String path) throws IOException {
        FileInputStream fis = new FileInputStream(path);
        BufferedInputStream bis = new BufferedInputStream(fis);

        psdManipulator.open(bis);
        psdManipulator.run();
        return psdManipulator.getPsd();
    }
}
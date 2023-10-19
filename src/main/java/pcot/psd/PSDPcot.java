package pcot.psd;

import pcot.psd.entity.PsdEntity;
import pcot.psd.entity.PsdLayer;
import pcot.psd.entity.PsdReader;
import java.io.*;

public class PSDPcot {
    private final PsdReader psdManipulator;
    public PSDPcot() {
        psdManipulator = new PsdReader();
    }

    public PsdEntity parsingPSD(String path) throws IOException {
        FileInputStream fis = new FileInputStream(path);
        BufferedInputStream bis = new BufferedInputStream(fis);

        psdManipulator.open(bis);
        psdManipulator.run();
        return psdManipulator;
    }
    public PsdEntity parsingPSD(BufferedInputStream bis) throws IOException {
        psdManipulator.open(bis);
        psdManipulator.run();
        return psdManipulator;
    }
    public String psdLayerToString(PsdLayer[] layers) {
        String str = new String();
        return getPsdLayerString(layers, 0, str);
    }

    private String getPsdLayerString(PsdLayer[] psdLayers, int debs, String str) {
        for (PsdLayer psdLayer : psdLayers) {
            for (int j = 0; j < debs; j++) str += (">");
            if(debs > 0) str += (" ");
            str += (psdLayer.getName()) + "\n";
            if (psdLayer.getFolderYn() == 'Y') {
                str += getPsdLayerString(psdLayer.getChild(), debs + 1, "");
            }
        }
        return str;
    }
}
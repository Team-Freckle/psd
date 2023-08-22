package psd.manipulator;

import psd.component.PsdLayer;
import psd.manipulator.PsdReader;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PsdEditor extends PsdReader {
    public void addLayer(PsdLayer layer) throws IOException {
        ByteArrayOutputStream info = new ByteArrayOutputStream();
        info.write(layer.getTop());
        info.write(layer.getLeft());
        info.write(layer.getBottom());
        info.write(layer.getRight());
        info.write((short) layer.getChannelCount());
        for(int i = 0; i < layer.getChannelCount(); i++) {
            info.write((short) layer.getChannelId()[i]);
            info.write(layer.getChannelSize()[i]);
        }
        info.write("8BIM".getBytes());
        info.write(layer.getModeKey().getBytes());
        info.write((byte) layer.getTransparency());
        info.write(layer.getClipping() == 'Y' ? (byte)1:(byte)0);
        byte flag = 0;
        if(layer.getProtectTransparency() == 'Y') flag |= 0x01;
        if(layer.getVision() == 'Y') flag |= 0x02;
        info.write(flag);
        info.write((byte) 0);
        info.write(8);
        info.write(0);
        info.write(0);
        byte[] nameBytes = layer.getName().getBytes();
        int padding = 4 - (nameBytes.length % 4);
        byte[] paddingName = new byte[nameBytes.length + padding];
        System.arraycopy(nameBytes, 0, paddingName, 0, nameBytes.length);
        info.write(paddingName);

        byte[] export = info.toByteArray();
        String print = "";
        for (byte b : export) {
            print += (char) b;
        }

        System.out.println(print);
    }
}

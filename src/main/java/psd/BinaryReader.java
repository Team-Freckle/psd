package psd;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BinaryReader {
    protected BufferedInputStream input;

    public String getBinary() throws IOException {
        String moreBinary = "";
        int data;
        int p = 1;
        while ((data = input.read()) != -1) {
            moreBinary += p++ + " : " + data + " " + (char) data + " " + "\n";
        }
        moreBinary += "-------------\n";
        return moreBinary;
    }

    public void setInput(InputStream stream) {
        if (stream instanceof BufferedInputStream) input = (BufferedInputStream) stream;
        else input = new BufferedInputStream(stream);
    }
}

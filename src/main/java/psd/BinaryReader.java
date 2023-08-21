package psd;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BinaryReader {
    protected BufferedInputStream input;
    protected int inputLen;

    public String getBinary(int start, int end) throws IOException {
        String moreBinary = "";
        int data;
        input.skip(start);

        while (start++ < end) {
            data = input.read();
            moreBinary += getStreamOffset() + " : " + data + " " + (char) data + " " + "\n";
        }
        moreBinary += "-------------\n";
        return moreBinary;
    }

    public void setInput(InputStream stream) throws IOException {
        if (stream instanceof BufferedInputStream) input = (BufferedInputStream) stream;
        else input = new BufferedInputStream(stream);
        inputLen = input.available();
    }
    protected int getStreamOffset() throws IOException {
        return inputLen - input.available() + 1;
    }
}
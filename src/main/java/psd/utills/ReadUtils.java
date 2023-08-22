package psd.utills;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ReadUtils {
    protected BufferedInputStream input;
    protected int inputLen;

    /**
     * stream(psd) 넣기
     * @param stream
     */
    public void setInput(InputStream stream) throws IOException {
        if (stream instanceof BufferedInputStream) input = (BufferedInputStream) stream;
        else input = new BufferedInputStream(stream);
        inputLen = input.available();
    }
    /**
     * 1Byte 읽기
     * @return
     */
    public int readByte() {
        int rByte = 0;
        try {
            rByte = input.read();
        } catch (IOException e) {

        }
        return rByte;
    }
    /**
     * XByte 읽기
     * @return
     */
    public int readBytes(byte[] bytes, int n) throws IOException {
        if (bytes == null)
            return 0;
        int r = 0;
        try {
            r = input.read(bytes, 0, n);
        } catch (IOException e) {
            throw new IOException(new Exception("file format error"));
        }
        if (r < n) {
            throw new IOException(new Exception("file format error"));
        }
        return r;
    }


    /**
     * 4Byte(int) 읽기
     * @return
     */
    public int readInt() {
        return (((((readByte() << 8) | readByte()) << 8) | readByte()) << 8) | readByte();
    }

    /**
     * 2Byte(short) 읽기
     * @return
     */
    public short readShort() {
        return (short) ((readByte() << 8) | readByte());
    }

    /**
     * 문자열 읽기
     * @return
     */
    public String readString(int len) {
        String rString = "";
        for (int i = 0; i < len; i++) {
            rString = rString + (char) readByte();
        }
        return rString;
    }
    /**
     * 배열 헤드 넘기기
     */
    public void jumpBytes(int n) {
        for (int i = 0; i < n; i++) {
            readByte();
        }
    }

    /**
     * 마지막으로 확인한 byte index 반환
     */
    public int getStreamOffset() throws IOException {
        return inputLen - input.available();
    }
}

package psd;

import psd.domain.PsdHeader;
import psd.domain.PsdLayer;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PsdReader {
    protected BufferedInputStream input;
    protected int inputLen;
    protected int bufferLen;
    protected PsdHeader psdHeader;
    protected int layerCount;
    protected PsdLayer[] psdLayers;
    protected int frameCount;
    protected int readCount;
    protected BufferedImage[] frames;

    public PsdReader() {
        bufferLen = 0;
        psdHeader = new PsdHeader();
        layerCount = 0;
        psdLayers = null;
        frameCount = 0;
        frames = null;
        readCount = 0;
    }

    /**
     * stream(psd) 열기
     * @param stream
     */
    public void open(InputStream stream) throws IOException {
        setInput(stream);
    }

    public void run() throws IOException {
        readHeader();
        readLayers();
    }

    public void readHeader() throws IOException {
        if (! readString(4).equals("8BPS")) {
            throw new RuntimeException("Signature not match");
        }

        psdHeader.setVersion(readShort());
        jumpBytes(6);
        psdHeader.setChannelCount(readShort());
        psdHeader.setHeight(readInt());
        psdHeader.setWidth(readInt());
        psdHeader.setChannelBitsDepth(readShort());
        psdHeader.setColorMode(getColorMode(readShort()));
        psdHeader.setColorModeLen(readInt());
        jumpBytes(psdHeader.getColorModeLen());
        psdHeader.setImageResourcesLen(readInt());
        jumpBytes(psdHeader.getImageResourcesLen());
    }

    public void readLayers() throws IOException {
        bufferLen = readInt();
        if(bufferLen < 0) return;
        final int layerInfoLen = readInt();
        layerCount = readShort();

        PsdLayer[] psdLayers = new PsdLayer[layerCount];
        for(int iLayerCount = 0; iLayerCount < layerCount; iLayerCount++) {
            PsdLayer layer = new PsdLayer();
            layer.setTop(readInt());
            layer.setLeft(readInt());
            layer.setBottom(readInt());
            layer.setRight(readInt());
            layer.setChannelCount(readShort());
            int[] channelId = new int[layer.getChannelCount()];
            int[] channelLine = new int[layer.getChannelCount()];
            for(int iLayerChannel = 0; iLayerChannel < layer.getChannelCount(); iLayerChannel++) {
                channelId[iLayerChannel] = readShort();
                channelLine[iLayerChannel] = readInt();
            }
            layer.setChannelId(channelId);
            layer.setChannelLine(channelLine);
            if (!readString(4).equals("8BIM")) {
                throw new RuntimeException("sign not match");
            }
            layer.setModeKey(readString(4));
            layer.setTransparency(readByte());
            layer.setClipping(readByte() > 0);
            int flag = readByte();
            if (flag != 0) {
                layer.setProtectTransparency((flag & 0x01) == 1);
                layer.setVision((flag & 0x02) >> 1 == 1);
            }
            jumpBytes(1);
            int dataFieldLen = readInt();
            jumpBytes(dataFieldLen);
        }
        this.psdLayers = psdLayers;
    }

    public void readLayersImage() {

    }

    /**
     * stream(psd) 넣기
     * @param stream
     */
    protected void setInput(InputStream stream) throws IOException {
        if (stream instanceof BufferedInputStream) input = (BufferedInputStream) stream;
        else input = new BufferedInputStream(stream);
        inputLen = input.available();
    }

    /**
     * 1Byte 읽기
     * @return
     */
    protected int readByte() {
        int rByte = 0;
        try {
            rByte = input.read();
            readCount++;
        } catch (IOException e) {

        }
        return rByte;
    }
    /**
     * XByte 읽기
     * @return
     */
    protected int[] readBytes(int len) {
        int []rBytes = new int[len];

        for (int i = 0; i < len; i++) {
            rBytes[i] = readByte();
        }
        return rBytes;
    }

    /**
     * 4Byte(int) 읽기
     * @return
     */
    protected int readInt() {
        return (((((readByte() << 8) | readByte()) << 8) | readByte()) << 8) | readByte();
    }

    /**
     * 2Byte(short) 읽기
     * @return
     */
    protected short readShort() {
        return (short) ((readByte() << 8) | readByte());
    }

    /**
     * 문자열 읽기
     * @return
     */
    protected String readString(int len) {
        String rString = "";
        for (int i = 0; i < len; i++) {
            rString = rString + (char) readByte();
        }
        return rString;
    }
    /**
     * 배열 헤드 넘기기
     */
    protected void jumpBytes(int n) {
        for (int i = 0; i < n; i++) {
            readByte();
        }
    }

    /**
     * 색상 모드 가져오기
     */
    protected String getColorMode(int n) {
        switch (n) {
            case 0:
                return "Bitmap";
            case 1:
                return "GrayScale";
            case 2:
                return "Indexed";
            case 3:
                return "RGB";
            case 4:
                return "CMYK";
            case 7:
                return "MultiChannel";
            case 8:
                return "Duotone";
            case 9:
                return "Lab";
            default:
                throw new RuntimeException("colorMode is not support");
        }
    }

    /**
     * 재설정
     */
    protected void init() throws IOException {
        input.close();
        inputLen = input.available();
        bufferLen = 0;
        psdHeader = null;
        layerCount = 0;
        psdLayers = null;
        frameCount = 0;
        frames = null;
        readCount = 0;
    }
    protected int getStreamOffset() throws IOException {
        return inputLen - input.available() + 1;
    }
}
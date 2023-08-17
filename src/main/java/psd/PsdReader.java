package psd;

import lombok.Data;
import psd.domain.PsdHeader;
import psd.domain.PsdLayer;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.Arrays;

@Data
public class PsdReader {
    protected BufferedInputStream input;
    protected int inputLen;
    protected int bufferLen;
    protected PsdHeader psdHeader;
    protected int layerCount;
    protected PsdLayer[] psdLayers;
    protected short[] lineLen;
    protected int iLine;
    protected String encoding;
    protected BufferedImage preview;
    protected int layerMaskInfoLen;

    public PsdReader() {
        bufferLen = 0;
        layerMaskInfoLen = 0;
        psdHeader = new PsdHeader();
        layerCount = 0;
        psdLayers = null;
    }

    /**
     * stream(psd) 열기
     * @param stream
     */
    public void open(InputStream stream) throws IOException {
        setInput(stream);
    }

    public void run() throws IOException, PsdException {
        try {
            System.out.println("file is " + inputLen + "byte!");
            readHeader();
            System.out.println("header end " + getStreamOffset());
            readLayers();
            System.out.println("layer info end " + getStreamOffset());
            if (layerCount == 0) {
                makeDummyLayer();
            }
            readLayersImage();
            System.out.println("layer image end " + getStreamOffset());
            readPreview();
            System.out.println("preview end " + getStreamOffset());
        }
        catch (Exception e) {
            System.out.println("err point : " + getStreamOffset());
            throw e;
            //throw new PsdException(e, getStreamOffset());
        }
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
        layerMaskInfoLen = getStreamOffset() + bufferLen;
        if(bufferLen < 0) return;
        final int layerInfoLen = readInt();
        layerCount = readShort();

        if(layerCount > 0) {
            psdLayers = new PsdLayer[layerCount];
        }
        for(int iLayerCount = 0; iLayerCount < layerCount; iLayerCount++) {
            PsdLayer layer = new PsdLayer();
            layer.setTop(readInt());
            layer.setLeft(readInt());
            layer.setBottom(readInt());
            layer.setRight(readInt());
            layer.setHeight(layer.getBottom() - layer.getTop());
            layer.setWidth(layer.getRight() - layer.getLeft());
            layer.setChannelCount(readShort());
            int[] channelId = new int[layer.getChannelCount()];
            for(int iLayerChannel = 0; iLayerChannel < layer.getChannelCount(); iLayerChannel++) {
                channelId[iLayerChannel] = readShort();
                int size = readInt();
            }
            layer.setChannelId(channelId);
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
            int dataFieldLen = readInt() + getStreamOffset();
            int layerMaskAdjustmentLen = readInt();
            jumpBytes(layerMaskAdjustmentLen);
            int layerBlendingRangesLen = readInt();
            jumpBytes(layerBlendingRangesLen);
            int nameLen = readByte();
            if((nameLen + 1) % 4 > 0) nameLen += 4 - (nameLen + 1) % 4;
            jumpBytes(nameLen);

            while (getStreamOffset() < dataFieldLen) {
                String sign = readString(4);
                if(! (sign.equals("8BIM") || sign.equals("8B64"))) {
                    throw new RuntimeException("sign not match : " + getStreamOffset());
                }
                layer = readMoreLayerInfo(layer, readString(4), readInt());
            }

            psdLayers[iLayerCount] = layer;
        }
    }

    protected PsdLayer readMoreLayerInfo(PsdLayer layer, String key, int len) {
        switch (key) {
            case "luni":
                layer.setName(readUtf16(len).substring(2));
                break;
            default:
                jumpBytes(len);
        }
        return layer;
    }
    protected void makeDummyLayer() throws IOException {
        // creat dummy layer for non-layered image
        encoding = readShort() == 1 ? "rle" : "raw";
        layerCount = 1;
        psdLayers = new PsdLayer[1];
        PsdLayer layer = new PsdLayer();
        psdLayers[0] = layer;
        layer.setLeft(0);
        layer.setRight(psdHeader.getHeight());
        layer.setTop(0);
        layer.setBottom(psdHeader.getWidth());
        int nc = Math.min(psdHeader.getChannelCount(), 4);
        if (encoding.equals("rle")) {
            // get list of rle encoded line lengths for all channels
            readLineLen(layer.getTop() * nc);
        }
        layer.setChannelCount(nc);
        layer.setChannelId(new int[nc]);
        int[] channelID = layer.getChannelId();
        for (int i = 0; i < nc; i++) {
            int id = i;
            if (i == 3)
                id = -1;
            channelID[i] = id;
        }
        layer.setChannelId(channelID);
    }
    public void readLayersImage() throws IOException, PsdException {
        for(int iLayerCount = 0; iLayerCount < layerCount; iLayerCount++) {
            PsdLayer layer = psdLayers[iLayerCount];
            byte[] r = null, g = null, b = null, a = null;
            for(int iChannelCount = 0; iChannelCount < layer.getChannelCount(); iChannelCount++) {
                int id = layer.getChannelId()[iChannelCount];
                switch (id) {
                    case 0 -> r = readChannelImage(layer.getWidth(), layer.getHeight());
                    case 1 -> g = readChannelImage(layer.getWidth(), layer.getHeight());
                    case 2 -> b = readChannelImage(layer.getWidth(), layer.getHeight());
                    case -1 -> a = readChannelImage(layer.getWidth(), layer.getHeight());
                    default -> readChannelImage(layer.getWidth(), layer.getHeight());
                }
            }
            int n = layer.getWidth() * layer.getHeight();
            if (r == null)
                r = generateByteArray(n, 0);
            if (g == null)
                g = generateByteArray(n, 0);
            if (b == null)
                b = generateByteArray(n, 0);
            if (a == null)
                a = generateByteArray(n, 255);

            BufferedImage image = makeRGBImage(layer.getWidth()
                    , layer.getHeight()
                    , r, g, b, a);
            psdLayers[iLayerCount].setFrame(image);
        }
        jumpBytes(layerMaskInfoLen - getStreamOffset());
        System.out.println("layer mask jump " + getStreamOffset());
    }

    protected void readPreview() throws IOException, PsdException {
        short encoding = readShort();
        if(encoding == 0) this.encoding = "raw";
        else if(encoding == 1) this.encoding = "rle";
        else if(encoding == 2) this.encoding = "zip";
        else if(encoding == 3) this.encoding = "pzip";

        System.out.println(this.encoding);
        byte[] r = null, g = null, b = null, a = null;

        short[][] channelL = new short[psdHeader.getChannelCount()][];
        for(int iChannelCount = 0; iChannelCount < psdHeader.getChannelCount(); iChannelCount++) {
            readLineLen(psdHeader.getHeight());
            channelL[iChannelCount] = lineLen;
        }
        for(int iChannelCount = 0; iChannelCount < psdHeader.getChannelCount(); iChannelCount++) {
            lineLen = channelL[iChannelCount];
            iLine = 0;

            switch (iChannelCount) {
                case 0 -> r = readLayerCompressedChannelImage(psdHeader.getWidth(), psdHeader.getHeight());
                case 1 -> g = readLayerCompressedChannelImage(psdHeader.getWidth(), psdHeader.getHeight());
                case 2 -> b = readLayerCompressedChannelImage(psdHeader.getWidth(), psdHeader.getHeight());
                case 3 -> a = readLayerCompressedChannelImage(psdHeader.getWidth(), psdHeader.getHeight());
                default -> readLayerCompressedChannelImage(psdHeader.getWidth(), psdHeader.getHeight());
            }
        }
        int n = psdHeader.getWidth() * psdHeader.getHeight();
        if (r == null)
            r = generateByteArray(n, 0);
        if (g == null)
            g = generateByteArray(n, 0);
        if (b == null)
            b = generateByteArray(n, 0);
        if (a == null)
            a = generateByteArray(n, 255);
        preview = makeRGBImage(psdHeader.getWidth(), psdHeader.getHeight(), r, g, b, a);
    }

    protected byte[] readChannelImage(int width, int height) throws IOException, PsdException {
        byte[] b = null;
        int size = width * height;
        short encoding = readShort();
        if(encoding == 0) this.encoding = "raw";
        else if(encoding == 1) this.encoding = "rle";
        else if(encoding == 2) this.encoding = "zip";
        else if(encoding == 3) this.encoding = "pzip";

        boolean isRLE = this.encoding.equals("rle");
        if (isRLE) {
            readLineLen(height);
        }
        if (isRLE) {
            b = readLayerCompressedChannelImage(width, height);
        } else if(this.encoding.equals("raw")) {
            b = new byte[size];
            readBytes(b, size);
        }
        return b;
    }

    protected byte[] readLayerCompressedChannelImage(int width, int height) throws IOException, PsdException {
        byte[] b = new byte[width * height];
        byte[] s = new byte[width * 2];
        int pos = 0;
        for (int i = 0; i < height; i++) {
            if (iLine >= lineLen.length) {
                throw new RuntimeException("format err");
            }
            int len = lineLen[iLine++];
            readBytes(s, len);
            decodeRLE(s, 0, len, b, pos);
            pos += width;
        }
        return b;
    }

    protected void decodeRLE(byte[] encoding, int encodingIndex, int srcLen, byte[] raw, int rawIndex) {
        try {
            int max = encodingIndex + srcLen;
            while (encodingIndex < max) {
                byte b = encoding[encodingIndex++];
                int n = b;
                if (n < 0) {
                    n = 1 - n;
                    b = encoding[encodingIndex++];
                    for (int i = 0; i < n; i++) {
                        raw[rawIndex++] = b;
                    }
                } else {
                    n = n + 1;
                    System.arraycopy(encoding, encodingIndex, raw, rawIndex, n);
                    rawIndex += n;
                    encodingIndex += n;
                }
            }
        } catch (Exception e) {
        }
    }

    protected void readLineLen(int lineCount) throws IOException {
        System.out.println("linelen start (" + lineCount + ") " + getStreamOffset());
        lineLen = new short[lineCount];
        for(int i = 0; i < lineCount; i++) {
            lineLen[i] = readShort();
        }
        System.out.println("linelen " + getStreamOffset() + " " + Arrays.toString(lineLen));
        System.out.println((lineLen[0] >> 8) & 0xFF);
        System.out.println(lineLen[0] &0xFF);
        iLine = 0;
    }

    protected BufferedImage makeRGBImage(int width, int height, byte[] r, byte[] g, byte[] b, byte[] a) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int[] data = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        int n = width * height;
        int j = 0;
        while (j < n) {
            try {
                int ac = a[j] & 0xff;
                int rc = r[j] & 0xff;
                int gc = g[j] & 0xff;
                int bc = b[j] & 0xff;
                data[j] = (((((ac << 8) | rc) << 8) | gc) << 8) | bc;
            } catch (Exception e) {
            }
            j++;
        }
        return image;
    }

    protected byte[] generateByteArray(int size, int value) {
        byte[] b = new byte[size];
        if (value != 0) {
            byte v = (byte) value;
            for (int i = 0; i < size; i++) {
                b[i] = v;
            }
        }
        return b;
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
        } catch (IOException e) {

        }
        return rByte;
    }
    /**
     * XByte 읽기
     * @return
     */
    protected int readBytes(byte[] bytes, int n) throws IOException, PsdException {
        if (bytes == null)
            return 0;
        int r = 0;
        try {
            r = input.read(bytes, 0, n);
        } catch (IOException e) {
            throw new PsdException(new Exception("file format error"), getStreamOffset());
        }
        if (r < n) {
            throw new PsdException(new Exception("file format error"), getStreamOffset());
        }
        return r;
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
     * utf 16 디코딩
     * @return
     */
    protected String readUtf16(int len) {
        ArrayList<Byte> rBytes = new ArrayList<>();
        try {
            boolean first = true;
            Byte[] inputB = new Byte[2];
            for(int i = 0; i < len; i++) {
                inputB[first ? 0 : 1] = (byte) input.read();

                if(! first) {  // && ! (inputB[0] == 0 && inputB[1] == 0)
                    rBytes.add(inputB[0]);
                    rBytes.add(inputB[1]);
                }

                first = ! first;
            }

        } catch (IOException e) {

        }
        byte[] byteArray = new byte[rBytes.size()];
        for(int i = 0; i < rBytes.size(); i++) {
            byteArray[i] = rBytes.get(i);
        }

        return new String(byteArray, 0, byteArray.length, StandardCharsets.UTF_16);
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
    }
    protected int getStreamOffset() throws IOException {
        return inputLen - input.available() + 1;
    }

    @Override
    public String toString() {
        String layer = "";
        for(int i = 0; i < layerCount; i++) {
            layer += psdLayers[i].toString() + "\n";
        }
        return psdHeader.toString() + "\n" + layer;
    }
}
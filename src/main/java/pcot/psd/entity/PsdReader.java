package pcot.psd.entity;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Stack;

public class PsdReader extends PsdEntity {
    protected BufferedInputStream input;
    protected int inputLen;
    protected int bufferLen;
    protected int layerCount;
    protected short[] lineLen;
    protected Stack<Integer> folderStack;
    protected int iLine;
    protected String encoding;
    protected int layerMaskInfoLen;

    public PsdReader() {
        bufferLen = 0;
        layerMaskInfoLen = 0;
        psdHeader = new PsdHeader();
        layerCount = 0;
        folderStack = new Stack<>();
        psdLayers = null;
    }

    /**
     * stream(psd) 열기
     * @param stream
     */
    public void open(InputStream stream) throws IOException {
        setInput(stream);
    }

    public void run() throws IOException {
        try {
            readHeader();
            readLayers();
            if (layerCount == 0) {
                makeDummyLayer();
            }
            readLayersImage();
            readPreview();
        }
        catch (Exception e) {
            throw e;
            //throw new PsdException(e, getStreamOffset());
        }
    }

    protected void readHeader() throws IOException {
        if (! readString(4).equals("8BPS")) {
            throw new RuntimeException("Signature not match");
        }

        psdHeader.version = readShort();
        jumpBytes(6);
        psdHeader.channelCount = readShort();
        psdHeader.height = readInt();
        psdHeader.width = readInt();
        psdHeader.channelBitsDepth = readShort();
        psdHeader.colorMode = getColorMode(readShort());
        psdHeader.colorModeLen = readInt();
        jumpBytes(psdHeader.colorModeLen);
        psdHeader.imageResourcesLen = readInt();
        jumpBytes(psdHeader.imageResourcesLen);
    }

    protected void readLayers() throws IOException {
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
            layer.top = readInt();
            layer.left = readInt();
            layer.bottom = readInt();
            layer.right = readInt();
            layer.height = layer.bottom - layer.top;
            layer.width = layer.right - layer.left;
            layer.channelCount = readShort();
            int[] channelId = new int[layer.channelCount];
            for(int iLayerChannel = 0; iLayerChannel < layer.channelCount; iLayerChannel++) {
                channelId[iLayerChannel] = readShort();
                int size = readInt();
            }
            layer.channelId = channelId;
            if (!readString(4).equals("8BIM")) {
                throw new RuntimeException("sign not match");
            }
            layer.modeKey = readString(4);
            layer.transparency = readByte();
            layer.clipping = readByte() > 0 ? 'Y' : 'N';

            int flag = readByte();
            layer.protectTransparency = (flag & 0x01) == 1 ? 'Y' : 'N';
            layer.vision = (flag & 0x02) >> 1 == 1 ? 'N' : 'Y';

            jumpBytes(1);
            int dataFieldLen = readInt() + getStreamOffset();
            int layerMaskAdjustmentLen = readInt();
            jumpBytes(layerMaskAdjustmentLen);
            int layerBlendingRangesLen = readInt();
            jumpBytes(layerBlendingRangesLen);

            int nameLen = readByte();
            layer.name = readString(nameLen);

            if(layer.name.equals("</Layer set>")) folderStack.push(iLayerCount);

            if((nameLen + 1) % 4 > 0) jumpBytes(4 - (nameLen + 1) % 4);

            while (getStreamOffset() < dataFieldLen) {
                String sign = readString(4);
                if(! (sign.equals("8BIM") || sign.equals("8B64"))) {
                    throw new RuntimeException("sign not match : " + getStreamOffset());
                }
                layer = readMoreLayerInfo(layer, readString(4), readInt(), iLayerCount);
            }

            psdLayers[iLayerCount] = layer;
        }
    }

    protected PsdLayer readMoreLayerInfo(PsdLayer layer, String key, int len, int iLayerCount) {
        switch (key) {
            case "luni":
                layer.name = readUtf16(len).substring(2);
                break;

            case "lsct":
                int type = readInt();
                if (type == 1 || type == 2) {
                    int open = folderStack.pop();
                    ArrayList<PsdLayer> lay = new ArrayList<>();
                    psdLayers[open].folder = 1;

                    for (int i = 0; i < iLayerCount - open - 1; i++) {
                        int target = open + 1 + i;

                        if (psdLayers[target].folder % 10 == 0) {
                            lay.add(psdLayers[target]);
                            psdLayers[target].folder = 1;
                        }
                        else if(psdLayers[target].folder % 10 == 1) {
                            // 무시하기 < 이미 폴더에 포함됨
                        }
                    }
                    layer.child = lay.toArray(new PsdLayer[lay.size()]);
                }
                if (len >= 12) {
                    jumpBytes(8);
                }
                if (len >= 16) {
                    jumpBytes(4);
                }
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
        layer.left = 0;
        layer.right = psdHeader.height;
        layer.top = 0;
        layer.bottom = psdHeader.width;
        int nc = Math.min(psdHeader.channelCount, 4);
        if (encoding.equals("rle")) {
            // get list of rle encoded line lengths for all channels
            readLineLen(layer.height * nc);
        }
        layer.channelCount = nc;
        layer.channelId = new int[nc];
        int[] channelID = layer.channelId;
        for (int i = 0; i < nc; i++) {
            int id = i;
            if (i == 3)
                id = -1;
            channelID[i] = id;
        }
        layer.channelId = channelID;
    }
    protected void readLayersImage() throws IOException {
        for(int iLayerCount = 0; iLayerCount < layerCount; iLayerCount++) {
            PsdLayer layer = psdLayers[iLayerCount];
            if(layer.width <= 0 || layer.height <= 0) {
                continue;
            }

            byte[] r = null, g = null, b = null, a = null;
            for(int iChannelCount = 0; iChannelCount < layer.channelCount; iChannelCount++) {
                int id = layer.channelId[iChannelCount];
                switch (id) {
                    case 0 -> r = readChannelImage(layer.width, layer.height);
                    case 1 -> g = readChannelImage(layer.width, layer.height);
                    case 2 -> b = readChannelImage(layer.width, layer.height);
                    case -1 -> a = readChannelImage(layer.width, layer.height);
                    default -> readChannelImage(layer.width, layer.height);
                }
            }
            int n = layer.width * layer.height;
            if (r == null)
                r = generateByteArray(n, 0);
            if (g == null)
                g = generateByteArray(n, 0);
            if (b == null)
                b = generateByteArray(n, 0);
            if (a == null)
                a = generateByteArray(n, 255);

            psdLayers[iLayerCount].frame = makeRGBImage(layer.width
                    , layer.height
                    , r, g, b, a);
        }
        jumpBytes(layerMaskInfoLen - getStreamOffset());
    }

    protected void readPreview() throws IOException {
        short encoding = readShort();
        if(encoding == 0) this.encoding = "raw";
        else if(encoding == 1) this.encoding = "rle";
        else if(encoding == 2) this.encoding = "zip";
        else if(encoding == 3) this.encoding = "pzip";

        byte[] r = null, g = null, b = null, a = null;

        short[][] channelL = new short[psdHeader.channelCount][];
        for(int iChannelCount = 0; iChannelCount < psdHeader.channelCount; iChannelCount++) {
            readLineLen(psdHeader.height);
            channelL[iChannelCount] = lineLen;
        }
        for(int iChannelCount = 0; iChannelCount < psdHeader.channelCount; iChannelCount++) {
            lineLen = channelL[iChannelCount];
            iLine = 0;

            switch (iChannelCount) {
                case 0 -> r = readLayerCompressedChannelImage(psdHeader.width, psdHeader.height);
                case 1 -> g = readLayerCompressedChannelImage(psdHeader.width, psdHeader.height);
                case 2 -> b = readLayerCompressedChannelImage(psdHeader.width, psdHeader.height);
                case 3 -> a = readLayerCompressedChannelImage(psdHeader.width, psdHeader.height);
                default -> readLayerCompressedChannelImage(psdHeader.width, psdHeader.height);
            }
        }
        int n = psdHeader.width * psdHeader.height;
        if (r == null)
            r = generateByteArray(n, 0);
        if (g == null)
            g = generateByteArray(n, 0);
        if (b == null)
            b = generateByteArray(n, 0);
        if (a == null)
            a = generateByteArray(n, 255);
        preview = makeRGBImage(psdHeader.width, psdHeader.height, r, g, b, a);
    }

    protected byte[] readChannelImage(int width, int height) throws IOException {
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

    protected byte[] readLayerCompressedChannelImage(int width, int height) throws IOException {
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
        lineLen = new short[lineCount];
        for(int i = 0; i < lineCount; i++) {
            lineLen[i] = readShort();
        }
        iLine = 0;
    }

    protected PcotBufferedImage makeRGBImage(int width, int height, byte[] r, byte[] g, byte[] b, byte[] a) {
        PcotBufferedImage image = new PcotBufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
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
    protected int readBytes(byte[] bytes, int n) throws IOException {
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
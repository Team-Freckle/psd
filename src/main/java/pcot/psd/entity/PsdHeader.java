package pcot.psd.entity;

public class PsdHeader {
    protected int version;
    protected int channelCount;
    protected int height;
    protected int width;
    protected int channelBitsDepth;
    protected String colorMode;
    protected int colorModeLen;
    protected int imageResourcesLen;

    public int getVersion() {
        return version;
    }

    public int getChannelCount() {
        return channelCount;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getChannelBitsDepth() {
        return channelBitsDepth;
    }

    public String getColorMode() {
        return colorMode;
    }

    public int getColorModeLen() {
        return colorModeLen;
    }

    public int getImageResourcesLen() {
        return imageResourcesLen;
    }

    @Override
    public boolean equals(Object o) {
        if (PsdHeader.class != o.getClass()) return false;

        PsdHeader p = (PsdHeader) o;
        return this.version == p.version
                && this.channelCount == p.channelCount
                && this.height == p.height
                && this.width == p.width
                && this.channelBitsDepth == p.channelBitsDepth
                && this.colorMode.equals(p.colorMode)
                && this.colorModeLen == p.colorModeLen
                && this.imageResourcesLen == p.imageResourcesLen;
    }
}

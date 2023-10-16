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

    public boolean isDifferent(PsdHeader o) {
        return this.version == o.version
                && this.channelCount == o.channelCount
                && this.height == o.height
                && this.width == o.width
                && this.channelBitsDepth == o.channelBitsDepth
                && this.colorMode.equals(o.colorMode);
    }
}

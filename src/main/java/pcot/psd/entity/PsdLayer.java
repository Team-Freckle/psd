package pcot.psd.entity;

import java.util.Arrays;

public class PsdLayer {
    protected int top;
    protected int bottom;
    protected int left;
    protected int right;
    protected int width;
    protected int height;
    protected int channelCount;
    protected int[] channelId;
    protected String modeKey;
    protected String name;
    protected int transparency;
    protected char clipping;
    protected char protectTransparency;
    protected char vision;
    protected PcotBufferedImage frame;
    protected char folder;

    public int getTop() {
        return top;
    }

    public int getBottom() {
        return bottom;
    }

    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getChannelCount() {
        return channelCount;
    }

    public int[] getChannelId() {
        return channelId;
    }

    public String getModeKey() {
        return modeKey;
    }

    public String getName() {
        return name;
    }

    public int getTransparency() {
        return transparency;
    }

    public char getClipping() {
        return clipping;
    }

    public char getProtectTransparency() {
        return protectTransparency;
    }

    public char getVision() {
        return vision;
    }

    public PcotBufferedImage getFrame() {
        return frame;
    }

    public char getFolder() {
        return folder;
    }

    public boolean isDifferent(PsdLayer o) {
        return this.top == o.top
                && this.bottom == o.bottom
                && this.left == o.left
                && this.right == o.right
                && this.width == o.width
                && this.height == o.height
                && this.channelCount == o.channelCount
                && Arrays.equals(this.channelId, o.channelId)
                && this.modeKey.equals(o.modeKey)
                && this.name.equals(o.name)
                && this.transparency == o.transparency
                && this.clipping == o.clipping
                && this.protectTransparency == o.protectTransparency
                && this.vision == o.vision
                && this.folder == o.folder
                && (this.frame == null || o.frame == null || (this.frame.equals(o.frame)));
    }
}

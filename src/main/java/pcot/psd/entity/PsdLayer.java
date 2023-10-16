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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PsdLayer p))
            return false;

        return this.top == p.top
                && this.bottom == p.bottom
                && this.left == p.left
                && this.right == p.right
                && this.width == p.width
                && this.height == p.height
                && this.channelCount == p.channelCount
                && Arrays.equals(this.channelId, p.channelId)
                && this.modeKey.equals(p.modeKey)
                && this.name.equals(p.name)
                && this.transparency == p.transparency
                && this.clipping == p.clipping
                && this.protectTransparency == p.protectTransparency
                && this.vision == p.vision
                && this.folder == p.folder
                && (this.frame == null || p.frame == null || (this.frame.equals(p.frame)));
    }
}

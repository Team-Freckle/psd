package psd.domain;

import lombok.Data;

@Data
public class PsdLayer {
    private int top;
    private int bottom;
    private int left;
    private int right;
    private int channelCount;
    private int[] channelId;
    private int[] channelLine;
    private int channel;
    private String modeKey;
    private String name;
    private int transparency;
    private boolean clipping;
    private boolean protectTransparency;
    private boolean vision;
    public boolean getClipping() {
        return clipping;
    }
    public boolean getProtectTransparency() {
        return protectTransparency;
    }
    public boolean getVision() {
        return vision;
    }
}

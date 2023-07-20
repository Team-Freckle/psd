package psd.domain;

import lombok.Data;

import java.awt.image.BufferedImage;

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
    private boolean getClipping() {
        return clipping;
    }
    private boolean getProtectTransparency() {
        return protectTransparency;
    }
    private boolean getVision() {
        return vision;
    }
    private BufferedImage frame;
}

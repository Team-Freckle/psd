package psd.domain;

import lombok.Data;

@Data
public class PsdHeader {
    private String name;
    private int version;
    private int channelCount;
    private int height;
    private int width;
    private int channelBitsDepth;
    private String colorMode;
    private int colorModeLen;
    private int imageResourcesLen;
}

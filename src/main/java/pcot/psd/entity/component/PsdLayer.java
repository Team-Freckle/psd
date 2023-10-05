package pcot.psd.entity.component;

import lombok.Data;
import pcot.psd.entity.buffered.PcotBufferedImage;

import java.awt.image.BufferedImage;

@Data
public class PsdLayer {
    private Long idx;
    private Long psdIdx;
    private int top;
    private int bottom;
    private int left;
    private int right;
    private int width;
    private int height;
    private int channelCount;
    private int[] channelId;
    private int[] channelLine;
    private int channel;
    private String modeKey;
    private String name;
    private int transparency;
    private char clipping;
    private char protectTransparency;
    private char vision;
    private PcotBufferedImage frame;
    private String comment;
    private char folder;
}

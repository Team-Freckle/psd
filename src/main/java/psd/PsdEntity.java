package psd;

import lombok.Data;
import psd.component.PsdHeader;
import psd.component.PsdLayer;

import java.awt.image.BufferedImage;

@Data
public class PsdEntity {
    protected Psd psd;
    protected PsdHeader psdHeader;
    protected PsdLayer[] psdLayers;
    protected BufferedImage preview;
}

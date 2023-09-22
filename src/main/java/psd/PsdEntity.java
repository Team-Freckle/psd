package psd;

import lombok.Data;
import psd.component.PsdHeader;
import psd.component.PsdLayer;

import java.awt.image.BufferedImage;
import java.util.List;

@Data
public class PsdEntity {
    protected Psd psd;
    protected PsdHeader psdHeader;
    protected PsdLayer[] psdLayers;
    protected BufferedImage preview;

    public void setPsdLayers(List<PsdLayer> psdLayers) {
        PsdLayer[] layers = new PsdLayer[psdLayers.size()];
        this.psdLayers = psdLayers.toArray(layers);
    }
    public void setPsdLayers(PsdLayer[] psdLayers) {
        this.psdLayers = psdLayers;
    }
}

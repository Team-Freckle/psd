package pcot.psd.entity;

import lombok.Data;
import pcot.psd.entity.buffered.PcotBufferedImage;
import pcot.psd.entity.component.PsdHeader;
import pcot.psd.entity.component.PsdLayer;
import java.util.List;

@Data
public class PsdEntity {
    protected Psd psd;
    protected PsdHeader psdHeader;
    protected PsdLayer[] psdLayers;
    protected PcotBufferedImage preview;

    public void setPsdLayers(List<PsdLayer> psdLayers) {
        PsdLayer[] layers = new PsdLayer[psdLayers.size()];
        this.psdLayers = psdLayers.toArray(layers);
    }
    public void setPsdLayers(PsdLayer[] psdLayers) {
        this.psdLayers = psdLayers;
    }
}

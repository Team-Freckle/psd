package pcot.psd.entity;

import java.util.Arrays;

public class PsdEntity {
    protected Psd psd;
    protected PsdHeader psdHeader;
    protected PsdLayer[] psdLayers;
    protected PcotBufferedImage preview;

    public Psd getPsd() {
        return psd;
    }

    public PsdHeader getPsdHeader() {
        return psdHeader;
    }

    public PsdLayer[] getPsdLayers() {
        return psdLayers;
    }

    public PcotBufferedImage getPreview() {
        return preview;
    }

    public boolean isDifferent(PsdEntity o) {
        return this.psd.equals(o.getPsd())
                && this.psdHeader.equals(o.psdHeader)
                && Arrays.equals(this.psdLayers, o.psdLayers)
                && (this.preview == null || o.preview == null || (this.preview.equals(o.preview)));
    }
}

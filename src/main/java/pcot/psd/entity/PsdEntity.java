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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PsdEntity p))
            return false;

        return this.psd.equals(p.getPsd())
                && this.psdHeader.equals(p.psdHeader)
                && Arrays.equals(this.psdLayers, p.psdLayers)
                && (this.preview == null || p.preview == null || (this.preview.equals(p.preview)));
    }
}

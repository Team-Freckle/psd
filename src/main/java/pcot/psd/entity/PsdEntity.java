package pcot.psd.entity;

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
}

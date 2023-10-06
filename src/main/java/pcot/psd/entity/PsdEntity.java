package pcot.psd.entity;

public class PsdEntity {
    protected Psd psd;
    protected PsdHeader psdHeader;
    protected PsdLayer[] psdLayers;
    protected PcotBufferedImage preview;

    public void setPsd(Psd psd) {
        this.psd = psd;
    }

    public void setPsdHeader(PsdHeader psdHeader) {
        this.psdHeader = psdHeader;
    }

    public void setPsdLayers(PsdLayer[] psdLayers) {
        this.psdLayers = psdLayers;
    }

    public void setPreview(PcotBufferedImage preview) {
        this.preview = preview;
    }
}

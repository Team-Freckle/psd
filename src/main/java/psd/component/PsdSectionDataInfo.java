package psd.component;

import lombok.Data;

@Data
public class PsdSectionDataInfo {
    private int P_colorMod;
    private int L_colorMod;
    private int P_imageResource;
    private int L_imageResource;
    private int P_layerMaskInfo;
    private int L_layerMaskInfo;
    private int L_layerInfo;
    private int[] P_layerRecord;
    private int[] L_layerRecord;
    private int P_channelImageData;
    private int P_preview;
}
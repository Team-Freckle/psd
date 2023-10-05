package pcot.psd;

import pcot.psd.entity.Psd;

import java.io.IOException;

public interface PSDPcot {
    public Psd parsingPSD(String path) throws IOException;
}

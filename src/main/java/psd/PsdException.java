package psd;

import java.io.IOException;

public class PsdException extends Throwable {
    public PsdException(Exception e, int point) {
        super(e.getMessage() + " " + point, e.getCause());
        super.setStackTrace(e.getStackTrace());
    }
}

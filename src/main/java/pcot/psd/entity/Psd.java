package pcot.psd.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Psd {
    private Long idx;
    private Long nodeIdx;
    private String name;
    private int size;
    private String uploadTime;
    private String comment;
}

package team.domain;

import team.infra.AbstractEvent;
import lombok.Data;
import java.util.*;

@Data
public class AsCanceled extends AbstractEvent {

    private Long id;
    private String status;
    private Long requestId;
    private Date date;
    private String customerName;
    private String phoneNumber;
    private Integer price;
    private String symtom;
    private String engineerName;
    private String productId;
}

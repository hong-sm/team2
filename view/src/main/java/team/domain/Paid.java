package team.domain;

import team.infra.AbstractEvent;
import lombok.Data;
import java.util.*;

@Data
public class Paid extends AbstractEvent {

    private Long id;
    private Long requestId;
    private Date payDate;
    private Integer price;
    private String status;
}

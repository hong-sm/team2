package team.domain;

import team.infra.AbstractEvent;
import lombok.Data;
import java.util.*;

@Data
public class ServiceRequested extends AbstractEvent {

    private Long id;
    private String symtom;
    private String productId;
    private String customerName;
    private String phoneNumber;
    private Date requestDate;
    private Date date;
}

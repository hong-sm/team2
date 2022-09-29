package team.domain;

import team.domain.*;
import team.infra.AbstractEvent;
import lombok.*;
import java.util.*;
@Data
@ToString
public class ServiceCancelled extends AbstractEvent {

    private Long id;
    private String symtom;
    private String productId;
    private String customerName;
    private String phoneNumber;
    private Date date;
}



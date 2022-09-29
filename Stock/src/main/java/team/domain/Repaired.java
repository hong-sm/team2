package team.domain;

import team.domain.*;
import team.infra.AbstractEvent;
import lombok.*;
import java.util.*;
@Data
@ToString
public class Repaired extends AbstractEvent {

    private Long id;
    private String status;
    private Long requestId;
    private Date requestDate;
    private String customerName;
    private String phoneNumber;
    private Integer price;
    private String symtom;
    private Date date;
    private String engineerName;
}



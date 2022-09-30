package team.domain;

import team.domain.*;
import team.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class Paid extends AbstractEvent {

    private Long id;
    private Long requestId;
    private Date payDate;
    private Integer price;
    private String status;

    public Paid(Pay aggregate){
        super(aggregate);
    }
    public Paid(){
        super();
    }
}

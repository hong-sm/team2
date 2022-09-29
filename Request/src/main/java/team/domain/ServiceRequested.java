package team.domain;

import team.domain.*;
import team.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class ServiceRequested extends AbstractEvent {

    private Long id;
    private String symtom;
    private String productId;
    private String customerName;
    private String phoneNumber;
    private Date requestDate;
    private Date date;

    public ServiceRequested(Request aggregate){
        super(aggregate);
    }
    public ServiceRequested(){
        super();
    }
}

package team.domain;

import team.domain.*;
import team.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class ServiceCancelled extends AbstractEvent {

    private Long id;
    private String symtom;
    private String productId;
    private String customerName;
    private String phoneNumber;
    private Date date;

    public ServiceCancelled(Request aggregate){
        super(aggregate);
    }
    public ServiceCancelled(){
        super();
    }
}

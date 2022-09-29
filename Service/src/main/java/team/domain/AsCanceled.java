package team.domain;

import team.domain.*;
import team.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
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

    public AsCanceled(Service aggregate){
        super(aggregate);
    }
    public AsCanceled(){
        super();
    }
}

package team.domain;

import team.domain.ServiceRequested;
import team.domain.ServiceCancelled;
import team.RequestApplication;
import javax.persistence.*;
import java.util.List;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name="Request_table")
@Data

public class Request  {

    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    
    
    
    
    
    private Long id;
    
    
    
    
    
    private String symtom;
    
    
    
    
    
    private String productId;
    
    
    
    
    
    private String customerName;
    
    
    
    
    
    private String phoneNumber;
    
    
    
    
    
    private Date date;
    
    
    
    
    
    private Boolean isCancel;

    @PostPersist
    public void onPostPersist(){


        ServiceRequested serviceRequested = new ServiceRequested(this);
        serviceRequested.publishAfterCommit();



        ServiceCancelled serviceCancelled = new ServiceCancelled(this);
        serviceCancelled.publishAfterCommit();

    }

    public static RequestRepository repository(){
        RequestRepository requestRepository = RequestApplication.applicationContext.getBean(RequestRepository.class);
        return requestRepository;
    }






}

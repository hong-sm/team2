package team.domain;

import team.domain.Paid;
import team.PaymentApplication;
import javax.persistence.*;
import java.util.List;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name="Pay_table")
@Data

public class Pay  {

    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    
    
    
    
    
    private Long id;
    
    
    
    
    
    private Long requestId;
    
    
    
    
    
    private Date payDate;
    
    
    
    
    
    private Integer price;

    @PostPersist
    public void onPostPersist(){


        Paid paid = new Paid(this);
        paid.publishAfterCommit();

    }

    public static PayRepository repository(){
        PayRepository payRepository = PaymentApplication.applicationContext.getBean(PayRepository.class);
        return payRepository;
    }






}

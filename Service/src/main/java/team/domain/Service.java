package team.domain;

import team.domain.AsCanceled;
import team.ServiceApplication;
import javax.persistence.*;
import java.util.List;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name="Service_table")
@Data

public class Service  {

    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    
    
    
    
    
    private Long id;
    
    
    
    
    
    private String status;
    
    
    
    
    
    private Long requestId;
    
    
    
    
    
    private Date date;
    
    
    
    
    
    private String customerName;
    
    
    
    
    
    private String phoneNumber;
    
    
    
    
    
    private Integer price;
    
    
    
    
    
    private String symtom;
    
    
    
    
    
    private String engineerName;

    @PostPersist
    public void onPostPersist(){


        AsCanceled asCanceled = new AsCanceled(this);
        asCanceled.publishAfterCommit();

        // Get request from Stock
        //team.external.Stock stock =
        //    Application.applicationContext.getBean(team.external.StockService.class)
        //    .getStock(/** mapping value needed */);

    }

    public static ServiceRepository repository(){
        ServiceRepository serviceRepository = ServiceApplication.applicationContext.getBean(ServiceRepository.class);
        return serviceRepository;
    }



    public void productRepair(){
        Repaired repaired = new Repaired(this);
        repaired.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        team.external.Pay pay = new team.external.Pay();
        // mappings goes here
        ServiceApplication.applicationContext.getBean(team.external.PayService.class)
            .pay(pay);

    }
    public void accept(){
        AsAccepted asAccepted = new AsAccepted(this);
        asAccepted.publishAfterCommit();

    }

    public static void loadToServiceList(ServiceRequested serviceRequested){

        /** Example 1:  new item 
        Service service = new Service();
        repository().save(service);

        */

        /** Example 2:  finding and process
        
        repository().findById(serviceRequested.get???()).ifPresent(service->{
            
            service // do something
            repository().save(service);


         });
        */

        
    }
    public static void cancelAs(ServiceCancelled serviceCancelled){

        /** Example 1:  new item 
        Service service = new Service();
        repository().save(service);

        AsCanceled asCanceled = new AsCanceled(service);
        asCanceled.publishAfterCommit();
        */

        /** Example 2:  finding and process
        
        repository().findById(serviceCancelled.get???()).ifPresent(service->{
            
            service // do something
            repository().save(service);

            AsCanceled asCanceled = new AsCanceled(service);
            asCanceled.publishAfterCommit();

         });
        */

        
    }


}

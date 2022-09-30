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
    
    
    
    
    
    private String productId;

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
        // 지불 서비스 호출
        pay.setStatus("PAID");
        pay.setRequestId(repaired.getRequestId());
        ServiceApplication.applicationContext.getBean(team.external.PayService.class)
            .pay(pay);
        
        //지불 상태로 변경
        repository().findById(repaired.getId()).ifPresent(service->{
            
            service.setStatus("PAID"); 
            service.setDate(new Date());
            repository().save(service);
        });

    }
    public void accept(){
        AsAccepted asAccepted = new AsAccepted(this);
        asAccepted.publishAfterCommit();

    }

    public static void loadToServiceList(ServiceRequested serviceRequested){

        
        //AS요청으로 부터 받은 정보 세팅
        Service service = new Service();
        service.setCustomerName(serviceRequested.getCustomerName());
        service.setDate(serviceRequested.getDate());
        service.setPhoneNumber(serviceRequested.getPhoneNumber());
        service.setRequestId(serviceRequested.getId());        
        service.setProductId(serviceRequested.getProductId());
        service.setDate(new Date());
        service.setStatus("REQUESTED");        
        repository().save(service);        

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

        // 취소 상태로 변경
        repository().findByRequestId(serviceCancelled.getId()).ifPresent(service->{
            
            service.setStatus("CANCELED"); // do something
            service.setDate(new Date());
            repository().save(service);

            AsCanceled asCanceled = new AsCanceled(service);
            asCanceled.publishAfterCommit();

         });
        

        
    }
    


}

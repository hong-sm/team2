package team.domain;

import team.StockApplication;
import javax.persistence.*;
import java.util.List;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name="Stock_table")
@Data

public class Stock  {

    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    
    
    
    
    
    private Long id;
    
    
    
    
    
    private String relatedProductId;
    
    
    
    
    
    private Long stock;
    
    
    
    
    
    private Integer unitPrice;

    @PostPersist
    public void onPostPersist(){
    }

    public static StockRepository repository(){
        StockRepository stockRepository = StockApplication.applicationContext.getBean(StockRepository.class);
        return stockRepository;
    }




    public static void partRequest(Repaired repaired){

        /** Example 1:  new item 
        Stock stock = new Stock();
        repository().save(stock);

        */        
        
        repository().findByRelatedProductId(repaired.getProductId()).ifPresent(stock->{
            //재고를 하나 줄임
            stock.setStock(stock.getStock()-1);
            repository().save(stock);


         });
        

        
    }


}

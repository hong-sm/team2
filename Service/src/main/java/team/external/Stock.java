package team.external;

import lombok.Data;
import java.util.Date;
@Data
public class Stock {

    private Long id;
    private String relatedProductId;
    private Long stock;
    private Integer unitPrice;
}



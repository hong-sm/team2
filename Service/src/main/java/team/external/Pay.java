package team.external;

import lombok.Data;
import java.util.Date;
@Data
public class Pay {

    private Long id;
    private Long requestId;
    private Date payDate;
    private Integer price;
}



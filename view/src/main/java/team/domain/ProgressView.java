package team.domain;

import javax.persistence.*;
import java.util.List;
import java.util.Date;
import lombok.Data;

@Entity
@Table(name="ProgressView_table")
@Data
public class ProgressView {

        @Id
        //@GeneratedValue(strategy=GenerationType.AUTO)
        private Long id;
        private String symtom;
        private String productId;
        private String customerName;
        private String phoneNumber;
        private Date requestDate;
        private String status;
        private Date serviceDate;
        private String payStatus;
        private Date payDate;


}
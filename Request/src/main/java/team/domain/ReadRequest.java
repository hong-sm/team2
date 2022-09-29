package team.domain;

import javax.persistence.*;
import java.util.List;
import java.util.Date;
import lombok.Data;

@Entity
@Table(name="ReadRequest_table")
@Data
public class ReadRequest {

        @Id
        //@GeneratedValue(strategy=GenerationType.AUTO)
        private Long id;


}
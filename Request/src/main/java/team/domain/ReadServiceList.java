package team.domain;

import javax.persistence.*;
import java.util.List;
import java.util.Date;
import lombok.Data;

@Entity
@Table(name="ReadServiceList_table")
@Data
public class ReadServiceList {

        @Id
        //@GeneratedValue(strategy=GenerationType.AUTO)
        private Long id;


}
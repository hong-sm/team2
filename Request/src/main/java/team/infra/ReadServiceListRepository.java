package team.infra;

import team.domain.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;

@RepositoryRestResource(collectionResourceRel="readServiceLists", path="readServiceLists")
public interface ReadServiceListRepository extends PagingAndSortingRepository<ReadServiceList, Long> {

    

    
}

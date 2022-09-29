package team.infra;

import team.domain.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;

@RepositoryRestResource(collectionResourceRel="readRequests", path="readRequests")
public interface ReadRequestRepository extends PagingAndSortingRepository<ReadRequest, Long> {

    

    
}

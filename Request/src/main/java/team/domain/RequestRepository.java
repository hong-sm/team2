package team.domain;

import team.domain.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="requests", path="requests")
public interface RequestRepository extends PagingAndSortingRepository<Request, Long>{

}

package team.infra;

import team.domain.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;

@RepositoryRestResource(collectionResourceRel="progressViews", path="progressViews")
public interface ProgressViewRepository extends PagingAndSortingRepository<ProgressView, Long> {

    List<ProgressView> findByRequestId(Long requestId);


    
}

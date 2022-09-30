package team.domain;

import team.domain.*;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="services", path="services")
public interface ServiceRepository extends PagingAndSortingRepository<team.domain.Service, Long>{
    Optional<team.domain.Service> findByRequestId(long requestId);
}

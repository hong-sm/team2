package team.domain;

import team.domain.*;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="stocks", path="stocks")
public interface StockRepository extends PagingAndSortingRepository<Stock, Long>{
    Optional<Stock> findByRelatedProductId(String relatedProductId);
}

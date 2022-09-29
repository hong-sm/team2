package team.infra;
import team.domain.*;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;
import org.springframework.hateoas.EntityModel;

@Component
public class StockHateoasProcessor implements RepresentationModelProcessor<EntityModel<Stock>>  {

    @Override
    public EntityModel<Stock> process(EntityModel<Stock> model) {

        
        return model;
    }
    
}

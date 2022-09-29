package team.infra;
import team.domain.*;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;
import org.springframework.hateoas.EntityModel;

@Component
public class ServiceHateoasProcessor implements RepresentationModelProcessor<EntityModel<Service>>  {

    @Override
    public EntityModel<Service> process(EntityModel<Service> model) {
        model.add(Link.of(model.getRequiredLink("self").getHref() + "/product-repair").withRel("product-repair"));
        model.add(Link.of(model.getRequiredLink("self").getHref() + "/accept").withRel("accept"));

        
        return model;
    }
    
}

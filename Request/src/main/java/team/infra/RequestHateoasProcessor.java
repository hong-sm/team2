package team.infra;
import team.domain.*;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;
import org.springframework.hateoas.EntityModel;

@Component
public class RequestHateoasProcessor implements RepresentationModelProcessor<EntityModel<Request>>  {

    @Override
    public EntityModel<Request> process(EntityModel<Request> model) {
        model.add(Link.of(model.getRequiredLink("self").getHref() + "/servicecancel").withRel("servicecancel"));

        
        return model;
    }
    
}

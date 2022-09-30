package team.infra;
import team.domain.*;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

@RestController
// @RequestMapping(value="/requests")
@Transactional
public class RequestController {
    @Autowired
    RequestRepository requestRepository;




    @RequestMapping(value = "requests/{id}/servicecancel",
        method = RequestMethod.PUT,
        produces = "application/json;charset=UTF-8")
    public Request serviceCancel(@PathVariable(value = "id") Long id, HttpServletRequest request, HttpServletResponse response) throws Exception {
            System.out.println("##### /request/serviceCancel  called #####");
            Optional<Request> optionalRequest = requestRepository.findById(id);
            
            optionalRequest.orElseThrow(()-> new Exception("No Entity Found"));
            Request request = optionalRequest.get();
            request.serviceCancel();
            
            requestRepository.save(request);
            return request;
            
    }
    



}

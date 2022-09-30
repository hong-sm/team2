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

import team.ServiceApplication;

@RestController
// @RequestMapping(value="/services")
@Transactional
public class ServiceController {
    @Autowired
    ServiceRepository serviceRepository;



    @RequestMapping(value = "services/{id}/product-repair",
        method = RequestMethod.PUT,
        produces = "application/json;charset=UTF-8")
    public Service productRepair(@PathVariable(value = "id") Long id, HttpServletRequest request, HttpServletResponse response) throws Exception {
            System.out.println("##### /service/productRepair  called #####");
            Optional<Service> optionalService = serviceRepository.findById(id);
            
            optionalService.orElseThrow(()-> new Exception("No Entity Found"));
            Service service = optionalService.get();            
            service.setStatus("REPAIRED");
            serviceRepository.save(service);
            service.productRepair();

            return service;
            
    }
    



    @RequestMapping(value = "services/{id}/accept",
        method = RequestMethod.PUT,
        produces = "application/json;charset=UTF-8")
    public Service accept(@PathVariable(value = "id") Long id, HttpServletRequest request, HttpServletResponse response) throws Exception {
            System.out.println("##### /service/accept  called #####");
            
            Optional<Service> optionalService = serviceRepository.findById(id);
            
            optionalService.orElseThrow(()-> new Exception("No Entity Found"));                        
            
            Service service = optionalService.get();

            team.external.Stock stock =
            ServiceApplication.applicationContext.getBean(team.external.StockService.class)
            .getStock(service.getProductId());

            if(stock == null || stock.getStock() == null || stock.getStock() < 1) {
                
                service.setStatus("OUTOFSTOCK");
                serviceRepository.save(service);
                return service;
                //throw new RuntimeException("Insuffient Stock!");
            }
            service.accept();
            service.setStatus("ACCEPTED");
            service.setPrice(stock.getUnitPrice());
            serviceRepository.save(service);
            return service;
            
    }
    



}

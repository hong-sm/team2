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
// @RequestMapping(value="/stocks")
@Transactional
public class StockController {
    @Autowired
    StockRepository stockRepository;

    @RequestMapping(value = "stocks/product/{id}",
    method = RequestMethod.GET,
    produces = "application/json;charset=UTF-8")
    public Stock accept(@PathVariable(value = "id") String relatedProductId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("##### /stocks/product  called #####");
        
        Optional<Stock> optionalStock = stockRepository.findByRelatedProductId(relatedProductId);
        
        optionalStock.orElseThrow(()-> new Exception("No Entity Found"));                        
        
        Stock stock = optionalStock.get();
        return stock;
        
}




}

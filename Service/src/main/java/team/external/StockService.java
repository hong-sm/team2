package team.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@FeignClient(name = "Stock", url = "${api.url.Stock}", fallback = StockServiceImpl.class)
public interface StockService {
    @RequestMapping(method= RequestMethod.GET, path="/stocks/{id}")
    public Stock getStock(@PathVariable("id") Long id);
}


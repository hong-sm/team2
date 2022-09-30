package team.external;

import org.springframework.stereotype.Service;

@Service
public class StockServiceImpl implements StockService {


    /**
     * Fallback
     */
    public Stock getStock(String id) {
        Stock stock = new Stock();
        return stock;
    }
}


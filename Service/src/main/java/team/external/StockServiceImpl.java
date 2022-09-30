package team.external;

import org.springframework.stereotype.Service;

@Service
public class StockServiceImpl implements StockService {


    /**
     * Fallback
     */
    public Stock getStock(Long id) {
        Stock stock = new Stock();
        return stock;
    }
}


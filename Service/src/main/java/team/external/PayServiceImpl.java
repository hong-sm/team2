package team.external;

import org.springframework.stereotype.Service;

@Service
public class PayServiceImpl implements PayService {


    /**
     * Fallback
     */
    public Pay getPay(Long id) {
        Pay pay = new Pay();
        System.out.println("Pay Fallback Call!");
        return pay;
    }

    @Override
    public void pay(Pay pay) {
        // TODO Auto-generated method stub
        
    }
}


package team.common;


import team.ServiceApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = { ServiceApplication.class })
public class CucumberSpingConfiguration {
    
}

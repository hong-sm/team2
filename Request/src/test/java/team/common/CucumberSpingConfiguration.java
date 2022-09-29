package team.common;


import team.RequestApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = { RequestApplication.class })
public class CucumberSpingConfiguration {
    
}

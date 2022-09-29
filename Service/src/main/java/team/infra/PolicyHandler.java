package team.infra;

import javax.naming.NameParser;

import javax.naming.NameParser;
import javax.transaction.Transactional;

import team.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import team.domain.*;


@Service
@Transactional
public class PolicyHandler{
    @Autowired ServiceRepository serviceRepository;
    
    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}

    @StreamListener(value=KafkaProcessor.INPUT, condition="headers['type']=='ServiceRequested'")
    public void wheneverServiceRequested_LoadToServiceList(@Payload ServiceRequested serviceRequested){

        ServiceRequested event = serviceRequested;
        System.out.println("\n\n##### listener LoadToServiceList : " + serviceRequested + "\n\n");


        

        // Sample Logic //
        team.domain.Service.loadToServiceList(event);
        

        

    }

    @StreamListener(value=KafkaProcessor.INPUT, condition="headers['type']=='ServiceCancelled'")
    public void wheneverServiceCancelled_CancelAs(@Payload ServiceCancelled serviceCancelled){

        ServiceCancelled event = serviceCancelled;
        System.out.println("\n\n##### listener CancelAs : " + serviceCancelled + "\n\n");


        

        // Sample Logic //
        team.domain.Service.cancelAs(event);
        

        

    }

}



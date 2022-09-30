package team.infra;

import team.domain.*;
import team.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ProgressViewViewHandler {


    @Autowired
    private ProgressViewRepository progressViewRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenServiceRequested_then_CREATE_1 (@Payload ServiceRequested serviceRequested) {
        try {

            if (!serviceRequested.validate()) return;

            // view 객체 생성
            ProgressView progressView = new ProgressView();
            // view 객체에 이벤트의 Value 를 set 함
            progressView.setSymtom(serviceRequested.getSymtom());
            progressView.setProductId(serviceRequested.getProductId());
            progressView.setCustomerName(serviceRequested.getCustomerName());
            progressView.setPhoneNumber(serviceRequested.getPhoneNumber());
            progressView.setRequestDate(serviceRequested.getDate());
            progressView.setId(serviceRequested.getId());
            // view 레파지 토리에 save
            progressViewRepository.save(progressView);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whenAsAccepted_then_UPDATE_1(@Payload AsAccepted asAccepted) {
        try {
            if (!asAccepted.validate()) return;
                // view 객체 조회
            Optional<ProgressView> progressViewOptional = progressViewRepository.findById(asAccepted.getRequestId());

            if( progressViewOptional.isPresent()) {
                 ProgressView progressView = progressViewOptional.get();
            // view 객체에 이벤트의 eventDirectValue 를 set 함
                progressView.setStatus(asAccepted.getStatus());    
                progressView.setServiceDate(asAccepted.getDate());    
                // view 레파지 토리에 save
                 progressViewRepository.save(progressView);
                }


        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenAsCanceled_then_UPDATE_2(@Payload AsCanceled asCanceled) {
        try {
            if (!asCanceled.validate()) return;
                // view 객체 조회
            Optional<ProgressView> progressViewOptional = progressViewRepository.findById(asCanceled.getRequestId());

            if( progressViewOptional.isPresent()) {
                 ProgressView progressView = progressViewOptional.get();
            // view 객체에 이벤트의 eventDirectValue 를 set 함
                progressView.setStatus(asCanceled.getStatus());    
                progressView.setServiceDate(asCanceled.getDate());    
                // view 레파지 토리에 save
                 progressViewRepository.save(progressView);
                }


        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenRepaired_then_UPDATE_3(@Payload Repaired repaired) {
        try {
            if (!repaired.validate()) return;
                // view 객체 조회
            Optional<ProgressView> progressViewOptional = progressViewRepository.findById(repaired.getRequestId());

            if( progressViewOptional.isPresent()) {
                 ProgressView progressView = progressViewOptional.get();
            // view 객체에 이벤트의 eventDirectValue 를 set 함
                progressView.setStatus(repaired.getStatus());    
                progressView.setServiceDate(repaired.getDate());    
                // view 레파지 토리에 save
                 progressViewRepository.save(progressView);
                }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

}


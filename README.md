![image](https://user-images.githubusercontent.com/43290879/193503556-adf36c7c-8bba-41e7-94eb-4cf85a2ecf22.png)


# 2조 : 가전 A/S 센터

가전 A/S센터의 수리 접수, 재고관리, 고장 수리, 결제를 처리하는 시스템입니다. 

# Table of contents

- [가전 A/S 센터](#---)
  - [서비스 시나리오](#서비스-시나리오)
  - [체크포인트](#체크포인트)
  - [분석/설계](#분석설계)
  - [구현](#구현)
    - [DDD 의 적용](#ddd-의-적용)
    - [동기식 호출과 Fallback 처리](#동기식-호출과-Fallback-처리)
    - [비동기식 호출과 Eventual Consistency](#비동기식-호출과-Eventual-Consistency)
  - [운영](#운영)
    - [CI/CD 설정](#cicd설정)
    - [동기식 호출 / 서킷 브레이킹 / 장애격리](#동기식-호출-서킷-브레이킹-장애격리)
    - [오토스케일 아웃](#오토스케일-아웃)
    - [무정지 재배포](#무정지-재배포)

# 서비스 시나리오

기능적 요구사항
1. 고객이 수리대상 제품 항목을 선택하여 AS요청을 한다.
1. AS요청이 접수되면 서비스목록(Service List)에 저장된다.
1. 고객은 AS요청을 취소할 수 있다.
1. AS요청이 취소되면 서비스목록(Service List)에서 요청취소처리(상태변경)된다.
1. 엔지니어가 AS수락(accept)하면 AS를 진행한다.
1. 엔지니어가 수리 완료하면 부품재고(Stock)을 감소 및 결재연계를 수행한다.
7. 고객은 AS요청의 진행상황을 조회할 수 있다.

비기능적 요구사항
1. 엔지니어는 수리 부품 재고가 없으면 AS요청을 accept 할 수 없다.

장애격리
1. A/S처리가 진행되지 않더라도 A/S접수는 365일 24시간 받을 수 있어야 한다 Async (event-driven), Eventual Consistency

성능
1. 사용자는 전체 A/S의 처리 상태를 확인할 수 있어야 한다. CQRS

# 체크포인트

- 분석 설계

  - 이벤트스토밍: 
    - 스티커 색상별 객체의 의미를 제대로 이해하여 헥사고날 아키텍처와의 연계 설계에 적절히 반영하고 있는가?
    - 각 도메인 이벤트가 의미있는 수준으로 정의되었는가?
    - 어그리게잇: Command와 Event 들을 ACID 트랜잭션 단위의 Aggregate 로 제대로 묶었는가?
    - 기능적 요구사항과 비기능적 요구사항을 누락 없이 반영하였는가?    

  - 서브 도메인, 바운디드 컨텍스트 분리
    - 팀별 KPI 와 관심사, 상이한 배포주기 등에 따른  Sub-domain 이나 Bounded Context 를 적절히 분리하였고 그 분리 기준의 합리성이 충분히 설명되는가?
      - 적어도 3개 이상 서비스 분리
    - 폴리글랏 설계: 각 마이크로 서비스들의 구현 목표와 기능 특성에 따른 각자의 기술 Stack 과 저장소 구조를 다양하게 채택하여 설계하였는가?
    - 서비스 시나리오 중 ACID 트랜잭션이 크리티컬한 Use 케이스에 대하여 무리하게 서비스가 과다하게 조밀히 분리되지 않았는가?
  - 컨텍스트 매핑 / 이벤트 드리븐 아키텍처 
    - 업무 중요성과  도메인간 서열을 구분할 수 있는가? (Core, Supporting, General Domain)
    - Request-Response 방식과 이벤트 드리븐 방식을 구분하여 설계할 수 있는가?
    - 장애격리: 서포팅 서비스를 제거 하여도 기존 서비스에 영향이 없도록 설계하였는가?
    - 신규 서비스를 추가 하였을때 기존 서비스의 데이터베이스에 영향이 없도록 설계(열려있는 아키택처)할 수 있는가?
    - 이벤트와 폴리시를 연결하기 위한 Correlation-key 연결을 제대로 설계하였는가?

  - 헥사고날 아키텍처
    - 설계 결과에 따른 헥사고날 아키텍처 다이어그램을 제대로 그렸는가?
    
- 구현
  - [DDD] 분석단계에서의 스티커별 색상과 헥사고날 아키텍처에 따라 구현체가 매핑되게 개발되었는가?
    - Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 데이터 접근 어댑터를 개발하였는가
    - [헥사고날 아키텍처] REST Inbound adaptor 이외에 gRPC 등의 Inbound Adaptor 를 추가함에 있어서 도메인 모델의 손상을 주지 않고 새로운 프로토콜에 기존 구현체를 적응시킬 수 있는가?
    - 분석단계에서의 유비쿼터스 랭귀지 (업무현장에서 쓰는 용어) 를 사용하여 소스코드가 서술되었는가?
  - Request-Response 방식의 서비스 중심 아키텍처 구현
    - 마이크로 서비스간 Request-Response 호출에 있어 대상 서비스를 어떠한 방식으로 찾아서 호출 하였는가? (Service Discovery, REST, FeignClient)
    - 서킷브레이커를 통하여  장애를 격리시킬 수 있는가?
  - 이벤트 드리븐 아키텍처의 구현
    - 카프카를 이용하여 PubSub 으로 하나 이상의 서비스가 연동되었는가?
    - Correlation-key:  각 이벤트 건 (메시지)가 어떠한 폴리시를 처리할때 어떤 건에 연결된 처리건인지를 구별하기 위한 Correlation-key 연결을 제대로 구현 하였는가?
    - Message Consumer 마이크로서비스가 장애상황에서 수신받지 못했던 기존 이벤트들을 다시 수신받아 처리하는가?
    - Scaling-out: Message Consumer 마이크로서비스의 Replica 를 추가했을때 중복없이 이벤트를 수신할 수 있는가
    - CQRS: Materialized View 를 구현하여, 타 마이크로서비스의 데이터 원본에 접근없이(Composite 서비스나 조인SQL 등 없이) 도 내 서비스의 화면 구성과 잦은 조회가 가능한가?

  - 폴리글랏 플로그래밍
    - 각 마이크로 서비스들이 하나이상의 각자의 기술 Stack 으로 구성되었는가?
    - 각 마이크로 서비스들이 각자의 저장소 구조를 자율적으로 채택하고 각자의 저장소 유형 (RDB, NoSQL, File System 등)을 선택하여 구현하였는가?
  - API 게이트웨이
    - API GW를 통하여 마이크로 서비스들의 집입점을 통일할 수 있는가?
    - 게이트웨이와 인증서버(OAuth), JWT 토큰 인증을 통하여 마이크로서비스들을 보호할 수 있는가?
- 운영
  - SLA 준수
    - 셀프힐링: Liveness Probe 를 통하여 어떠한 서비스의 health 상태가 지속적으로 저하됨에 따라 어떠한 임계치에서 pod 가 재생되는 것을 증명할 수 있는가?
    - 서킷브레이커, 레이트리밋 등을 통한 장애격리와 성능효율을 높힐 수 있는가?
    - 오토스케일러 (HPA) 를 설정하여 확장적 운영이 가능한가?
    - 모니터링, 앨럿팅: 
  - 무정지 운영 CI/CD (10)
    - Readiness Probe 의 설정과 Rolling update을 통하여 신규 버전이 완전히 서비스를 받을 수 있는 상태일때 신규버전의 서비스로 전환됨을 siege 등으로 증명 
    - Contract Test :  자동화된 경계 테스트를 통하여 구현 오류나 API 계약위반를 미리 차단 가능한가?


# 분석/설계


## AS-IS 조직 (Horizontally-Aligned)
  ![image](https://user-images.githubusercontent.com/43290879/193505101-dd90cef0-8822-4f78-94c7-21732fde0441.png)

## TO-BE 조직 (Vertically-Aligned)
  ![image](https://user-images.githubusercontent.com/43290879/193504611-f359d25a-3aa5-454a-91ab-3217cd03d1a9.png)


## Event Storming 결과
* MSAEz 로 모델링한 이벤트스토밍 결과:  https://labs.msaez.io/#/storming/0m7bkHadOlOND3T69AxivsAYOek1/753ebcb08791bf7fb3ece01238af2511


### 이벤트 도출
![image](https://user-images.githubusercontent.com/43290879/193505243-358f2a04-c3bc-4004-b0b9-1b569ec3e941.png)

    - 가전 A/S 센터 시스템을 개발하기 위한 이벤트들을 도출함

### 이벤트 시간순 정렬
![image](https://user-images.githubusercontent.com/43290879/193505276-b45f2929-9f40-4fc1-9ed2-6c364995c375.png)

    - 도출된 이벤트를 시간순으로 정렬

### 불필요 이벤트 정리
![image](https://user-images.githubusercontent.com/43290879/193505652-dc1088ec-4fee-47ab-a91d-0fbc06786c91.png)

    - 잘못 도출되거나 불필요한 이벤트를 정리
        - 부품요청됨 : 부품 요청은 엔지니어가 재고를 추가하는 것으로 정리
        - 엔지니어 배정됨 : 엔지니어는 항상 배정되는 것으로 업무 정의

### 바운디드 컨텍스트로 묶기
![image](https://user-images.githubusercontent.com/43290879/193505799-3c19483c-979d-452a-84bb-d05a80af0fbd.png)

    - 유사한 종류의 이벤트를 바운디드 컨텍스트로 묶기
        - 요청 : 고객이 서비스를 요청하거나 취소
        - 서비스 : 요청받은 서비스를 처리
        - 결제 : 서비스 처리후 결제
        - 재고 : 서비스 처리를 위한 재고 관리
        
    - 도메인 서열 분리 
        - Core Domain : 요청, 서비스
        - Supporting Domain : 재고
        - General Domain : 결제

### 완성된 모델

![image](https://user-images.githubusercontent.com/43290879/193506224-558b68b8-0275-4f04-ae83-218f5388def7.png)

    - View Model 추가

### 요구사항을 커버하는지 검증

![image](https://user-images.githubusercontent.com/43290879/193517960-23e1c0b8-de4b-4cca-a15e-add9da5578ae.png)

    - 1) 고객이 A/S를 요청하면 서비스센터에 접수된다 (ok)
    - 2) 엔지니어가 재고를 확인하여 재고가 있는 경우 서비스를 받아들인다 (ok)
    - 3) A/S가 완료되면 완료상태로 변경하고 사용한 재고는 차감하고 고객은 결제를 하여 서비스를 완료한다. (ok)    

![image](https://user-images.githubusercontent.com/43290879/193507142-aee14cec-bf07-4153-8ffa-4b3bba9e144a.png)

    - 고객이 A/S 신청을 취소할 수 있다 (ok)
    - 취소가 되면 신청 및 A/S상태를 취소로 변경하고 이후 이후 A/S 작업은 중단된다 (ok)
    - 고객이 A/S 상태를 중간중간 조회한다 (View-green sticker 의 추가로 ok)     

### 비기능 요구사항에 대한 검증

    - 마이크로 서비스를 넘나드는 시나리오에 대한 트랜잭션 처리
        - A/S 완료시 결제처리:  최종 수리된 물건을 받기 위해서는 결제를 완료해야 하기 때문에 Request-Response 방식 처리
        - A/S 승인시 재고 확인: A/S 승인을 하기 위해서는 수리를 위한 부품의 재고가 있는지 확인이 되어야 하기 때문에 Request-Response 방식으로 처리
        - 나머지 모든 inter-microservice 트랜잭션: A/S접수, 재고차감 등 모든 이벤트에 대해 데이터 일관성의 시점이 크리티컬하지 않은 모든 경우가 대부분이라 판단, Eventual Consistency 를 기본으로 채택함.



## 헥사고날 아키텍처 다이어그램 도출
    
![image](https://user-images.githubusercontent.com/43290879/193509676-61ae917a-2463-48d6-b196-d636fe22ebda.png)


    - Chris Richardson, MSA Patterns 참고하여 Inbound adaptor와 Outbound adaptor를 구분함
    - 호출관계에서 PubSub 과 Req/Resp 를 구분함
    - 서브 도메인과 바운디드 컨텍스트의 분리:  각 팀의 KPI 별로 아래와 같이 관심 구현 스토리를 나눠가짐


# 구현

분석/설계 단계에서 도출된 헥사고날 아키텍처에 따라, 각 BC별로 대변되는 마이크로 서비스들을 스프링부트로 구현하였다. 구현한 각 서비스를 로컬에서 실행하는 방법은 아래와 같다 (각자의 포트넘버는 8081 ~ 808n 이다)

```
cd Request
mvn spring-boot:run

cd Service
mvn spring-boot:run 

cd Stock
mvn spring-boot:run  

cd Payment
mvn spring-boot:run  

cd view
mvn spring-boot:run  
```

## DDD 의 적용

- 각 서비스내에 도출된 핵심 Aggregate Root 객체를 Entity 로 선언하였다: (예시는 service 마이크로 서비스). 이때 가능한 현업에서 사용하는 언어 (유비쿼터스 랭귀지)를 그대로 사용. Getter/Setter는 lombok에 의해서 자동으로 생성

```
package team.domain;

import team.domain.AsCanceled;
import team.ServiceApplication;
import javax.persistence.*;
import java.util.List;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name="Service_table")
@Data

public class Service  {    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)    
    private Long id; 
    private String status;
    private Long requestId;
    private Date date;
    private String customerName;
    private String phoneNumber;
    private Integer price;
    private String symtom;
    private String engineerName;
    private String productId;
    
    @PostPersist
    public void onPostPersist(){
        AsCanceled asCanceled = new AsCanceled(this);
        asCanceled.publishAfterCommit();        
    }

    public static ServiceRepository repository(){
        ServiceRepository serviceRepository = ServiceApplication.applicationContext.getBean(ServiceRepository.class);
        return serviceRepository;
    }
    
    public void productRepair(){
        Repaired repaired = new Repaired(this);
        repaired.publishAfterCommit();

        team.external.Pay pay = new team.external.Pay();
        // 지불 서비스 호출
        pay.setStatus("PAID");
        pay.setRequestId(repaired.getRequestId());
        ServiceApplication.applicationContext.getBean(team.external.PayService.class)
            .pay(pay);
        
        //지불 상태로 변경
        repository().findById(repaired.getId()).ifPresent(service->{
            
            service.setStatus("PAID"); 
            service.setDate(new Date());
            repository().save(service);
        });

    }
    public void accept(){
        AsAccepted asAccepted = new AsAccepted(this);
        asAccepted.publishAfterCommit();

    }

    public static void loadToServiceList(ServiceRequested serviceRequested){
        
        //AS요청으로 부터 받은 정보 세팅
        Service service = new Service();
        service.setCustomerName(serviceRequested.getCustomerName());
        service.setDate(serviceRequested.getDate());
        service.setPhoneNumber(serviceRequested.getPhoneNumber());
        service.setRequestId(serviceRequested.getId());        
        service.setProductId(serviceRequested.getProductId());
        service.setDate(new Date());
        service.setStatus("REQUESTED");        
        repository().save(service);        
    }
    public static void cancelAs(ServiceCancelled serviceCancelled){
        // 취소 상태로 변경
        repository().findByRequestId(serviceCancelled.getId()).ifPresent(service->{
            
            service.setStatus("CANCELED"); // do something
            service.setDate(new Date());
            repository().save(service);

            AsCanceled asCanceled = new AsCanceled(service);
            asCanceled.publishAfterCommit();
         });      
    }
}

```
- Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 다양한 데이터소스 유형 (RDB or NoSQL) 에 대한 별도의 처리가 없도록 데이터 접근 어댑터를 자동 생성하기 위하여 Spring Data REST 의 RestRepository 를 적용하였다
```
package team.domain;

import team.domain.*;
import java.util.Optional;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="services", path="services")
public interface ServiceRepository extends PagingAndSortingRepository<team.domain.Service, Long>{
    Optional<team.domain.Service> findByRequestId(long requestId);
}
```
- 적용 후 REST API 의 테스트
```
# request 서비스의 A/S 신청 : 고장난 제품ID, 고장 증상 고객정보를 입력하여 A/S요청
http :8081/requests symtom="화면이 안나와요" productId="1" customerName="Hong"
```
![image](https://user-images.githubusercontent.com/43290879/193704949-fcd70df2-a9a1-4635-8623-40114c90f795.png)

```
# service 상태 확인 : 서비스 센터로 접수된 정보 확인
http :8082/services/1
```
![image](https://user-images.githubusercontent.com/43290879/193705028-4e85ae6d-abd4-4d5b-8133-b44d8822dab5.png)

```
# stock 서비스의 재고 추가 : 수리하기 위한 부품 추가
http :8083/stocks relatedProductId="1" stock=5 unitPrice=5000
```
![image](https://user-images.githubusercontent.com/43290879/193705244-bc40a7ec-6143-4061-8b95-12fa2ebce716.png)

```
# service 서비스의 엔지니어 A/S승인 : 엔지니어가 A/S 접수 승인
http PUT :8082/services/1/accept
```
![image](https://user-images.githubusercontent.com/43290879/193705386-d31888d1-2196-4d80-864d-3f4396be7a4b.png)

```
# service 서비스의 엔지니어 수리 완료 및 결제 : 엔지니어가 최종 수리 완료 및 고객 결제 처리
http PUT :8082/services/1/product-repair
```
![image](https://user-images.githubusercontent.com/43290879/193705763-774d7ffb-4796-4c1d-ada5-40b2bf7abaa2.png)

## 동기식 호출과 Fallback 처리

분석단계에서의 조건 중 하나로 A/S승인(service)->재고확인(stock), 수리완료(service)->결재(pay)간의 호출은 동기식 일관성을 유지하는 트랜잭션으로 처리하기로 하였다. 호출 프로토콜은 이미 앞서 Rest Repository 에 의해 노출되어있는 REST 서비스를 FeignClient 를 이용하여 호출하도록 한다. 

- 재고확인을 호출하기 위하여 Stub과 (FeignClient) 를 이용하여 Service 대행 인터페이스 (Proxy) 를 구현 

```
package team.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "Stock", url = "${api.url.Stock}", fallback = StockServiceImpl.class)
public interface StockService {
    @RequestMapping(method= RequestMethod.GET, path="/stocks/product/{id}")
    public Stock getStock(@PathVariable("id") String id);    
}
```

- 엔지니어가 A/S승인을 할 때 재고개수를 확인하도록 처리
```
# ServiceController.java (Controller)

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
            }
            service.accept();
            service.setStatus("ACCEPTED");
            service.setPrice(stock.getUnitPrice());
            serviceRepository.save(service);
            return service;
            
    }
```

- 결제서비스를 호출하기 위하여 Stub과 (FeignClient) 를 이용하여 Service 대행 인터페이스 (Proxy) 를 구현

```
package team.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@FeignClient(name = "Payment", url = "${api.url.Payment}", fallback = PayServiceImpl.class)
public interface PayService {
    @RequestMapping(method= RequestMethod.POST, path="/pays")
    public void pay(@RequestBody Pay pay);
}
```

- 엔지니어가 수리를 완료한 후 결제를 수행하도록 처리
```
# ServiceController.java (Controller)

    public void productRepair(){
        Repaired repaired = new Repaired(this);
        repaired.publishAfterCommit();

        team.external.Pay pay = new team.external.Pay();
        // 지불 서비스 호출
        pay.setStatus("PAID");
        pay.setRequestId(repaired.getRequestId());
        ServiceApplication.applicationContext.getBean(team.external.PayService.class)
            .pay(pay);
        
        //지불 상태로 변경
        repository().findById(repaired.getId()).ifPresent(service->{
            
            service.setStatus("PAID"); 
            service.setDate(new Date());
            repository().save(service);
        });

    }
```
- fallback 구현
```
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
```

- 동기식 호출에서는 호출 시간에 따른 타임 커플링이 발생하며, A/S 승인처리시 재고서비스가 내려가 있는 경우 정상 처리 불가 확 :


```
#재고 (stock) 서비스를 잠시 내려놓음 (ctrl+c)

#A/S승인 처리
http PUT :8082/services/1/accept   #재고없음으로 Fail

#결제서비스 재기동
cd Stock
mvn spring-boot:run

#A/S승인 처리
http PUT :8082/services/1/accept   #Success
```

## 비동기식 호출 / 시간적 디커플링 / 장애격리 / 최종 (Eventual) 일관성 테스트

고객이 A/S 신청을 한 후 서비스 센터에 A/S 신청접수가 처리되는 부분은 비동기식으로 처리하여 A/S 신청이 블로킹 되지 않도록 처리한다. 
 
- 이를 위하여 A/S신청(request)에 기록을 남긴 후에 곧바로 서비스측에 도메인 이벤트를 카프카로 송출한다(Publish)
 
```
package team.domain;

@Entity
@Table(name="Request_table")
@Data

public class Request  {

 ...
    @PostPersist
    public void onPostPersist(){
        ServiceRequested serviceRequested = new ServiceRequested(this);
        serviceRequested.publishAfterCommit();
    }
}
```
- Service 서비스에서는 A/S접수시 이를 수신하여 자신의 정책을 처리하도록 PolicyHandler 를 구현한다:

```
package team.infra;

...

@Service
@Transactional
public class PolicyHandler{

...
    @StreamListener(value=KafkaProcessor.INPUT, condition="headers['type']=='ServiceRequested'")
    public void wheneverServiceRequested_LoadToServiceList(@Payload ServiceRequested serviceRequested){
        ServiceRequested event = serviceRequested;
        System.out.println("\n\n##### listener LoadToServiceList : " + serviceRequested + "\n\n");  
        team.domain.Service.loadToServiceList(event);    
    }
}

```
Service에서는 loadToServiceList의 이벤트를 다음과 같이 처리하여 Service Agggregate에 A/S신청 정보가 저장되도록 처리하였다:
  
```
package team.domain;
...
@Entity
@Table(name="Service_table")
@Data
...
public class Service  {
  public static void loadToServiceList(ServiceRequested serviceRequested){
        //AS요청으로 부터 받은 정보 세팅
        Service service = new Service();
        service.setCustomerName(serviceRequested.getCustomerName());
        service.setDate(serviceRequested.getDate());
        service.setPhoneNumber(serviceRequested.getPhoneNumber());
        service.setRequestId(serviceRequested.getId());        
        service.setProductId(serviceRequested.getProductId());
        service.setDate(new Date());
        service.setStatus("REQUESTED");        
        repository().save(service);    
    }
}

```

# 운영

## CI/CD 설정

각 구현체들은 각자의 source repository 에 구성되었고, 사용한 CI/CD 플랫폼은 GCP를 사용하였으며, pipeline build script 는 각 프로젝트 폴더 이하에 cloudbuild.yml 에 포함되었다.


## 동기식 호출 / 서킷 브레이킹 / 장애격리

* 서킷 브레이킹 프레임워크의 선택: Spring FeignClient + Hystrix 옵션을 사용하여 구현함

시나리오는 단말앱(app)-->결제(pay) 시의 연결을 RESTful Request/Response 로 연동하여 구현이 되어있고, 결제 요청이 과도할 경우 CB 를 통하여 장애격리.

- Hystrix 를 설정:  요청처리 쓰레드에서 처리시간이 610 밀리가 넘어서기 시작하여 어느정도 유지되면 CB 회로가 닫히도록 (요청을 빠르게 실패처리, 차단) 설정
```
# application.yml
feign:
  hystrix:
    enabled: true
    
hystrix:
  command:
    # 전역설정
    default:
      execution.isolation.thread.timeoutInMilliseconds: 610

```

- 피호출 서비스(결제:pay) 의 임의 부하 처리 - 400 밀리에서 증감 220 밀리 정도 왔다갔다 하게
```
# (pay) 결제이력.java (Entity)

    @PrePersist
    public void onPrePersist(){  //결제이력을 저장한 후 적당한 시간 끌기

        ...
        
        try {
            Thread.currentThread().sleep((long) (400 + Math.random() * 220));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
```

* 부하테스터 siege 툴을 통한 서킷 브레이커 동작 확인:
- 동시사용자 100명
- 60초 동안 실시

```
$ siege -c100 -t60S -r10 --content-type "application/json" 'http://localhost:8081/orders POST {"item": "chicken"}'

** SIEGE 4.0.5
** Preparing 100 concurrent users for battle.
The server is now under siege...

HTTP/1.1 201     0.68 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.68 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.70 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.70 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.73 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.75 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.77 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.97 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.81 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.87 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.12 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.16 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.17 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.26 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.25 secs:     207 bytes ==> POST http://localhost:8081/orders

* 요청이 과도하여 CB를 동작함 요청을 차단

HTTP/1.1 500     1.29 secs:     248 bytes ==> POST http://localhost:8081/orders   
HTTP/1.1 500     1.24 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     1.23 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     1.42 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     2.08 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.29 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     1.24 secs:     248 bytes ==> POST http://localhost:8081/orders

* 요청을 어느정도 돌려보내고나니, 기존에 밀린 일들이 처리되었고, 회로를 닫아 요청을 다시 받기 시작

HTTP/1.1 201     1.46 secs:     207 bytes ==> POST http://localhost:8081/orders  
HTTP/1.1 201     1.33 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.36 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.63 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.65 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.68 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.69 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.71 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.71 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.74 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.76 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     1.79 secs:     207 bytes ==> POST http://localhost:8081/orders

* 다시 요청이 쌓이기 시작하여 건당 처리시간이 610 밀리를 살짝 넘기기 시작 => 회로 열기 => 요청 실패처리

HTTP/1.1 500     1.93 secs:     248 bytes ==> POST http://localhost:8081/orders    
HTTP/1.1 500     1.92 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     1.93 secs:     248 bytes ==> POST http://localhost:8081/orders

* 생각보다 빨리 상태 호전됨 - (건당 (쓰레드당) 처리시간이 610 밀리 미만으로 회복) => 요청 수락

HTTP/1.1 201     2.24 secs:     207 bytes ==> POST http://localhost:8081/orders  
HTTP/1.1 201     2.32 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     2.16 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     2.19 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     2.19 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     2.19 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     2.21 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     2.29 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     2.30 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     2.38 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     2.59 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     2.61 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     2.62 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     2.64 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.01 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.27 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.33 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.45 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.52 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.57 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.69 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.70 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.69 secs:     207 bytes ==> POST http://localhost:8081/orders

* 이후 이러한 패턴이 계속 반복되면서 시스템은 도미노 현상이나 자원 소모의 폭주 없이 잘 운영됨


HTTP/1.1 500     4.76 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.23 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.76 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.74 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.82 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.82 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.84 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.66 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     5.03 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.22 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.19 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.18 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.69 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.65 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     5.13 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.84 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.25 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.25 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.80 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.87 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.33 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.86 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.96 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.34 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 500     4.04 secs:     248 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.50 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.95 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.54 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     4.65 secs:     207 bytes ==> POST http://localhost:8081/orders


:
:

Transactions:		        1025 hits
Availability:		       63.55 %
Elapsed time:		       59.78 secs
Data transferred:	        0.34 MB
Response time:		        5.60 secs
Transaction rate:	       17.15 trans/sec
Throughput:		        0.01 MB/sec
Concurrency:		       96.02
Successful transactions:        1025
Failed transactions:	         588
Longest transaction:	        9.20
Shortest transaction:	        0.00

```
- 운영시스템은 죽지 않고 지속적으로 CB 에 의하여 적절히 회로가 열림과 닫힘이 벌어지면서 자원을 보호하고 있음을 보여줌. 하지만, 63.55% 가 성공하였고, 46%가 실패했다는 것은 고객 사용성에 있어 좋지 않기 때문에 Retry 설정과 동적 Scale out (replica의 자동적 추가,HPA) 을 통하여 시스템을 확장 해주는 후속처리가 필요.

- Retry 의 설정 (istio)
- Availability 가 높아진 것을 확인 (siege)

### 오토스케일 아웃
앞서 CB 는 시스템을 안정되게 운영할 수 있게 해줬지만 사용자의 요청을 100% 받아들여주지 못했기 때문에 이에 대한 보완책으로 자동화된 확장 기능을 적용하고자 한다. 


- 결제서비스에 대한 replica 를 동적으로 늘려주도록 HPA 를 설정한다. 설정은 CPU 사용량이 15프로를 넘어서면 replica 를 10개까지 늘려준다:
```
kubectl autoscale deploy pay --min=1 --max=10 --cpu-percent=15
```
- CB 에서 했던 방식대로 워크로드를 2분 동안 걸어준다.
```
siege -c100 -t120S -r10 --content-type "application/json" 'http://localhost:8081/orders POST {"item": "chicken"}'
```
- 오토스케일이 어떻게 되고 있는지 모니터링을 걸어둔다:
```
kubectl get deploy pay -w
```
- 어느정도 시간이 흐른 후 (약 30초) 스케일 아웃이 벌어지는 것을 확인할 수 있다:
```
NAME    DESIRED   CURRENT   UP-TO-DATE   AVAILABLE   AGE
pay     1         1         1            1           17s
pay     1         2         1            1           45s
pay     1         4         1            1           1m
:
```
- siege 의 로그를 보아도 전체적인 성공률이 높아진 것을 확인 할 수 있다. 
```
Transactions:		        5078 hits
Availability:		       92.45 %
Elapsed time:		       120 secs
Data transferred:	        0.34 MB
Response time:		        5.60 secs
Transaction rate:	       17.15 trans/sec
Throughput:		        0.01 MB/sec
Concurrency:		       96.02
```


## 무정지 재배포

* 먼저 무정지 재배포가 100% 되는 것인지 확인하기 위해서 Autoscaler 이나 CB 설정을 제거함

- seige 로 배포작업 직전에 워크로드를 모니터링 함.
```
siege -c100 -t120S -r10 --content-type "application/json" 'http://localhost:8081/orders POST {"item": "chicken"}'

** SIEGE 4.0.5
** Preparing 100 concurrent users for battle.
The server is now under siege...

HTTP/1.1 201     0.68 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.68 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.70 secs:     207 bytes ==> POST http://localhost:8081/orders
HTTP/1.1 201     0.70 secs:     207 bytes ==> POST http://localhost:8081/orders
:

```

- 새버전으로의 배포 시작
```
kubectl set image ...
```

- seige 의 화면으로 넘어가서 Availability 가 100% 미만으로 떨어졌는지 확인
```
Transactions:		        3078 hits
Availability:		       70.45 %
Elapsed time:		       120 secs
Data transferred:	        0.34 MB
Response time:		        5.60 secs
Transaction rate:	       17.15 trans/sec
Throughput:		        0.01 MB/sec
Concurrency:		       96.02

```
배포기간중 Availability 가 평소 100%에서 70% 대로 떨어지는 것을 확인. 원인은 쿠버네티스가 성급하게 새로 올려진 서비스를 READY 상태로 인식하여 서비스 유입을 진행한 것이기 때문. 이를 막기위해 Readiness Probe 를 설정함:

```
# deployment.yaml 의 readiness probe 의 설정:


kubectl apply -f kubernetes/deployment.yaml
```

- 동일한 시나리오로 재배포 한 후 Availability 확인:
```
Transactions:		        3078 hits
Availability:		       100 %
Elapsed time:		       120 secs
Data transferred:	        0.34 MB
Response time:		        5.60 secs
Transaction rate:	       17.15 trans/sec
Throughput:		        0.01 MB/sec
Concurrency:		       96.02

```

배포기간 동안 Availability 가 변화없기 때문에 무정지 재배포가 성공한 것으로 확인됨.

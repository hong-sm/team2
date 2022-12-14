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
    - [서킷 브레이킹](#서킷-브레이킹)
    - [Deployment](#Deployment)
    - [API Gateway (Ingress)](#api-Gateway-Ingress)
    - [오토스케일 아웃](#오토스케일-아웃)
    - [무정지 재배포](#무정지-재배포)
    - [Persistence Volume/ConfigMap/Secret & Polyglot](#persistence-volumeconfigmapsecret--polyglot)
    - [Self-healing(liveness probe)](#self-healingliveness-probe)

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

![image](https://user-images.githubusercontent.com/43290879/193727801-4f00cba6-8d11-4d09-a0b1-9dfe29b9a4cc.png)

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
  
![image](https://user-images.githubusercontent.com/43290879/193714108-2c67f71d-49c8-4fbd-a1df-e549563f4dbb.png)

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

```
# CQRS를 통한 서비스 상태 확인 대시보드 ( request 서비스와 service 서비스의 정보를 조합하여 보여줌)
http :8085/progressViews
```
![image](https://user-images.githubusercontent.com/43290879/193711831-af8b9002-5255-4827-a7cb-9f89ba68f16e.png)


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
public class StockServiceImpl implements StockService {


    /**
     * Fallback
     */
    public Stock getStock(String id) {
        Stock stock = new Stock();
        System.out.println("FallBack Stock");
        return stock;
    }
}
```

- 동기식 호출에서는 호출 시간에 따른 타임 커플링이 발생하며, A/S 승인처리시 재고서비스가 내려가 있는 경우 정상 처리 불가 확인 :


```
#재고 (stock) 서비스를 잠시 내려놓음 (ctrl+c)

#A/S승인 처리
http PUT :8082/services/1/accept   #재고없음으로 Fail

#재고 서비스 재기동
cd Stock
mvn spring-boot:run

#A/S승인 처리
http PUT :8082/services/1/accept   #Success
```
 - 재고 서비스가 내려가 있는 동안 Fallback이 동작함
![image](https://user-images.githubusercontent.com/43290879/193722595-01ea64b2-4860-4d2c-aac8-f0a31caf0e2c.png)

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

## 서킷 브레이킹
- 동작 환경 : Stock의 Sevice를 삭제한 상태에서 Service -> Stock 호출
- Siege를 이용한 Service -> Stock 호출
![image](https://user-images.githubusercontent.com/43290879/193741344-bcd19058-aed2-449e-9789-5ed55d5c61b2.png)

- Fallback 호출 로그
![image](https://user-images.githubusercontent.com/43290879/193741408-077d0699-5e02-4a26-9c66-4b27bcc5b086.png)

- Stock Service가 구동하지 않는 상태
![image](https://user-images.githubusercontent.com/43290879/193741594-ebc28239-146c-4772-b2b5-e902c8ac99b9.png)

## Deployment
- AWS에 클러스터를 생성하여 EKS 환경 구성
- A/S 센터의 마이크로 서비스 각각을 EKS를 통해 Deploy가 된 것을 확인(접속 화면은 아래 Ingress 참고)

![image](https://user-images.githubusercontent.com/43290879/193721361-45c43e1e-4c2a-4400-b170-945926c9b207.png)


## API Gateway (Ingress)
다음과 같이 서비스를 Ingress로 구성하여 하나의 진입점으로 접속 할 수 있도록 설정

```
apiVersion: "extensions/v1beta1"
kind: "Ingress"
metadata: 
  name: "servicecenter-ingress"
  annotations: 
    kubernetes.io/ingress.class: "nginx"
spec: 
  rules: 
    - 
      http: 
        paths: 
          - 
            path: /requests
            pathType: Prefix
            backend: 
              serviceName: request
              servicePort: 8080
          - 
            path: /services
            pathType: Prefix
            backend: 
              serviceName: service
              servicePort: 8080
          - 
            path: /stocks
            pathType: Prefix
            backend: 
              serviceName: stock
              servicePort: 8080
          - 
            path: /pays
            pathType: Prefix
            backend: 
              serviceName: payment
              servicePort: 8080
          - 
            path: /progressViews
            pathType: Prefix
            backend: 
              serviceName: view
              servicePort: 8080
```

 - ingress가 정상적으로 뜨고 주소 확인이 가능
![image](https://user-images.githubusercontent.com/43290879/193720385-2bb63aae-c81e-415e-91d0-b46113fd81fe.png)

 - 해당 주소로 정상적으로 접속하여 서비스 확인
![image](https://user-images.githubusercontent.com/43290879/193720631-25bcf4e1-2926-42a6-8f45-17e65ce03fcc.png)


## 오토스케일 아웃
○ Auto Scale-Out 설정

 - cpu 사용율이 20%를 초과하면 Pod를 늘리도록 설정한다
```
kubectl autoscale deployment stock --cpu-percent=20 --min=1 --max=3
```
```
gitpod /workspace/team2 (main) $ kubectl get hpa
NAME    REFERENCE          TARGETS   MINPODS   MAXPODS   REPLICAS   AGE
stock   Deployment/stock   1%/20%    1         3         1          111m
```
 - deployment.yaml 파일을 수정하여 resources.requests.cpu: "200m"을 추가한다
```
gitpod /workspace/team2/Stock/kubernetes (main) $ cat deployment.yaml 
apiVersion: apps/v1
kind: Deployment
metadata:
  name: stock
  labels:
    app: stock
spec:
  replicas: 1
  selector:
    matchLabels:
      app: stock
  template:
    metadata:
      labels:
        app: stock
    spec:
      containers:
        - name: stock
          image: hongsm/stock:v1
          ports:
            - containerPort: 8080
          resources:
            requests:
              cpu: "200m"
          readinessProbe:
            httpGet:
              path: '/actuator/health'
              port: 8080
```
 - 변경된 yaml 파일을 사용하여 쿠버네티스에 배포한다.
```
kubectl delete -f deployment.yml
kubectl apply -f deployment.yml
```

○ Auto Scale-Out 증명
 - siege 명령으로 부하를 주어서 Pod 가 늘어나도록 한다.
```
root@siege:/# siege -c20 -t60S -v http://stock:8080/services
** SIEGE 4.0.4
** Preparing 20 concurrent users for battle.
The server is now under siege...
HTTP/1.1 404     0.01 secs:     110 bytes ==> GET  /services
HTTP/1.1 404     0.01 secs:     110 bytes ==> GET  /services
HTTP/1.1 404     0.01 secs:     110 bytes ==> GET  /services
...
중간생략
HTTP/1.1 404     0.02 secs:     110 bytes ==> GET  /services
HTTP/1.1 404     0.02 secs:     110 bytes ==> GET  /services
HTTP/1.1 404     0.03 secs:     110 bytes ==> GET  /services

Lifting the server siege...
Transactions:                  47423 hits
Availability:                 100.00 %
Elapsed time:                  59.47 secs
Data transferred:               4.97 MB
Response time:                  0.02 secs
Transaction rate:             797.43 trans/sec
Throughput:                     0.08 MB/sec
Concurrency:                   16.30
Successful transactions:           0
Failed transactions:               0
Longest transaction:            0.29
Shortest transaction:           0.00
```

 - kubectl get po -w 명령을 사용하여 pod 가 생성되는 것을 확인한다.

```
gitpod /workspace/team2/Stock/kubernetes (main) $ kubectl get po -w
NAME                       READY   STATUS             RESTARTS   AGE
httpie                     1/1     Running            0          3h21m
liveness-exec              0/1     CrashLoopBackOff   33         111m
my-kafka-0                 1/1     Running            1          3h32m
my-kafka-zookeeper-0       1/1     Running            0          3h32m
mysql                      1/1     Running            0          3h10m
payment-7dc8885b79-77ldf   1/1     Running            0          101m
request-745b9b9d74-vl576   1/1     Running            0          130m
service-5c75dbcdc6-rnljc   1/1     Running            0          129m
siege                      1/1     Running            0          113m
stock-69dbb7c8b4-5k7b8     1/1     Running            0          83m
view-858fd6bccb-7mckr      1/1     Running            0          129m
stock-69dbb7c8b4-nklgs     0/1     Pending            0          0s
stock-69dbb7c8b4-nklgs     0/1     Pending            0          0s
stock-69dbb7c8b4-mvjdt     0/1     Pending            0          0s
stock-69dbb7c8b4-nklgs     0/1     ContainerCreating   0          0s
stock-69dbb7c8b4-mvjdt     0/1     Pending             0          0s
stock-69dbb7c8b4-mvjdt     0/1     ContainerCreating   0          0s
stock-69dbb7c8b4-nklgs     0/1     Running             0          1s
stock-69dbb7c8b4-mvjdt     0/1     Running             0          2s
stock-69dbb7c8b4-nklgs     1/1     Running             0          25s
stock-69dbb7c8b4-mvjdt     1/1     Running             0          25s
```

 - kubectl get hpa 명령어로 CPU 값이 늘어난 것을 확인 한다.
```
gitpod /workspace/team2 (main) $ kubectl get hpa
NAME    REFERENCE          TARGETS   MINPODS   MAXPODS   REPLICAS   AGE
stock   Deployment/stock   1%/20%    1         3         1          96m

gitpod /workspace/team2 (main) $ kubectl get hpa
NAME    REFERENCE          TARGETS    MINPODS   MAXPODS   REPLICAS   AGE
stock   Deployment/stock   621%/20%   1         3         3          97m
```


## 무정지 재배포
1) siege로 부하 전송
```
siege -c100 -t120S -r10 --content-type "application/json" 'http://payment:8080/pays'
```
2) image version up
```
gitpod /workspace/team2/Payment (main) $ docker build -t hongsm/payment:v4 .
Sending build context to Docker daemon   76.3MB
Step 1/6 : FROM openjdk:15-jdk-alpine
 ---> f02adfce91a2
Step 2/6 : COPY target/*SNAPSHOT.jar app.jar
 ---> Using cache
 ---> 204bec0feb4f
Step 3/6 : EXPOSE 8080
 ---> Using cache
 ---> aeffbed37567
Step 4/6 : ENV TZ=Asia/Seoul
 ---> Using cache
 ---> 605a86f45f74
Step 5/6 : RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
 ---> Using cache
 ---> 1a759eea8ace
Step 6/6 : ENTRYPOINT ["java","-Xmx400M","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar","--spring.profiles.active=docker"]
 ---> Using cache
 ---> fb1072a4b38c
Successfully built fb1072a4b38c
Successfully tagged hongsm/payment:v4
gitpod /workspace/team2/Payment (main) $ docker push hongsm/payment:v4
The push refers to repository [docker.io/hongsm/payment]
be2e03a67fab: Layer already exists 
2a99bbd38e24: Layer already exists 
ca35920ce48a: Layer already exists 
a9711b2e31f2: Layer already exists 
50644c29ef5a: Layer already exists 
v4: digest: sha256:2e804a132583818c878e94eb99cdfb3ce914817d5988f03d898e61b0776fcbf0 size: 1370
gitpod /workspace/team2/Payment (main) $ kubectl apply -f kubernetes/deployment.yaml
deployment.apps/payment configured
```
3) 결과 확인
```
root@siege:/# siege -c100 -t120S -r10 --content-type "application/json" 'http://payment:8080/pays'
[error] CONFIG conflict: selected time and repetition based testing
defaulting to time-based testing: 120 seconds
** SIEGE 4.0.4
** Preparing 100 concurrent users for battle.
The server is now under siege...
Lifting the server siege...
Transactions:                  33947 hits
Availability:                 100.00 %
Elapsed time:                 119.95 secs
Data transferred:              19.68 MB
Response time:                  0.35 secs
Transaction rate:             283.01 trans/sec
Throughput:                     0.16 MB/sec
Concurrency:                   99.85
Successful transactions:       33947
Failed transactions:               0
Longest transaction:           15.90
Shortest transaction:           0.00

gitpod /workspace/team2/Payment (main) $ kubectl get all -l app=payment
NAME                          READY   STATUS    RESTARTS   AGE
pod/payment-76c75d894-l9md4   1/1     Running   0          6m35s

NAME              TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)    AGE
service/payment   ClusterIP   10.100.87.172   <none>        8080/TCP   3h6m

NAME                      READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/payment   1/1     1            1           3h18m

NAME                                 DESIRED   CURRENT   READY   AGE
replicaset.apps/payment-5d6c958c7    0         0         0       3h1m
replicaset.apps/payment-6678f566dd   0         0         0       161m
replicaset.apps/payment-66b4656587   0         0         0       3h18m
replicaset.apps/payment-76c75d894    1         1         1       6m36s
replicaset.apps/payment-7dc8885b79   0         0         0       153m
gitpod /workspace/team2/Payment (main) $ 
```

## Persistence Volume/ConfigMap/Secret & Polyglot

payment 서비스에 mysql을 적용하여 위의 요건을 적용하고자 한다.

1) pom.xml 변경
   - mysql을 사용하기 위하여 dependency를 추가한다.
```
    	<dependency>
	    	<groupId>mysql</groupId>
		    <artifactId>mysql-connector-java</artifactId>
	    </dependency>
```
2) application.yml 변경
  - mysql을 사용하기 위하여 mysql 정보를 추가한다.
```
spring:
  jpa:
    hibernate:
      naming:
        physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
      ddl-auto: update
    properties:
      hibernate:
        show_sql: true
        format_sql: true
        dialect: org.hibernate.dialect.MySQL57Dialect
  datasource:
    url: jdbc:mysql://${_DATASOURCE_ADDRESS:3306}/${_DATASOURCE_TABLESPACE}
    username: ${_DATASOURCE_USERNAME}
    password: ${_DATASOURCE_PASSWORD}
    driverClassName: com.mysql.cj.jdbc.Driver
```
3) deployment.yaml 변경
  - 환경 정보 사용을 위하여 해당 내용 추가한다.
```
          env:
            - name: _DATASOURCE_ADDRESS
              value: mysql
            - name: _DATASOURCE_TABLESPACE
              value: paymentdb
            - name: _DATASOURCE_USERNAME
              value: root
            - name: _DATASOURCE_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: mysql-pass
                  key: password
```
4) mysql 서비스 구동 (pod 생성, pvc 생성, svc 생성, secret 생성) 
 
 4-1) pod 생성
```
apiVersion: v1
kind: Pod
metadata:
  name: mysql
  labels:
    name: lbl-k8s-mysql
spec:
  containers:
  - name: mysql
    image: mysql:latest
    env:
    - name: MYSQL_ROOT_PASSWORD
      valueFrom:
        secretKeyRef:
          name: mysql-pass
          key: password
    ports:
    - name: mysql
      containerPort: 3306
      protocol: TCP
    volumeMounts:
    - name: k8s-mysql-storage
      mountPath: /var/lib/mysql
  volumes:
  - name: k8s-mysql-storage
    persistentVolumeClaim:
      claimName: "fs"
```
4-2) pvc 생성
```
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: fs
  labels:
    app: test-pvc
spec:
  accessModes:
  - ReadWriteOnce
  resources:
    requests:
      storage: 1Mi
```
4-3) service 생성
```
apiVersion: v1
kind: Service
metadata:
  labels:
    name: lbl-k8s-mysql
  name: mysql
spec:
  ports:
  - port: 3306
    protocol: TCP
    targetPort: 3306
  selector:
    name: lbl-k8s-mysql
  type: ClusterIP
```
4-4) secret 생성
```
apiVersion: v1
kind: Secret
metadata:
  name: mysql-pass
type: Opaque
data:
  password: YWRtaW4=     
```
5) 적용 후 DB 저장 내용

- 수행
```
root@httpie:/# http payment:8080/pays requestId=10
HTTP/1.1 201 
Connection: keep-alive
Content-Type: application/json
Date: Tue, 04 Oct 2022 02:33:06 GMT
Keep-Alive: timeout=60
Location: http://payment:8080/pays/1
Transfer-Encoding: chunked
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers

{
    "_links": {
        "pay": {
            "href": "http://payment:8080/pays/1"
        }, 
        "self": {
            "href": "http://payment:8080/pays/1"
        }
    }, 
    "payDate": "2022-10-04T02:33:07.187+00:00", 
    "price": null, 
    "requestId": 10, 
    "status": null
}
```
- DB 저장 결과
```
mysql> show databases;
+--------------------+
| Database           |
+--------------------+
| information_schema |
| mysql              |
| paymentdb          |
| performance_schema |
| sys                |
+--------------------+
5 rows in set (0.00 sec)

mysql> use paymentdb;
Reading table information for completion of table and column names
You can turn off this feature to get a quicker startup with -A

Database changed
mysql> show tables;
+---------------------+
| Tables_in_paymentdb |
+---------------------+
| Pay_table           |
| hibernate_sequence  |
+---------------------+
2 rows in set (0.00 sec)

mysql> select * from Pay_table;
+----+----------------------------+-------+-----------+--------+
| id | payDate                    | price | requestId | status |
+----+----------------------------+-------+-----------+--------+
|  1 | 2022-10-04 02:33:07.187000 |  NULL |        10 | NULL   |
+----+----------------------------+-------+-----------+--------+
1 row in set (0.00 sec)

mysql>  
```
## Self-healing(liveness probe)

1) livenessProbe 설정 (Request 서비스 대상)
   - [경로]/workspace/team2/Request/kubernetes/deployment.yaml
```
          livenessProbe:
            httpGet:
              path: '/actuator/health'
              port: 8080
            initialDelaySeconds: 120
            timeoutSeconds: 2
            periodSeconds: 5
            failureThreshold: 5  
```
2) livenessProbe 테스트
  - 2-1) Request 서비스 상태 확인
```
        gitpod /workspace/team2/Request (main) $ kubectl get all
        NAME                           READY   STATUS             RESTARTS   AGE
        pod/httpie                     1/1     Running            0          3h55m
        pod/my-kafka-0                 1/1     Running            1          4h5m
        pod/my-kafka-zookeeper-0       1/1     Running            0          4h5m
        pod/mysql                      1/1     Running            0          3h44m
        pod/payment-7dc8885b79-77ldf   1/1     Running            0          134m
        pod/request-8679c4fdf6-xhhtf   1/1     Running            0          61s
        pod/service-54f6955d57-8lsw8   0/1     ImagePullBackOff   0          3m56s
        pod/service-5c75dbcdc6-rnljc   1/1     Running            0          163m
        pod/siege                      1/1     Running            0          147m
        pod/stock-69dbb7c8b4-zqhms     1/1     Running            0          12m
        pod/view-858fd6bccb-7mckr      1/1     Running            0          163m
```

  2-2) Request 서비스 강제 다운 DOWN

```
        gitpod /workspace/team2/Request (main) $ kubectl exec httpie -it -- bash
        root@httpie:/# 
        root@httpie:/# 
        root@httpie:/# http put request:8080/actuator/down
        HTTP/1.1 200 
        Connection: keep-alive
        Content-Type: application/json
        Date: Tue, 04 Oct 2022 04:47:08 GMT
        Keep-Alive: timeout=60
        Transfer-Encoding: chunked

        {
            "status": "DOWN"
        }
```
  2-3) Request 서비스 재기동 확인 

     - pod/request-8679c4fdf6-xhhtf 서비스의 재기동 확인(RESTARTS: 1) 
```
        gitpod /workspace/team2/Request (main) $ kubectl get all
        NAME                           READY   STATUS             RESTARTS   AGE
        pod/httpie                     1/1     Running            0          3h57m
        pod/my-kafka-0                 1/1     Running            1          4h7m
        pod/my-kafka-zookeeper-0       1/1     Running            0          4h7m
        pod/mysql                      1/1     Running            0          3h46m
        pod/payment-7dc8885b79-77ldf   1/1     Running            0          136m
        pod/request-8679c4fdf6-xhhtf   0/1     Running            1          2m34s
        pod/service-54f6955d57-8lsw8   0/1     ImagePullBackOff   0          5m29s
        pod/service-5c75dbcdc6-rnljc   1/1     Running            0          165m
        pod/siege                      1/1     Running            0          148m
        pod/stock-69dbb7c8b4-zqhms     1/1     Running            0          14m
        pod/view-858fd6bccb-7mckr      1/1     Running            0          164m
```

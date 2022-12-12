# TACBAETICS

![TACBAETICS](https://user-images.githubusercontent.com/113872320/207004883-f000f66a-6340-48ca-a242-664b8d4dafc5.png)

## 프로젝트 기획
### 프로젝트 설명
> 💡 택배를 전략적(Tactics)으로 관리할 수 있는 배송 관리 시스템 **TACBAETICS** 입니다.
> <br/><br/>

### 설계 고려사항
<details>
<summary>📌 데이터 기준</summary>
<div markdown="1">
<br/>
 1. 택배 데이터 수 : 500만개
 
 - 2021년 택배 물동량 36억건
    - [2021년 택배물동량 36억개, 가격 인상도 145원에 달해](https://www.klnews.co.kr/news/articleView.html?idxno=304003)
    - [택배시장, 대한통운 압도적 1위](https://brunch.co.kr/@logibridge/247)
    
 - 2021년 국내 하루 택배물동량 
 	- 하루평균 약 천만건 
	- 그 중 가장 큰 점유율을 가지고 있는 CJ대한통운 점유율 약 41.9%, 
	- 따라서 대략 5,000,000 건의 물량을 커버할 수 있도록 테스트 및 설계
</div>
</details>

<details>
<summary>📌 Latency, Throughput 목표</summary>
<div markdown="1">
<br/>

 1. Latency 목표값 설정  
 
  ```
 📢 택배 업무의 실시간성으로 인해 택배기사는 1초이내 최대한 빠른 응답을 바랄것이며, 관리자도 단순조회는 1초이내, 소량 업데이트는 10초이내, 대량업데이트는 1분이내를 원할 것으로 예측한다.
  ```
  
   * 일반적인 경우 : 0.05~0.1초
   * 복잡한 트랜잭션이 필요한 경우 : 10초이내
  
 2. Throughput 목표값 설정
 
  ```
 📢 News1 자료(2021년 기준)를 통한 예측으로, DAU(Daily Active User, 하루 순수 이용자) 추이는 평균 약 2800 명이다.
  ```
  
   * DAU : 2800 (단위 : 명)
   * 안전계수 : 3
   * 1일 평균 접속 수에 대한 최대 피크 때 배율 : 2배<br/><br/>
   * 1명당 평균 접속 수 : 1회<br/>
   &nbsp; ⇒ 2,800(명) * 1(회) / 86,400(초) * 3(안전계수) * 2(1일 평균 접속 수에 대한 최대 피크 때 배율) = 약 185 rps

  	
</div>
</details>

<details>
<summary>📌 동시성 제어 기준</summary>
<br/>

 ```
 📢 1개의 물류센터 당 약 25,000건 시나리오, 따라서 25,000건당 관리자1명, 택배기사 65명의 사용자 예측
 ```
 
 1. 1초당 최대 동시 접속자 수 : 2800명 (5,000,000건 기준)
	
 2. 시간 당 처리량 : 가용성이 보장되는 범위의 최대치
 
 	* 앞선 Latency의 내용을 참고하여 택배기사는 가능한 빠른 응답을 원하고 있음
 
 
 
<div markdown="1">
</div>
</details> 

## 아키텍처
![tacbaeticsArchitect](https://user-images.githubusercontent.com/113872320/206975527-8d22c161-c81f-449f-83d7-8c676e68f1b1.png)

## 핵심 기능

### 🔍 검색(필터 및 정렬)

> * **Query DSL**을 통한 관리자용 택배의 각종 정보 및 배송상태 조회기능
> * 택배기사용 택배(자신에게 할당된) 정보 조회기능
> * 지역별 택배 현황 카운팅 기능으로 택배 배송율 모니터링 기능
> * 쿼리 성능 개선을 통해 모든 페이지에서 10초 이내로 응답(Latency 목표값 달성)
> * 커버링 인덱스 사용을 통한 쿼리 최적화 및 성능개선

### 📦 업데이트

> * 전체 업무 시작전 지역별 배송 담당자 배정 기능을 통한 벌크업데이트 기능
> * 운송장 검색을 통한 택배 상태 및 배송담당자 수정 기능
> * 택배기사의 실시간 배송완료처리 및 완료취소 기능
> * 성능 개선(bulk update 쿼리)

### 🧐 지역별 배송 담당자 배정 자동 추천 기능
> * 지역별 난이도 및 택배기사의 희망수량에 따라 배정표 자동 추천 기능

### 🐈‍⬛ Github Actions + Docker를 활용한 CI/CD
> * 배포 자동화를 통해 효율적인 협업 및 작업 환경 구축
> * 심플하고 접근이 쉬운 Github Actions로 결정

## 트러블슈팅

<details>
<summary>🏪 관리자용 검색 및 업데이트 성능 개선</summary>
<div markdown="1">

- **필요성**
    - 택배 데이터가 500만개 및 이에따라 동시사용자가 증가하면서 **응답시간이 증가**
    - 조회에서는 택배기사별 route당 갯수, 수정에서는 임시할당에 시간이 많이 소모되어 나머지 부분에서도 문제가 발생하는 것으로 판단된다.
    - 그러므로 2개의 api를 개선후 결과 확인 필요

  ⇒ 관리자의 입장에서 응답시간이 길다고 이탈하지는 않겠지만, 업무의 효율성이 떨어지게됨

  ⇒ 목표 : 페이지 로딩 시간 **5초 이내**

- **진행 단계**

### 문제

- 조회에서는 택배기사별 route당 갯수, 수정에서는 임시할당에 시간이 많이 소모되어 나머지 부분에서도 문제가 발생하는 것으로 판단된다.
- 그러므로 2개의 api를 개선후 결과 확인 필요

### 해결 시도

### 조회 기능 개선

- 택배기사 route count
1. 택배기사에 대한 조회와 이에 따른 count의 쿼리가 따로 분리되어 있음
2. for문안 if문과 리스트에 추가시키는 부분에 문제가 생기는 것으로 판단
    
    (프론트 처리하기 편하게 만든 결과값을 결국 프론트단에서 처리해야 불필요한 코드 제거됨)
    

⇒ 추가1 : for문에서 쿼리가 많이 나간다는 것 ⇒ user당 조회 count 쿼리로 해결해야됨

⇒ 추가2 : 한 컨트롤러당 하나의 역할을 해야 하는데 기능을 세분화 시키지 않았음

(Controller의 각 기능별 분리 필요 == 택배기사 테이블 조회(할당별), route count 조회로 나누기)

### 수정 기능 개선

- 지역별 배송 담당자 배정을 통한 전체 할당기능
1. for문이 사용되면서 쿼리가 10번 나가는 것은 개선방법이 없을것으로 판단, update 쿼리에서 사용되는 서브쿼리를 따로 조회 쿼리를 사용하는 방법으로 진행
	
분석 : 임시할당의 Throughput 5.1kb/sec 에서 5.4/sec로 약 5%상승 서브쿼리 제거후 임시할당의 평균과 최소, 최대가 줄어들어 조금더 안정적이 됨.

**그러나 아직 오류가 크게 줄어들지 않았다.**

1. hashmap을 사용 username을 종류별로 줄여서 update where 조건을 eq대신 in으로 대체
    
    (전체적인 update 쿼리문 줄이는 방법)
   
분석 : 임시할당의 Throughput 4.2kb/sec 에서 6.1kb/sec로 **약 31%** 상승, username 중복이 없는 경우는 적용전 결과와 차이가 없었지만 중복이 있는 경우는 성능이 개선됨
	
👇🏻 **더 자세한 내용이 알고싶다면?** 👇🏻

[관리자용 성능 테스트 및 개선](https://www.notion.so/fce84cd73e1d48f192c152ce39c11a9c)

</div>
</details>

<details>
<summary>🚛 택배기사용 검색 및 업데이트 성능 개선</summary>
<div markdown="1">

- **필요성**
    - 택배 데이터가 100만개, 500만개로 증가하면서 이에따라 시나리오대로 동시사용자 또한 증가하면서 **에러율 및 응답시간 증가** 

        ⇒ 택배기사의 업무 실시간성으로 생각할때, 빠른 응답시간을 보장하지 않는다면 업무에 많은 클레임이 발생할 것이라고 예측됨.

        ⇒ 목표 : 페이지 로딩 시간 **최소 5초 이내**

- **진행 단계**
    - 1. Covering Index 생성

	    - **적용 계기**
	
            ⇒ 기존 쿼리 실행 시 택배기사 상태별 조회 부분에서 많은 시간이 소요 됨

            ⇒  쿼리를 충족시키는 데 필요한 모든 데이터를 갖는 Covering 인덱스를 통해 `where, select, order by`를 인덱스 검색으로 빠르게 처리하고 걸러진 데이터를 통해서만 데이터 블록에 접근

    - 2. 카운트 쿼리 최적화

        - **적용 계기**

            ⇒ 기존의 쿼리에선 배송상태별 개수를 count할때 배송 상태별 쿼리를 한번식 두번보냄.

            ⇒ group by로 배송상태(state)를 묶어주고 배송상태별 개수를 select함으로써 한번의 쿼리로 해결.

        - **결과 분석**

            ⇒ 개선된 부분

            ⇒ 100만건의 조회에서 쿼리최적화를 한 이후 조회성능이 1.4초에서 0.5초로 **240%** 상승함.

            ⇒ 500만건의 조회에서는 조회성능이 7.6초에서 2.8초로 **270%** 상승함.
	
👇🏻 **더 자세한 내용이 알고싶다면?** 👇🏻

[택배기사용 성능 테스트 및 개선](https://www.notion.so/6dc8b9b16db8470782f8391606d1e5b7)

</div>
</details>

<details>
<summary>🧨 부하 테스트</summary>
<div markdown="1">

- **테스트 계기**
    - 하루 평균 최대 오백만건 가량되는 대량의 택배 데이터량과 이를 사용하는 택배기사의 동시접속자 수에 따른 부하 테스트를 하기위함 
    
- **병목 현상 확인**
    - 관리자의 수, 택배기사의 수를 데이터의 양과 함께 증가시킴으로써 테스트

      ⇒ 택배 조회 , 검색 페이지 로딩 시 RDS의 **CPU % 테스트필요**까지 상승 (RDS CPU 점유율 확인필요)

- **대안**
    1. 
- **결과**

- **결과분석**

👇🏻 **더 자세한 내용이 알고싶다면?** 👇🏻

[부하 테스트 및 개선](노션링크)

</div>
</details>

<details>
<summary>💬 로깅</summary>
<div markdown="1">

- **로깅 기능의 필요성 및 목표**
    - 애플리케이션 최적화를 위해서 **로직이 작동하는 시간**을 기록 및 측정
    - 로직의 검증을 위해서 사용자의 **요청 및 서버의 응답**을 기록
    - 기존에 작성된 로직에 영향을 끼치거나 로직의 변경이 있으면 안된다.
- **문제점**
    - 로그가 필요한 곳에 일일이 로그 로직을 작성해야 한다.
    - 중복된 로그 로직 때문에 유지보수 및 업데이트 비용이 발생한다.
- **문제 해결**
    - 로그 기능을 횡단 관심사(부가 기능)라고 판단 **AOP**를 사용하여 일관성 있는 로직을 구현

</div>
</details>

## 프로젝트 관리
<details>
<summary>지속적인 배포(CD)</summary>
<div markdown="1">

   * 지속적인 배포의 필요성
     * 기능이 추가될 때마다 배포해야하는 불편함이 있어 배포 자동화의 필요성 인식
   * 대안
   
     |Jenkins|Github Actions|
     |------|------|
     |무료|일정 사용량 이상 시 유료|
     |작업 또는 작업이 동기화되어 제품을 시장에 배포하는데 더 많은 시간이 소요|클라우드가 있으므로, 별도 설치 필요 없음|
     |계정 및 트리거를 기반으로하며 Github 이벤트를 준수하지 않는 빌드를 중심으로 함|모든 Github 이벤트에 대한 작업을 제공하고 다양한 언어와 프레임워크를 지원|
     |전 세계 많은 사람들이 이용해 문서가 다양|젠킨스에 비해 문서가 없음|
     |캐싱 메커니즘을 지원하기 위해 플러그인 사용 가능|캐싱이 필요한 경우 자체 캐싱 메커니즘을 작성해야함|
     
   * 선택
     * GitHub Actions 편의성 및 접근성이 좋다고 판단, 의견 수렴 후 선택.
	
</div>
</details>

<details>
<summary>Git</summary>
<div markdown="1">
<br/>

   * Git Commit 메시지 컨벤션의 필요성
     * commit된 코드가 어떤 내용을 작성 했는 지 파악하려면 commit을 확인해야 한다.
     * 프로젝트 진행 중에는 수 많은 코드가 commit되기 때문에 일일이 내용을 확인하기 힘들기 때문에 
메시지 컨벤션을 통해서 제목이나 description을 통해서 commit의 정보를 전달한다.
   * Git Commit 메시지 컨벤션 전략
   
   ```
   Feat : 내가 작업한 기능 구현 완료
   Fix : 버그 수정 및 기능 수정완료
   Build : 빌드 수정 완료
   Chore : 자잘한 수정 완료
   Ci : Ci 설정 수정완료
   Docs : 문서 수정에 대한 커밋
   Style : 코드 스타일 혹은 포맷 등에 관한 커밋
   Refactor : 코드 리팩토링에 대한 커밋
   Test : 테스트 코드 수정에 대한 커밋
   ```
   
 👇🏻더 자세한 내용이 알고싶다면?👇🏻<br/>
    &nbsp; 🚥 &nbsp; [Git](https://www.notion.so/Git-3d521c25cdc14f82b8892075a813288a)
</div>
</details>


## 설계
<details>
<summary>📘 DB 설계</summary>
<div markdown="1">
<br/>
	
![tacbaeticsErd](https://user-images.githubusercontent.com/113872320/206990749-c1b7e39b-0320-403d-939d-0a97fd815d24.png)
	
</div>
</details>

<details>
<summary>📝 API 설계</summary>
<div markdown="1">
<br/>
	
- [API 명세서](https://www.notion.so/API-f34d6a71a69846749050d51fd0f44bcf)
	
</div>
</details>

## 팀원

|이름|포지션|분담|@ Email|Github|
|------|------|------|------|------|
|권순한|BackEnd|프로젝트 매니징<br/> 시나리오 설계<br/>데이터 생성<br/>업데이트 기능|soonable@gmail.com|https://github.com/soonhankwon|
|이재선|BackEnd|검색(쿼리 최적화) <br/>택배기사용 조회기능<br/>택배기사용 업데이트<br/>부하 테스트|jason1208@naver.com|https://github.com/sun1203|
|최규범|BackEnd|회원가입<br/>로그인<br/>관리자용 조회기능<br/>관리자용 업데이트<br/>부하 테스트|rbqjachl95@google.com|https://github.com/GGuiGui|

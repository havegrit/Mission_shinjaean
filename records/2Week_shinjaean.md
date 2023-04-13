### 미션 요구사항 분석 & 체크리스트

---
**필수미션**
- 호감표시 할 때 예외처리 케이스 3가지 처리


  1. case 4: 한명의 인스타회원이 다른 인스타회원에게 중복으로 호감표시를 할 수 없습니다.

      예를들어 본인의 인스타ID가 aaaa, 상대의 인스타ID가 bbbb 라고 하자.
      
      aaaa 는 bbbb 에게 호감을 표시한다.(사유 : 외모)
      
      잠시 후 aaaa 는 bbbb 에게 다시 호감을 표시한다.(사유 : 외모)
      
      이 경우에는 처리되면 안된다.(rq.historyBack)


  2. case 5: 한명의 인스타회원이 11명 이상의 호감상대를 등록 할 수 없습니다.
    
      예를들어 본인의 인스타ID가 aaaa 라고 하자.
      
      aaaa 는 bbbb, cccc, dddd, eeee, ffff, gggg, hhhh, iiii, jjjj, kkkk 에 대해서 호감표시를 했다.(사유는 상관없음, aaaa는 현재까지 10명에게 호감표시를 했음)
      
      잠시 후 aaaa 는 llll 에게 호감표시를 한다.(사유는 상관없음)
      
      이 경우에는 처리되면 안된다.(rq.historyBack)

  3. case 6: "case 4" 가 발생했을 때 기존의 사유와 다른 사유로 호감을 표시하는 경우에는 성공으로 처리한다.
     
      예를들어 본인의 인스타ID가 aaaa, 상대의 인스타ID가 bbbb 라고 하자.
      
      aaaa 는 bbbb 에게 호감을 표시한다.(사유 : 외모)
      
      잠시 후 aaaa 는 bbbb 에게 다시 호감을 표시한다.(사유 : 성격)
      
      이 경우에는 새 호감상대로 등록되지 않고, 기존 호감표시에서 사유만 수정된다.
      
      외모 => 성격
      
      resultCode=S-2
      
      msg=bbbb 에 대한 호감사유를 외모에서 성격으로 변경합니다.

**선택미션**
- 네이버 로그인

  => 지난 주차 미션 제출 후 리팩토링 진행하면서 추가적으로 구현 완료

### N주차 미션 요약

---

**📌 중복 호감 표시 제한**

 => 현재 로그인한 사용자의 인스타 데이터를 이용하여 사용자의 호감 데이터를 전부 가져와 각 데이터들 중에 등록하고자 하는 이름이 호감 상대로 이미 등록되었는지 체크하고, 등록 되었다면 에러 메시지를, 아니라면 정상적인 등록 절차를 진행하도록 한다.

**[구현]**
- LikeablePersonController - 액션 메서드 add() 수정

     => 서비스의 findByFromInstaMemberId() 메소드를 통해 사용자가 호감 표시하여 생성된 LikeablePerson 데이터를 전부 가져와서

     => 모든 LikeablePerson 객체의 "toInstaMember.username"이 "addFrom.username"과 일치하는 데이터가 하나라도 존재하면

     => 에러 메시지를 포함한 RsData를 인자로 가지는 Rq.historyBack() 메소드를 실행하여 실패 메시지와 함께 이전 페이지로 돌아가도록 구현했다.


- Test Case 생성

    => 이미 존재하는 호감 상대(insta_user4) 추가 시도. user3에게는 호감 표시한 상대가 2명(insta_user4, insta_user100) 존재한다. 

    => 위 시뮬레이션 결과, 400 상태 코드(= 호감 상대 추가 실패)를 반환하는지 검증하고, 실제로 데이터의 개수가 기존 2개가 맞는지 검증하도록 구현했다.

**📌 [11명 이상 호감 표시 제한]**

 => 현재 로그인한 사용자의 인스타 데이터를 이용하여 사용자의 호감 데이터를 전부 가져와 List의 크기가 10이상이면, 에러 메시지를, 아니라면 정상 절차를 진행하도록 한다.

**[구현]**
- LikeablePersonController - 액션 메서드 add() 수정

    => 서비스의 findByFromInstaMemberId() 메소드를 통해 사용자가 호감 표시하여 생성된 LikeablePerson 데이터를 전부 가져와 List의 사이즈가 10이상 인지 체크한다.

    => List의 사이즈가 10이상 이면, 실패 메시지를 포함한 RsData를 인자로 가지는 Rq.historyBack() 메소드를 실행하여 실패 메시지와 함게 이전 페이지로 돌아가도록 구현


- Test Case 생성

    => 더미 데이터 8개 생성. user3의 기존 2개의 데이터와 합하여 총 10개의 데이터가 존재하게 된다.

    => 11번째 호감 데이터 생성 시도.

    => 위 시뮬레이션 결과, 400 상태 코드를 반환(데이터 생성 불가능)하는지 검증하고, 실제로 데이터가 10개에서 증가되지 않았는지 검증하도록 테스트를 구현했다.

**[리팩토링]**

 =>  사용자의 모든 호감 데이터를 가져온 리스트의 사이즈를 비교하는 부분에서 하드 코딩된 부분을 수정 했다.

- application.yml 파일에 프로그램 내부에서 사용될 변하지 않는 변수를 정의

- AppConfig class 생성

    => @Configuration 어노테이션을 작성. likeablePersonFromMax 라는 필드와 해당 필드의 값을 초기화 해줄 setLikeablePersonFromMax() 메소드를 생성

    => setLikeablePersonFromMax() 메소드에 @Value 어노테이션을 사용하여 application.yml 파일 속에 정의된 사용자 지정 변수(custom.likeablePerson.from.max = 10)를 해당 메소드에서 사용할 수 있다. 이 메소드에서는 세터 함수의 인자로 해당 데이터를 주입하게 된다.

- 적용

    => controller class에서 하드 코딩된 부분을 AppConfig.likeablerPersonFromMax 으로 교체. 필드를 static 선언했기 때문에 인스턴스화 없이 사용이 가능하다.


**📌 [호감 사유 변경]**

 => 중복 호감 표시가 발생했을 때, 호감 사유가 기존과 다르다면 바뀐 사유로 변경 작업을 진행한다.

**[구현]**

- LikeablePersonController - 액션 메서드 add() 수정

    => 기존에 등록된 호감 표시 횟수가 10이상인지 먼저 체크했었는데, 중복을 먼저 체크하도록 코드의 순서를 변경했다. 

    => 기존에 중복 호감 표시를 처리하는 과정 안에 기존 호감 표시의 호감 사유가 바뀌었는지 체크하고, 바뀌었다면 수정하도록 구현했다.


- LikeablePersonService - modifyAttractionTypeCode() 메소드 생성

    => ⚠️ 주의할 것. 메소드에 @Transactional 어노테이션 작성해줘야 정상적으로 DB에 내용이 반영됨

    => 호감 이유(attractiveTypeCode)와 수정 날짜(modifyDate)를 세터를 통해 변경된 데이터로 세팅

    => repository.save(entity) 세이브 메소드를 통해 변경된 객체 데이터를 다시 저장하도록 했다.

    => 마지막으로 성공 결과 코드와 성공 메시지를 포함한 RsData 객체를 반환한다.


-  LikeablePersonService - attractionTypeCodeToString() 메소드 생성

    => 성공 메시지에는 "이전 attractionTypeCode"와 "변경된 attractionTypeCode"의 숫자 코드가 아닌 그와 상응하는 문자열(1=외모, 2=성격, 3=능력)이 필요하다.

    => 타입 코드를 인자로 넘기면 그와 상응하는 문자열을 반환하도록 구현했다.  


- LikeablePerson - Setter 설정

    => modifyDate, attractiveTypeCode 두 필드에 대해서 변경된 값을 세팅하기 위해 세터가 필요. @Setter 어노테이션을 작성했다.


- LikeablePersonServiceTests - modifyAttractionTypeCode() 메소드 테스트 생성

    => 수정 기능이 제대로 작동하는지 검증하기 위한 테스트를 진행

    => LikeablePerson 엔티티 중에서 아이디(기본키)가 1인 데이터를 가져와 modifyAttractionTypeCode() 메소드를 통해 attractionTypeCode를 1에서 2로 수정하고, 데이터가 2로 잘 변경되었는지 검증하도록 구현했다. 

**[리팩토링]**

 => 이전에 사용자의 모든 호감 표시 데이터를 가져온 리스트에서 toInstaMember의 username(변경하려는 인스타 멤버의 이름)이 일치하는 LikeablePerson 객체를 가져와서 수정을 진행했는데, 강사님 힌트를 듣고 리팩토링을 진행했다.

- findByFromInstaMemberIdAndToInstaMember_username()

    => 위 메소드는 fromInstaMemberId와 toInstaMember.username 두 가지 조건을 모두 만족하는 데이터를 추출하는 메소드이다.

    => repository class에 명명 컨벤션(findBy[A]And[B])이 잘 지켜진 메소드 명을 작성

    => service class에도 repository class에 id와 username을 전달하는 메소드를 작성

    => controller class에서는 service class로 현재 사용자 인스타 아이디(primary key)와 addForm에서 작성한 호감 상대 인스타 아이디(username)를 전달하도록 구현

### 미션 제출 이후

---

**[피어리뷰 피드백]**

=> 전체적으로 기존에 강사님이 작성했던 코드에 대한 이해도가 떨어져 쓸 데 없는 코드 작성이 많았다.

- LikeablePerson class - modifyDate 불필요한 Setter 

    => modifyDate는 엔티티가 수정된 날짜를 저장하는 필드이다. 

    => 해당 필드에는 이미 @LastModifiedDate 어노테이션이 작성되어 있어서 추가적으로 세터의 작성이 필요없다.

    => @Setter 어노테이션 삭제 필요. 어노테이션으로 자동으로 관리되기 때문에 서비스 클래스에서 세팅하는 작업도 삭제가 필요하다.

- LikeablePerson class - getAttractiveTypeDisplayName()

    => getAttractiveTypeDisplayName() 메소드는 액션 타입 코드를 각 타입에 맞는 문자열로 변환해주는 기능을 가지고 있다.

    => 기존에 이런 메소드가 작성되어 있는지 확인하지 못 했다. 그래서 서비스 클래스에서 정확히 똑같이 동작하는 메소드를 만들어 사용했다.

    => 이미 구현되어 있는 기능을 사용. 불필요하게 작성했던 메소드 삭제 필요. 그에 따른 약간의 서비스 코드 수정이 필요하겠다.

- 네이버 로그인 - 회원 이름 간단하게(response에서 id키의 값만 파싱) 출력하기

    => 이번 둘째주 미션 진행하면서 구현하려 했는데, 깜빡하고 있었다.

    => 리팩토링 진행하면서 구현 예정.

**[리팩토링]**

-[x] modifyDate 필드 세터 관련
    
    => 필드에 @Setter 어노테이션 삭제

    => 서비스 클래스에 modifyDate 세팅하는 코드 삭제


-[x] service class - 불필요한 메서드 삭제

    => 중복되는 기능의 메소드는 삭제하고, 기존에 LikeablePerson class에 작성되어 있던 getAttractiveTypeDisplayName() 메소드를 이용하는 방식으로 코드를 수정


-[x] InstaMember.fromLikeablePeople 필드

    => 컨트롤러 클래스에서 사용자의 모든 호감 데이터를 가져올 때, findByFromInstaMemberId() 메소드를 통해 가져왔던 것을 InstaMember 객체의 fromLikeablePeople 필드 데이터를 가져오는 것으로 대체


-[ ] 네이버 로그인 시, response 데이터에서 id키 값만 추출하여 username 으로 만들 

-[ ] 이미 등록한 호감 상대의 호감 사유를 변경할 때, 확인하는 작업을 진행하면 좋을 것 같다.
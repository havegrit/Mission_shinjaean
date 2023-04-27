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

   aaaa 는 bbbb, cccc, dddd, eeee, ffff, gggg, hhhh, iiii, jjjj, kkkk 에 대해서 호감표시를 했다.(사유는 상관없음, aaaa는 현재까지 10명에게 호감표시를
   했음)

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

=> 현재 로그인한 사용자의 인스타 데이터를 이용하여 사용자의 호감 데이터를 전부 가져와 각 데이터들 중에 등록하고자 하는 이름이 호감 상대로 이미 등록되었는지 체크하고, 등록 되었다면 에러 메시지를, 아니라면
정상적인 등록 절차를 진행하도록 한다.

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

  => setLikeablePersonFromMax() 메소드에 @Value 어노테이션을 사용하여 application.yml 파일 속에 정의된 사용자 지정 변수(
  custom.likeablePerson.from.max = 10)를 해당 메소드에서 사용할 수 있다. 이 메소드에서는 세터 함수의 인자로 해당 데이터를 주입하게 된다.

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


- LikeablePersonService - attractionTypeCodeToString() 메소드 생성

  => 성공 메시지에는 "이전 attractionTypeCode"와 "변경된 attractionTypeCode"의 숫자 코드가 아닌 그와 상응하는 문자열(1=외모, 2=성격, 3=능력)이 필요하다.

  => 타입 코드를 인자로 넘기면 그와 상응하는 문자열을 반환하도록 구현했다.


- LikeablePerson - Setter 설정

  => modifyDate, attractiveTypeCode 두 필드에 대해서 변경된 값을 세팅하기 위해 세터가 필요. @Setter 어노테이션을 작성했다.


- LikeablePersonServiceTests - modifyAttractionTypeCode() 메소드 테스트 생성

  => 수정 기능이 제대로 작동하는지 검증하기 위한 테스트를 진행

  => LikeablePerson 엔티티 중에서 아이디(기본키)가 1인 데이터를 가져와 modifyAttractionTypeCode() 메소드를 통해 attractionTypeCode를 1에서 2로 수정하고,
  데이터가 2로 잘 변경되었는지 검증하도록 구현했다.

**[리팩토링]**

=> 이전에 사용자의 모든 호감 표시 데이터를 가져온 리스트에서 toInstaMember의 username(변경하려는 인스타 멤버의 이름)이 일치하는 LikeablePerson 객체를 가져와서 수정을 진행했는데,
강사님 힌트를 듣고 리팩토링을 진행했다.

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

 => 컨트롤러 클래스에서 사용자의 모든 호감 데이터를 가져올 때, findByFromInstaMemberId() 메소드를 통해 가져왔던 것을 InstaMember 객체의 fromLikeablePeople 필드
 데이터를 가져오는 것으로 대체


-[x] application.yml 대문자 'L'을 소문자 'l'로 변경

 => Caused by: java.lang.IllegalArgumentException: Could not resolve placeholder 'custom.likeablePerson.from.max' in
 value "${custom.likeablePerson.from.max}"

 => 에러 발견. placeholder를 해결할 수 없다는 말 인데, application.yml 파일에 작성한 내용과 placeholder가 대소문자까지 완전 동일해야함.

 => custom.'L'ikeablePerson.from.max: 10

 => custom.'l'ikeablePerson.from.max: 10


-[x] 네이버 로그인 시, response 데이터에서 id키 값만 추출하여 username 으로 만들기

 => 네이버 OAuth API를 통해 전달 받는 데이터(프로필) 중 id 데이터를 username의 일부로 사용하도록 구현

 => id 데이터는 OAuth2User 객체를 통해 전달받는 데이터(Name, Granted Authorities, User Attributes) 중 User Attributes의 response 키와 맵핑되어
 저장되어 있는데, OAuth2User 클래스 getAttributes(String name) 메소드를 통해 User Attributes의 name(여기서는 response)키와 맵핑된 데이터(프로필 데이터)를
 가져온다.

 => 그렇게 가져온 LinkedHashMap 타입의 response 데이터 안에는 또 프로필 데이터(id, email, name)가 존재한다. 데이터를 가져오기 위해서 LinkedHashMap<String,
 String> 타입 변수에 저장한다.
   ````
   ❓ 한 가지 의문이 있는데, 
   "oAuth2User.getAttribute("response").getClass() = class java.util.LinkedHashMap"
   getAttribute() 메소드를 통해 가져온 response는 LinkedHashMap 타입의 데이터는 맞지만, LinkedHashMap<String, String>으로 형변환이 되어야 key 데이터로 value 데이터를 가져올 수 있다.
   ❗ 해답
   LinkedHashMap은 제네릭스를 사용하여, 키(key)와 값(value)의 타입이 구체적으로 명시되지 않으면 LinkedHashMap<Object, Object> 타입으로 취급된다. 그렇기 때문에 LinkedHashMap<String, String> 타입으로의 형변환이 필요로 하다.
   ````
 => id 데이터를 가져오기 위해 get(key) 메소드를 사용했다.


-[x] 하드 코딩 수정

 => RsData에 포함되는 에러 메시지에도 하드 코딩된 부분을 수정했다.


-[x] LikeablePersonService class - canActorDelete() 메소드 추가

 => 기존에 서비스의 delete() 메소드에서 데이터를 삭제할 수 있는지 검증하는 부분을 canActorDelete() 메소드로 따로 분리했다.

 => canActorDelete() 메소드는 검증을 거쳐 삭제할 수 있다면, 성공 메시지와 likeablePerson 객체를 담은 RsData를 반환하도록 했다.

 => 이에 따라 서비스의 delete() 메소드 및 컨틀롤러 단의 수정도 진행되었다.

 => 우선 서비스의 delete() 메소드는 데이터를 삭제하는 기능만을 남겨놓고, 성공 메시지를 담은 RsData를 반환하도록 했다.

 => 컨트롤러는 사용자의 InstaMember, 삭제하려는 LikeablePerson 객체의 id 데이터를 서비스의 canActorDelete() 메소드로 보내, 우선 삭제가 가능한지 검증을 거치도록 한 후,
 문제가 없다면 서비스의 delete() 메소드로 likeablePerson 객체를 전달하여 삭제하는 과정을 진행하고, 최종적으로 성공 메시지를 출력하고 "/likeablePerson/list" 으로 리디렉션 된다.


-[x] LikeablePerson 데이터가 삭제될 때, fromInstaMember/toInstaMember 객체의 fromLikeablePeople/toLikeablePeople 데이터도 삭제

 => 서비스의 delete() 메소드가 실행되어 repository에서 삭제 작업을 진행하기 전에 fromInstaMember/toInstaMember 객체의
 fromLikeablePeople/toLikeablePeople 데이터를 삭제하도록 구현했다.


-[ ] 이미 등록한 호감 상대의 호감 사유를 변경할 때, 확인하는 작업을 진행하면 좋을 것 같다.

-[ ] UI 적용시켜 보기

-[x] 호감 표시 등록 후 3시간이 지나야 수정/삭제가 가능하도록 구현

    => Duration::between 메소드를 통해서 현재 시간과 수정하려는 데이터의 modifyDate 간의 차이를 구한 뒤, 그 차이가 3시간 미만이라면 에러 메시지를 출력하도록 구현했다.

**[트러블슈팅]**

-[x] LikeablePersonControllerTests 에러 발생

 => 중복 호감 표시 테스트에서 4xx 상태코드가 반환되어야 하는데, 3xx 상태코드가 반환됨. 즉, 리디렉션 된다.

 => 웹 브라우저에서는 정상적으로 잘 작동되는데, 테스트 코드가 문제가 있는 것 같다.

 => 원인은 테스트 하려는 데이터의 호감 코드가 원래 1인데, 사유를 1로 변경하는 테스트가 아니라, 2로 변경하는 테스트를 진행하고 있었고, 결과 성공적으로 사유가 변경되고 리디렉션 되었다.


-[x] 본인을 호감 상대로 등록하려고 할 때는 폼에 작성한 인스타 아이디가 안사라지는데, 중복된 호감 표시를 할 때는 폼에 작성한 아이디가 사라진다.

 => 원인 파악: 본인을 호감 상대로 등록하려고 할 때에는, add.html 에서 폼이 발송(submit)되기 전에 경고 메시지를 출력하고 종료하기 때문에 폼 양식이 유지 되었던 것. 후자의 경우는 백엔드 단에서
 검증을 진행해야 하므로 폼이 발송되어야 하고, 폼이 발송되고 나면 백엔드 단에서 검증울 거치고, redirection 되거나 historyBack 되기 때문에 기존 작성 내용이 초기화 된다.

 => "/likeable/add"에서 폼 발송이 발생할 때(POST), addForm의 데이터를 기억했다가, "likeable/add" GET 요청이 들어올 때, 이 데이터를 전달해줘야 한다.

 => 해결: HttpSession을 사용. 폼이 발송되어 POST 요청이 발생할 때, HttpSession::setAttribute() 메소드를 사용해서 addForm의 username 데이터를 세션에 저장.
 GET 요청이 들어올 때, HttpSession::getAttribute() 메소드를 사용해 세션에 저장한 데이터를 가져올 수 있다.
 
-[x] 로그인 한 상태에서 개발을 진행한 후, 페이지가 새로고침되면 세션을 삭제해야만 웹페이지가 정상 작동한다.

    => 원인은 잘 모르겠지만, DB 갈아엎고, 프로젝트 다시 실행해줬더니 문제없이 작동한다.

-[x] 로그아웃 시, 등록했던 본인의 인스타 정보가 삭제된다.

    => 원인을 추측해보자면, 프로젝트 내용이 변경되면, 자동으로 다시 빌드가 되고, 프로젝트를 다시 실행하는 것과 같아진다. 프로젝트가 실행될 때, application.yml 파일에 jpa.hibernate.ddl-auto: create 로 설정해놨기 때문에 DB가 새로 생성되고, 이전에 있던 DB 내용이 전부 삭제되는 것 때문에 그런 상황이 발생하는 것 같다.

### 미션 요구사항 분석 & 체크리스트

---

**[기능 추가]**

☑️ 호감 표시 취소

- "호감목록" 페이지에서 삭제 버튼을 누르면, 삭제를 확인하는 팝업창이 나타나고, 확인을 누르면 호감을 표시했던 상대를 목록에서 지움
  
    => 인증되지 않은 사용자 혹은 다른 사용자가 삭제할 수 없도록 구현할 것

☑️ Google Login
- 구글 계정으로 회원가입 및 로그인 가능

    => OAuth 2.0 사용

### 1주차 미션 요약

---

**[접근 방법]**

**- 호감 표시 취소**

호감을 표시하면 'LikeablePerson' 객체를 생성하고, 'LikeablePersonRepository'를 통해 DB에 데이터를 저장하게 된다.
호감 표시를 취소한다는 것은 이전에 생성되어 DB에 저장된 LikeablePerson 객체 데이터를 LikeablePersonRepository를 통해 DB에서 데이터를 삭제하는 것이다.
LikeablePersonService에서 LikeablePersonRepository를 통해 DB의 데이터를 삭제하는 메서드를 구현하고, LikeablePersonController에서는 현재 로그인 되어있는 사용자 데이터를 가져와 삭제하려는 LikeablePerson 데이터의 주인이 맞는지 검증하는 과정을 구현해야 할 것이다. 

[구현]
- Controller - 액션 메서드 작성

    => @PreAuthorize("isAuthenticated()") 어노테이션을 통해 로그인 하지 않은 사용자는 해당 서비스를 사용하지 못 하도록 설정

    => Rq 객체를 통해 로그인한 멤버의 instaMember 객체를 가져온 뒤, null이 아닌 경우 즉, 인스타 아이디를 등록한 경우에만 삭제 과정을 진핼할 수 있도록 구현

    => 사용자 검증은 필요하지 않다고 판단했다. 이유는 Rq로 전달받은 현재 세션에 로그인한 사용자 정보를 바탕으로 LikeablePerson 데이터를 가져오기 때문에, 이미 검증이 되었다고 판단, 본인의 데이터가 아니면 어떤 데이터도 삭제되지 않을 것이다.

    => 사용자가 임의로 URL을 조작하여, 다른 사용자의 데이터를 삭제하려 할 때, 잘못된 접근이라는 토스트 메세지를 출력되도록 구현했다. 


- Service - 데이터를 삭제하는 메서드 작성

  => 기본적으로 LikeablePersonService는 @Transactional(readOnly = true) 어노테이션을 사용하기 때문에, 삭제 메서드에 @Transactional 어노테이션을 사용

  => 서비스의 삭제 메소드는 성공 메세지와 LikeablePerson 객체 데이터를 가지고 있는 RsData<LikeablePerson> 객체를 반환하여, 성공적으로 데이터를 삭제했을 시, 토스트 메세지를 사용자에게 보여주도록 구현했다. 

**- Google login**

카카오 로그인과 동일하게 개발을 진행하면 될 것으로 생각된다. 구현에 앞서 관련 영상 세 개를 시청하며 'OAuth 2.0'에 대해 간단하게 파악한 후 진행하였다.

1. WEB2 - OAuth 2.0 : 1.수업소개 (https://youtu.be/hm2r6LtUbk8)

2. WEB2 - OAuth 2.0 : 2. 역할 (https://youtu.be/vo_0PW3V5zU)

3. WEB2 - OAuth 2.0 : 3. 등록 (https://youtu.be/_mm5ks5aWQ4)

application.yml 파일 설정하는 방법은 chatGPT를 활용

Q. Spring Boot에서 Google OAuth API를 사용하기위한 application.yml 파일 구성

**[구현]**

- Google Cloud Platform 
  - 새 프로젝트 생성
  - 사용자 인증 정보(OAuth client ID) 생성
  
    => redirection uri 설정하기
  
- application.yml 파일 설정

  => 우리의 서비스에서 Google 계정으로 로그인 하기 위한 설정 작업을 진행

  => Google OAuth Client ID, Secret 사용

  => client-authentication-method 설정은 따로 하지 않는다. 아마 디폴트 설정을 따라가면 되는듯 하다.

  => 카카오와는 다르게 구글은 scope 설정을 지정해줘야 정상적으로 작동하기 때문에 'scope'를 'profile'로 지정해주었다.

  => 최종적으로 'client id', 'client secret', 'scope'만 작성해줘도 잘 작동하는 것으로 보인다. provider 설정도 작성하지 않아도 된다.


**[특이사항]**

이슈 발생
- LikeablePersonService

  => service를 통해 repository.delete() 메소드를 실행하도록 구현했다. 프로그램이 실행은 되나 delete 쿼리가 실행이 안되는 문제 발생

  => service 클래스에 @Transactional(readOnly = true) 어노테이션이 사용되었으므로, 쓰기 작업(likeablePerson 객체 삭제)을 수행하는 메서드에 @Transactional(readOnly=false) 어노테이션을 추가해줘야 한다. "readOnly" 속성은 "false"가 디폴트 값이므로 생략가능

- 문제없이 잘 실행 되다가, OS 업데이트 후 실행이 되지 않는다.

  => 정확한 원인을 파악하지는 못 했지만, DB Driver를 mariaDB에서 MySQL로 변경해주니 다시 문제없이 실행된다. 

- 구글 계정으로 회원가입 실패

  => "/member/login" 페이지에서 '구글 로그인하기' 누르면 구글 계정으로 로그인하는 페이지가 나타나긴 하지만 실제 구글 로그인 후 리디렉션 되어도 회원이 생성되지 않는다.

  => 병순 멘토님 찬스로 google OAuth의 client-authentication-method가 post 방식이 맞는지 생각해보라는 말씀에, 해당 설정을 지우고 실행해봤더니, 확실히 결과는 달라졌다. 그래도 아직 회원은 생성되지 않는다. 더 나아가 리디렉션 된 후에 root 페이지 말고는 작동하지 않는다.

  => 세션을 삭제 해주면, 다시 작동은 하게 된다. 

  => 구글 로그인 후 "/instaMember/connect" 페이지 요청하면 응답이 잘 나오는거 보니, 로그인 되어 인증에는 성공한 거 같은데, 인증만 된 유령 회원이 된다. 아무런 정보도 등록되지 않았고, 인스타 아이디 등록도 되지 않는다.

  => 카카오 로그인 할 때는  'CustomOAuth2UserService'에 오버라이딩 된 'loadUser()' 메소드가 잘 실행되지만, 구글 로그인 시에는 실행이 안되는 것으로 보인다.

  => 또 병순 멘토님 찬스로 application.yml 파일에서 google client 등록 설정 중 scope를 설정하지 않아서 문제가 발생한 것을 알게되었다. "spring.security.oauth2.client.registration.google.scope"를 "profile"로 지정해주어 해결.

  => 추가적으로 카카오는 스코프를 지정하지 않아도 잘 처리해준다. 이거 때문에 구글에도 스코프를 지정하지 않았던 것이 실수였다. 멘토님께서 말씀하시기로 플랫폼 마다 스코프 설정이 조금씩 다르기 때문에 공식문서를 잘 살펴봐야 한다고 말씀해주셨다.


**[리팩토링]**

호감 표시 취소
- 컨트롤러 - 액션 메서드 수정

  => 피어 리뷰에서 for 문 사용하지말고 Service에서 getLikeablePerson 메소드를 만들어서 likeablePerson을 가져오는 방법을 사용해보라는 의견이 있었다. 거기에서 착안하여 메소드 수정을 진행하였다.
기존 코드의 경우 필요없는 데이터를 가져온다는 것이 문제였다. 현재 세션에 로그인한 사용자의 호감 표시에 대한 모든 데이터를 가져와서 id와 비교후 삭제를 진행했다.
개선된 코드는 불필요하게 데이터를 가져오지 않고, 필요한 데이터만 가져와 검증을 거쳐 삭제하도록 구현했다.
검증은 가져온 LikeablePerson 객체의 fromInstaMember 데이터와 현재 세션 사용자의 InstaMember 데이터를 비교하는 것으로 구현했다. 인스타 아이디를 등록하지 않은 사용자 또는 자신의 데이터가 아닌 사용자가 데이터를 삭제하려는 경우 "잘못된 접근입니다."라는 메시지를 출력한다.
<br><br>
  => 코드를 전체적으로 살펴보면서 서비스를 통해 받은 LikeablePerson 객체를 다시 컨트롤러에서 서비스에게 전달하는 흐름이 어색하다고 판한하여 전면적인 수정을 진행했다.
<br><br>
  => 컨트롤러에서는 현재 세션에 로그인한 사용자가 인스타 아이디를 등록했는지 검증하도록 했고, 삭제에 실패했다면, historyBack을 성공했다면 리디렉션과 함께 성공 메시지를 반환하도록 했다. 데이터의 존재 유무, 사용자에게 삭제 권한이 있는지 판단하는 것은 서비스에게 넘겼다. 
  <br><br>
  => Get 요청은 csrf 공격에 취약하다. 그래서 호감 삭제 요청 시, 기존에 GET 방식으로 요청하던 것을 POST 방식으로 요청하도록 "list.html" 파일을 수정했다. 'a'태그는 버튼 클릭 시, get 방식으로 url을 요청한다. 그래서 POST 방식으로 요청하도록 설정ㅇㄹ 'form'태그를 추가했고, 'a'태그를 클릭하면, 'form' 태그가 submit 되도록 구현했다.

  => 다음으로 액션 메서드 매핑 어노테이션을 "@DeleteMapping" 어노테이션으로 변경했다. 기존 맵핑 URL이 "/likeablePerson/delete/{id}"에서 "/likeablePerson/{id}"로 줄어들었다. 
  
  => @DeleteMapping 어노테이션을 사용하기 위해서, appication.yml 파일에 "spring.mvc.hidden-method.filter.enabled: true" 속성을 추가했다.

OAuth 로그인
- application.yml 파일 수정

  => 기존에 클라이언트 아이디와 시크릿 코드가 노출되어 있었던 방식에서 환경 변수를 이용하는 방식으로 수정했다.
      Run > Edit Configurations > Modify options > Add Run Options 다이얼로그에서 Operating Systems 탭에 Environment variables 를 클릭해주면, 환경 변수를 추가할 수 있는 메뉴가 생성된다.
  <br><br>
  => 네이버 로그인도 가능하도록 registration 과 provider 설정을 추가했다.

이슈 사항

- 네이버 로그인 실패

  => registration 설정이 잘못 이었는지, 인증 화면으로는 넘어가는데, 리디렉션 되어 넘어오면 계속 에러를 발생시켰다.
  
  => 더 정확한 원인을 파악하기 위해 "application.yml" 파일에 "org.springframework.security: trace" 설정을 추가하여, 시큐리티 관련 자세한 로그를 확인하고자 했다.
로그가 엄청 길었지만, 마지막 부분에 authorization-grant-type 속성의 데이터가 null일 수 없다는 메시지를 확인했고, 설정 파일을 살펴봤더니, authorization-grant-type 속성이 지정이 되어있지 않았다.
authorization-grant-type 속성을 authorization_code 로 지정해주니 네이버 로그인이 정상적으로 작동하기 시작했다.


- 호감 표시 취소 실패 메시지 출력

  => 데이터 삭제에 실패했을 때, 메시지가 출력되지 않는다. 원인이 뭘까 ?

**[미션 제출 이후 추가 사항]**
- Rq.historyBack()

  => 메소드 자체가 클라이언트의 요청에 문제가 있을 때, 이전 페이지로 보내는 기능이기 때문에 400 상태 코드를 반환하는 것이 맞다. 메소드 실행 시 기존에는 200 상태 코드를 반환했지만, 400 상태 코드를 반환하도록 변경했다.

  => 이와 관련해서 테스트 케이스를 추가했다. 존재하지 않는 데이터를 삭제했을 때, 정상적으로 400 상태 코드를 반환하는지, 권한이 없는 데이터를 삭제 했을 때, 400 상태 코드를 반환하고, 실제로 데이터가 삭제되지 않는지 테스트를 진행했다.
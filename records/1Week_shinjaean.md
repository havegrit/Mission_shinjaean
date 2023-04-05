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



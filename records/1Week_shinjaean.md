### 미션 요구사항 분석 & 체크리스트

---

**[기능 추가]**

☑️ 호감 표시 취소

- "호감목록" 페이지에서 삭제 버튼을 누르면, 삭제를 확인하는 팝업창이 나타나고, 확인을 누르면 호감을 표시했던 상대를 목록에서 지움
  
    => 인증되지 않은 사용자 혹은 다른 사용자가 삭제할 수 없도록 구현할 것

☑️ Google Login
- 구글 계정으로 회원가입 및 로그인 가능

    => OAuth 사용

### 1주차 미션 요약

---

**[접근 방법]**

호감을 표시하면 'LikeablePerson' 객체를 생성하고, 'LikeablePersonRepository'를 통해 DB에 데이터를 저장하게 된다.
호감 표시를 취소한다는 것은 이전에 생성되어 DB에 저장된 LikeablePerson 객체 데이터를 LikeablePersonRepository를 통해 DB에서 데이터를 삭제하는 것이다.
LikeablePersonService에서 LikeablePersonRepository를 통해 DB의 데이터를 삭제하는 메서드를 구현하고, LikeablePersonController에서는 현재 로그인 되어있는 사용자 데이터를 가져와 삭제하려는 LikeablePerson 데이터의 주인이 맞는지 검증하는 과정을 구현해야 할 것이다. 

[구현]
- Controller - 액션 메서드 작성

    => @PreAuthorize("isAuthenticated()") 어노테이션을 통해 로그인 하지 않은 사용자는 해당 서비스를 사용하지 못 하도록 설정

    => Rq 객체를 통해 로그인한 멤버의 instaMember 객체를 가져온 뒤, null이 아닌 경우 즉, 인스타 아이디를 등록한 경우에만 삭제 과정을 진핼할 수 있도록 구현

    => 사용자 검증은 필요하지 않다고 판단했다. 이유는 Rq로 전달받은 현재 세션에 로그인한 사용자 정보를 바탕으로 LikeablePerson 데이터를 가져오기 때문에, 본인의 데이터가 아니면 어떤 데이터도 삭제되지 않을 것이다.

    => 그렇다면 사용자에게도 잘못된 접근이라는 걸 안내해줄 필요가 있다고 판단


**[특이사항]**

트러블슈팅
- LikeablePersonService

  => service를 통해 repository.delete() 메소드를 실행하도록 구현했다. 프로그램이 실행은 되나 delete 쿼리가 실행이 안되는 문제 발생

  => service 클래스에 @Transactional(readOnly = true) 어노테이션이 사용되었으므로, 쓰기 작업(likeablePerson 객체 삭제)을 수행하는 메서드에 @Transactional(readOnly=false) 어노테이션을 추가해줘야 한다. "readOnly" 속성은 "false"가 디폴트 값이므로 생략가능

구현 과정에서 아쉬웠던 점 / 궁금했던 점을 정리합니다.

- 추후 리팩토링 시, 어떤 부분을 추가적으로 진행하고 싶은지에 대해 구체적으로 작성해주시기 바랍니다.

  **참고: [Refactoring]**

    - 사용자가 임의로 url을 작성하여, 다른 사용자의 데이터를 삭제하려고 할 때, 안내 메시지를 출력
    - 1차 리팩토링은 기능 개발을 종료한 후, 스스로 코드를 다시 천천히 읽어보면서 진행합니다.
    - 2차 리팩토링은 피어리뷰를 통해 전달받은 다양한 의견과 피드백을 조율하여 진행합니다.
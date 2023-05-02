### 미션 요구사항 분석 & 체크리스트

---

-[ ] 네이버클라우드플랫폼을 통한 배포(도메인 없이, IP로 접속)

-[x] 호감표시/호감사유변경 후, 개별 호감표시건에 대해서, 3시간 동안은 호감취소와 호감사유변경을 할 수 없도록 작업

### N주차 미션 요약

---

**[접근 방법]**
- **재사용 대기 시간**
<br><br>
-[x] 컨트롤러에서 호감 취소(cancel 메소드), 호감 사유 변경 시(showModify 메소드), 실행 가능한지 파악하는 메소드(호감 취소 - canCancel 메소드, 호감 사유 변경 - canModifyLike 메소드)에서 재사용 대기 시간을 체크하도록 구현

  => LikeablePerson 클래스에 정의된 isModifyUnlocked() 메소드를 활용하여 마지막으로 객체가 생성 혹은 수정된 시간으로 부터 3시간이 경과하였는지 검증하도록 했다.
<br><br>
-[x] 초단위 올림하여 시간 표시

  => 초 단위가 0을 초과하면, 분 단위를 올림하고, 분 단위가 59 라면, 분 단위를 0으로 초기화하고, 시간 단위를 올림하도록 구현했다.

**[특이사항]**

- 재사용 대기 시간 구현

    => 이렇게 쉽게 구현되는 게 맞는지 의문이다...
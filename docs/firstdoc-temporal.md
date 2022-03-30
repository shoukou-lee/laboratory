관찰

Mar-27-2022
- Non-Tx 메서드 호출 -> 롤백 안됨
- Tx 메서드 호출 -> 롤백 됨
- Non-Tx 메서드 내부에서 Non-Tx메서드 호출 -> 롤백 안됨
- Non-Tx 메서드 내부에서 Tx 메서드를 호출 -> 롤백 안됨 (AOP와 연관이 있다고 한다 .. 이부분을 더 찾아볼것)
- Tx 메서드 내부에서 Non-Tx 메서드 호출 -> 롤백 됨
- Tx 메서드 내부에서 Tx 메서드 호출 -> 롤백 됨 (분명 안됐던것 같은데 된다.. 다시 짚어보자)

Mar-30-2022
- 연관성이 많아 보이지는 않지만 ..  
- 1:N 양방향 연관관계를 가진 엔티티들을 Fetch join 할때, @Transactional 유무에 따라 다른 쿼리가 발생함 .. 왜그럴까 ?
- MemberServiceTest.transactionalFetchJoin/nonTransactionalFetchJoin 메서드 실행 로그 참고

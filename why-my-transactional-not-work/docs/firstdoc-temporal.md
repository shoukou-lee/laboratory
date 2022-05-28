관찰

Mar-27-2022
- Non-Tx 메서드 호출 -> 롤백 안됨
- Tx 메서드 호출 -> 롤백 됨
- Non-Tx 메서드 내부에서 Non-Tx메서드 호출 -> 롤백 안됨
- Non-Tx 메서드 내부에서 Tx 메서드를 호출 -> 롤백 안됨 (AOP와 연관이 있다고 한다 .. 이부분을 더 찾아볼것)
- Tx 메서드 내부에서 Non-Tx 메서드 호출 -> 롤백 됨
- Tx 메서드 내부에서 Tx 메서드 호출 -> 롤백 됨 (분명 안됐던것 같은데 된다.. 다시 짚어보자)

Apr-03-2022
- https://stackoverflow.com/questions/34641670/restcontroller-methods-seem-to-be-transactional-by-default-why
- 트랜잭션 범위를 개별로 설정했는데, Entity Manager가 여전히 살아있다 .. 이건 또 OSIV 랑 연관이 있다고 한다....
- OpenEntityManagerInViewInterceptor ?

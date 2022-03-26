## 트랜잭션 외않되 ?

1. Class-level @Transactional을 사용하지 않는 경우
    1) 한 메서드에서 다른 @Transactional 메서드를 호출하는 경우
    2) @Transactional 메서드에서 RunTimeException을 throw하는 경우와, 다른 Exception을 throw하는 경우
2. Class-level @Transactional을 사용한 경우
    1) 접근제한자가 private인 @Transactional 메서드를 호출하는 경우

3. @Transactional이 뭘까? Spring AOP란? Proxy란?
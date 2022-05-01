package com.shoukou.whymytransactionalnotwork.controller;

import com.shoukou.whymytransactionalnotwork.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;

    /**
     * saveMember에서 memberRepository.save(m)만 하고, team.getMembers().add(m)을 하지 않았다고 가정하자.
     * saveMember와 fetchJoin()이 개별적인 트랜잭션 범위를 가지므로, fetchJoin 이후에는 team.getMembers().isNotEmpty()일까?
     * - Spring OSIV에 의해 영속성 컨텍스트는 컨트롤러와 뷰 레이어까지 살아있고, Lazy loading이 가능한 상태이다. 따라서 여전히 empty()이다.
     * - application.properties에서 spring.jpa.open-in-view : false로 설정하면 OSIV가 disable 되고,
     * - 영속성 컨텍스트는 서비스 레이어까지만 생존하게 되므로, 처음 생각한 대로 동작한다.
     */
    @GetMapping("/launch")
    void callService() {
        Long id = memberService.saveTeam();
        memberService.saveMember(id);
        memberService.fetchJoin(id);
    }

}

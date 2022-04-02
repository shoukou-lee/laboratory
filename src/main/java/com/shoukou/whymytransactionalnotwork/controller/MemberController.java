package com.shoukou.whymytransactionalnotwork.controller;

import com.shoukou.whymytransactionalnotwork.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/launch")
    void callService() {
        Long id = memberService.saveTeam();
        memberService.saveMember(id);
        memberService.fetchJoin(id);
    }

}

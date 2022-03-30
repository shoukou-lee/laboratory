package com.shoukou.whymytransactionalnotwork.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Member {

    @Id
    @Column(name = "MEMBER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team team;

    public Member(String name) {
        this.name = name;
    }

    public Member(String name, Team team) {
        this.name = name;
        this.team = team;
    }
}

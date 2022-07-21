package iam.shoukou.jpaexample.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class Member {

    @Id
    @Column(name = "MEMBER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int number;

    @Version
    @Column(name = "OPTLOCK")
    private int version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    private Team team;

    public Member(String name) {
        this.name = name;
        this.number = 0;
    }

    public Member(String name, Team team) {
        this.name = name;
        this.team = team;
        this.number = 0;
    }

}

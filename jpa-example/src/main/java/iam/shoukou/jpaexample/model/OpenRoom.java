package iam.shoukou.jpaexample.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@NoArgsConstructor
@Entity
@DiscriminatorValue("OPEN")
public class OpenRoom extends Room {

    private boolean isJoinable;

    public OpenRoom(String name, boolean isJoinable) {
        super(name);
        this.isJoinable = isJoinable;
    }
}

package iam.shoukou.jpaexample.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@NoArgsConstructor
@Entity
@DiscriminatorValue("PRIV")
public class PrivateRoom extends Room {

    private boolean isDm;

    public PrivateRoom(String name, boolean isDm) {
        super(name);
        this.isDm = isDm;
    }
}

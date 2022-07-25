package iam.shoukou.jpaexample.model;

import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@NoArgsConstructor
@Entity
public class Order {

    @EmbeddedId
    OrderId orderId;

    State state;
    enum State {
        PENDING,
        SHIPPING,
        SHIPPED
    }

}

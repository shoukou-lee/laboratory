package iam.shoukou.jpaexample.model.order;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class OrderId implements Serializable {

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "item_id")
    private Long itemId;

    public OrderId(Long customerId, Long itemId) {
        this.customerId = customerId;
        this.itemId = itemId;
    }
}

package iam.shoukou.jpaexample.model.order;

import lombok.*;

import javax.persistence.*;

/**
 * ORDER는 SQL에서 선점된 키워드다 ..
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class OrderDetail {

    @EmbeddedId
    private OrderId orderId = new OrderId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("customerId")
    @JoinColumn
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("itemId")
    @JoinColumn
    private Item item;

    private String state;

    public OrderDetail(Customer customer, Item item) {
        this.customer = customer;
        this.item = item;
        this.state = "PENDING";
    }
}

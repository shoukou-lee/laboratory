package iam.shoukou.jpaexample.model;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
public class OrderId implements Serializable {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Item item;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Customer customer;

}

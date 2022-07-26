package iam.shoukou.jpaexample.model.order;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Address {
    String city;
    String state;
    String postalCode;

    public Address(String city, String state, String postalCode) {
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
    }
}

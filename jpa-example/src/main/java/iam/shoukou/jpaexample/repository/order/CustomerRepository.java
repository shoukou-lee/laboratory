package iam.shoukou.jpaexample.repository.order;

import iam.shoukou.jpaexample.model.order.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}

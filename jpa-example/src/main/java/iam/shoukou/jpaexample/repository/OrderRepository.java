package iam.shoukou.jpaexample.repository;

import iam.shoukou.jpaexample.model.Order;
import iam.shoukou.jpaexample.model.OrderId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, OrderId> {
}

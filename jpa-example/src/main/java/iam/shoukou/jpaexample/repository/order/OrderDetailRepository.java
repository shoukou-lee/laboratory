package iam.shoukou.jpaexample.repository.order;

import iam.shoukou.jpaexample.model.order.OrderDetail;
import iam.shoukou.jpaexample.model.order.OrderId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderId> {



    @Query("select o from OrderDetail o join fetch o.customer join fetch o.item where o.orderId.customerId = :customerId")
    List<OrderDetail> findByCustomerIdFetchAll(Long customerId);

}

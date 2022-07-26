package iam.shoukou.jpaexample.repository.order;

import iam.shoukou.jpaexample.model.order.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}

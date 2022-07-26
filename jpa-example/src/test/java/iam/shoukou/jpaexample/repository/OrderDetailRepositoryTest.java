package iam.shoukou.jpaexample.repository;

import iam.shoukou.jpaexample.QueryDslTestConfig;
import iam.shoukou.jpaexample.model.order.*;
import iam.shoukou.jpaexample.repository.order.CustomerRepository;
import iam.shoukou.jpaexample.repository.order.ItemRepository;
import iam.shoukou.jpaexample.repository.order.OrderDetailRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QueryDslTestConfig.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class OrderDetailRepositoryTest {

    @Autowired
    OrderDetailRepository orderDetailRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    ItemRepository itemRepository;

    @BeforeEach
    void init() {

    }

    @AfterEach
    void tearDown() {
        orderDetailRepository.deleteAll();
        customerRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    @DisplayName("FK 복합키를 PK로 사용하는 엔티티 저장 후 조회 테스트")
    void findCompositePKofTwoFKs() {
        // given
        Customer customer = new Customer("maxikong", new Address("Seongnam", "Bundang", "12345"));
        customerRepository.save(customer);

        Item item = new Item("M2 Air", 169.0);
        itemRepository.save(item);

        OrderDetail orderDetail = new OrderDetail(customer, item);
        orderDetailRepository.save(orderDetail);

        // when
        OrderDetail found = orderDetailRepository.findById(new OrderId(customer.getId(), item.getId()))
                .orElseThrow(() -> new RuntimeException("No order found"));

        // then
        assertThat(found.getOrderId().getItemId()).isEqualTo(item.getId());
        assertThat(found.getCustomer().getId()).isEqualTo(customer.getId());
    }

    @Test
    @DisplayName("FK 복합키 Customer ID로 조회")
    void findByCustomerId() {
        // given
        Customer customer = new Customer("maxikong", new Address("Seongnam", "Bundang", "12345"));
        customerRepository.save(customer);

        Item item = new Item("M2 Air", 169.0);
        itemRepository.save(item);

        Item item2 = new Item("Magic mouse", 1300.0);
        itemRepository.save(item2);

        orderDetailRepository.save(new OrderDetail(customer, item));
        orderDetailRepository.save(new OrderDetail(customer, item2));

        // when
        List<OrderDetail> found = orderDetailRepository.findByCustomerIdFetchAll(customer.getId());

        // then
        assertThat(found).hasSize(2);
        assertThat(found.stream().mapToDouble(i -> i.getItem().getPrice()).sum()).isEqualTo(1469.0);
    }

}

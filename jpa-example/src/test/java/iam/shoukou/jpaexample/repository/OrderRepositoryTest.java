package iam.shoukou.jpaexample.repository;

import iam.shoukou.jpaexample.QueryDslTestConfig;
import iam.shoukou.jpaexample.model.Order;
import iam.shoukou.jpaexample.model.OrderId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@DataJpaTest
@Import(QueryDslTestConfig.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class OrderRepositoryTest {

    @Autowired
    OrderRepository orderRepository;

    @BeforeEach
    void init() {

    }

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();
    }

    @Test
    void test() {

        orderRepository.save(new Order());

        List<Order> all = orderRepository.findAll();

        System.out.println(all);
    }


}

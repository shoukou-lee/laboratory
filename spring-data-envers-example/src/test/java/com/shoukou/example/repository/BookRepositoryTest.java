package com.shoukou.example.repository;

import com.shoukou.example.domain.Book;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.history.Revisions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class BookRepositoryTest {

    @Autowired
    BookRepository bookRepository;

    @BeforeEach
    void init() {

    }

    @AfterEach
    void tearDown() {
        bookRepository.deleteAll();
    }

    @Test
    void saveAndReviseOnce() {
        // given
        String titleBeforeModify = "수정 전";
        String titleAfterModify = "수정 후";
        Book book = bookRepository.save(
                Book.builder()
                        .title(titleBeforeModify)
                        .build()
        );

        book.modifyTitle(titleAfterModify);
        bookRepository.save(book);

        // when - book id로 모든 변경 내역 조회 후, 첫번째 revision의 book을 가져옴
        Revisions<Long, Book> revisions = bookRepository.findRevisions(book.getId());

        Book bookBeforeModify = revisions.getContent().get(0).getEntity();

        // then
        assertThat(bookBeforeModify.getId()).isEqualTo(book.getId()); // id는 변경 전후가 같은가?
        assertThat(bookBeforeModify.getTitle()).isEqualTo(titleBeforeModify); // title은 변경 전과 같은가?
        assertThat(bookBeforeModify.getModifiedAt()).isEqualTo(bookBeforeModify.getPublishedAt()); // 생성, 수정 시간이 같은가?
    }
}

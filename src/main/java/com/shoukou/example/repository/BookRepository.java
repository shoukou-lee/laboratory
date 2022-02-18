package com.shoukou.example.repository;

import com.shoukou.example.domain.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.history.Revision;
import org.springframework.data.history.RevisionSort;
import org.springframework.data.history.Revisions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long>, RevisionRepository<Book, Long, Integer> {
    /* 상속 메서드 */
//    Optional<Revision<N, T>> findLastChangeRevision(ID id);
//    Revisions<N, T> findRevisions(ID id);
//    Page<Revision<N, T>> findRevisions(ID id, Pageable pageable);
//    Optional<Revision<N, T>> findRevision(ID id, N revisionNumber);
}

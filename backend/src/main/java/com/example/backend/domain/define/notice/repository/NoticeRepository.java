package com.example.backend.domain.define.notice.repository;

import com.example.backend.domain.define.notice.Notice;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface NoticeRepository extends MongoRepository<Notice, String>, NoticeRepositoryCustom {
    List<Notice> findByUserId(Long userId);

    Optional<Notice> findById(String id);

    void deleteById(String id);

    void deleteAllByUserId(Long userId);
}

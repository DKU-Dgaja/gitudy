package com.example.backend.domain.define.notice.repository;

import com.example.backend.domain.define.notice.Notice;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NoticeRepository extends MongoRepository<Notice, Long> {
    Notice findByUserId(Long userId);
}

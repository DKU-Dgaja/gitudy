package com.example.backend.domain.define.notice.repository;

import com.example.backend.domain.define.notice.Notice;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class NoticeRepositoryImpl implements NoticeRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<Notice> findUserNoticeListByUserId(Long userId, LocalDateTime time, Long limit) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId)
                .andOperator(Criteria.where("localDateTime").lte(time))); // 현재 시간 이전의 알림을 모두 포함
        query.with(Sort.by(Sort.Direction.DESC, "localDateTime")); // 시간에 따라 내림차순 정렬
        query.limit(limit.intValue());

        return mongoTemplate.find(query, Notice.class);
    }

}

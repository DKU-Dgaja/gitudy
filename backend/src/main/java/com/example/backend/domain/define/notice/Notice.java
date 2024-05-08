package com.example.backend.domain.define.notice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notice")
public class Notice {

    @Id
    private String id;

    private Long studyInfoId;

    private Long userId;

    private String title;

    private String message;

    private LocalDateTime localDateTime;
}

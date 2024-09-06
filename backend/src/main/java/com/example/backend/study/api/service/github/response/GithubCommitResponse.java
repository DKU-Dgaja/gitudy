package com.example.backend.study.api.service.github.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kohsuke.github.GHCommit;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GithubCommitResponse {
    private String sha;                 // 커밋 식별자
    private String authorName;          // 커밋 작성자 깃허브 id
    private String message;             // 커밋 메시지
    private LocalDateTime commitDate;       // 커밋 날짜 정보

    public static GithubCommitResponse of(GHCommit commit) throws IOException {

        // // Date 객체를 LocalDateTime으로 변환
        LocalDateTime commitDate = LocalDateTime.ofInstant(commit.getCommitDate().toInstant(), ZoneId.systemDefault());

        return GithubCommitResponse.builder()
                .sha(commit.getSHA1())
                .authorName(commit.getAuthor().getLogin())
                .message(commit.getCommitShortInfo().getMessage())
                .commitDate(commitDate)
                .build();
    }
}
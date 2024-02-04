package com.example.backend.domain.define.study.info.constant;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepositoryInfo {
    @Column(name = "REPOSITORY_OWNER")
    private String owner;

    @Column(name = "REPOSITORY_NAME")
    private String name;

    @Column(name = "BRANCH_NAME")
    private String branchName;
}

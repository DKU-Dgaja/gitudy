package com.example.backend.study.api.controller.info.request;

import com.example.backend.common.validation.ValidRepoName;

public record RepoNameCheckRequest(
        @ValidRepoName
        String name
) {
}

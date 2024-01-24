package com.example.backend.domain.define.state;

import com.example.backend.domain.define.state.LoginState;
import org.springframework.data.redis.repository.cdi.RedisRepositoryBean;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface LoginStateRepository extends CrudRepository<LoginState, String> {
}

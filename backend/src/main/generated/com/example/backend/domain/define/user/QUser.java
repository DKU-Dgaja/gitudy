package com.example.backend.domain.define.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -2021682598L;

    public static final QUser user = new QUser("user");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final StringPath platformId = createString("platformId");

    public final EnumPath<com.example.backend.domain.define.user.constant.UserPlatformType> platformType = createEnum("platformType", com.example.backend.domain.define.user.constant.UserPlatformType.class);

    public final StringPath profileImageUrl = createString("profileImageUrl");

    public final BooleanPath pushAlarmYn = createBoolean("pushAlarmYn");

    public final EnumPath<com.example.backend.domain.define.user.constant.UserRole> role = createEnum("role", com.example.backend.domain.define.user.constant.UserRole.class);

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}


package com.example.backend.domain.define.test;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDslTest is a Querydsl query type for DslTest
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDslTest extends EntityPathBase<DslTest> {

    private static final long serialVersionUID = -334605113L;

    public static final QDslTest dslTest = new QDslTest("dslTest");

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QDslTest(String variable) {
        super(DslTest.class, forVariable(variable));
    }

    public QDslTest(Path<? extends DslTest> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDslTest(PathMetadata metadata) {
        super(DslTest.class, metadata);
    }

}


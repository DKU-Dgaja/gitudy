package site.gitudy.webhooks.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.core.mapping.MongoMappingContext


@Configuration
class MongoDBConfig(
    private val mongoMappingContext: MongoMappingContext
) {
    @Bean
    fun reactiveMappingMongoConverter(): MappingMongoConverter {
        val converter = MappingMongoConverter(
            ReactiveMongoTemplate.NO_OP_REF_RESOLVER,
            mongoMappingContext
        )

        converter.setTypeMapper(DefaultMongoTypeMapper(null))
        return converter
    }
}
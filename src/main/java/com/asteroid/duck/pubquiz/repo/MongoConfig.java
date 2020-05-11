package com.asteroid.duck.pubquiz.repo;

import com.asteroid.duck.pubquiz.model.QuestionId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.env.Environment;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MongoConfig {

    @Autowired
    private Environment env;

    @Autowired
    private MongoDbFactory mongo;

    @Bean
    public MongoTemplate mongoTemplate() {
        MappingMongoConverter converter = new MappingMongoConverter(
                new DefaultDbRefResolver(mongo), new MongoMappingContext());
        converter.setCustomConversions(customConversions());
        converter.afterPropertiesSet();
        return new MongoTemplate(mongo, converter);
    }

    @Bean
    public CustomConversions customConversions(){
        List<Converter<?,?>> converters = new ArrayList<Converter<?,?>>();
        converters.add(new StringToQuestionId());
        converters.add(new QuestionIdToString());
        return new CustomConversions(CustomConversions.StoreConversions.NONE, converters);
    }

    class StringToQuestionId implements Converter<String, QuestionId> {

        @Override
        public QuestionId convert(String source) {
            return source == null ? null : QuestionId.parse(source);
        }
    }

    class QuestionIdToString implements Converter<QuestionId, String> {

        @Override
        public String convert(QuestionId source) {
            return source == null ? null : source.toString();
        }
    }


}
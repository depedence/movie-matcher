package movie.matcher.ru.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Bean
    public RestAssuredConfig restAssuredConfig() {
        return RestAssuredConfig.config()
                .objectMapperConfig(
                        ObjectMapperConfig.objectMapperConfig()
                                .jackson2ObjectMapperFactory((cls, charset) ->
                                        new ObjectMapper()
                                                .registerModule(new JavaTimeModule())
                                                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                                )
                );
    }

}
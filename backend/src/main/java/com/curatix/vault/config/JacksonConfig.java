package com.curatix.vault.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    /**
     * Registers the Hibernate6Module so Jackson can safely handle
     * Hibernate ByteBuddy lazy proxies — instead of crashing with
     * "Type definition error: ByteBuddyInterceptor", uninitialized
     * proxies are serialized as null.
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Handle Hibernate lazy proxies gracefully
        Hibernate6Module hibernate6Module = new Hibernate6Module();
        // Don't force-load lazy associations; serialize as null if not initialized
        hibernate6Module.disable(Hibernate6Module.Feature.USE_TRANSIENT_ANNOTATION);
        mapper.registerModule(hibernate6Module);

        // Handle Java 8 date/time types (LocalDateTime, LocalDate, etc.)
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper;
    }
}

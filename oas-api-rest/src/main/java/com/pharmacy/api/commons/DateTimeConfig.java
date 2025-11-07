package com.pharmacy.api.commons;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.quarkus.jackson.ObjectMapperCustomizer;

import jakarta.inject.Singleton;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Singleton
public class DateTimeConfig implements ObjectMapperCustomizer {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Override
    public void customize(ObjectMapper mapper) {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(OffsetDateTime.class, new JsonDeserializer<OffsetDateTime>() {
            @Override
            public OffsetDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                String date = p.getText();
                try {
                    LocalDateTime localDateTime = LocalDateTime.parse(date, formatter);
                    return localDateTime.atOffset(ZoneOffset.UTC);
                } catch (Exception e) {
                    throw new IOException("Error parsing date: " + date + ". Expected format: dd/MM/yyyy HH:mm:ss", e);
                }
            }
        });
        mapper.registerModule(module);
    }
}
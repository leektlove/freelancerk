package com.freelancerk.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import javax.persistence.AttributeConverter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JpaMapConverter implements AttributeConverter<Map<String, Object>, String> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object>  map) {
        if (map == null || map.isEmpty()) {
            return "";
        }

        String jsonString = null;
        try {
            jsonString = objectMapper.writeValueAsString(map);
        } catch (final JsonProcessingException e) {
            log.error("JSON writing error", e);
        }

        return jsonString;
    }


    @Override
    public Map<String, Object> convertToEntityAttribute(String jsonString) {
        if (Strings.isBlank(jsonString)) {
            return Maps.newHashMap();
        }

        Map<String, Object> map = new HashMap<>();
        TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>(){};
        try {
            map = objectMapper.readValue(jsonString, typeReference);
        } catch (final IOException e) {
            log.error("JSON reading error", e);
        }

        return map;
    }
}

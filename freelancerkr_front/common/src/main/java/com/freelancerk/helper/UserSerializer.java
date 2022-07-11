package com.freelancerk.helper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.freelancerk.domain.User;

import java.io.IOException;

public class UserSerializer extends StdSerializer<User> {

    public UserSerializer() {
        this(null);
    }

    protected UserSerializer(Class<User> t) {
        super(t);
    }

    @Override
    public void serialize(User user, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", user.getId());
        jsonGenerator.writeStringField("nickname", user.getNickname());
        jsonGenerator.writeStringField("uid", user.getUid());
        jsonGenerator.writeObjectField("authType", user.getAuthType());
        jsonGenerator.writeStringField("username", user.getUsername());
        jsonGenerator.writeStringField("role", user.getRoles());
        jsonGenerator.writeEndObject();
    }
}

package com.freelancerk.helper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.freelancerk.domain.User;

import java.io.IOException;

public class UserDeserializer extends StdDeserializer<User> {

    public UserDeserializer() {
        this(null);
    }

    protected UserDeserializer(Class<User> t) {
        super(t);
    }

    @Override
    public User deserialize(JsonParser parser, DeserializationContext deserializer) throws IOException {
        User user = new User();
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);

        JsonNode idNode = node.get("id");
        Long id = idNode.asLong();
        user.setId(id);

        JsonNode uidNode = node.get("uid");
        String uid = uidNode.asText();
        user.setUid(uid);

        JsonNode roleNode = node.get("role");
        User.Role role = User.Role.valueOf(roleNode.asText());
        user.setRoles(role.name());

        return user;
    }
}

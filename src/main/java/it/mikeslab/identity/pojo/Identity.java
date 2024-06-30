package it.mikeslab.identity.pojo;


import it.mikeslab.commons.api.database.SerializableMapConvertible;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
public class Identity implements SerializableMapConvertible<Identity> {

    private Map<String, Object> values;
    private UUID uuid; // mandatory

    // Creates a filter for the identity
    public Identity(UUID uuid) {
        this.uuid = uuid;
        this.values = new HashMap<>();
    }

    @Override
    public Identity fromMap(Map<String, Object> map) {

        Identity identity = new Identity();

        identity.getValues().putAll(map);
        identity.getValues().remove(getIdentifierName());

        return identity;
    }



    @Override
    public Map<String, Object> toMap() {

        Map<String, Object> map = new HashMap<>();

        map.put(getIdentifierName(), uuid.toString());

        map.putAll(values);

        return map;

    }

    @Override
    public String getIdentifierName() {
        return "uuid";
    }

    @Override
    public Object getIdentifierValue() {
        return uuid.toString();
    }

}

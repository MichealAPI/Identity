package it.mikeslab.identity.pojo;


import it.mikeslab.commons.api.database.SerializableMapConvertible;
import it.mikeslab.commons.api.logger.LogUtils;
import it.mikeslab.identity.IdentityPlugin;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

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

        if (map != null) {

            identity.setValues(new HashMap<>(map));

            LogUtils.debug(
                    LogUtils.LogSource.DATABASE,
                    "Retrieved values from Database: " + map.toString()
            );

            identity.getValues().remove(this.getUniqueIdentifierName());
        }

        return identity;
    }



    @Override
    public Map<String, Object> toMap() {

        Map<String, Object> map = new HashMap<>();

        map.put(
                this.getUniqueIdentifierName(),
                this.getUniqueIdentifierValue()
        );

        if (values != null && !values.isEmpty()) {
            LogUtils.debug(
                    LogUtils.LogSource.DATABASE,
                    "Populating a map based on a Identity POJO instance which contains: " + values.toString()
            );
            map.putAll(values);
        }

        return map;

    }

    @Override
    public String getUniqueIdentifierName() {
        return "uuid";
    }

    @Override
    public Object getUniqueIdentifierValue() {
        return uuid.toString();
    }

    @Override
    public Set<String> identifiers() {
        return IdentityPlugin.INVENTORY_IDENTIFIERS;
    }

}

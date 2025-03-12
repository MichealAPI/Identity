package it.mikeslab.identity.pojo;


import it.mikeslab.commons.api.database.util.SimpleMapConvertible;
import it.mikeslab.identity.IdentityPlugin;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
public class Identity extends SimpleMapConvertible<UUID, Identity> {

    public Identity(UUID referenceUUID) {
        super(referenceUUID, "uuid");
    }

    /*
     * Authorized key values for the Identity entity
     */
    @Override
    public Set<String> identifiers() {
        return IdentityPlugin.INVENTORY_IDENTIFIERS;
    }

}

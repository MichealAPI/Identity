package it.mikeslab.identity.inventory.pojo;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ValueMenuContext {

    private final int baseValue, max, min;

}

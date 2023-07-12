package it.mikeslab.identity.update;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Update {
    private final UpdateType updateType;
    private final String shortDescription;
}

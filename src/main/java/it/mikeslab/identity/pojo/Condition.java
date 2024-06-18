package it.mikeslab.identity.pojo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Getter
@RequiredArgsConstructor
public class Condition {

    private final boolean valid;
    private final Optional<String> errorMessage;

}

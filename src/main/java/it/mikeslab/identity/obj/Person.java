package it.mikeslab.identity.obj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Person {
    private String name, gender;
    private int age = -1;

    public boolean hasName() {
        return name != null;
    }

    public boolean hasGender() {
        return gender != null;
    }

    public boolean hasAge() {
        return age != -1;
    }

}



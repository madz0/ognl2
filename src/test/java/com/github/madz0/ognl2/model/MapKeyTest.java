package com.github.madz0.ognl2.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class MapKeyTest {

    private Map<KeyEnum, String> map;
    private Map<KeyEnum, MyTest2> map2;
    private Map<String, MyTest2> map3;

    public enum KeyEnum {
        KEY1,
        ;
    }
}

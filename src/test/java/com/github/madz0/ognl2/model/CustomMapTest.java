package com.github.madz0.ognl2.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class CustomMapTest {
    public static class CustomMap extends HashMap<String, String> {

    }

    CustomMap map;
}

package com.github.madz0.ognl2.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class MyTest {
    public enum X {
        TEST
    }
    private List<Object> objects;
    private List<Map<String, String>> l;
    private List<Map<String, X>> l2;
    private Map<String, List<String>> m;
}

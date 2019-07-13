package com.github.madz0.ognl2.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class MyTest {
    private List<Object> objects;
    private List<Map<String, String>> l;
    private Map<String, List<String>> m;
}

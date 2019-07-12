package com.github.madz0.ognl2.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class MyTest2 extends BaseModel {
    @MyTestAnnotation
    private List<InsideTest> objects;
    @MyTestAnnotation
    private Map<String, InsideTest> map;
    @MyTestAnnotation
    private Set<InsideTest> set;
    private List<MyTest2> myTest2List;
    private Set<String> stringSet;
}

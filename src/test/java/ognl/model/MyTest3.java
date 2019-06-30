package ognl.model;

import java.util.Map;

public class MyTest3 extends BaseModel {
    private MyTest2 myTest2;
    @MyTestAnnotation
    private InsideTest insideTest;

    private Map<String, TestEnum> testEnumMap;
    private Map<String, String> testStringMap;
    private Map<Integer, String> testIntegerMap;

    public InsideTest getInsideTest() {
        return insideTest;
    }

    public void setInsideTest(InsideTest insideTest) {
        this.insideTest = insideTest;
    }

    public MyTest2 getMyTest2() {
        return myTest2;
    }

    public void setMyTest2(MyTest2 myTest2) {
        this.myTest2 = myTest2;
    }

    public Map<String, TestEnum> getTestEnumMap() {
        return testEnumMap;
    }

    public void setTestEnumMap(Map<String, TestEnum> testEnumMap) {
        this.testEnumMap = testEnumMap;
    }

    public Map<String, String> getTestStringMap() {
        return testStringMap;
    }

    public void setTestStringMap(Map<String, String> testStringMap) {
        this.testStringMap = testStringMap;
    }

    public Map<Integer, String> getTestIntegerMap() {
        return testIntegerMap;
    }

    public void setTestIntegerMap(Map<Integer, String> testIntegerMap) {
        this.testIntegerMap = testIntegerMap;
    }

    public enum TestEnum {
        TEST1,
        TEST2,
        ;
    }
}

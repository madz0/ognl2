package ognl.model;

public class MyTest3 extends BaseModel {
    private MyTest2 myTest2;
    @MyTestAnnotation
    private InsideTest insideTest;

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
}

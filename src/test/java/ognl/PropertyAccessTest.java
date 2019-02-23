package ognl;

import ognl.model.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PropertyAccessTest {

  @Test
  public void testTest() throws OgnlException {
    
    OgnlContext context = (OgnlContext) Ognl.createDefaultContext(null, new DefaultMemberAccess(false));
    context.extend();
    MyTest root = new MyTest();
    Ognl.getValue("objects[0]=salam", context, root);
    assertEquals("salam", ((List)root.getObjects()).get(0));
  }

  @Test
  public void testBind() throws OgnlException {
    OgnlContext context = (OgnlContext) Ognl.createDefaultContext(null, new DefaultMemberAccess(false));
    context.extend();
    MyTest3 root = new MyTest3();
    List<String> bindingList = new ArrayList<>();
    bindingList.add("myTest2.objects[0].name=obj0_name");
    bindingList.add("myTest2.objects[0].id=0");
    bindingList.add("myTest2.objects[1].id=1");
    bindingList.add("myTest2.objects[1].name=obj1_name");
    bindingList.add("insideTest.id=2");
    bindingList.add("insideTest.name=inside_2");

    bindingList.add("myTest2.myTest2List[0].id=7");
    bindingList.add("myTest2.myTest2List[0].objects[0].name=obj3_name");
    bindingList.add("myTest2.myTest2List[0].objects[0].id=3");
    bindingList.add("myTest2.myTest2List[0].objects[1].id=4");
    bindingList.add("myTest2.myTest2List[0].objects[1].name=obj4_name");

    bindingList.add("myTest2.myTest2List[1].id=8");
    bindingList.add("myTest2.myTest2List[1].objects[0].name=obj5_name");
    bindingList.add("myTest2.myTest2List[1].objects[0].id=5");
    bindingList.add("myTest2.myTest2List[1].objects[1].id=6");
    bindingList.add("myTest2.myTest2List[1].objects[1].name=obj6_name");

    Ognl.getValue(bindingList, context, root);
  }
}

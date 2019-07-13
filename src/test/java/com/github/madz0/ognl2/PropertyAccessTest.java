package com.github.madz0.ognl2;

import com.github.madz0.ognl2.model.*;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PropertyAccessTest {

    @Test
    public void testTest() throws OgnlException {

        OgnlContext context = (OgnlContext) Ognl.createDefaultContext(null, new DefaultMemberAccess(false));
        context.extend();
        MyTest root = new MyTest();
        Ognl.getValue("objects[0]='salam'", context, root);
        assertEquals("salam", ((List) root.getObjects()).get(0));
    }

    @Test
    public void testTestMap() throws OgnlException {

        OgnlContext context = (OgnlContext) Ognl.createDefaultContext(null, new DefaultMemberAccess(false));
        context.extend();
        MyTest2 root = new MyTest2();
        Ognl.getValue("map['name'].name='salam'", context, root);
        assertEquals("salam", root.getMap().get("name").getName());
    }

    @Test
    public void testBind() throws OgnlException {
        OgnlContext context = (OgnlContext) Ognl.createDefaultContext(null, new DefaultMemberAccess(false));
        context.extend();
        MyTest3 root = new MyTest3();
        List<Map.Entry<String, Object>> bindingList = new ArrayList<>();
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.objects[0].name", "obj0_name"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.objects[0].name", "obj0_name"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.objects[0].id", "0"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.objects[1].id", "1"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.objects[1].name", "obj1_name"));
        bindingList.add(new AbstractMap.SimpleEntry<>("insideTest.id", "2"));
        bindingList.add(new AbstractMap.SimpleEntry<>("insideTest.name", "inside_2"));

        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.id", "9"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.myTest2List[0].id", "7"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.myTest2List[0].objects[0].name", "obj3_name"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.myTest2List[0].objects[0].id", "3"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.myTest2List[0].objects[1].id", "4"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.myTest2List[0].objects[1].name", "obj4_name"));

        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.myTest2List[1].id", "8"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.myTest2List[1].objects[0].name", "obj5_name"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.myTest2List[1].objects[0].id", "5"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.myTest2List[1].objects[1].id", "6"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.myTest2List[1].objects[1].name", "obj6_name"));

        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.set[0].name", "set0"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.set[1].name", "set1"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.stringSet[0]", "stringSet0"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.stringSet[1]", "stringSet1"));

        Ognl.getValue(bindingList, context, root);
        assertEquals(Long.valueOf(9), root.getMyTest2().getId());
        assertEquals(Long.valueOf(7), root.getMyTest2().getMyTest2List().get(0).getId());
        assertEquals(Long.valueOf(8), root.getMyTest2().getMyTest2List().get(1).getId());
        assertEquals(Long.valueOf(3), root.getMyTest2().getMyTest2List().get(0).getObjects().get(0).getId());
        assertEquals(Long.valueOf(4), root.getMyTest2().getMyTest2List().get(0).getObjects().get(1).getId());
        assertEquals(Long.valueOf(5), root.getMyTest2().getMyTest2List().get(1).getObjects().get(0).getId());
        assertEquals(Long.valueOf(6), root.getMyTest2().getMyTest2List().get(1).getObjects().get(1).getId());
        assertEquals(Long.valueOf(0), root.getMyTest2().getObjects().get(0).getId());
        Iterator<InsideTest> it = root.getMyTest2().getSet().iterator();
        assertEquals("set0", it.next().getName());
        assertEquals("set1", it.next().getName());
        Iterator<String> itStr = root.getMyTest2().getStringSet().iterator();
        assertEquals("stringSet0", itStr.next());
        assertEquals("stringSet1", itStr.next());
        assertEquals("obj0_name", root.getMyTest2().getObjects().get(0).getName());
    }

    @Test
    public void testBindWithFirstLetter() throws OgnlException {
        OgnlContext context = (OgnlContext) Ognl.createDefaultContext(null, new DefaultMemberAccess(false));
        context.extend();
        context.setFirstUnknownIgnorance(true);
        MyTest3 root = new MyTest3();
        List<Map.Entry<String, Object>> bindingList = new ArrayList<>();
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.id", "500"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.myTest2.objects[0].name", "obj0_name"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.myTest2.objects[0].name", "obj0_name"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.myTest2.objects[0].id", "0"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.myTest2.objects[1].id", "1"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.myTest2.objects[1].name", "obj1_name"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.insideTest.id", "2"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.insideTest.name", "inside_2"));

        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.myTest2.id", "9"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.myTest2.myTest2List[0].id", "7"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.myTest2.myTest2List[0].objects[0].name", "obj3_name"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.myTest2.myTest2List[0].objects[0].id", "3"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.myTest2.myTest2List[0].objects[1].id", "4"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.myTest2.myTest2List[0].objects[1].name", "obj4_name"));

        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.myTest2.myTest2List[1].id", "8"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.myTest2.myTest2List[1].objects[0].name", "obj5_name"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.myTest2.myTest2List[1].objects[0].id", "5"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.myTest2.myTest2List[1].objects[1].id", "6"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.myTest2.myTest2List[1].objects[1].name", "obj6_name"));

        Ognl.getValue(bindingList, context, root);
        assertEquals(Long.valueOf(9), root.getMyTest2().getId());
        assertEquals(Long.valueOf(7), root.getMyTest2().getMyTest2List().get(0).getId());
        assertEquals(Long.valueOf(8), root.getMyTest2().getMyTest2List().get(1).getId());
        assertEquals(Long.valueOf(3), root.getMyTest2().getMyTest2List().get(0).getObjects().get(0).getId());
        assertEquals(Long.valueOf(4), root.getMyTest2().getMyTest2List().get(0).getObjects().get(1).getId());
        assertEquals(Long.valueOf(5), root.getMyTest2().getMyTest2List().get(1).getObjects().get(0).getId());
        assertEquals(Long.valueOf(6), root.getMyTest2().getMyTest2List().get(1).getObjects().get(1).getId());
        assertEquals(Long.valueOf(0), root.getMyTest2().getObjects().get(0).getId());
    }

    @Test
    public void testBindWithFirstLetterAndClass() throws OgnlException {
        OgnlContext context = (OgnlContext) Ognl.createDefaultContext(null, new DefaultMemberAccess(false));
        context.extend();
        context.setFirstUnknownIgnorance(true);
        List<Map.Entry<String, Object>> bindingList = new ArrayList<>();
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.id", "500"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.myTest2.objects[0].name", "obj0_name"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.myTest2.objects[0].name", "obj0_name"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.myTest2.objects[0].id", "0"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.myTest2.objects[1].id", "1"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.myTest2.objects[1].name", "obj1_name"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.insideTest.id", "2"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.insideTest.name", "inside_2"));

        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.myTest2.id", "9"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.myTest2.myTest2List[0].id", "7"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.myTest2.myTest2List[0].objects[0].name", "obj3_name"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.myTest2.myTest2List[0].objects[0].id", "3"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.myTest2.myTest2List[0].objects[1].id", "4"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.myTest2.myTest2List[0].objects[1].name", "obj4_name"));

        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.myTest2.myTest2List[1].id", "8"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.myTest2.myTest2List[1].objects[0].name", "obj5_name"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.myTest2.myTest2List[1].objects[0].id", "5"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.myTest2.myTest2List[1].objects[1].id", "6"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest3.myTest2.myTest2List[1].objects[1].name", "obj6_name"));

        MyTest3 root = Ognl.getValue(bindingList, context, MyTest3.class);
        assertEquals(Long.valueOf(9), root.getMyTest2().getId());
        assertEquals(Long.valueOf(7), root.getMyTest2().getMyTest2List().get(0).getId());
        assertEquals(Long.valueOf(8), root.getMyTest2().getMyTest2List().get(1).getId());
        assertEquals(Long.valueOf(3), root.getMyTest2().getMyTest2List().get(0).getObjects().get(0).getId());
        assertEquals(Long.valueOf(4), root.getMyTest2().getMyTest2List().get(0).getObjects().get(1).getId());
        assertEquals(Long.valueOf(5), root.getMyTest2().getMyTest2List().get(1).getObjects().get(0).getId());
        assertEquals(Long.valueOf(6), root.getMyTest2().getMyTest2List().get(1).getObjects().get(1).getId());
        assertEquals(Long.valueOf(0), root.getMyTest2().getObjects().get(0).getId());
    }

    @Test
    public void testBindWithWrongProperty() throws OgnlException {
        OgnlContext context = (OgnlContext) Ognl.createDefaultContext(null, new DefaultMemberAccess(false));
        context.extend();
        MyTest3 root = new MyTest3();
        List<Map.Entry<String, Object>> bindingList = new ArrayList<>();
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.objects[0]._name", "wrong_name"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.objects[0].name", "obj0_name"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.objects[0].name", "obj0_name"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.objects[0].id", "0"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.objects[1].id", "1"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.objects[1].name", "obj1_name"));
        bindingList.add(new AbstractMap.SimpleEntry<>("insideTest.id", "2"));
        bindingList.add(new AbstractMap.SimpleEntry<>("insideTest.name", "inside_2"));

        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.id", "9"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.myTest2List[0].id", "7"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.myTest2List[0].objects[0].name", "obj3_name"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.myTest2List[0].objects[0].id", "3"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.myTest2List[0].objects[1].id", "4"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.myTest2List[0].objects[1].name", "obj4_name"));

        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.myTest2List[1].id", "8"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.myTest2List[1].objects[0].name", "obj5_name"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.myTest2List[1].objects[0].id", "5"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.myTest2List[1].objects[1].id", "6"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.myTest2List[1].objects[1].name", "obj6_name"));

        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.set[0].name", "set0"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.set[1].name", "set1"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.stringSet[0]", "stringSet0"));
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.stringSet[1]", "stringSet1"));

        Ognl.getValue(bindingList, context, root);
        assertEquals(Long.valueOf(9), root.getMyTest2().getId());
        assertEquals(Long.valueOf(7), root.getMyTest2().getMyTest2List().get(0).getId());
        assertEquals(Long.valueOf(8), root.getMyTest2().getMyTest2List().get(1).getId());
        assertEquals(Long.valueOf(3), root.getMyTest2().getMyTest2List().get(0).getObjects().get(0).getId());
        assertEquals(Long.valueOf(4), root.getMyTest2().getMyTest2List().get(0).getObjects().get(1).getId());
        assertEquals(Long.valueOf(5), root.getMyTest2().getMyTest2List().get(1).getObjects().get(0).getId());
        assertEquals(Long.valueOf(6), root.getMyTest2().getMyTest2List().get(1).getObjects().get(1).getId());
        assertEquals(null, root.getMyTest2().getObjects().get(0).getId());
        Iterator<InsideTest> it = root.getMyTest2().getSet().iterator();
        assertEquals("set0", it.next().getName());
        assertEquals("set1", it.next().getName());
        Iterator<String> itStr = root.getMyTest2().getStringSet().iterator();
        assertEquals("stringSet0", itStr.next());
        assertEquals("stringSet1", itStr.next());
        assertEquals(null, root.getMyTest2().getObjects().get(0).getName());
    }

    @Ignore
    @Test
    public void testBindWithNullٔ() throws OgnlException {
        OgnlContext context = (OgnlContext) Ognl.createDefaultContext(null, new DefaultMemberAccess(false));
        context.extend();
        MyTest3 root = new MyTest3();
        List<Map.Entry<String, Object>> bindingList = new ArrayList<>();
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.objects[0].name", "null"));

        Ognl.getValue(bindingList, context, root);
        assertNull(root.getMyTest2().getObjects().get(0).getName());
    }

    @Test
    public void bindWithValueContainingDotTest() throws OgnlException {
        OgnlContext context = (OgnlContext) Ognl.createDefaultContext(null, new DefaultMemberAccess(false));
        context.extend();
        MyTest3 root = new MyTest3();
        List<Map.Entry<String, Object>> bindingList = new ArrayList<>();
        bindingList.add(new AbstractMap.SimpleEntry<>("myTest2.objects[0].name", "x.y.y"));

        Ognl.getValue(bindingList, context, root);
        assertEquals("x.y.y", root.getMyTest2().getObjects().get(0).getName());
    }

    @Test
    public void testMapEnumKeyBind() throws OgnlException {
        OgnlContext context = (OgnlContext) Ognl.createDefaultContext(null, new DefaultMemberAccess(false));
        context.extend();
        MapKeyTest root = new MapKeyTest();
        List<Map.Entry<String, Object>> bindingList = new ArrayList<>();
        bindingList.add(new AbstractMap.SimpleEntry<>("map['KEY1']", "salam"));
        Ognl.getValue(bindingList, context, root);
        assertEquals("salam", root.getMap().get(MapKeyTest.KeyEnum.KEY1));
    }

    @Test
    public void mapEnumKeyBindComplexTest() throws OgnlException {
        OgnlContext context = (OgnlContext) Ognl.createDefaultContext(null, new DefaultMemberAccess(false));
        context.extend();
        MapKeyTest root = new MapKeyTest();
        List<Map.Entry<String, Object>> bindingList = new ArrayList<>();
        bindingList.add(new AbstractMap.SimpleEntry<>("map2['KEY1'].objects[0].name", "salam"));
        Ognl.getValue(bindingList, context, root);
        assertEquals("salam", root.getMap2().get(MapKeyTest.KeyEnum.KEY1).getObjects().get(0).getName());
    }

    @Test
    public void mapStringKeyBindComplexTest() throws OgnlException {
        OgnlContext context = (OgnlContext) Ognl.createDefaultContext(null, new DefaultMemberAccess(false));
        context.extend();
        MapKeyTest root = new MapKeyTest();
        List<Map.Entry<String, Object>> bindingList = new ArrayList<>();
        bindingList.add(new AbstractMap.SimpleEntry<>("map3['KEY1'].objects[0].name", "salam"));
        Ognl.getValue(bindingList, context, root);
        assertEquals("salam", root.getMap3().get("KEY1").getObjects().get(0).getName());
    }

    @Ignore
    @Test
    public void mapStringKeyBindWithoutQuotation() throws OgnlException {
        OgnlContext context = (OgnlContext) Ognl.createDefaultContext(null, new DefaultMemberAccess(false));
        context.extend();
        MapKeyTest root = new MapKeyTest();
        List<Map.Entry<String, Object>> bindingList = new ArrayList<>();
        bindingList.add(new AbstractMap.SimpleEntry<>("map3[KEY1].objects[0].name", "salam"));
        Ognl.getValue(bindingList, context, root);
        assertEquals("salam", root.getMap3().get("KEY1").getObjects().get(0).getName());
    }

    @Test
    public void bindObjectToPropertyTest() throws OgnlException {
        OgnlContext context = (OgnlContext) Ognl.createDefaultContext(null, new DefaultMemberAccess(false));
        context.extend();
        MyTest root = new MyTest();
        List<Object> objects = Arrays.asList("ok");
        List<Map.Entry<String, Object>> bindingList = new ArrayList<>();
        bindingList.add(new AbstractMap.SimpleEntry<>("objects", objects));
        Ognl.getValue(bindingList, context, root);
        assertEquals("ok", root.getObjects().get(0));
    }

    @Test
    public void bindSetEmptyToArrayElementTest() throws OgnlException {
        OgnlContext context = (OgnlContext) Ognl.createDefaultContext(null, new DefaultMemberAccess(false));
        context.extend();
        MyTest root = new MyTest();
        root.setObjects(Arrays.asList("salam", "bye"));
        List<Map.Entry<String, Object>> bindingList = new ArrayList<>();
        bindingList.add(new AbstractMap.SimpleEntry<>("objects[0]", ""));
        Ognl.getValue(bindingList, context, root);
        assertEquals("", root.getObjects().get(0));
    }

    @Test
    public void bindEnumAsMapValueAndKeyWithoutQuotationTest() throws OgnlException {
        OgnlContext context = (OgnlContext) Ognl.createDefaultContext(null, new DefaultMemberAccess(false));
        context.extend();
        MyTest3 root = new MyTest3();
        List<Map.Entry<String, Object>> bindingList = new ArrayList<>();
        bindingList.add(new AbstractMap.SimpleEntry<>("testEnumMap[name]", "TEST1"));
        Ognl.getValue(bindingList, context, root);
        assertEquals(MyTest3.TestEnum.TEST1, root.getTestEnumMap().get("name"));
    }

    @Test
    public void bindStringMapValueTest() throws OgnlException {
        OgnlContext context = (OgnlContext) Ognl.createDefaultContext(null, new DefaultMemberAccess(false));
        context.extend();
        MyTest3 root = new MyTest3();
        List<Map.Entry<String, Object>> bindingList = new ArrayList<>();
        bindingList.add(new AbstractMap.SimpleEntry<>("testStringMap[\"name\"]", "TEST1"));
        Ognl.getValue(bindingList, context, root);
        assertEquals("TEST1", root.getTestStringMap().get("name"));
    }

    @Test
    public void bindIntegerMapValueTest() throws OgnlException {
        OgnlContext context = (OgnlContext) Ognl.createDefaultContext(null, new DefaultMemberAccess(false));
        context.extend();
        MyTest3 root = new MyTest3();
        List<Map.Entry<String, Object>> bindingList = new ArrayList<>();
        bindingList.add(new AbstractMap.SimpleEntry<>("testIntegerMap[1]", "TEST1"));
        Ognl.getValue(bindingList, context, root);
        assertEquals("TEST1", root.getTestIntegerMap().get(1));
    }

    @Test
    public void bindStringMapOrderTest() throws OgnlException {
        OgnlContext context = (OgnlContext) Ognl.createDefaultContext(null, new DefaultMemberAccess(false));
        context.extend();
        MyTest3 root = new MyTest3();
        List<Map.Entry<String, Object>> bindingList = new ArrayList<>();
        bindingList.add(new AbstractMap.SimpleEntry<>("testStringMap[name1]", "TEST1"));
        bindingList.add(new AbstractMap.SimpleEntry<>("testStringMap[name2]", "TEST2"));
        Ognl.getValue(bindingList, context, root);
        assertEquals("TEST1", root.getTestStringMap().entrySet().iterator().next().getValue());
        root = new MyTest3();
        bindingList = new ArrayList<>();
        bindingList.add(new AbstractMap.SimpleEntry<>("testStringMap[name2]", "TEST2"));
        bindingList.add(new AbstractMap.SimpleEntry<>("testStringMap[name1]", "TEST1"));
        Ognl.getValue(bindingList, context, root);
        assertEquals("TEST2", root.getTestStringMap().entrySet().iterator().next().getValue());
    }

    @Test
    public void bindCustomMapTest() throws OgnlException {
        OgnlContext context = (OgnlContext) Ognl.createDefaultContext(null, new DefaultMemberAccess(false));
        context.extend();
        CustomMapTest root = new CustomMapTest();
        List<Map.Entry<String, Object>> bindingList = new ArrayList<>();
        bindingList.add(new AbstractMap.SimpleEntry<>("map[name1]", "TEST1"));
        bindingList.add(new AbstractMap.SimpleEntry<>("map[name2]", "TEST2"));
        Ognl.getValue(bindingList, context, root);
        assertEquals("TEST1", root.getMap().get("name1"));
    }

    @Test
    public void bindMapInsideListTest() throws OgnlException {
        OgnlContext context = (OgnlContext) Ognl.createDefaultContext(null, new DefaultMemberAccess(false));
        context.extend();
        MyTest root = new MyTest();
        List<Map.Entry<String, Object>> bindingList = new ArrayList<>();
        bindingList.add(new AbstractMap.SimpleEntry<>("l[0][k1]", "TEST1"));
        bindingList.add(new AbstractMap.SimpleEntry<>("l[0][k2]", "TEST2"));
        Ognl.getValue(bindingList, context, root);
        assertEquals("TEST1", root.getL().get(0).get("k1"));
    }

    @Test
    public void bindMapInsideList2Test() throws OgnlException {
        OgnlContext context = (OgnlContext) Ognl.createDefaultContext(null, new DefaultMemberAccess(false));
        context.extend();
        MyTest root = new MyTest();
        List<Map.Entry<String, Object>> bindingList = new ArrayList<>();
        bindingList.add(new AbstractMap.SimpleEntry<>("l[0][k1]", "TEST1"));
        bindingList.add(new AbstractMap.SimpleEntry<>("l[1][k1]", "TEST2"));
        Ognl.getValue(bindingList, context, root);
        assertEquals("TEST2", root.getL().get(1).get("k1"));
    }

    @Test
    public void bindListInsideMapTest() throws OgnlException {
        OgnlContext context = (OgnlContext) Ognl.createDefaultContext(null, new DefaultMemberAccess(false));
        context.extend();
        MyTest root = new MyTest();
        List<Map.Entry<String, Object>> bindingList = new ArrayList<>();
        bindingList.add(new AbstractMap.SimpleEntry<>("m[k1][0]", "TEST1"));
        bindingList.add(new AbstractMap.SimpleEntry<>("m['k1'][1]", "TEST2"));
        Ognl.getValue(bindingList, context, root);
        assertEquals("TEST1", root.getM().get("k1").get(0));
    }

    @Test
    public void bindListInsideMap2Test() throws OgnlException {
        OgnlContext context = (OgnlContext) Ognl.createDefaultContext(null, new DefaultMemberAccess(false));
        context.extend();
        MyTest root = new MyTest();
        List<Map.Entry<String, Object>> bindingList = new ArrayList<>();
        bindingList.add(new AbstractMap.SimpleEntry<>("m[k1][0]", "TEST1"));
        bindingList.add(new AbstractMap.SimpleEntry<>("m[k2][0]", "TEST2"));
        Ognl.getValue(bindingList, context, root);
        assertEquals("TEST2", root.getM().get("k2").get(0));
    }
}

# Object-Graph Navigation Language - OGNL2

## If you don't know what is OGNL, then go check out [https://github.com/jkuhnert/ognl](https://github.com/jkuhnert/ognl)

### I created OGNL2, because I needed to make some changes to the original library!
### The most notable different with the original library is that Ognl2 is capable of
### setting value to a chain of expressions like `myTest2.objects[0].name=test`
### It instantiates or expands intermediate objects, lists or maps in the path,
### if it is necessary

#### You may wondering why I did not created a pull request? because I originally derived this
#### project about 4 years ago and accidentally lost all the source codes. There was only 
#### a compiled version left of.
#### Since then I could find a proper time or motivation to bring it back. Until now that I needed
#### to make some changes to the library. That's why I decided to to make it Ognl2
 
Other changes are notably as following:

- Added support to navigate and apply a list of expressions an apply callbacks:

    ```java
    OgnlContext context = (OgnlContext) Ognl.createDefaultContext(null, new DefaultMemberAccess(false));
    context.extend();
    MyTest3 root = new MyTest3();
    List<Map.Entry<String, Object>> bindingList = new ArrayList<>();
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
    ```
    With the help of introducing a new data structure called `MapNode` which represents hirarchy,
     
    and a new interface called `ObjectConstructor` navigation of every node in the hirarchy will 
    call the `processObjectForGet` method of it which enables us to do great stuff because we 
    can intercept and change the way the graph is going to be navigated. 
    
    For example can fetch an item from database and assign it to the object property.
    
    `MapNode` let us to query the lower levels of the hirarchy. 
    
    For example you can query if the object has any id property and get its value.

- It can expand `List` and `Arrays`
- It supports parametrized type instantiation
- And some other mintor extendings
// --------------------------------------------------------------------------
// Copyright (c) 2004, Drew Davidson and Luke Blanshard
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are
// met:
//
// Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
// Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
// Neither the name of the Drew Davidson nor the names of its contributors
// may be used to endorse or promote products derived from this software
// without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
// FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
// COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
// INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
// BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
// OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
// AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
// THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
// DAMAGE.
// --------------------------------------------------------------------------
package org.ognl.test;

import junit.framework.TestSuite;
import com.github.madz0.ognl2.OgnlRuntime;
import org.ognl.test.objects.*;

import java.util.List;

public class InterfaceInheritanceTest extends OgnlTestCase {

    private static Root ROOT = new Root();

    static {
        ROOT.getBeans().setBean("testBean", new Bean1());
        ROOT.getBeans().setBean("evenOdd", new EvenOdd());

        List list = new ListSourceImpl();
        list.add("test1");

        ROOT.getMap().put("customList", list);
    }

    private static Object[][] TESTS = {
            {ROOT, "myMap", ROOT.getMyMap()},
            {ROOT, "myMap.test", ROOT},
            {ROOT.getMyMap(), "list", ROOT.getList()},
            {ROOT, "myMap.array[0]", new Integer(ROOT.getArray()[0])},
            {ROOT, "myMap.list[1]", ROOT.getList().get(1)},
            {ROOT, "myMap[^]", new Integer(99)},
            {ROOT, "myMap[$]", null},
            {ROOT.getMyMap(), "array[$]", new Integer(ROOT.getArray()[ROOT.getArray().length - 1])},
            {ROOT, "[\"myMap\"]", ROOT.getMyMap()},
            {ROOT, "myMap[null]", null},
            {ROOT, "myMap[#x = null]", null},
            {ROOT, "myMap.(null,test)", ROOT},
            {ROOT, "myMap[null] = 25", new Integer(25)},
            {ROOT, "myMap[null]", new Integer(25), new Integer(50), new Integer(50)},
            {ROOT, "beans.testBean", ROOT.getBeans().getBean("testBean")},
            {ROOT, "beans.evenOdd.next", "even"},
            {ROOT, "map.comp.form.clientId", "form1"},
            {ROOT, "map.comp.getCount(genericIndex)", Integer.valueOf(0)},
            {ROOT, "map.customList.total", Integer.valueOf(1)},
            {ROOT, "myTest.theMap['key']", "value" },
            {ROOT, "contentProvider.hasChildren(property)", Boolean.TRUE},
            {ROOT, "objectIndex instanceof java.lang.Object", Boolean.TRUE}
    };

    /*
     * =================================================================== Public static methods
     * ===================================================================
     */
    public static TestSuite suite()
    {
        TestSuite result = new TestSuite();

        for (int i = 0; i < TESTS.length; i++) {
            if (TESTS[i].length == 3) {
                result.addTest(new InterfaceInheritanceTest((String) TESTS[i][1], TESTS[i][0], (String) TESTS[i][1],
                                                            TESTS[i][2]));
            } else {
                if (TESTS[i].length == 4) {
                    result.addTest(new InterfaceInheritanceTest((String) TESTS[i][1], TESTS[i][0],
                                                                (String) TESTS[i][1], TESTS[i][2], TESTS[i][3]));
                } else {
                    if (TESTS[i].length == 5) {
                        result.addTest(new InterfaceInheritanceTest((String) TESTS[i][1], TESTS[i][0],
                                                                    (String) TESTS[i][1], TESTS[i][2], TESTS[i][3], TESTS[i][4]));
                    } else {
                        throw new RuntimeException("don't understand TEST format");
                    }
                }
            }
        }

        return result;
    }

    /*
    * =================================================================== Constructors
    * ===================================================================
    */
    public InterfaceInheritanceTest()
    {
        super();
    }

    public InterfaceInheritanceTest(String name)
    {
        super(name);
    }

    public InterfaceInheritanceTest(String name, Object root, String expressionString, Object expectedResult,
                                    Object setValue, Object expectedAfterSetResult)
    {
        super(name, root, expressionString, expectedResult, setValue, expectedAfterSetResult);
    }

    public InterfaceInheritanceTest(String name, Object root, String expressionString, Object expectedResult,
                                    Object setValue)
    {
        super(name, root, expressionString, expectedResult, setValue);
    }

    public InterfaceInheritanceTest(String name, Object root, String expressionString, Object expectedResult)
    {
        super(name, root, expressionString, expectedResult);
    }

    public void setUp()
    {
        super.setUp();

        OgnlRuntime.setPropertyAccessor(BeanProvider.class, new BeanProviderAccessor());
    }
}

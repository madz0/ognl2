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
import com.github.madz0.ognl2.MethodFailedException;
import com.github.madz0.ognl2.NoSuchPropertyException;
import org.ognl.test.objects.IndexedSetObject;
import org.ognl.test.objects.Root;

public class IndexAccessTest extends OgnlTestCase {

    private static Root ROOT = new Root();
    private static IndexedSetObject INDEXED_SET = new IndexedSetObject();

    private static Object[][] TESTS = {
            {ROOT, "list[index]", ROOT.getList().get(ROOT.getIndex())},
            {ROOT, "list[objectIndex]", ROOT.getList().get(ROOT.getObjectIndex().intValue())},
            {ROOT, "array[objectIndex]", ROOT.getArray()[ROOT.getObjectIndex().intValue()] },
            {ROOT, "array[getObjectIndex()]", ROOT.getArray()[ROOT.getObjectIndex().intValue()] },
            {ROOT, "array[genericIndex]", ROOT.getArray()[((Integer)ROOT.getGenericIndex()).intValue()] },
            {ROOT, "booleanArray[self.objectIndex]", Boolean.FALSE },
            {ROOT, "booleanArray[getObjectIndex()]", Boolean.FALSE },
            {ROOT, "booleanArray[nullIndex]", NoSuchPropertyException.class},
            {ROOT, "list[size() - 1]", MethodFailedException.class},
            {ROOT, "(index == (array.length - 3)) ? 'toggle toggleSelected' : 'toggle'", "toggle toggleSelected"},
            {ROOT, "\"return toggleDisplay('excdisplay\"+index+\"', this)\"", "return toggleDisplay('excdisplay1', this)"},
            {ROOT, "map[mapKey].split('=')[0]", "StringStuff"},
            {ROOT, "booleanValues[index1][index2]", Boolean.FALSE},
            {ROOT, "tab.searchCriteria[index1].displayName", "Woodland creatures"},
            {ROOT, "tab.searchCriteriaSelections[index1][index2]", Boolean.TRUE},
            {ROOT, "tab.searchCriteriaSelections[index1][index2]", Boolean.TRUE, Boolean.FALSE, Boolean.FALSE},
            {ROOT, "map['bar'].value", 100, 50, 50},
            {INDEXED_SET, "thing[\"x\"].val", 1, 2, 2}
    };

    /*
     * =================================================================== Public static methods
     * ===================================================================
     */
    public static TestSuite suite()
    {
        TestSuite result = new TestSuite();

        for (int i = 0; i < TESTS.length; i++)
        {
            if (TESTS[i].length == 5)
            {
                result.addTest(new IndexAccessTest((String) TESTS[i][1], TESTS[i][0], (String) TESTS[i][1], TESTS[i][2],
                                                   TESTS[i][3], TESTS[i][4]));
            } else
            {
                result.addTest(new IndexAccessTest((String) TESTS[i][1], TESTS[i][0], (String) TESTS[i][1], TESTS[i][2]));
            }
        }
        return result;
    }

    /*
    * =================================================================== Constructors
    * ===================================================================
    */
    public IndexAccessTest()
    {
        super();
    }

    public IndexAccessTest(String name)
    {
        super(name);
    }

    public IndexAccessTest(String name, Object root, String expressionString, Object expectedResult,
                           Object setValue, Object expectedAfterSetResult)
    {
        super(name, root, expressionString, expectedResult, setValue, expectedAfterSetResult);
    }

    public IndexAccessTest(String name, Object root, String expressionString, Object expectedResult, Object setValue)
    {
        super(name, root, expressionString, expectedResult, setValue);
    }

    public IndexAccessTest(String name, Object root, String expressionString, Object expectedResult)
    {
        super(name, root, expressionString, expectedResult);
    }

    public void setUp()
    {
        super.setUp();
    }
}

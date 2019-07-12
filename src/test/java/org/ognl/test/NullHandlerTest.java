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
import org.ognl.test.objects.CorrectedObject;

public class NullHandlerTest extends OgnlTestCase
{
    private static CorrectedObject CORRECTED = new CorrectedObject();

    private static Object[][] TESTS = {
            // NullHandler
            { CORRECTED, "stringValue", "corrected" },
            { CORRECTED, "getStringValue()", "corrected" },
            { CORRECTED, "#root.stringValue", "corrected" },
            { CORRECTED, "#root.getStringValue()", "corrected" },
    };

    /*
     * =================================================================== Public static methods
     * ===================================================================
     */
    public static TestSuite suite()
    {
        TestSuite result = new TestSuite();

        for(int i = 0; i < TESTS.length; i++) {
            if (TESTS[i].length == 3) {
                result
                        .addTest(new NullHandlerTest((String) TESTS[i][1], TESTS[i][0], (String) TESTS[i][1],
                                                     TESTS[i][2]));
            } else {
                if (TESTS[i].length == 4) {
                    result.addTest(new NullHandlerTest((String) TESTS[i][1], TESTS[i][0], (String) TESTS[i][1],
                                                       TESTS[i][2], TESTS[i][3]));
                } else {
                    if (TESTS[i].length == 5) {
                        result.addTest(new NullHandlerTest((String) TESTS[i][1], TESTS[i][0], (String) TESTS[i][1],
                                                           TESTS[i][2], TESTS[i][3], TESTS[i][4]));
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
    public NullHandlerTest()
    {
        super();
    }

    public NullHandlerTest(String name)
    {
        super(name);
    }

    public NullHandlerTest(String name, Object root, String expressionString, Object expectedResult, Object setValue,
                           Object expectedAfterSetResult)
    {
        super(name, root, expressionString, expectedResult, setValue, expectedAfterSetResult);
    }

    public NullHandlerTest(String name, Object root, String expressionString, Object expectedResult, Object setValue)
    {
        super(name, root, expressionString, expectedResult, setValue);
    }

    public NullHandlerTest(String name, Object root, String expressionString, Object expectedResult)
    {
        super(name, root, expressionString, expectedResult);
    }

    /*
     * =================================================================== Overridden methods
     * ===================================================================
     */
    public void setUp()
    {
        super.setUp();
        _compileExpressions = false;
        OgnlRuntime.setNullHandler(CorrectedObject.class, new CorrectedObjectNullHandler("corrected"));
    }
}

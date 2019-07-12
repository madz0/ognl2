//--------------------------------------------------------------------------
//  Copyright (c) 2004, Drew Davidson and Luke Blanshard
//  All rights reserved.
//
//  Redistribution and use in source and binary forms, with or without
//  modification, are permitted provided that the following conditions are
//  met:
//
//  Redistributions of source code must retain the above copyright notice,
//  this list of conditions and the following disclaimer.
//  Redistributions in binary form must reproduce the above copyright
//  notice, this list of conditions and the following disclaimer in the
//  documentation and/or other materials provided with the distribution.
//  Neither the name of the Drew Davidson nor the names of its contributors
//  may be used to endorse or promote products derived from this software
//  without specific prior written permission.
//
//  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
//  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
//  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
//  FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
//  COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
//  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
//  BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
//  OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
//  AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
//  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
//  THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
//  DAMAGE.
//--------------------------------------------------------------------------
package org.ognl.test;

import junit.framework.TestSuite;
import com.github.madz0.ognl2.OgnlException;
import org.ognl.test.objects.Simple;

import java.math.BigDecimal;
import java.math.BigInteger;

public class NumberFormatExceptionTest extends OgnlTestCase
{
    private static Simple           SIMPLE = new Simple();

    private static Object[][]       TESTS = {
                                        // NumberFormatException handling (default is to throw NumberFormatException on bad string conversions)
                                        { SIMPLE, "floatValue", new Float(0f), new Float(10f), new Float(10f) },   
                                        { SIMPLE, "floatValue", new Float(10f), "x10x", OgnlException.class },      

                                        { SIMPLE, "intValue", new Integer(0), new Integer(34), new Integer(34) },   
                                        { SIMPLE, "intValue", new Integer(34), "foobar", OgnlException.class },    
                                        { SIMPLE, "intValue", new Integer(34), "", OgnlException.class },          
                                        { SIMPLE, "intValue", new Integer(34), "       \t", OgnlException.class }, 
                                        { SIMPLE, "intValue", new Integer(34), "       \t1234\t\t", new Integer(1234) },
                                        
                                        { SIMPLE, "bigIntValue", BigInteger.valueOf(0), BigInteger.valueOf(34), BigInteger.valueOf(34) },
                                        { SIMPLE, "bigIntValue", BigInteger.valueOf(34), null, null },
                                        { SIMPLE, "bigIntValue", null, "", OgnlException.class },    
                                        { SIMPLE, "bigIntValue", null, "foobar", OgnlException.class },      

                                        { SIMPLE, "bigDecValue", new BigDecimal(0.0), new BigDecimal(34.55), new BigDecimal(34.55) },   
                                        { SIMPLE, "bigDecValue", new BigDecimal(34.55), null, null },           
                                        { SIMPLE, "bigDecValue", null, "", OgnlException.class },              
                                        { SIMPLE, "bigDecValue", null, "foobar", OgnlException.class }
                                        
                                    };

	/*===================================================================
		Public static methods
	  ===================================================================*/
    public static TestSuite suite()
    {
        TestSuite       result = new TestSuite();

        for (int i = 0; i < TESTS.length; i++) {
            if (TESTS[i].length == 3) {
                result.addTest(new NumberFormatExceptionTest((String)TESTS[i][1], TESTS[i][0], (String)TESTS[i][1], TESTS[i][2]));
            } else {
                if (TESTS[i].length == 4) {
                    result.addTest(new NumberFormatExceptionTest((String)TESTS[i][1], TESTS[i][0], (String)TESTS[i][1], TESTS[i][2], TESTS[i][3]));
                } else {
                    if (TESTS[i].length == 5) {
                        result.addTest(new NumberFormatExceptionTest((String)TESTS[i][1], TESTS[i][0], (String)TESTS[i][1], TESTS[i][2], TESTS[i][3], TESTS[i][4]));
                    } else {
                        throw new RuntimeException("don't understand TEST format");
                    }
                }
            }
        }
        return result;
    }

	/*===================================================================
		Constructors
	  ===================================================================*/
	public NumberFormatExceptionTest()
	{
	    super();
	}

	public NumberFormatExceptionTest(String name)
	{
	    super(name);
	}

    public NumberFormatExceptionTest(String name, Object root, String expressionString, Object expectedResult, Object setValue, Object expectedAfterSetResult)
    {
        super(name, root, expressionString, expectedResult, setValue, expectedAfterSetResult);
    }

    public NumberFormatExceptionTest(String name, Object root, String expressionString, Object expectedResult, Object setValue)
    {
        super(name, root, expressionString, expectedResult, setValue);
    }

    public NumberFormatExceptionTest(String name, Object root, String expressionString, Object expectedResult)
    {
        super(name, root, expressionString, expectedResult);
    }
}

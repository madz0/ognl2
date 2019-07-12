package com.github.madz0.ognl2;

import junit.framework.TestCase;

/**
 * Test for OGNL-118.
 */
public class InExpressionTest extends TestCase {

    public void test_String_In()
            throws Exception
    {
        OgnlContext context = (OgnlContext) Ognl.createDefaultContext(null, new DefaultMemberAccess(false));
        Node node = Ognl.parseExpression("#name in {\"Greenland\", \"Austin\", \"Africa\", \"Rome\"}");
        Object root = null;

        context.put("name", "Austin");
        assertEquals(Boolean.TRUE, Ognl.getValue(node, context, root));
    }
}

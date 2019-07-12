package org.ognl.test;

import junit.framework.TestCase;
import com.github.madz0.ognl2.DefaultMemberAccess;
import com.github.madz0.ognl2.Ognl;
import com.github.madz0.ognl2.OgnlContext;
import com.github.madz0.ognl2.SimpleNode;

/**
 * Tests for {@link SimpleNode#isChain(OgnlContext)}.
 */
public class ChainTest extends TestCase {

    public void test_isChain() throws Exception {
        OgnlContext context = (OgnlContext) Ognl.createDefaultContext(null, new DefaultMemberAccess(false));

        SimpleNode node = (SimpleNode) Ognl.parseExpression("#name");
        assertFalse(node.isChain(context));

        node = (SimpleNode) Ognl.parseExpression("#name.lastChar");
        assertTrue(node.isChain(context));

        node = (SimpleNode) Ognl.parseExpression("#{name.lastChar, #boo}");
        assertTrue(node.isChain(context));

        node = (SimpleNode) Ognl.parseExpression("boo = #{name.lastChar, #boo, foo()}");
        assertTrue(node.isChain(context));

        node = (SimpleNode) Ognl.parseExpression("{name.lastChar, #boo, foo()}");
        assertTrue(node.isChain(context));

        node = (SimpleNode) Ognl.parseExpression("(name.lastChar, #boo, foo())");
        assertTrue(node.isChain(context));
    }

}

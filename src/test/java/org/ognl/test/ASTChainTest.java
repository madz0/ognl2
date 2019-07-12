package org.ognl.test;

import com.github.madz0.ognl2.ASTChain;
import junit.framework.TestCase;
import com.github.madz0.ognl2.DefaultMemberAccess;
import com.github.madz0.ognl2.Ognl;
import com.github.madz0.ognl2.OgnlContext;
import org.ognl.test.objects.IndexedSetObject;

/**
 * Tests for {@link ASTChain}.
 */
public class ASTChainTest extends TestCase {

    public void test_Get_Indexed_Value() throws Exception {

        OgnlContext context = (OgnlContext) Ognl.createDefaultContext(null, new DefaultMemberAccess(false));
        IndexedSetObject root = new IndexedSetObject();

        String expr = "thing[\"x\"].val";

        assertEquals(1, Ognl.getValue(expr, context, root));

        Ognl.setValue(expr, context, root, new Integer(2));
        
        assertEquals(2, Ognl.getValue(expr, context, root));
    }
}

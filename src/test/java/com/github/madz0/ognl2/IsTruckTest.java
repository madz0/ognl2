package com.github.madz0.ognl2;

import junit.framework.TestCase;

import java.util.List;

public class IsTruckTest extends TestCase {

    public void testIsTruckMethod() throws Exception{
        OgnlContext context = (OgnlContext) Ognl.createDefaultContext(null, new DefaultMemberAccess(false));
        boolean actual = (Boolean) Ognl.getValue("isTruck", context, new TruckHolder());

        assertTrue(actual);
    }

}

class TruckHolder {

    public boolean getIsTruck() {
        return true;
    }

}

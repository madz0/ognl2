// --------------------------------------------------------------------------
// Copyright (c) 1998-2004, Drew Davidson and Luke Blanshard
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
package com.github.madz0.ognl2;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author Luke Blanshard (blanshlu@netscape.net)
 * @author Drew Davidson (drew@ognl.org)
 */
public class ASTStaticField extends SimpleNode implements NodeType
{

    private String className;
    private String fieldName;

    private Class _getterClass;

    public ASTStaticField(int id)
    {
        super(id);
    }

    public ASTStaticField(OgnlParser p, int id)
    {
        super(p, id);
    }

    /** Called from parser action. */
    void init(String className, String fieldName)
    {
        this.className = className;
        this.fieldName = fieldName;
    }

    protected Object getValueBody(OgnlContext context, Object source)
            throws OgnlException
    {
        return OgnlRuntime.getStaticField(context, className, fieldName);
    }

    public boolean isNodeConstant(OgnlContext context)
            throws OgnlException
    {
        boolean result = false;
        Exception reason = null;

        try {
            Class c = OgnlRuntime.classForName(context, className);

            /*
            * Check for virtual static field "class"; this cannot interfere with normal static
            * fields because it is a reserved word. It is considered constant.
            */
            if (fieldName.equals("class"))
            {
                result = true;
            } else if (OgnlRuntime.isJdk15() && c.isEnum())
            {
                result = true;
            } else
            {
                Field f = OgnlRuntime.getField(c, fieldName);
                if (f == null) {
                    throw new NoSuchFieldException(fieldName);
                }

                if (!Modifier.isStatic(f.getModifiers()))
                    throw new OgnlException("Field " + fieldName + " of class " + className + " is not static");

                result = Modifier.isFinal(f.getModifiers());
            }
        } catch (ClassNotFoundException e) {
            reason = e;
        } catch (NoSuchFieldException e) {
            reason = e;
        } catch (SecurityException e) {
            reason = e;
        }

        if (reason != null)
            throw new OgnlException("Could not get static field " + fieldName
                                    + " from class " + className, reason);

        return result;
    }

    Class getFieldClass(OgnlContext context)
            throws OgnlException
    {
        Exception reason = null;

        try {
            Class c = OgnlRuntime.classForName(context, className);

            /*
            * Check for virtual static field "class"; this cannot interfere with normal static
            * fields because it is a reserved word. It is considered constant.
            */
            if (fieldName.equals("class"))
            {
                return c;
            } else if (OgnlRuntime.isJdk15() && c.isEnum())
            {
                return c;
            } else
            {
                Field f = c.getField(fieldName);

                return f.getType();
            }
        } catch (ClassNotFoundException e) {
            reason = e;
        } catch (NoSuchFieldException e) {
            reason = e;
        } catch (SecurityException e) {
            reason = e;
        }

        if (reason != null) { throw new OgnlException("Could not get static field " + fieldName + " from class "
                                                      + className, reason); }

        return null;
    }

    public Class getGetterClass()
    {
        return _getterClass;
    }

    public Class getSetterClass()
    {
        return _getterClass;
    }

    public String toString()
    {
        return "@" + className + "@" + fieldName;
    }

    public String toGetSourceString(OgnlContext context, Object target)
    {
        try {

            Object obj = OgnlRuntime.getStaticField(context, className, fieldName);

            context.setCurrentObject(obj);

            _getterClass = getFieldClass(context);

            context.setCurrentType(_getterClass);

        } catch (Throwable t)
        {
            throw OgnlOps.castToRuntime(t);
        }

        return className + "." + fieldName;
    }

    public String toSetSourceString(OgnlContext context, Object target)
    {
        try {

            Object obj = OgnlRuntime.getStaticField(context, className, fieldName);

            context.setCurrentObject(obj);

            _getterClass = getFieldClass(context);

            context.setCurrentType(_getterClass);

        } catch (Throwable t)
        {
            throw OgnlOps.castToRuntime(t);
        }

        return className + "." + fieldName;
    }
}

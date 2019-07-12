//--------------------------------------------------------------------------
//	Copyright (c) 2002, Drew Davidson and Luke Blanshard
//  All rights reserved.
//
//	Redistribution and use in source and binary forms, with or without
//  modification, are permitted provided that the following conditions are
//  met:
//
//	Redistributions of source code must retain the above copyright notice,
//  this list of conditions and the following disclaimer.
//	Redistributions in binary form must reproduce the above copyright
//  notice, this list of conditions and the following disclaimer in the
//  documentation and/or other materials provided with the distribution.
//	Neither the name of the Drew Davidson nor the names of its contributors
//  may be used to endorse or promote products derived from this software
//  without specific prior written permission.
//
//	THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
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
package com.github.madz0.ognl2;

/**
 * @author Luke Blanshard (blanshlu@netscape.net)
 * @author Drew Davidson (drew@ognl.org)
 */
public class ASTInstanceof extends SimpleNode implements NodeType
{
    private String targetType;

    public ASTInstanceof(int id) {
        super(id);
    }

    public ASTInstanceof(OgnlParser p, int id) {
        super(p, id);
    }

    void setTargetType( String targetType ) {
        this.targetType = targetType;
    }

    protected Object getValueBody( OgnlContext context, Object source ) throws OgnlException
    {
        Object value = _children[0].getValue( context, source );
        return OgnlRuntime.isInstance(context, value, targetType) ? Boolean.TRUE : Boolean.FALSE;
    }

    public String toString()
    {
        return _children[0] + " instanceof " + targetType;
    }
    
    public Class getGetterClass()
    {
        return boolean.class;
    }
    
    public Class getSetterClass()
    {
        return null;
    }
    
    public String toGetSourceString(OgnlContext context, Object target)
    {
        try {

            String ret = "";

            if (ASTConst.class.isInstance(_children[0]))
                ret = ((Boolean)getValueBody(context, target)).toString();
            else
                ret = _children[0].toGetSourceString(context, target) + " instanceof " + targetType;
            
            context.setCurrentType(Boolean.TYPE);

            return ret;

        } catch (Throwable t)
        {
            throw OgnlOps.castToRuntime(t);
        }
    }
    
    public String toSetSourceString(OgnlContext context, Object target)
    {
        return toGetSourceString(context, target);
    }
}

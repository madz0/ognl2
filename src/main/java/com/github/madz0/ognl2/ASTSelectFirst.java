//--------------------------------------------------------------------------
//	Copyright (c) 1998-2004, Drew Davidson and Luke Blanshard
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

import com.github.madz0.ognl2.enhance.UnsupportedCompilationException;
import com.github.madz0.ognl2.enhance.UnsupportedCompilationException;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author Luke Blanshard (blanshlu@netscape.net)
 * @author Drew Davidson (drew@ognl.org)
 */
class ASTSelectFirst extends SimpleNode
{
    public ASTSelectFirst(int id) {
        super(id);
    }

    public ASTSelectFirst(OgnlParser p, int id) {
        super(p, id);
    }

    protected Object getValueBody( OgnlContext context, Object source ) throws OgnlException
    {
        Node                expr = _children[0];
        List                answer = new ArrayList();
        ElementsAccessor    elementsAccessor = OgnlRuntime.getElementsAccessor( OgnlRuntime.getTargetClass(source) );

        for (Enumeration e = elementsAccessor.getElements(source); e.hasMoreElements(); ) {
            Object      next = e.nextElement();

            if (OgnlOps.booleanValue(expr.getValue(context, next))) {
                answer.add(next);
                break;
            }
        }
        return answer;
    }

    public String toString()
    {
        return "{^ " + _children[0] + " }";
    }
    
    public String toGetSourceString(OgnlContext context, Object target)
    {
        throw new UnsupportedCompilationException("Eval expressions not supported as native java yet.");
    }
    
    public String toSetSourceString(OgnlContext context, Object target)
    {
        throw new UnsupportedCompilationException("Eval expressions not supported as native java yet.");
    }
}

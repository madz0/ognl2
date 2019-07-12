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

import com.github.madz0.ognl2.enhance.UnsupportedCompilationException;
import com.github.madz0.ognl2.enhance.UnsupportedCompilationException;

/**
 * @author Luke Blanshard (blanshlu@netscape.net)
 * @author Drew Davidson (drew@ognl.org)
 */
class ASTEval extends SimpleNode
{

    public ASTEval(int id)
    {
        super(id);
    }

    public ASTEval(OgnlParser p, int id)
    {
        super(p, id);
    }

    protected Object getValueBody(OgnlContext context, Object source)
        throws OgnlException
    {
        Object result, expr = _children[0].getValue(context, source), previousRoot = context.getRoot();
        Node node;

        source = _children[1].getValue(context, source);
        node = (expr instanceof Node) ? (Node) expr : (Node) Ognl.parseExpression(expr.toString());
        try {
            context.setRoot(source);
            result = node.getValue(context, source);
        } finally {
            context.setRoot(previousRoot);
        }
        return result;
    }

    protected void setValueBody(OgnlContext context, Object target, Object value)
        throws OgnlException
    {
        Object expr = _children[0].getValue(context, target), previousRoot = context.getRoot();
        Node node;

        target = _children[1].getValue(context, target);
        node = (expr instanceof Node) ? (Node) expr : (Node) Ognl.parseExpression(expr.toString());
        try {
            context.setRoot(target);
            node.setValue(context, target, value);
        } finally {
            context.setRoot(previousRoot);
        }
    }

    @Override
    public boolean isEvalChain(OgnlContext context) throws OgnlException {
        return true;
    }

    public String toString()
    {
        return "(" + _children[0] + ")(" + _children[1] + ")";
    }
    
    public String toGetSourceString(OgnlContext context, Object target)
    {
        throw new UnsupportedCompilationException("Eval expressions not supported as native java yet.");
    }
    
    public String toSetSourceString(OgnlContext context, Object target)
    {
        throw new UnsupportedCompilationException("Map expressions not supported as native java yet.");
    }
}

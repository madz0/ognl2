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

import java.util.List;
import java.util.Map;

/**
 * Implementation of PropertyAccessor that uses reflection on the target object's class to find a
 * field or a pair of set/get methods with the given property name.
 *
 * @author Luke Blanshard (blanshlu@netscape.net)
 * @author Drew Davidson (drew@ognl.org)
 */
public class ObjectMethodAccessor implements MethodAccessor
{

    /* MethodAccessor interface */
    public Object callStaticMethod(Map context, Class targetClass, String methodName, Object[] args)
            throws MethodFailedException
    {
        List methods = OgnlRuntime.getMethods(targetClass, methodName, true);

        return OgnlRuntime.callAppropriateMethod((OgnlContext) context, targetClass,
                                                 null, methodName, null, methods, args);
    }

    public Object callMethod(Map context, Object target, String methodName, Object[] args)
            throws MethodFailedException
    {
        Class targetClass = (target == null) ? null : target.getClass();
        List methods = OgnlRuntime.getMethods(targetClass, methodName, false);

        if ((methods == null) || (methods.size() == 0))
        {
            methods = OgnlRuntime.getMethods(targetClass, methodName, true);

        }

        return OgnlRuntime.callAppropriateMethod((OgnlContext) context, target,
                                                 target, methodName, null, methods, args);
    }
}

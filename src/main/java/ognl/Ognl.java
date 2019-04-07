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
package ognl;

import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import ognl.enhance.ExpressionAccessor;
import ognl.extended.Config;
import ognl.extended.MapNode;
import ognl.internal.extended.MutableInt;

import static ognl.extended.Config.CURRENT_INDEX_KEY;

/**
 * <p>
 * This class provides static methods for parsing and interpreting OGNL expressions.
 * </P>
 * <p>
 * The simplest use of the Ognl class is to get the value of an expression from an object, without
 * extra context or pre-parsing.
 * </P>
 *
 * <PRE>
 * <p>
 * import ognl.Ognl; import ognl.OgnlException; try { result = Ognl.getValue(expression, root); }
 * catch (OgnlException ex) { // Report error or recover }
 *
 * </PRE>
 *
 * <p>
 * This will parse the expression given and evaluate it against the root object given, returning the
 * result. If there is an error in the expression, such as the property is not found, the exception
 * is encapsulated into an {@link ognl.OgnlException OgnlException}.
 * </P>
 * <p>
 * Other more sophisticated uses of Ognl can pre-parse expressions. This provides two advantages: in
 * the case of user-supplied expressions it allows you to catch parse errors before evaluation and
 * it allows you to cache parsed expressions into an AST for better speed during repeated use. The
 * pre-parsed expression is always returned as an <CODE>Object</CODE> to simplify use for programs
 * that just wish to store the value for repeated use and do not care that it is an AST. If it does
 * care it can always safely cast the value to an <CODE>AST</CODE> type.
 * </P>
 * <p>
 * The Ognl class also takes a <I>context map</I> as one of the parameters to the set and get
 * methods. This allows you to put your own variables into the available namespace for OGNL
 * expressions. The default context contains only the <CODE>#root</CODE> and <CODE>#context</CODE>
 * keys, which are required to be present. The <CODE>addDefaultContext(Object, Map)</CODE> method
 * will alter an existing <CODE>Map</CODE> to put the defaults in. Here is an example that shows
 * how to extract the <CODE>documentName</CODE> property out of the root object and append a
 * string with the current user name in parens:
 * </P>
 *
 * <PRE>
 * <p>
 * private Map context = new HashMap(); public void setUserName(String value) {
 * context.put("userName", value); } try { // get value using our own custom context map result =
 * Ognl.getValue("documentName + \" (\" + ((#userName == null) ? \"&lt;nobody&gt;\" : #userName) +
 * \")\"", context, root); } catch (OgnlException ex) { // Report error or recover }
 *
 * </PRE>
 *
 * @author Luke Blanshard (blanshlu@netscape.net)
 * @author Drew Davidson (drew@ognl.org)
 * @version 27 June 1999
 */
public abstract class Ognl {

    /**
     * Parses the given OGNL expression and returns a tree representation of the expression that can
     * be used by <CODE>Ognl</CODE> static methods.
     *
     * @param expression the OGNL expression to be parsed
     * @return a tree representation of the expression
     * @throws ExpressionSyntaxException if the expression is malformed
     * @throws OgnlException             if there is a pathological environmental problem
     */
    public static Node parseExpression(String expression)
            throws OgnlException {
        try {
            OgnlParser parser = new OgnlParser(new StringReader(expression));
            return parser.topLevelExpression();
        } catch (ParseException e) {
            throw new ExpressionSyntaxException(expression, e);
        } catch (TokenMgrError e) {
            throw new ExpressionSyntaxException(expression, e);
        }
    }

    /**
     * Parses and compiles the given expression using the {@link ognl.enhance.OgnlExpressionCompiler} returned
     * from {@link ognl.OgnlRuntime#getCompiler()}.
     *
     * @param context    The context to use.
     * @param root       The root object for the given expression.
     * @param expression The expression to compile.
     * @return The node with a compiled accessor set on {@link ognl.Node#getAccessor()} if compilation
     * was successfull. In instances where compilation wasn't possible because of a partially null
     * expression the {@link ExpressionAccessor} instance may be null and the compilation of this expression
     * still possible at some as yet indertermined point in the future.
     * @throws Exception If a compilation error occurs.
     */
    public static Node compileExpression(OgnlContext context, Object root, String expression)
            throws Exception {
        Node expr = (Node) Ognl.parseExpression(expression);

        OgnlRuntime.compileExpression(context, expr, root);

        return expr;
    }

    /**
     * Creates and returns a new standard naming context for evaluating an OGNL expression.
     *
     * @param root the root of the object graph
     * @return a new Map with the keys <CODE>root</CODE> and <CODE>context</CODE> set
     * appropriately
     * @deprecated it will be removed soon
     */
    @Deprecated
    public static Map createDefaultContext(Object root) {
        return addDefaultContext(root, null, null, null, new OgnlContext(null, null, null));
    }

    /**
     * Creates and returns a new standard naming context for evaluating an OGNL expression.
     *
     * @param root          The root of the object graph.
     * @param classResolver The resolver used to instantiate {@link Class} instances referenced in the expression.
     * @return a new OgnlContext with the keys <CODE>root</CODE> and <CODE>context</CODE> set
     * appropriately
     * @deprecated it will be removed soon
     */
    @Deprecated
    public static Map createDefaultContext(Object root, ClassResolver classResolver) {
        return addDefaultContext(root, null, classResolver, null, new OgnlContext(classResolver, null, null));
    }

    /**
     * Creates and returns a new standard naming context for evaluating an OGNL expression.
     *
     * @param root          The root of the object graph.
     * @param classResolver The resolver used to instantiate {@link Class} instances referenced in the expression.
     * @param converter     Converter used to convert return types of an expression in to their desired types.
     * @return a new Map with the keys <CODE>root</CODE> and <CODE>context</CODE> set
     * appropriately
     * @deprecated it will be removed soon
     */
    @Deprecated
    public static Map createDefaultContext(Object root, ClassResolver classResolver, TypeConverter converter) {
        return addDefaultContext(root, null, classResolver, converter, new OgnlContext(classResolver, converter, null));
    }

    /**
     * Creates and returns a new standard naming context for evaluating an OGNL expression.
     *
     * @param root          The root of the object graph.
     * @param memberAccess  Java security handling object to determine semantics for accessing normally private/protected
     *                      methods / fields.
     * @param classResolver The resolver used to instantiate {@link Class} instances referenced in the expression.
     * @param converter     Converter used to convert return types of an expression in to their desired types.
     * @return a new Map with the keys <CODE>root</CODE> and <CODE>context</CODE> set
     * appropriately
     */
    public static Map createDefaultContext(Object root, MemberAccess memberAccess, ClassResolver classResolver,
                                           TypeConverter converter) {
        return addDefaultContext(root, memberAccess, classResolver, converter, new OgnlContext(classResolver, converter, memberAccess));
    }

    /**
     * Creates and returns a new standard naming context for evaluating an OGNL expression.
     *
     * @param root         The root of the object graph.
     * @param memberAccess Java security handling object to determine semantics for accessing normally private/protected
     *                     methods / fields.
     * @return a new Map with the keys <CODE>root</CODE> and <CODE>context</CODE> set
     * appropriately
     */
    public static Map createDefaultContext(Object root, MemberAccess memberAccess) {
        return addDefaultContext(root, memberAccess, null, null, new OgnlContext(null, null, memberAccess));
    }

    /**
     * Appends the standard naming context for evaluating an OGNL expression into the context given
     * so that cached maps can be used as a context.
     *
     * @param root    the root of the object graph
     * @param context the context to which OGNL context will be added.
     * @return Context Map with the keys <CODE>root</CODE> and <CODE>context</CODE> set
     * appropriately
     * @deprecated will be removed soon
     */
    @Deprecated
    public static Map addDefaultContext(Object root, Map context) {
        return addDefaultContext(root, null, null, null, context);
    }

    /**
     * Appends the standard naming context for evaluating an OGNL expression into the context given
     * so that cached maps can be used as a context.
     *
     * @param root          The root of the object graph.
     * @param classResolver The resolver used to instantiate {@link Class} instances referenced in the expression.
     * @param context       The context to which OGNL context will be added.
     * @return Context Map with the keys <CODE>root</CODE> and <CODE>context</CODE> set
     * appropriately
     */
    public static Map addDefaultContext(Object root, ClassResolver classResolver, Map context) {
        return addDefaultContext(root, null, classResolver, null, context);
    }

    /**
     * Appends the standard naming context for evaluating an OGNL expression into the context given
     * so that cached maps can be used as a context.
     *
     * @param root          The root of the object graph.
     * @param classResolver The resolver used to instantiate {@link Class} instances referenced in the expression.
     * @param converter     Converter used to convert return types of an expression in to their desired types.
     * @param context       The context to which OGNL context will be added.
     * @return Context Map with the keys <CODE>root</CODE> and <CODE>context</CODE> set
     * appropriately
     */
    public static Map addDefaultContext(Object root, ClassResolver classResolver,
                                        TypeConverter converter, Map context) {
        return addDefaultContext(root, null, classResolver, converter, context);
    }

    /**
     * Appends the standard naming context for evaluating an OGNL expression into the context given
     * so that cached maps can be used as a context.
     *
     * @param root          the root of the object graph
     * @param memberAccess  Definition for handling private/protected access.
     * @param classResolver The class loading resolver that should be used to resolve class references.
     * @param converter     The type converter to be used by default.
     * @param context       Default context to use, if not an {@link OgnlContext} will be dumped into
     *                      a new {@link OgnlContext} object.
     * @return Context Map with the keys <CODE>root</CODE> and <CODE>context</CODE> set
     * appropriately
     */
    public static Map addDefaultContext(Object root, MemberAccess memberAccess, ClassResolver classResolver,
                                        TypeConverter converter, Map context) {
        OgnlContext result;

        if (context instanceof OgnlContext) {
            result = (OgnlContext) context;
        } else {
            result = new OgnlContext(memberAccess, classResolver, converter, context);
        }

        result.setRoot(root);
        return result;
    }

    /**
     * Configures the {@link ClassResolver} to use for the given context.  Will be used during
     * expression parsing / execution to resolve class names.
     *
     * @param context       The context to place the resolver.
     * @param classResolver The resolver to use to resolve classes.
     * @deprecated it ignores any attempts to modify ClassResolver, ClassResolver can be defined
     * only when creating a new context
     */
    public static void setClassResolver(Map context, ClassResolver classResolver) {
        // noop
    }

    /**
     * Gets the previously stored {@link ClassResolver} for the given context - if any.
     *
     * @param context The context to get the configured resolver from.
     * @return The resolver instance, or null if none found.
     * @deprecated it always return null, access to class resolver was prohibited
     */
    public static ClassResolver getClassResolver(Map context) {
        return null;
    }

    /**
     * Configures the type converter to use for a given context. This will be used
     * to convert into / out of various java class types.
     *
     * @param context   The context to configure it for.
     * @param converter The converter to use.
     * @deprecated do not use
     */
    @Deprecated
    public static void setTypeConverter(Map context, TypeConverter converter) {
        // no-op
    }

    /**
     * Gets the currently configured {@link TypeConverter} for the given context - if any.
     *
     * @param context The context to get the converter from.
     * @return The converter - or null if none found.
     */
    public static TypeConverter getTypeConverter(Map context) {
        if (context instanceof OgnlContext) {
            return ((OgnlContext) context).getTypeConverter();
        }
        return null;
    }

    /**
     * Sets the root object to use for all expressions in the given context - doesn't necessarily replace
     * root object instances explicitly passed in to other expression resolving methods on this class.
     *
     * @param context The context to store the root object in.
     * @param root    The root object.
     */
    public static void setRoot(Map context, Object root) {
        context.put(OgnlContext.ROOT_CONTEXT_KEY, root);
    }

    /**
     * Gets the stored root object for the given context - if any.
     *
     * @param context The context to get the root object from.
     * @return The root object - or null if none found.
     */
    public static Object getRoot(Map context) {
        return context.get(OgnlContext.ROOT_CONTEXT_KEY);
    }

    /**
     * Gets the last {@link Evaluation} executed on the given context.
     *
     * @param context The context to get the evaluation from.
     * @return The {@link Evaluation} - or null if none was found.
     */
    public static Evaluation getLastEvaluation(Map context) {
        return (Evaluation) context.get(OgnlContext.LAST_EVALUATION_CONTEXT_KEY);
    }

    /**
     * Evaluates the given OGNL expression tree to extract a value from the given root object. The
     * default context is set for the given context and root via <CODE>addDefaultContext()</CODE>.
     *
     * @param tree    the OGNL expression tree to evaluate, as returned by parseExpression()
     * @param context the naming context for the evaluation
     * @param root    the root object for the OGNL expression
     * @return the result of evaluating the expression
     * @throws MethodFailedException            if the expression called a method which failed
     * @throws NoSuchPropertyException          if the expression referred to a nonexistent property
     * @throws InappropriateExpressionException if the expression can't be used in this context
     * @throws OgnlException                    if there is a pathological environmental problem
     */
    public static Object getValue(Node tree, Map context, Object root)
            throws OgnlException {
        return getValue(tree, context, root, null);
    }

    /**
     * Evaluates the given OGNL expression tree to extract a value from the given root object. The
     * default context is set for the given context and root via <CODE>addDefaultContext()</CODE>.
     *
     * @param tree       the OGNL expression tree to evaluate, as returned by parseExpression()
     * @param context    the naming context for the evaluation
     * @param root       the root object for the OGNL expression
     * @param resultType the converted type of the resultant object, using the context's type converter
     * @return the result of evaluating the expression
     * @throws MethodFailedException            if the expression called a method which failed
     * @throws NoSuchPropertyException          if the expression referred to a nonexistent property
     * @throws InappropriateExpressionException if the expression can't be used in this context
     * @throws OgnlException                    if there is a pathological environmental problem
     */
    public static Object getValue(Node tree, Map context, Object root, Class resultType)
            throws OgnlException {
        Object result;
        OgnlContext ognlContext = (OgnlContext) addDefaultContext(root, context);

        Node node = (Node) tree;

        if (node.getAccessor() != null)
            result = node.getAccessor().get(ognlContext, root);
        else
            result = node.getValue(ognlContext, root);

        if (resultType != null) {
            result = getTypeConverter(context).convertValue(context, root, null, null, result, resultType);
        }
        return result;
    }

    /**
     * Gets the value represented by the given pre-compiled expression on the specified root
     * object.
     *
     * @param expression The pre-compiled expression, as found in {@link Node#getAccessor()}.
     * @param context    The ognl context.
     * @param root       The object to retrieve the expression value from.
     * @return The value.
     */
    public static Object getValue(ExpressionAccessor expression, OgnlContext context, Object root) {
        return expression.get(context, root);
    }

    /**
     * Gets the value represented by the given pre-compiled expression on the specified root
     * object.
     *
     * @param expression The pre-compiled expression, as found in {@link Node#getAccessor()}.
     * @param context    The ognl context.
     * @param root       The object to retrieve the expression value from.
     * @param resultType The desired object type that the return value should be converted to using the {@link #getTypeConverter(java.util.Map)} }.
     * @return The value.
     */
    public static Object getValue(ExpressionAccessor expression, OgnlContext context,
                                  Object root, Class resultType) {
        return getTypeConverter(context).convertValue(context, root, null, null, expression.get(context, root), resultType);
    }

    /**
     * Evaluates the given OGNL expression to extract a value from the given root object in a given
     * context
     *
     * @param expression the OGNL expression to be parsed
     * @param context    the naming context for the evaluation
     * @param root       the root object for the OGNL expression
     * @return the result of evaluating the expression
     * @throws MethodFailedException            if the expression called a method which failed
     * @throws NoSuchPropertyException          if the expression referred to a nonexistent property
     * @throws InappropriateExpressionException if the expression can't be used in this context
     * @throws OgnlException                    if there is a pathological environmental problem
     * @see #parseExpression(String)
     * @see #getValue(Node, Object)
     */
    public static Object getValue(String expression, Map context, Object root)
            throws OgnlException {
        return getValue(expression, context, root, null);
    }

    /**
     * Evaluates the given OGNL expression to extract a value from the given root object in a given
     * context
     *
     * @param expression the OGNL expression to be parsed
     * @param context    the naming context for the evaluation
     * @param root       the root object for the OGNL expression
     * @param resultType the converted type of the resultant object, using the context's type converter
     * @return the result of evaluating the expression
     * @throws MethodFailedException            if the expression called a method which failed
     * @throws NoSuchPropertyException          if the expression referred to a nonexistent property
     * @throws InappropriateExpressionException if the expression can't be used in this context
     * @throws OgnlException                    if there is a pathological environmental problem
     * @see #parseExpression(String)
     * @see #getValue(Node, Object)
     */
    public static Object getValue(String expression, Map context, Object root, Class resultType)
            throws OgnlException {
        return getValue(parseExpression(expression), context, root, resultType);
    }

    /**
     * Evaluates the given OGNL expression tree to extract a value from the given root object.
     *
     * @param tree the OGNL expression tree to evaluate, as returned by parseExpression()
     * @param root the root object for the OGNL expression
     * @return the result of evaluating the expression
     * @throws MethodFailedException            if the expression called a method which failed
     * @throws NoSuchPropertyException          if the expression referred to a nonexistent property
     * @throws InappropriateExpressionException if the expression can't be used in this context
     * @throws OgnlException                    if there is a pathological environmental problem
     * @deprecated it will be removed soon
     */
    @Deprecated
    public static Object getValue(Node tree, Object root)
            throws OgnlException {
        return getValue(tree, root, null);
    }

    /**
     * Evaluates the given OGNL expression tree to extract a value from the given root object.
     *
     * @param tree       the OGNL expression tree to evaluate, as returned by parseExpression()
     * @param root       the root object for the OGNL expression
     * @param resultType the converted type of the resultant object, using the context's type converter
     * @return the result of evaluating the expression
     * @throws MethodFailedException            if the expression called a method which failed
     * @throws NoSuchPropertyException          if the expression referred to a nonexistent property
     * @throws InappropriateExpressionException if the expression can't be used in this context
     * @throws OgnlException                    if there is a pathological environmental problem
     */
    public static Object getValue(Node tree, Object root, Class resultType)
            throws OgnlException {
        return getValue(tree, createDefaultContext(root), root, resultType);
    }

    /**
     * Convenience method that combines calls to <code> parseExpression </code> and
     * <code> getValue</code>.
     *
     * @param expression the OGNL expression to be parsed
     * @param root       the root object for the OGNL expression
     * @return the result of evaluating the expression
     * @throws ExpressionSyntaxException        if the expression is malformed
     * @throws MethodFailedException            if the expression called a method which failed
     * @throws NoSuchPropertyException          if the expression referred to a nonexistent property
     * @throws InappropriateExpressionException if the expression can't be used in this context
     * @throws OgnlException                    if there is a pathological environmental problem
     * @see #parseExpression(String)
     * @see #getValue(Node, Object)
     */
    public static Object getValue(String expression, Object root)
            throws OgnlException {
        return getValue(expression, root, null);
    }

    /**
     * Convenience method that combines calls to <code> parseExpression </code> and
     * <code> getValue</code>.
     *
     * @param expression the OGNL expression to be parsed
     * @param root       the root object for the OGNL expression
     * @param resultType the converted type of the resultant object, using the context's type converter
     * @return the result of evaluating the expression
     * @throws ExpressionSyntaxException        if the expression is malformed
     * @throws MethodFailedException            if the expression called a method which failed
     * @throws NoSuchPropertyException          if the expression referred to a nonexistent property
     * @throws InappropriateExpressionException if the expression can't be used in this context
     * @throws OgnlException                    if there is a pathological environmental problem
     * @see #parseExpression(String)
     * @see #getValue(Node, Object)
     */
    public static Object getValue(String expression, Object root, Class resultType)
            throws OgnlException {
        return getValue(parseExpression(expression), root, resultType);
    }

    /**
     * Evaluates the given OGNL expression tree to insert a value into the object graph rooted at
     * the given root object. The default context is set for the given context and root via <CODE>addDefaultContext()</CODE>.
     *
     * @param tree    the OGNL expression tree to evaluate, as returned by parseExpression()
     * @param context the naming context for the evaluation
     * @param root    the root object for the OGNL expression
     * @param value   the value to insert into the object graph
     * @throws MethodFailedException            if the expression called a method which failed
     * @throws NoSuchPropertyException          if the expression referred to a nonexistent property
     * @throws InappropriateExpressionException if the expression can't be used in this context
     * @throws OgnlException                    if there is a pathological environmental problem
     */
    public static void setValue(Object tree, Map context, Object root, Object value)
            throws OgnlException {
        OgnlContext ognlContext = (OgnlContext) addDefaultContext(root, context);
        Node n = (Node) tree;

        if (n.getAccessor() != null) {
            n.getAccessor().set(ognlContext, root, value);
            return;
        }

        n.setValue(ognlContext, root, value);
    }

    /**
     * Sets the value given using the pre-compiled expression on the specified root
     * object.
     *
     * @param expression The pre-compiled expression, as found in {@link Node#getAccessor()}.
     * @param context    The ognl context.
     * @param root       The object to set the expression value on.
     * @param value      The value to set.
     */
    public static void setValue(ExpressionAccessor expression, OgnlContext context,
                                Object root, Object value) {
        expression.set(context, root, value);
    }

    /**
     * Evaluates the given OGNL expression to insert a value into the object graph rooted at the
     * given root object given the context.
     *
     * @param expression the OGNL expression to be parsed
     * @param root       the root object for the OGNL expression
     * @param context    the naming context for the evaluation
     * @param value      the value to insert into the object graph
     * @throws MethodFailedException            if the expression called a method which failed
     * @throws NoSuchPropertyException          if the expression referred to a nonexistent property
     * @throws InappropriateExpressionException if the expression can't be used in this context
     * @throws OgnlException                    if there is a pathological environmental problem
     */
    public static void setValue(String expression, Map context, Object root, Object value)
            throws OgnlException {
        setValue(parseExpression(expression), context, root, value);
    }

    /**
     * Evaluates the given OGNL expression tree to insert a value into the object graph rooted at
     * the given root object.
     *
     * @param tree  the OGNL expression tree to evaluate, as returned by parseExpression()
     * @param root  the root object for the OGNL expression
     * @param value the value to insert into the object graph
     * @throws MethodFailedException            if the expression called a method which failed
     * @throws NoSuchPropertyException          if the expression referred to a nonexistent property
     * @throws InappropriateExpressionException if the expression can't be used in this context
     * @throws OgnlException                    if there is a pathological environmental problem
     */
    public static void setValue(Object tree, Object root, Object value)
            throws OgnlException {
        setValue(tree, createDefaultContext(root), root, value);
    }

    /**
     * Convenience method that combines calls to <code> parseExpression </code> and
     * <code> setValue</code>.
     *
     * @param expression the OGNL expression to be parsed
     * @param root       the root object for the OGNL expression
     * @param value      the value to insert into the object graph
     * @throws ExpressionSyntaxException        if the expression is malformed
     * @throws MethodFailedException            if the expression called a method which failed
     * @throws NoSuchPropertyException          if the expression referred to a nonexistent property
     * @throws InappropriateExpressionException if the expression can't be used in this context
     * @throws OgnlException                    if there is a pathological environmental problem
     * @see #parseExpression(String)
     * @see #setValue(Object, Object, Object)
     */
    public static void setValue(String expression, Object root, Object value)
            throws OgnlException {
        setValue(parseExpression(expression), root, value);
    }

    /**
     * Checks if the specified {@link Node} instance represents a constant
     * expression.
     *
     * @param tree    The {@link Node} to check.
     * @param context The context to use.
     * @return True if the node is a constant - false otherwise.
     * @throws OgnlException If an error occurs checking the expression.
     */
    public static boolean isConstant(Object tree, Map context)
            throws OgnlException {
        return ((SimpleNode) tree).isConstant((OgnlContext) addDefaultContext(null, context));
    }

    /**
     * Checks if the specified expression represents a constant expression.
     *
     * @param expression The expression to check.
     * @param context    The context to use.
     * @return True if the node is a constant - false otherwise.
     * @throws OgnlException If an error occurs checking the expression.
     */
    public static boolean isConstant(String expression, Map context)
            throws OgnlException {
        return isConstant(parseExpression(expression), context);
    }

    /**
     * Same as {@link #isConstant(Object, java.util.Map)} - only the {@link Map} context
     * is created for you.
     *
     * @param tree The {@link Node} to check.
     * @return True if the node represents a constant expression - false otherwise.
     * @throws OgnlException If an exception occurs.
     */
    public static boolean isConstant(Object tree)
            throws OgnlException {
        return isConstant(tree, createDefaultContext(null));
    }

    /**
     * Same as {@link #isConstant(String, java.util.Map)} - only the {@link Map}
     * instance is created for you.
     *
     * @param expression The expression to check.
     * @return True if the expression represents a constant - false otherwise.
     * @throws OgnlException If an exception occurs.
     */
    public static boolean isConstant(String expression)
            throws OgnlException {
        return isConstant(parseExpression(expression), createDefaultContext(null));
    }

    public static boolean isSimpleProperty(Object tree, Map context)
            throws OgnlException {
        return ((SimpleNode) tree).isSimpleProperty((OgnlContext) addDefaultContext(null, context));
    }

    public static boolean isSimpleProperty(String expression, Map context)
            throws OgnlException {
        return isSimpleProperty(parseExpression(expression), context);
    }

    public static boolean isSimpleProperty(Object tree)
            throws OgnlException {
        return isSimpleProperty(tree, createDefaultContext(null));
    }

    public static boolean isSimpleProperty(String expression)
            throws OgnlException {
        return isSimpleProperty(parseExpression(expression), createDefaultContext(null));
    }

    public static boolean isSimpleNavigationChain(Object tree, Map context)
            throws OgnlException {
        return ((SimpleNode) tree).isSimpleNavigationChain((OgnlContext) addDefaultContext(null, context));
    }

    public static boolean isSimpleNavigationChain(String expression, Map context)
            throws OgnlException {
        return isSimpleNavigationChain(parseExpression(expression), context);
    }

    public static boolean isSimpleNavigationChain(Object tree)
            throws OgnlException {
        return isSimpleNavigationChain(tree, createDefaultContext(null));
    }

    public static boolean isSimpleNavigationChain(String expression)
            throws OgnlException {
        return isSimpleNavigationChain(parseExpression(expression), createDefaultContext(null));
    }

    /**
     * You can't make one of these.
     */
    private Ognl() {
    }

    public static Object getValue(MapNode node, Map context, Object root) throws OgnlException {
        int level = ((MutableInt) context.get(CURRENT_INDEX_KEY)).get();
        for (Iterator<Map.Entry<String, MapNode>> iterator = node.getChildren().entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, MapNode> nodeEntry = iterator.next();
            MapNode cNode = nodeEntry.getValue();
            if (cNode.getName() != null) {
                context.put(Config.NEXT_CHAIN, cNode);
                context.put(CURRENT_INDEX_KEY, new MutableInt(level));
                context.put(Config.EXPRESSION_SET, Boolean.TRUE);
                if (!cNode.getContainsValue()) {
                    getValue(parseExpression(cNode.getName()), context, root, null);
                } else {
                    setValue(parseExpression(cNode.getName()), context, root, cNode.getValue());
                }
            }
        }
        return root;
    }

    public static <T> T getValue(List<Map.Entry<String, Object>> expressions, Map context, Class<T> rootClass)
            throws OgnlException {
        if (expressions != null) {
            MapNode mapNode = tokenize(expressions);
            try {
                Object root = OgnlRuntime.createProperObject((OgnlContext) context, rootClass, rootClass.getComponentType(), mapNode);
                getValue(mapNode, context, root);
                return (T) root;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Object getValue(List<Map.Entry<String, Object>> expressions, Map context, Object root)
            throws OgnlException {
        if (expressions != null) {
            MapNode mapNode = tokenize(expressions);
            getValue(mapNode, context, root);
        }
        return root;
    }

    private static Map<String, Token> specialTokensMap = new HashMap<>();

    private static class Token {
        private Boolean isPartOfName;
        private String token;
        private NodeType nodeType;
        private Boolean isEndToken;

        Token(Boolean isPartOfName, String token, NodeType nodeType, Boolean isEndToken) {
            this.isPartOfName = isPartOfName;
            this.token = token;
            this.nodeType = nodeType;
            this.isEndToken = isEndToken;
        }

        public Boolean getIsPartOfName() {
            return isPartOfName;
        }

        public String getToken() {
            return token;
        }

        public NodeType getNodeType() {
            return nodeType;
        }

        public Boolean getEndToken() {
            return isEndToken;
        }

        @Override
        public String toString() {
            return token;
        }
    }

    static {
        specialTokensMap.put(".", new Token(false, ".", NodeType.SINGLE, false));
        specialTokensMap.put("[", new Token(true, "]", NodeType.COLLECTION, false));
        specialTokensMap.put("=", new Token(false, "=", NodeType.SINGLE, true));
        specialTokensMap.put("", new Token(false, "", NodeType.SINGLE, true));
    }

    public static MapNode tokenize(List<Map.Entry<String, Object>> expressions) {
        MapNode m = new MapNode("_root_", NodeType.SINGLE, null, false);
        m.setIsRoot(true);
        for (Map.Entry<String, Object> expr : expressions) {
            MapNode currentNode = null;
            StringBuilder name = new StringBuilder();
            Token nextToken = null;
            Boolean isEndDetected = false;
            char[] tokens = expr.getKey().toCharArray();
            int totalLen = tokens.length;
            TOKENS:
            for (char ch : tokens) {
                totalLen--;
                Token token = specialTokensMap.get(Character.toString(ch));
                NodeType nodeType = NodeType.UNKNOWN;
                if (isEndDetected) {
                    name.append(ch);
                } else if (token == null) {
                    name.append(ch);
                    if (nextToken != null && Character.toString(ch).equals(nextToken.getToken())) {
                        token = nextToken;
                    } else if (totalLen == 0) {
                        token = specialTokensMap.get("");
                    }
                } else {
                    nodeType = token.getNodeType();
                    if (currentNode != null && currentNode.getNodeType() == NodeType.UNKNOWN) {
                        currentNode.setNodeType(token.getNodeType());
                    }
                }
                if (name.length() == 0) {
                    continue;
                }
                if (!isEndDetected && token != null) {
                    Map<String, MapNode> childMap = null;
                    isEndDetected = token.getEndToken();
                    if (currentNode == null) {
                        childMap = m.getChildren();
                    } else {
                        childMap = currentNode.getChildren();
                    }
                    MapNode currentHolder = currentNode;
                    currentNode = childMap.get(name.toString());
                    if (currentNode == null) {
                        if (nodeType == NodeType.UNKNOWN) {
                            if (token.isEndToken && expr.getValue() instanceof Collections) {
                                nodeType = NodeType.COLLECTION;
                            }
                        }
                        currentNode = new MapNode(name.toString(), nodeType, currentHolder, expr.getValue() == null || expr.getValue().equals(""));
                        childMap.put(name.toString(), currentNode);
                    }
                    name = new StringBuilder();
                    if (Character.toString(ch).equals(token.getToken())) {
                        nextToken = null;
                    } else {
                        if (token.getIsPartOfName()) {
                            name.append(ch);
                        }
                        nextToken = token;
                    }
                }
            }

            if (currentNode != null) {
                currentNode.setContainsValue(true);
                currentNode.setValue(expr.getValue());
            }
        }
        return m;
    }

    public enum NodeType {
        SINGLE,
        COLLECTION,
        UNKNOWN
    }
}

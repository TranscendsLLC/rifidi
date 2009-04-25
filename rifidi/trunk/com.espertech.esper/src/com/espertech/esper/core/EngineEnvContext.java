/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.core;

import javax.naming.*;
import java.util.Hashtable;
import java.util.Map;
import java.util.HashMap;

/**
 * Implements a JNDI context for providing a directory for engine-external resources such as adapters.
 */
public class EngineEnvContext implements Context
{
    private Map<String, Object> context;

    /**
     * Ctor.
     */
    public EngineEnvContext()
    {
        context = new HashMap<String, Object>();
    }

    public Object lookup(Name name) throws NamingException
    {
        throw new UnsupportedOperationException();
    }

    public Object lookup(String name) throws NamingException
    {
        return context.get(name);
    }

    public void bind(Name name, Object obj) throws NamingException
    {
        throw new UnsupportedOperationException();
    }

    public void bind(String name, Object obj) throws NamingException
    {
        if (context.containsKey(name))
        {
            throw new NamingException("Already in context: " + name);
        }
        context.put(name, obj);
    }

    public void rebind(Name name, Object obj) throws NamingException
    {
        throw new UnsupportedOperationException();
    }

    public void rebind(String name, Object obj) throws NamingException
    {
        throw new UnsupportedOperationException();
    }

    public void unbind(Name name) throws NamingException
    {
        throw new UnsupportedOperationException();
    }

    public void unbind(String name) throws NamingException
    {
        throw new UnsupportedOperationException();
    }

    public void rename(Name oldName, Name newName) throws NamingException
    {
        throw new UnsupportedOperationException();
    }

    public void rename(String oldName, String newName) throws NamingException
    {
        throw new UnsupportedOperationException();
    }

    public NamingEnumeration<NameClassPair> list(Name name) throws NamingException
    {
        throw new UnsupportedOperationException();
    }

    public NamingEnumeration<NameClassPair> list(String name) throws NamingException
    {
        throw new UnsupportedOperationException();
    }

    public NamingEnumeration<Binding> listBindings(Name name) throws NamingException
    {
        throw new UnsupportedOperationException();
    }

    public NamingEnumeration<Binding> listBindings(String name) throws NamingException
    {
        throw new UnsupportedOperationException();
    }

    public void destroySubcontext(Name name) throws NamingException
    {
        throw new UnsupportedOperationException();
    }

    public void destroySubcontext(String name) throws NamingException
    {
        throw new UnsupportedOperationException();
    }

    public Context createSubcontext(Name name) throws NamingException
    {
        throw new UnsupportedOperationException();
    }

    public Context createSubcontext(String name) throws NamingException
    {
        throw new UnsupportedOperationException();
    }

    public Object lookupLink(Name name) throws NamingException
    {
        throw new UnsupportedOperationException();
    }

    public Object lookupLink(String name) throws NamingException
    {
        throw new UnsupportedOperationException();
    }

    public NameParser getNameParser(Name name) throws NamingException
    {
        throw new UnsupportedOperationException();
    }

    public NameParser getNameParser(String name) throws NamingException
    {
        throw new UnsupportedOperationException();
    }

    public Name composeName(Name name, Name prefix) throws NamingException
    {
        throw new UnsupportedOperationException();
    }

    public String composeName(String name, String prefix) throws NamingException
    {
        throw new UnsupportedOperationException();
    }

    public Object addToEnvironment(String propName, Object propVal) throws NamingException
    {
        throw new UnsupportedOperationException();
    }

    public Object removeFromEnvironment(String propName) throws NamingException
    {
        throw new UnsupportedOperationException();
    }

    public Hashtable<?, ?> getEnvironment() throws NamingException
    {
        throw new UnsupportedOperationException();
    }

    public void close() throws NamingException
    {
        throw new UnsupportedOperationException();
    }

    public String getNameInNamespace() throws NamingException
    {
        throw new UnsupportedOperationException();
    }
}

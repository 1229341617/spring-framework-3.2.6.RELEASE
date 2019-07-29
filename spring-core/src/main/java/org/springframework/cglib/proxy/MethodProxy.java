package org.springframework.cglib.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.springframework.cglib.core.CodeGenerationException;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastClass.Generator;

public class MethodProxy
{
  private Signature sig1;
  private Signature sig2;
  private MethodProxy.CreateInfo createInfo;
  private final Object initLock = new Object();
  private volatile MethodProxy.FastClassInfo fastClassInfo;
  
  public static MethodProxy create(Class c1, Class c2, String desc, String name1, String name2)
  {
    MethodProxy proxy = new MethodProxy();
    proxy.sig1 = new Signature(name1, desc);
    proxy.sig2 = new Signature(name2, desc);
    proxy.createInfo = new MethodProxy.CreateInfo(c1, c2);
    return proxy;
  }
  
  private void init()
  {
    if (this.fastClassInfo == null) {
      synchronized (this.initLock)
      {
        if (this.fastClassInfo == null)
        {
          MethodProxy.CreateInfo ci = this.createInfo;
          
          MethodProxy.FastClassInfo fci = new MethodProxy.FastClassInfo(null);
          fci.f1 = helper(ci, ci.c1);
          fci.f2 = helper(ci, ci.c2);
          fci.i1 = fci.f1.getIndex(this.sig1);
          fci.i2 = fci.f2.getIndex(this.sig2);
          this.fastClassInfo = fci;
          this.createInfo = null;
        }
      }
    }
  }
  
  private static FastClass helper(MethodProxy.CreateInfo ci, Class type)
  {
    FastClass.Generator g = new FastClass.Generator();
    g.setType(type);
    g.setClassLoader(ci.c2.getClassLoader());
    g.setNamingPolicy(ci.namingPolicy);
    g.setStrategy(ci.strategy);
    g.setAttemptLoad(ci.attemptLoad);
    return g.create();
  }
  
  public Signature getSignature()
  {
    return this.sig1;
  }
  
  public String getSuperName()
  {
    return this.sig2.getName();
  }
  
  public int getSuperIndex()
  {
    init();
    return this.fastClassInfo.i2;
  }
  
  FastClass getFastClass()
  {
    init();
    return this.fastClassInfo.f1;
  }
  
  FastClass getSuperFastClass()
  {
    init();
    return this.fastClassInfo.f2;
  }
  
  public static MethodProxy find(Class type, Signature sig)
  {
    try
    {
      Method m = type.getDeclaredMethod("CGLIB$findMethodProxy", MethodInterceptorGenerator.FIND_PROXY_TYPES);
      
      return (MethodProxy)m.invoke(null, new Object[] { sig });
    }
    catch (NoSuchMethodException e)
    {
      throw new IllegalArgumentException("Class " + type + " does not use a MethodInterceptor");
    }
    catch (IllegalAccessException e)
    {
      throw new CodeGenerationException(e);
    }
    catch (InvocationTargetException e)
    {
      throw new CodeGenerationException(e);
    }
  }
  
  public Object invoke(Object obj, Object[] args)
    throws Throwable
  {
    try
    {
      init();
      MethodProxy.FastClassInfo fci = this.fastClassInfo;
      return fci.f1.invoke(fci.i1, obj, args);
    }
    catch (InvocationTargetException e)
    {
      throw e.getTargetException();
    }
    catch (IllegalArgumentException e)
    {
      if (this.fastClassInfo.i1 < 0) {
        throw new IllegalArgumentException("Protected method: " + this.sig1);
      }
      throw e;
    }
  }
  
  public Object invokeSuper(Object obj, Object[] args)
    throws Throwable
  {
    try
    {
      init();
      MethodProxy.FastClassInfo fci = this.fastClassInfo;
      return fci.f2.invoke(fci.i2, obj, args);
    }
    catch (InvocationTargetException e)
    {
      throw e.getTargetException();
    }
  }
}

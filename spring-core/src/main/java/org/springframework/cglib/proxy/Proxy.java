package org.springframework.cglib.proxy;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import org.springframework.cglib.core.CodeGenerationException;

public class Proxy
  implements Serializable
{
  protected InvocationHandler h;
  private static final CallbackFilter BAD_OBJECT_METHOD_FILTER = new Proxy.1();
  
  protected Proxy(InvocationHandler h)
  {
    Enhancer.registerCallbacks(getClass(), new Callback[] { h, null });
    this.h = h;
  }
  
  public static InvocationHandler getInvocationHandler(Object proxy)
  {
    if (!(proxy instanceof Proxy.ProxyImpl)) {
      throw new IllegalArgumentException("Object is not a proxy");
    }
    return ((Proxy)proxy).h;
  }
  
  public static Class getProxyClass(ClassLoader loader, Class[] interfaces)
  {
    Enhancer e = new Enhancer();
    e.setSuperclass(Proxy.ProxyImpl.class);
    e.setInterfaces(interfaces);
    e.setCallbackTypes(new Class[] { InvocationHandler.class, NoOp.class });
    


    e.setCallbackFilter(BAD_OBJECT_METHOD_FILTER);
    e.setUseFactory(false);
    return e.createClass();
  }
  
  public static boolean isProxyClass(Class cl)
  {
    return cl.getSuperclass().equals(Proxy.ProxyImpl.class);
  }
  
  public static Object newProxyInstance(ClassLoader loader, Class[] interfaces, InvocationHandler h)
  {
    try
    {
      Class clazz = getProxyClass(loader, interfaces);
      return clazz.getConstructor(new Class[] { InvocationHandler.class }).newInstance(new Object[] { h });
    }
    catch (RuntimeException e)
    {
      throw e;
    }
    catch (Exception e)
    {
      throw new CodeGenerationException(e);
    }
  }
}

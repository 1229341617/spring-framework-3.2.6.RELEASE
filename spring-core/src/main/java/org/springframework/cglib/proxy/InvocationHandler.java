package org.springframework.cglib.proxy;

import java.lang.reflect.Method;

public abstract interface InvocationHandler
  extends Callback
{
  public abstract Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
    throws Throwable;
}

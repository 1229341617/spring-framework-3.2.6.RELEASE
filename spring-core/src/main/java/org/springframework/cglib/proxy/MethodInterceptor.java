package org.springframework.cglib.proxy;

import java.lang.reflect.Method;

public abstract interface MethodInterceptor
  extends Callback
{
  public abstract Object intercept(Object paramObject, Method paramMethod, Object[] paramArrayOfObject, MethodProxy paramMethodProxy)
    throws Throwable;
}

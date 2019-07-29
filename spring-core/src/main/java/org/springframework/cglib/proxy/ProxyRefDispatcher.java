package org.springframework.cglib.proxy;

public abstract interface ProxyRefDispatcher
  extends Callback
{
  public abstract Object loadObject(Object paramObject)
    throws Exception;
}

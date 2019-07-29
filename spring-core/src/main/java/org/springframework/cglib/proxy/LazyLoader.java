package org.springframework.cglib.proxy;

public abstract interface LazyLoader
  extends Callback
{
  public abstract Object loadObject()
    throws Exception;
}

package org.springframework.cglib.proxy;

import java.lang.reflect.Method;

public abstract interface CallbackFilter
{
  public abstract int accept(Method paramMethod);
  
  public abstract boolean equals(Object paramObject);
}

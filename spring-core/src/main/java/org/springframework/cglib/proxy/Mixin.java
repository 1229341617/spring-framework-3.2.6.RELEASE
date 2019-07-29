package org.springframework.cglib.proxy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.cglib.core.ClassesKey;
import org.springframework.cglib.core.KeyFactory;

public abstract class Mixin
{
  private static final Mixin.MixinKey KEY_FACTORY = (Mixin.MixinKey)KeyFactory.create(Mixin.MixinKey.class, KeyFactory.CLASS_BY_NAME);
  private static final Map ROUTE_CACHE = Collections.synchronizedMap(new HashMap());
  public static final int STYLE_INTERFACES = 0;
  public static final int STYLE_BEANS = 1;
  public static final int STYLE_EVERYTHING = 2;
  static Class class$net$sf$cglib$proxy$Mixin;
  
  public abstract Mixin newInstance(Object[] paramArrayOfObject);
  
  public static Mixin create(Object[] delegates)
  {
    Mixin.Generator gen = new Mixin.Generator();
    gen.setDelegates(delegates);
    return gen.create();
  }
  
  public static Mixin create(Class[] interfaces, Object[] delegates)
  {
    Mixin.Generator gen = new Mixin.Generator();
    gen.setClasses(interfaces);
    gen.setDelegates(delegates);
    return gen.create();
  }
  
  public static Mixin createBean(Object[] beans)
  {
    return createBean(null, beans);
  }
  
  public static Mixin createBean(ClassLoader loader, Object[] beans)
  {
    Mixin.Generator gen = new Mixin.Generator();
    gen.setStyle(1);
    gen.setDelegates(beans);
    gen.setClassLoader(loader);
    return gen.create();
  }
  
  public static Class[] getClasses(Object[] delegates)
  {
    return (Class[])Mixin.Route.access$100(route(delegates)).clone();
  }
  
  private static Mixin.Route route(Object[] delegates)
  {
    Object key = ClassesKey.create(delegates);
    Mixin.Route route = (Mixin.Route)ROUTE_CACHE.get(key);
    if (route == null) {
      ROUTE_CACHE.put(key, route = new Mixin.Route(delegates));
    }
    return route;
  }
}

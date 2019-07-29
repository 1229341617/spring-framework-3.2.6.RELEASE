package org.springframework.cglib.proxy;

import java.util.List;
import org.springframework.cglib.core.ClassEmitter;
import org.springframework.cglib.core.CodeEmitter;

abstract interface CallbackGenerator
{
  public abstract void generate(ClassEmitter paramClassEmitter, CallbackGenerator.Context paramContext, List paramList)
    throws Exception;
  
  public abstract void generateStatic(CodeEmitter paramCodeEmitter, CallbackGenerator.Context paramContext, List paramList)
    throws Exception;
}

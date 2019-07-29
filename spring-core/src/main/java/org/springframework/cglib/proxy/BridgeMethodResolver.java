package org.springframework.cglib.proxy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.springframework.asm.ClassReader;

class BridgeMethodResolver
{
  private final Map declToBridge;
  
  public BridgeMethodResolver(Map declToBridge)
  {
    this.declToBridge = declToBridge;
  }
  
  public Map resolveAll()
  {
    Map resolved = new HashMap();
    for (Iterator entryIter = this.declToBridge.entrySet().iterator(); entryIter.hasNext();)
    {
      Map.Entry entry = (Map.Entry)entryIter.next();
      Class owner = (Class)entry.getKey();
      Set bridges = (Set)entry.getValue();
      try
      {
        new ClassReader(owner.getName()).accept(new BridgeMethodResolver.BridgedFinder(bridges, resolved), 6);
      }
      catch (IOException ignored) {}
    }
    return resolved;
  }
}

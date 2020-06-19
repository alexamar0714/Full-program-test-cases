package cn.edu.ruc.iir.rainbow.redirect.index;

import java.util.HashMap;
import java.util.Map;

public class IndexFactory
{
    private static IndexFactory instance = null;

    public static IndexFactory Instance ()
    {
        if (instance == null)
        {
            instance = new IndexFactory();
        }
        return instance;
    }

    private Map<String, Index> indexCache = null;
    private IndexFactory ()
    {
        this.indexCache = new HashMap<>();
    }

    public void cacheIndex (String name, Index inverted)
    {
        this.indexCache.put(name, inverted);
    }

    public Index getIndex (String name)
    {
        return this.indexCache.get(name);
    }
}

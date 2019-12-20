package org.noear.solon.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClassWrap {
    private static Map<Class<?>, ClassWrap> _cache = new ConcurrentHashMap<>();

    public static ClassWrap get(Class<?> clz){
        ClassWrap cw = _cache.get(clz);
        if(cw == null){
            cw = new ClassWrap(clz);
            _cache.putIfAbsent(clz,cw);
        }
        return cw;
    }

    public final Class<?> clazz;
    public final List<MethodWrap> methodWraps;
    public final Field[] fields;

    protected ClassWrap(Class<?> clz ){
        clazz = clz;
        methodWraps = new ArrayList<>();

        for(Method m : clz.getDeclaredMethods()){
            methodWraps.add(MethodWrap.get(m));
        }

        fields = clz.getDeclaredFields();
    }
}
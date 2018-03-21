package io.mrchenli.concurrent.Cas;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeUtil {

    public static Unsafe getUnsafe() {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);//获取静态属性的值
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}

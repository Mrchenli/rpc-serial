package io.mrchenli;

import java.util.HashMap;

public class PackageInfo {


    public static void main(String[] args) {
        int a = Math.abs("console-consumer-61948".hashCode()) % 50;
        System.out.println(a);
        HashMap map = new HashMap();
        map.put("ad",11);
        map.put("bc",12);
    }

}

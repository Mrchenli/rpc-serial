package io.mrchenli.concurrent.ohmyfuture;

import java.util.Random;

public class ProductFactory {

    public MyFuture createProduct(String name){
        MyFuture f = new MyFuture();
        new Thread(() -> {
            Product p = new Product(new Random().nextInt(),name);
            f.setProduct(p);
        }).start();
        System.out.println("收到订单...你可以去上班了");
        return f;
    }

}

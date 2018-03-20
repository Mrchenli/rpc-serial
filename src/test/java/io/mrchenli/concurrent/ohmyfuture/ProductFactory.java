package io.mrchenli.concurrent.ohmyfuture;

import java.util.Random;

public class ProductFactory {

    public MyFuture createProduct(String name){
        MyFuture f = new MyFuture();
        Product p = new Product(new Random().nextInt(),name);
        f.setProduct(p);
        return f;
    }

}

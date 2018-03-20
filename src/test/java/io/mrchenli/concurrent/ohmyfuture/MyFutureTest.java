package io.mrchenli.concurrent.ohmyfuture;

public class MyFutureTest {

    public static void main(String[] args) throws InterruptedException {
        ProductFactory pf = new ProductFactory();
        MyFuture f = pf.createProduct("蛋糕");
        System.out.println("我去上班了,下了班我来取蛋糕...");
        System.out.println("我拿着蛋糕回家."+f.get());
    }

}

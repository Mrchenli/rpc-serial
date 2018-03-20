package io.mrchenli.concurrent.ohmyfuture;

/**
 * 订单
 */
public class MyFuture {
    private  Product product;

    public synchronized void setProduct(Product product) {
        this.product = product;
        notifyAll();
    }

    public synchronized Product get() throws InterruptedException {
        while (product==null){//这样会占用cpu资源
            wait();
        }
        return product;
    }
}

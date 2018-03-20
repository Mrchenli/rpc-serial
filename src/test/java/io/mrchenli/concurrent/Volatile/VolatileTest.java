package io.mrchenli.concurrent.Volatile;

public class VolatileTest {

    public static void main(String[] args) {
        VolatilePojo volatilePojo= new VolatilePojo();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                volatilePojo.setFlag(true);
                System.out.println("volatile pojo is true");
            }
        }).start();
        System.out.println("等待 volatile set true");
        while (!volatilePojo.isFlag()){//只有被修改后才能过去

        }
        System.out.println("volatile passed");

        UnVolatilePojo unVolatilePojo = new UnVolatilePojo();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                unVolatilePojo.setFlag(true);
                System.out.println("unvolatile pojo is true");
            }
        }).start();
        System.out.println("等待 unvolatile set true");
        while (!unVolatilePojo.isFlag()){//只有被修改后才能过去

        }
        System.out.println("unvolatile passed");
    }
}

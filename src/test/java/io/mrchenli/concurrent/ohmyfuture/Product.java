package io.mrchenli.concurrent.ohmyfuture;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Product {

    private int id;
    private String name;

    public Product(int id, String name) {
        //模仿创建产品很慢
        try {
            System.out.println("开始生产..."+name);
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.id = id;
        this.name = name;
        System.out.println(name+" 生产完成");
    }


}

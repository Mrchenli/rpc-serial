package io.mrchenli.protocol;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JsonSerializerTest {

    @Test
    public void testObjectMapper() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        Person person = new Person();
        person.setName("Tom");
        person.setAge(40);
        String jsonString  = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(person);
        System.out.println(jsonString);
        Person de= objectMapper.readValue(jsonString,Person.class);
        System.out.println(de);
    }


    @Data
    @NoArgsConstructor
    @JsonPropertyOrder({ "age", "name" ,"date"})
    @JsonIgnoreProperties({ "age"})
    static class Person{
        String name;
        int age;
        Date date =new Date();

        @Override
        public String toString() {
            return "name:"+name+",age:"+age+",date:"+date;
        }
    }




}
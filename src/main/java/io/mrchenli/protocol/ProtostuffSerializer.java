package io.mrchenli.protocol;

public class ProtostuffSerializer implements Serializer {


    @Override
    public <T> byte[] serialize(T obj) {
        return new byte[0];
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> cls) {
        return null;
    }


}

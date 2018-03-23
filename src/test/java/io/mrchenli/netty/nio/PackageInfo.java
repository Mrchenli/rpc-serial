package io.mrchenli.netty.nio;

/**
 * netty is a framework base on nio
 *
 *
 * 1.缓存区Buffer
 *      sort:
 *          ByteBuffer
 *          CharBuffer
 *          ShortBuffer
 *          IntBuffer
 *          LongBuffer
 *          FloatBuffer
 *          DoubleBuffer
 *      struct:
 *          field:
 *              capacity: int >0
 *              limit: int >0
 *              mark:  int >-1
 *              position: int >0
 *          method:
 *              clear(): Buffer
 *              flip(): Buffer
 *2.通道 Channel
 *      diff:
 *          stream:是单向的InputStream OutputStream
 *          channel:双向的 可以读、写、或者二者同时进行
 *3.多路复用器Selector
 *
 */
public class PackageInfo {
}

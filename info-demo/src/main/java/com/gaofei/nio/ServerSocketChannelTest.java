package com.gaofei.nio;

import com.gaofei.nio.processor.DefaultProcessor;
import com.gaofei.nio.reader.DefaultMessageReader;
import com.gaofei.nio.reader.MessageReader;
import com.gaofei.nio.writer.DefaultMessageWriter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by GaoQingming on 2018/2/6 0006.
 */
public class ServerSocketChannelTest {
    private static Selector readSelector;
    private static ExecutorService executorService = Executors.newCachedThreadPool();
    MessageReader messageReader = new DefaultMessageReader(readSelector, new DefaultProcessor(new DefaultMessageWriter()));

    static {
        try {
            readSelector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ServerSocketChannelTest().serverSocketChannelTest();
    }

    public void serverSocketChannelTest() {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(9999));
            serverSocketChannel.configureBlocking(true);
            //serverSocketChannel.configureBlocking(false);
            executorService.submit(() -> messageReader.read());
            while (true) {
                SocketChannel socketChannel = serverSocketChannel.accept();
                //如果不设置false，无法register
                socketChannel.configureBlocking(false);
                try {
                    socketChannel.register(readSelector, SelectionKey.OP_READ);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleSocketChannel(SocketChannel socketChannel) {
        try {
            socketChannel.register(readSelector, SelectionKey.OP_READ);
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        }
        //ByteBuffer byteBuffer = ByteBuffer.allocate(48);
        //try {
        //    int length = socketChannel.read(byteBuffer);
        //    while (length != -1) {
        //        System.out.println("本次读取的长度为：" + length);
        //        byteBuffer.flip();
        //        while (byteBuffer.hasRemaining()) {
        //            System.out.println("本次读取的内容为:" + (char)byteBuffer.get());
        //        }
        //        byteBuffer.clear();
        //        length = socketChannel.read(byteBuffer);
        //    }
        //    byteBuffer.clear();
        //    byteBuffer.put("server response".getBytes());
        //    byteBuffer.flip();
        //    while (byteBuffer.hasRemaining()) {
        //        socketChannel.write(byteBuffer);
        //    }
        //    socketChannel.close();
        //} catch (IOException e) {
        //    e.printStackTrace();
        //} finally {
        //    try {
        //        socketChannel.close();
        //    } catch (IOException e) {
        //        e.printStackTrace();
        //    }
        //}
    }
}

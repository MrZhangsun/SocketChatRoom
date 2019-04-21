package com.hsbc.chat.ipsearch;

import com.hsbc.chat.constants.UDPConstants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Function:
 *
 * @author zhangsunjiankun - 2019/4/6 上午7:46
 */
public class ClientSearcher {

    private static final int LISTEN_PORT = UDPConstants.PORT_CLIENT_RESPONSE;

    public static ServerInfo searchServer(int timeout) {
        System.out.println("UDP Searcher Started...");
        CountDownLatch latch = new CountDownLatch(1);
        Listener listener = null;
        try {
            listener = listen(latch);
            sendBroadcast();
            latch.await(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

        System.out.println("Searcher Finished...");
        if (listener == null) {
            return null;
        }

        List<ServerInfo> servers = listener.getServerAndClose();
        if (!servers.isEmpty()) {
            return servers.get(0);
        }
        return null;
    }


    private static Listener listen(CountDownLatch latch) throws InterruptedException{
        System.out.println("UDP Searcher start listen...");
        CountDownLatch startDownLatch = new CountDownLatch(1);
        Listener listener = new Listener(LISTEN_PORT, startDownLatch, latch);
        listener.start();
        startDownLatch.await();
        return listener;
    }


    private static void sendBroadcast() throws IOException {
        System.out.println("UDP Searcher sends broadcast starting...");
        DatagramSocket ds = new DatagramSocket();
        ByteBuffer allocate = ByteBuffer.allocate(128);
        allocate.put(UDPConstants.HEADER);
        allocate.putShort((short)1);
        allocate.putInt(LISTEN_PORT);
        DatagramPacket request = new DatagramPacket(allocate.array(), allocate.position() + 1);
        request.setAddress(InetAddress.getByName("255.255.255.255"));
        request.setPort(UDPConstants.SERVER_PORT);

        ds.send(request);
        ds.close();
        System.out.println("Send Broadcast Done..");
    }
}

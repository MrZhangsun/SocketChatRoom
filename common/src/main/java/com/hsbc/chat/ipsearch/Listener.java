package com.hsbc.chat.ipsearch;

import com.hsbc.chat.constants.UDPConstants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Function:
 *
 * @author zhangsunjiankun - 2019/4/6 上午8:04
 */
public class Listener extends Thread {
    private final int listenPort;
    private final CountDownLatch startDownLatch;
    private final CountDownLatch receiveDownLatch;
    private final List<ServerInfo> serverInfoList = new ArrayList<ServerInfo>(1);
    private final byte[] buffer = new byte[128];
    private final int minLen = UDPConstants.HEADER.length + 2 + 4;
    private boolean done = false;
    private DatagramSocket ds = null;

    public Listener(int listenPort, CountDownLatch startDownLatch, CountDownLatch receiveDownLatch) {
        super();
        this.listenPort = listenPort;
        this. startDownLatch = startDownLatch;
        this.receiveDownLatch = receiveDownLatch;
    }

    @Override
    public void run() {
        super.run();
        startDownLatch.countDown();
        try {
            ds = new DatagramSocket(listenPort);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            while (!done) {
                ds.receive(packet);

                String ip = packet.getAddress().getHostAddress();
                int port = packet.getPort();
                int length = packet.getLength();
                byte[] data = packet.getData();
                boolean isValid = length >= minLen;
                System.out.println("UDP Searcher receive from ip: " + ip + "\tport: " + port + "\tdata valid:" + isValid);

                if (!isValid) {
                    continue;
                }

                ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, UDPConstants.HEADER.length, length - minLen);
                final short cmd = byteBuffer.getShort();
                final int serverPort = byteBuffer.getInt();
                if (cmd != 2 || serverPort <= 0) {
                    System.out.println("UDP Searcher received cmd: " + "\tserver port: " + serverPort);
                    continue;
                }

                String sn = new String(buffer, minLen, length - minLen);
                ServerInfo serverInfo = new ServerInfo(serverPort, ip, sn);
                serverInfoList.add(serverInfo);
                receiveDownLatch.countDown();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    List<ServerInfo> getServerAndClose() {
        done = true;
        close();
        return serverInfoList;
    }

    private void close() {
        if (ds != null) {
            ds.close();
            ds = null;
        }
    }
}

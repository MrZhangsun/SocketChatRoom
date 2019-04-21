package com.hsbc.chat.ipsearch;

/**
 * Function:
 *
 * @author zhangsunjiankun - 2019/3/15 下午8:36
 */
public class Client {

    public static void main(String[] args) {
        ServerInfo info = ClientSearcher.searchServer(10000);
        System.out.println("server info: " + info);
    }
}

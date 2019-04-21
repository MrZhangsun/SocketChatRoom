package com.hsbc.chat.ipsearch;

import lombok.Data;
import lombok.ToString;

/**
 * Function:
 *
 * @author zhangsunjiankun - 2019/4/6 上午7:43
 */
@Data
@ToString
public class ServerInfo {

    private int port;
    private String address;
    private String sn;

    public ServerInfo(int port, String ip, String sn) {
        this.port = port;
        this.address = ip;
        this.sn = sn;
    }
}

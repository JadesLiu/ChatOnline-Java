/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imchatClient;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

/**
 *
 * @author LJY
 */
public class DataHandler {

    public static DatagramPacket toPacket(String sendMsg, InetAddress remoteAddress, int remotePort, InetAddress destAddress, int destPort) {
      //标识符1#，告诉服务器为私包，里面还整合了IP及端口数据。
        String buffer1, buffer2, buffer3, buffer4;
        buffer1 = "1";
        buffer2 = destAddress + "#";
        buffer3 = destPort + "#";
        buffer4 = buffer1 + buffer2 + buffer3 + sendMsg;    //标识符+IP+端口+信息内容
        byte[] buffer = buffer4.getBytes();

        return new DatagramPacket(buffer, buffer.length, remoteAddress, remotePort);
    }

    public static DatagramPacket toPacket(String sendMsg, InetAddress remoteAddress, int remotePort) {
     //标识符3#，告诉服务器此报需群发
        String str;
        str = "3" + "#" + sendMsg;
        byte[] buffer = str.getBytes();

        return new DatagramPacket(buffer, buffer.length, remoteAddress, remotePort);
    }

    public static DatagramPacket toPacket(int LocalPort, InetAddress remoteAddress, int remotePort) {
      //标识符2#，告诉服务器此报为客户端本地端口数据
        String str;
        str = "2" + "#" + LocalPort + "#";
        byte[] buffer = str.getBytes();

        return new DatagramPacket(buffer, buffer.length, remoteAddress, remotePort);

    }

    public static String toString(DatagramPacket p) throws UnknownHostException {
        //客户端送来的非文件报（即普通报）都会加flag标识符4#，故需要处理
        byte[] temp = p.getData();
        String str = new String(temp);

        if (str.charAt(0) == '4') {
            StringTokenizer token = new StringTokenizer(str, "#");

            String flag = token.nextToken();
            String msg = token.nextToken();
            return msg;
        } else {
            return "已收到文件";
        }

    }

}

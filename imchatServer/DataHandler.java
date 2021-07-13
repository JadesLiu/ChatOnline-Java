/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imchatServer;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

/**
 *
 * @author LJY
 */
public class DataHandler {
    
    public static DatagramPacket toPacket(String sendMsg, InetAddress remoteAddress, int remotePort){
        byte[] buffer=sendMsg.getBytes();
        
        return new DatagramPacket(buffer,buffer.length, remoteAddress, remotePort);
    }
    
public static String toString(DatagramPacket p) throws UnknownHostException{
    //用于返回根据客户端不同需求而设立的四种情况
        byte [] temp=p.getData();
        String str=new String(temp);
        switch (str.charAt(0)) {
            case '1':
                //将客户端私发的报解析并转发给目的客户端，返回的字符串为消息内容
                return new String (destIncluded(temp).getData());
            case '2':
                //将启动时客户端的ip和端口存入客户端的list
                return "客户端请求激活。";
            case'3':
                //群发
                InetAddress fake;
                fake=InetAddress.getByName("192.168.0.0");
                return new String(groupIncluded(temp,fake,0000).getData()); //为了得到信息而设的假地址和端口，只需要信息内容，不发送文件，无影响。
            default:
                return "已收到文件";
        }
    }
public static DatagramPacket destIncluded(byte[] temp) throws UnknownHostException{
    //处理私发包，转换含有目的IP、端口的报为普通报
    String str=new String(temp);
    StringTokenizer token=new StringTokenizer(str,"# /");
    
    String  flag=token.nextToken();    //消去flag识别符
    String  remoteAddressStr=token.nextToken();  //得目标IP地址
    String  remotePortStr=token.nextToken();     //得目标端口
    String  msg=notFileData(token.nextToken());   //得消息内容

    
    byte[] buffer=msg.getBytes();
    int remotePort=Integer.parseInt(remotePortStr);
    InetAddress remoteAddress=InetAddress.getByName(remoteAddressStr);  //转换格式
    
    return new DatagramPacket(buffer,buffer.length, remoteAddress, remotePort);//重新整理为普通报
}
public static int portIncluded(byte[] temp){
    //处理客户点击“启动”按钮时的内容为“端口数据”的报，用来提取并保存已启动的客户端的端口
    String str=new String(temp);
    StringTokenizer token=new StringTokenizer(str,"#");
    
    String flag=token.nextToken();
    String localPortStr=token.nextToken();
    int localPort=Integer.parseInt(localPortStr);
    
    return localPort;
}
public static DatagramPacket groupIncluded(byte[] temp,InetAddress iplist,int portlist) throws UnknownHostException{
    //处理群发消息时的数据报
    String str=new String(temp);
    StringTokenizer token=new StringTokenizer(str,"#");
    
    String  flag=token.nextToken();    
    String  msg=notFileData( token.nextToken());
    
    byte[] buffer=msg.getBytes();
    
    return new DatagramPacket(buffer,buffer.length, iplist, portlist);
}
public static String notFileData(String temp){
    //由客户端需要区分数据报是普通报或文件报，由于文件不方便在前加falg标识符进行处理，故将所有给客户端发送的普通报中加flag标识符4#，客户端有对应的toString处理方式
    //会导致一个弊端，服务器接受的客户消息前多出4#这个flag标识符，暂无方法改善
    return "4"+"#"+temp;
}
}
   

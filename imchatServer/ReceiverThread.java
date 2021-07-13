/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imchatServer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author LJY
 */
public class ReceiverThread extends Thread {     //数据包

    private byte[] buffer = new byte[1024];
    private DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
    private DatagramSocket socket, socket2;
    private IMController controller;
    private int inport;

    List<InetAddress> ipList = new ArrayList<>();
    List<Integer> portList = new ArrayList<>();

    public ReceiverThread(IMController controller, int inport) throws SocketException {//初始化
        this.socket = new DatagramSocket(inport);
        this.controller = controller;
        this.inport = inport;
    }
public static String writeFile(String filePathAndName,String fileContent){
    try{
        File f=new File(filePathAndName);
        if(!f.exists()){
            f.createNewFile();
        }
        try (OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(f),"UTF-8")) {
            BufferedWriter writer =new BufferedWriter(write);
            //PrintWriter writer=new PrintWriter(new BufferedWriter(filePathAndName)));
            //PrintWriter writer=new PrintWriter(new FileWriter(filePathAndName)));
            writer.write(fileContent);
        }
    }catch (IOException e){
        System.out.println("写文件内容操作出错。");
    }
   return "已存入。";
}
    @Override
    public void run() {

        controller.appendMessage("服务器接收端口已打开。端口号：" + inport);

        while (true) {
            try {
                //一直搜东西，搜到后放入dp包中,死循环
                Arrays.fill(buffer, (byte) 0);      //接收数组一定要清零
                dp = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(dp);
                } catch (IOException ex) {
                    Logger.getLogger(ReceiverThread.class.getName()).log(Level.SEVERE, null, ex);
                }

                String receivedMessage = DataHandler.toString(dp);                    //输出到controller里
                controller.appendMessage("[" + dp.getAddress() + ":" + dp.getPort() + "]:>>"+receivedMessage);//输出IP和端口号

                byte[] temp = dp.getData();
                String str = new String(temp);

                switch (str.charAt(0)) {
                    case '1':
                        //将客户端私发的报解析并转发给目的客户端
                        try {
                            socket2 = new DatagramSocket(null);
                        } catch (SocketException ex) {
                            Logger.getLogger(ReceiverThread.class.getName()).log(Level.SEVERE, null, ex);
                        }   try {
                            socket2.send(DataHandler.destIncluded(temp));
                            socket2.close();
                            
                        } catch (IOException ex) {
                            Logger.getLogger(ReceiverThread.class.getName()).log(Level.SEVERE, null, ex);
                        }   controller.appendMessage("服务器已经向目标发送：" + receivedMessage);
                        break;
                    
                    case '2':
                        //将启动时客户端的ip和端口存入客户端的list
                        ipList.add(dp.getAddress());
                        portList.add(DataHandler.portIncluded(temp));
                        controller.appendMessage("激活成功！客户端IP： " +dp.getAddress()+"  端口："+DataHandler.portIncluded(temp)+"已保存地址到服务器。");
                        break;
                        
                    case'3':
                        //为群发
                        for (int i = 0; i <= ipList.size()-1; i++) {
                            try {
                                socket2 = new DatagramSocket(null);
                            } catch (SocketException ex) {
                                Logger.getLogger(ReceiverThread.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            try {
                                socket2.send(DataHandler.groupIncluded(temp,ipList.get(i),portList.get(i)));
                            } catch (IOException ex) {
                                Logger.getLogger(ReceiverThread.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            socket2.close();
                            controller.appendMessage("服务器已经群发：" + receivedMessage);
                        }   break;

                    default:
                        //无flag则表示接收的是文件
                                                                       
                       String strf = new String(temp,"UTF-8");
                       FileWriter fw=new FileWriter("E:\\test.txt");
                       fw.write(strf);
                       fw.close();
                                                            
                       controller.appendMessage("接收成功。");
                }
            }catch (IOException ex) {
                Logger.getLogger(ReceiverThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}

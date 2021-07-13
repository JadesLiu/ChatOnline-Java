/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imchatClient;

import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author LJY
 */
public class ReceiverThread extends Thread {     //数据包

    private byte[] buffer = new byte[1024];
    private DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
    private DatagramSocket socket;
    private IMController controller;
    private int inport;

    public ReceiverThread(IMController controller, int inport) throws SocketException {//初始化
        this.socket = new DatagramSocket(inport);
        this.controller = controller;
        this.inport = inport;
    }

    @Override
    public void run() {

        controller.appendMessage("服务器接收端口已打开。端口号：" + inport);

        while (true) {
            try {
                //一直搜东西，搜到后放入dp报中,死循环
                Arrays.fill(buffer, (byte) 0);      //接收数组一定要清零
                dp = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(dp);
                } catch (IOException ex) {
                    Logger.getLogger(ReceiverThread.class.getName()).log(Level.SEVERE, null, ex);
                }

                String receivedMessage = DataHandler.toString(dp);                    //输出到controller里
                controller.appendMessage("[" + dp.getAddress() + ":" + dp.getPort() + "]:>>" + receivedMessage);//输出IP和端口号

                byte[] temp = dp.getData();
                String str = new String(temp);

                if (str.charAt(0) != '4') {   //说明不是普通报，而是文件报


                    String strf = new String(temp, "UTF-8");
                    try (FileWriter fw = new FileWriter("D:\\testChat.txt")) {
                        fw.write(strf);
                    }

                    controller.appendMessage("接收成功。");
                }
            } catch (IOException ex) {
                Logger.getLogger(ReceiverThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imchatServer;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import javafx.event.ActionEvent;

/**
 * Sample Skeleton for 'IM.fxml' Controller Class
 */
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

class Struct{
    private InetAddress ip;
    private int port;

    public InetAddress getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
    
}



public class IMController {

    private InetAddress remoteIP;   //远程接收端IP地址
    private int remotePort;         //远程接收端端口
    private int localPort;          //本地端口
    private String sendMessage;     //发送消息
    private String receivedMessage; //接收消息
    private DatagramSocket socket;

    public InetAddress getRemoteIP() throws UnknownHostException {  //取值，字符串变网址
        return InetAddress.getByName(tfRemoteIP.getText());
    }

    public int getRemotePort() {      //文本变整数
        return Integer.parseInt(tfRemotePort.getText());
    }

    public int getLocalPort() {
        return Integer.parseInt(tfServerPort.getText());
    }

    public String getSendMessage() {
        return tfSendMessage.getText();
    }

    
     @FXML
    private TextArea taMessageList;

    @FXML
    private Button btnStart;

    @FXML
    private TextField tfRemotePort;

    @FXML
    private TextField tfSendMessage;

    @FXML
    private TextField tfRemoteIP;

    @FXML
    private TextField tfServerPort;

    @FXML
    private Button btnSend;
    
    @FXML
    void OnStartClicked(ActionEvent event) throws SocketException { //按下按钮时打开窗口来接收,线程一直运行

        // taMessageList.setText("启动按钮被点击！");
        ReceiverThread receiver = new ReceiverThread(this, getLocalPort());

        receiver.start();
    }

    @FXML
    void OnSendClicked(ActionEvent event) throws SocketException, UnknownHostException, IOException {
        socket = new DatagramSocket(null);   
        socket.send(DataHandler.toPacket(DataHandler.notFileData(getSendMessage()), getRemoteIP(), getRemotePort()));

        appendMessage("<<:" + getSendMessage());
        socket.close();
        // taMessageList.setText("发送按钮被点击！");
    }

    void appendMessage(String msg) {
        taMessageList.appendText(msg + "\n");
    }

}

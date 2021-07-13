/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imchatClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
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

public class IMController {

    private static InputStream FileInputStream(File f) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private InetAddress remoteIP;   //远程接收端IP地址
    private int remotePort;         //远程接收端端口
    private int localPort;          //本地端口
    private String sendMessage;     //发送消息
    private String receivedMessage; //接收消息
    private DatagramSocket socket;

    public InetAddress getRemoteIP() throws UnknownHostException {  //取值，字符串变网址
        return InetAddress.getByName(tfRemoteIP.getText());
    }

    public InetAddress getDestIP() throws UnknownHostException {  //取值，字符串变网址
        return InetAddress.getByName(tfDestIP.getText());
    }

    public int getRemotePort() {      //文本变整数，取得输入的远程端口号
        return Integer.parseInt(tfRemotePort.getText());
    }

    public int getDestPort() {      //文本变整数，取得输入的远程端口号
        return Integer.parseInt(tfDestPort.getText());
    }

    public int getLocalPort() {
        return Integer.parseInt(tfLocalPort.getText());
    }

    public String getPath() {
        return tfPath.getText();
    }

    public String getSendMessage() {
        return tfSendMessage.getText();
    }
    @FXML // fx:id="tfPath"
    private TextField tfPath; // Value injected by FXMLLoader

    @FXML // fx:id="tfDestPort"
    private TextField tfDestPort; // Value injected by FXMLLoader

    @FXML // fx:id="tfDestIP"
    private TextField tfDestIP; // Value injected by FXMLLoader

    @FXML // fx:id="taMessageList"
    private TextArea taMessageList; // Value injected by FXMLLoader

    @FXML // fx:id="tfRemotePort"
    private TextField tfRemotePort; // Value injected by FXMLLoader

    @FXML // fx:id=LocalPort"
    private TextField tfLocalPort; // Value injected by FXMLLoader

    @FXML // fx:id="btnStart"
    private Button btnStart; // Value injected by FXMLLoader

    @FXML // fx:id="btnPSend"
    private Button btnPSend; // Value injected by FXMLLoader

    @FXML // fx:id="btnGSend"
    private Button btnGSend; // Value injected by FXMLLoader

    @FXML // fx:id="btnPSendFile"
    private Button btnPSendFile; // Value injected by FXMLLoader

    @FXML // fx:id="tfRemoteIP"
    private TextField tfRemoteIP; // Value injected by FXMLLoader

    @FXML // fx:id="tfSendMessage"
    private TextField tfSendMessage; // Value injected by FXMLLoader

    @FXML // fx:id="btnSend"
    private Button btnSend; // Value injected by FXMLLoader

    @FXML
    void OnStartClicked(ActionEvent event) throws SocketException, UnknownHostException, IOException { //按下按钮时打开窗口来接收,线程一直运行

        // taMessageList.setText("启动按钮被点击！");
        ReceiverThread receiver = new ReceiverThread(this, getLocalPort());

        receiver.start();
        socket = new DatagramSocket();
        socket.send(DataHandler.toPacket(getLocalPort(), getRemoteIP(), getRemotePort()));
        socket.close();

    }

    @FXML
    void OnGSendClicked(ActionEvent event) throws SocketException, UnknownHostException, IOException {
        socket = new DatagramSocket(null);   //本地socket，不用地址（本地一个，远程一个）

        socket.send(DataHandler.toPacket(getSendMessage(), getRemoteIP(), getRemotePort()));

        appendMessage("<<:" + getSendMessage());
        socket.close();
        tfSendMessage.clear();
        // taMessageList.setText("发送按钮被点击！");
    }

    @FXML

    void appendMessage(String msg) {
        taMessageList.appendText(msg + "\n");
    }

    @FXML
    void OnPSendClicked(ActionEvent event) throws SocketException, UnknownHostException, IOException {
        socket = new DatagramSocket(null);   
        socket.send(DataHandler.toPacket(getSendMessage(), getRemoteIP(), getRemotePort(), getDestIP(), getDestPort()));

        appendMessage("<<:" + getSendMessage());
        socket.close();
        tfSendMessage.clear();
        // taMessageList.setText("发送按钮被点击！");
    }

    @FXML
    void OnPSendClickedFile(ActionEvent event) throws FileNotFoundException, IOException {
    //客户端私发文件
        char[] c = new char[500];
        try (FileReader fr = new FileReader(getPath());) {
            int num = fr.read(c);
            String str = new String(c, 0, num);

            byte[] file = str.getBytes("UTF-8");
            DatagramPacket dpf = new DatagramPacket(file, file.length, getDestIP(), getDestPort());
            socket = new DatagramSocket(null);
            socket.send(dpf);
            socket.close();
            appendMessage("已经向目标发送文件。");

        }

    }

}

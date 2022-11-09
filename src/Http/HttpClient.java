package Http;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;


public class HttpClient {
    String request = null;                     //保存从客户端到Web服务器的HTTP请求报文
    String serverDomainName = null;           //Web服务器的域名
    int srvPort = -1;                           //Web服务器的端口


    Socket socket = null;                       //Socket对象
    PrintWriter outToServer = null;           //向Web服务器发送HTTP请求报文的输出流
    DataInputStream inFromServer = null;     //读取Web服务器HTTP响应报文内容的输入流
    DataOutputStream outToFile = null;      //将Web服务器HTTP响应报文内容写入另外一个文本文件的输出流

    public String getServerDomainName() {
        return serverDomainName;
    }

    public void setServerDomainName(String serverDomainName) {
        this.serverDomainName = serverDomainName;
    }

    public int getSrvPort() {
        return srvPort;
    }

    public void setSrvPort(int srvPort) {
        this.srvPort = srvPort;
    }


    /**
     * 判断是否连接到服务器
     *
     * @ return the connected
     */
    public boolean isConnected() {
        if (this.socket == null) {
            return false;
        } else return this.socket.isConnected();
    }

    /**
     * 设置 HTTP 请求报文的内容
     *
     * @param request: the request to set
     */
    public void setRequest(String request) {
        this.request = request;
    }


    /**
     * 构造函数
     *
     * @param serverDomainName :服务器域名
     * @param srvPort          : 服务器端口
     */
    public HttpClient(String serverDomainName, int srvPort) {
        this.serverDomainName = serverDomainName;
        this.srvPort = srvPort;
    }


    /**
     * 连接到服务器
     */
    public void Connect() {
        if (!this.isConnected()) {
            try {
                // 创建 socket 对象并连接到指定的域名和端口
                this.socket = new Socket(serverDomainName, srvPort);
                outToServer = new PrintWriter(this.socket.getOutputStream(), true);
                inFromServer = new DataInputStream(this.socket.getInputStream());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 断开与服务器的连接
     */
    public void Disconnect() {
        if (this.isConnected()) {
            try {
                this.inFromServer.close();
                this.outToServer.close();
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 向服务器发送 HTTP 请求报文
     */
    public void sendHttpRequest() {
        this.outToServer.println(this.request);
    }

    /**
     * 得到服务器的 HTTP 相应报文并保存在文件里
     *
     * @param fileName : 文件名
     */
    public void getHttpResponse(String fileName) {
        try {
            this.outToFile = new DataOutputStream(new FileOutputStream(fileName));

            // 从服务器的应答中读入一个字节
            byte b = this.inFromServer.readByte();
            while (true) {
                this.outToFile.writeByte(b);
                b = this.inFromServer.readByte();
            }
        } catch (EOFException e) {
            System.out.println("End of Response!");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭文件输出流
                this.outToFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        HttpClient httpClient = new HttpClient("www.163.com", 80);
        httpClient.Connect();

        // 构造请求 HTTP 请求报文的首部
        String request = "GET / HTTP/1 .0\r\n" +
                "Host: www .163 .com\r\n" +
                "Connection: close\r\n" +
                "User-agent: Mozilla/4 .0\r\n" +
                "Accept-Language: zh-cn\r\n" +
                "Accept-Charset: GB2312,uft-8\r\n" +
                "\r\n";

        httpClient.setRequest(request);
        httpClient.sendHttpRequest();
        httpClient.getHttpResponse("response.txt");
        httpClient.Disconnect();
        System.out.println("OK");
    }

}




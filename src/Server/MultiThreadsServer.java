package Server;

import java.io.*;
import java.net.*;

import NetConfig.NetConfig;


public class MultiThreadsServer {

    protected int port;
    protected String addr;

    /**
     * 默认无参构造函数
     * @throws IOException
     */
    public MultiThreadsServer() throws IOException {
        NetConfig netConfig = new NetConfig();
        netConfig.readConfig(); // may throw IOException

        this.port = netConfig.getPort();
        this.addr = netConfig.getAddr();
    }


    /**
     * 通过配置文件地址构造
     * @param configFileDir
     * @throws IOException
     */
    public MultiThreadsServer(String configFileDir) throws IOException {
        NetConfig netConfig = new NetConfig(configFileDir);
        netConfig.readConfig(); // may throw IOException

        this.port = netConfig.getPort();
        this.addr = netConfig.getAddr();
    }

    /**
     * 构造函数，跳过配置文件直接指定 port 和 addr
     * @param port
     * @param addr String
     * @throws IOException
     */
    public MultiThreadsServer(int port, String addr) throws IOException {
        this.port = port;
        this.addr = addr;
    }
    public MultiThreadsServer(String port, String addr) throws IOException {
        this.port = Integer.parseInt(port);
        this.addr = addr;
    }


    public void multiThreadsRun() {

        try {

            // 2nd param : backlog – requested maximum length of the queue of incoming connections
            ServerSocket serverSocket = new ServerSocket(port, 20, InetAddress.getByName(addr));

            System.out.println("Server socket is listening port : " + serverSocket.getLocalPort());;

            // Number of threads
            int threadsNum = 0;

            // 持续监听
            while (true) {
                // Listen for a new connection request
                Socket connectToClient = serverSocket.accept();

                // Print the new connect number on the console
                System.out.println("Starting thread " + threadsNum);

                // Create a new thread for the connection
                ThreadHandler thread = new ThreadHandler(connectToClient, threadsNum);

                // Start the new thread
                thread.start();

                // Increment i to number the next connection
                threadsNum++;
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    public static void main(String[] args) {



    }
}

// Define the thread class for handling a new connection
class ThreadHandler extends Thread {

    private String PCProjectDir = System.getProperty("user.dir");


    private Socket connectToClient; // A connected socket
    private int counter; // Number current thread

    // Construct a thread
    public ThreadHandler(Socket socket, int threadsNum) {
        connectToClient = socket;
        counter = threadsNum;
    }

    // Implement the run() method for the thread
    public void run() {
        try {
            System.out.println("Build a link with client:" + connectToClient.getPort());

            // Continuously serve the client
            while (true) {
                InputStream socketInputStream = this.connectToClient.getInputStream();

                byte[] buffer = new byte[socketInputStream.available()];
                socketInputStream.read(buffer);
                String request = new String(buffer);

                // 网络不阻塞时, request 长度不为 0
                if(request.length() != 0) {
                    System.out.println("Request is : " + request);

                    String firstLineOfRequest = request.substring(0, request.indexOf("\r\n"));
                    String[] firstLineParts = firstLineOfRequest.split(" ");
                    String uri = firstLineParts[1];//获取uri
                    String filename1 = uri.replace("/","");

                    String filename2 = this.PCProjectDir + "\\sources\\" + filename1;

                    File file1 = new File(filename2);

                    if(!file1.exists()) uri = "/";
                    // 定义缺省状态
                    if (uri.equals("/")) {
                        uri = "/error.html";    //把缺省状态和无法定位状态放到一起去，少写一个缺省文件
                        firstLineParts[1] = "/error.html";
                        System.out.println("当文件不存在时，默认打开 error.html ");
                    }

                    String contentType = this.getFileType(uri);

                    // 报文长度
                    long contentLength = getFileSize(this.PCProjectDir + "\\sources" + uri.toLowerCase());


                    String responseFirstLine = "HTTP/1.1 200 OK\r\n";
                    String responseHeader = "Content-Type:" + contentType + "\r\n";
                    String responseLength = "Content-Length:" + contentLength + "\r\n\r\n";

                    InputStream inputStream = new FileInputStream(this.PCProjectDir + "\\sources\\" + uri.toLowerCase());
                    OutputStream outputStream = connectToClient.getOutputStream();

                    //输出请求处理结果
                    outputStream.write(responseFirstLine.getBytes());
                    outputStream.write(responseHeader.getBytes());
                    outputStream.write(responseLength.getBytes());
                    System.out.println("Content-length=" + contentLength);
                    System.out.println("uri"+uri);


                    int len = 0;
                    byte[] bytes = new byte[1024];
                    while ((len = inputStream.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, len);
                    }
                    // 等待客户接受HTTP响应结果
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // connectToClient.close();
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }



    private static String getFileType(String rowUri) {
        String uri = rowUri.toLowerCase();
        String fileType;
        if(uri.contains(".html") || uri.contains(".htm")) {
            fileType = "text/html";
        }
        else if(uri.contains(".jpg") || uri.contains(".jpeg")) {
            fileType = "image/jpeg";
        }
        else fileType = "text/plain";
        return fileType;
    }

    private static long getFileSize(String filename) {
        File file = new File(filename);
        if (!file.exists() || !file.isFile()) {
            System.out.println("404!Not Found File!");
            return -1;
        } else
            return file.length();
    }


}
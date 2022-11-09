package NetConfig;
import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;

public class NetConfig {

    protected String configFile = ".\\defaultConfig.txt";

    protected int port;
    protected String addr;

    protected int infoNum = 0; // 已经读取到的配置信息个数


    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
        // set 之后应该读取新的配置信息
        this.infoNum = 0;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }




    public NetConfig() {}
    public NetConfig(String configFile) {
        this.configFile = configFile;
        this.infoNum = 0;
    }


    public void readConfig() throws IOException {
        BufferedReader configReader = new BufferedReader(new FileReader(this.configFile));
        String str = "", address = "";
        while( (str = configReader.readLine()) != null ) {
            // 配置文件当前行含有 port 的配置信息
            if(str.contains("port:")) {
                // 读取 "port:" 后的配置信息
                str = str.substring(str.indexOf("port:")+"port:".length(), str.indexOf("/"));
                this.port = Integer.parseInt(str);
                infoNum++; // 已经获取到了一个配置信息
            }
            // 配置文件当前行含有 address 的配置信息
            if(str.contains("addr:")) {
                this.addr = str.substring(str.indexOf("addr:")+"addr:".length(), str.indexOf("/"));
                infoNum++;
            }
            if(infoNum >= 2) break; // 已读取相应信息
        }
    }

    public void readConfig(String configFile) throws IOException {
        setConfigFile(configFile);
        readConfig();
    }

}

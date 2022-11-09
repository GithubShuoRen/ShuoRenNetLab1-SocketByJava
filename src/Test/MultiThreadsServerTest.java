package Test;

import Server.MultiThreadsServer;

import java.io.IOException;
import java.util.Scanner;

public class MultiThreadsServerTest {

    protected static int port;
    protected static String addr;
    protected static String configFileDir;

    public static void main(String[] args) throws IOException {
        int mode = printInfoAndGetMode();
        while (mode != 0) {

            switch (mode) {

                case 1:
                    MultiThreadsServer server1 = new MultiThreadsServer();
                    server1.multiThreadsRun();
                case 2:
                    MultiThreadsServer server2 = new MultiThreadsServer(configFileDir);
                    server2.multiThreadsRun();
                case 3:
                    MultiThreadsServer server3 = new MultiThreadsServer(port, addr);
                    server3.multiThreadsRun();
            }

            mode = printInfoAndGetMode();
        }
        System.out.println("Thank you!");
    }


    public static int printInfoAndGetMode() {
        int mode = 0;
        System.out.println("Welcome to ShuoRen NetLab1 Demo");
        System.out.println("There are 3 patterns as follow");
        System.out.println("pattern 1: default mode ;  please enter 1");
        System.out.println("pattern 2: Direction mode; please enter 2");
        System.out.println("pattern 3: port and addr mode; please enter port and addr");
        System.out.println("pattern 0: quit, please enter 0");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.next();
        String[] splitInput = input.split(" ");

        // 仅输入了一个数字
        if (input.length() == 1) {
            mode = Integer.parseInt(input);
            if (mode == 2) {
                System.out.print("please enter configFile Dir:");
                configFileDir = scanner.next();
            }
            if(mode == 3) {
                System.out.println("Please input addr and port: ");
                input = scanner.next();
                splitInput = input.split(":");
                addr = splitInput[0];
                port = Integer.parseInt(splitInput[1]);
            }
            // mode = 1, 或者其他形式， 统一进入 mode 1 default 模式
            else if (mode != 0) {
                mode = 1;
            }
        }
        // 没有按照 port addr 的格式输入
        else if (splitInput.length != 2) {
            System.out.println("input format error, enter default mode");
            mode = 1;
        }

        return mode;
    }


}

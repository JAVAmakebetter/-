package morePeopleChat;

import java.io.*;
import java.net.Socket;

public class client {
    public static void main(String[] args) {
        Socket socket= null;
        try {
            socket = new Socket("127.0.0.1",12345);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("客户端连接成功:");

        PrintWriter out= null;
        BufferedReader consoleReader= null;
        BufferedReader in = null;
        try {
            //向服务器发出的信息
            OutputStream os = socket.getOutputStream();
            out = new PrintWriter(os,true);

            //获取客户端输出在控制台输出中的信息
            consoleReader = new BufferedReader(new InputStreamReader(System.in));

            //获取来自服务器的信息         
            InputStream is = socket.getInputStream();
            in = new BufferedReader(new InputStreamReader(is));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String serverMessage, clientMessage;
        while (true){
            try {
                //获取来自服务器的信息，并打印在控制台
                serverMessage= in.readLine();
                System.out.println("其他人"+serverMessage);

                //获取来自控制台的信息，并用输出流发送给服务器
                clientMessage=consoleReader.readLine();
                out.write("我:"+clientMessage);
                out.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //如果客户端发送拜拜，关闭流，退出程序
            if(clientMessage.equals("拜拜")){
                try {
                    consoleReader.close();
                    out.close();
                    in.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
        }
    }
}

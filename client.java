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
            OutputStream os = socket.getOutputStream();
            out = new PrintWriter(os,true);

            consoleReader = new BufferedReader(new InputStreamReader(System.in));

            InputStream is = socket.getInputStream();
            in = new BufferedReader(new InputStreamReader(is));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String serverMessage, clientMessage;
        while (true){
            try {
                serverMessage= in.readLine();
                System.out.println("其他人"+serverMessage);

                clientMessage=consoleReader.readLine();
                out.write("我:"+clientMessage);
                out.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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

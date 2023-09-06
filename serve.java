package morePeopleChat;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class serve {
    public static final Set<PrintWriter> CLIENT_HANDLER_SET = new HashSet<PrintWriter>();
    //创建集合用于接收多个客户端发送信息。
    private static final int PORT = 12345;

    public static void main(String[] args) {
        Socket socket = null;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("服务器启动:");
        while (true) {
            try {
                System.out.println("服务器连接成功:");
                //利用无限循环，每当有一个客户端与服务器连接成功，就创建一个线程俩维护这个客户端
                new ClientHandler(serverSocket.accept()).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

class ClientHandler extends Thread {
    Socket socket;
    BufferedReader br;
    PrintWriter pw;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            //创建输入流，来获取客户端发来的信息
            InputStream is = socket.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));

            //创建一个打印输出流，把客户端的信息发送到 CLIENT_HANDLER_SET 集合中，后期遍历集合发送给每一个客户端，就实现了多人聊天
            OutputStream os = socket.getOutputStream();
            pw = new PrintWriter(os, true);

            //加锁，防止添加到一般被系统剥夺执行权
            synchronized (serve.CLIENT_HANDLER_SET) {
                serve.CLIENT_HANDLER_SET.add(pw);
            }
            String message;
            while ((message = br.readLine()) != null) {
                //创建方法，遍历集合，发送每一个客户端，实现多人聊天
                sendMessage(message);
            }
        } catch (IOException e) {
            synchronized (serve.CLIENT_HANDLER_SET) {
                serve.CLIENT_HANDLER_SET.remove(pw);
            }
        }
    }
    private void sendMessage(String message) {
        //遍历集合，加锁的目的防止出现多线程安全问题
            synchronized (serve.CLIENT_HANDLER_SET) {
            for (PrintWriter pw : serve.CLIENT_HANDLER_SET) {
                pw.println(message);
            }
        }
    }
}


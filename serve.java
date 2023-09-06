package morePeopleChat;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class serve {
    public static final Set<PrintWriter> CLIENT_HANDLER_SET = new HashSet<PrintWriter>();
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
            InputStream is = socket.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));

            OutputStream os = socket.getOutputStream();
            pw = new PrintWriter(os, true);
            synchronized (serve.CLIENT_HANDLER_SET) {
                serve.CLIENT_HANDLER_SET.add(pw);
            }
            String message;
            while ((message = br.readLine()) != null) {
                sendMessage(message);
            }
        } catch (IOException e) {
            synchronized (serve.CLIENT_HANDLER_SET) {
                serve.CLIENT_HANDLER_SET.remove(pw);
            }
        }
    }
    private void sendMessage(String message) {
            synchronized (serve.CLIENT_HANDLER_SET) {
            for (PrintWriter pw : serve.CLIENT_HANDLER_SET) {
                pw.println(message);
            }
        }
    }
}


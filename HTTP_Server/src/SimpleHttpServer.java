import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class SimpleHttpServer extends Thread {
    public static final int HTTP_PORT=8000;   //监听端口
    ServerSocket serverSocket=null;

    public SimpleHttpServer() {
        try {
            serverSocket=new ServerSocket(HTTP_PORT);
        }catch (IOException e){
            e.printStackTrace();
        }
        if(serverSocket==null){
            throw new RuntimeException("服务器Socket初始化失败");
        }
    }

    @Override
    public void run() {
        try {
            //无限循环，进入等待连接状态
            while (true){
                System.out.println("等待连接中...");
                //一旦接收到连接请求，构建一个线程来处理，避免阻塞服务器线程
                new DeliverThread(serverSocket.accept()).start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new SimpleHttpServer().start();
//        InetAddress inetAddress= null;
//        try {
//            inetAddress = InetAddress.getLocalHost();
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        } finally {
//        }
//        System.out.println(inetAddress.getHostAddress());
    }
}

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        try (ServerSocket ss = new ServerSocket(8080);){
            Socket client = ss.accept();
            System.out.println("Pippo");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

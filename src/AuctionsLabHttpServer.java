import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.sql.*;

public class AuctionsLabHttpServer {
    final static String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    final static String DATABASE = "auctions";
    final static String DB_URL = "jdbc:mysql://localhost:3306/" + DATABASE;
    final static String USER = "root";
    final static String PASS = "RISOSCOTTI";
    final static int PORT = 9090;
    final static int MULTICAST_PORT = 9097;
    static Connection sqlConnection = null;
    static Statement statement = null;

    public static void main(String[] args) throws Exception {
        try {

            System.out.println("-Connecting to Database...");
            sqlConnection = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println(">Connection with " + DB_URL + " Established\n");

            System.out.println("-Initializing SQL Statement...");
            statement = sqlConnection.createStatement();
            System.out.println(">SQL Statement Initialized\n");

            System.out.println("-Starting Node.JS Proxy...");
            runMiddleMan();

            System.out.println("-Starting UDP Handler...");
            new UDPServer(MULTICAST_PORT, statement);

            System.out.println("-Starting TCP Handler...");
            new TCPServer(PORT, statement);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void UdpConnection()
    {
        try {
			InetAddress group = InetAddress.getByName("224.0.0.1");
			MulticastSocket multicast = new MulticastSocket(3456);
			multicast.joinGroup(group);
			
			byte[] buffer = new byte[100];
			
			DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
			
			multicast.receive(packet);
			
			System.out.println(new String(buffer));
			
			multicast.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public static void runMiddleMan() {
        new Thread(() -> {
            try {
                // String[] command = {"cmd.exe", "/c", "code", "--reuse-window",
                // "--new-terminal", "--wait", "--command", "node middleMan.js"};
                String[] command = { "cmd.exe", "/c", "start", "cmd.exe", "/k", "node", "proxyServer.js" };
                ProcessBuilder pb = new ProcessBuilder(command);
                pb.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    /*
     * private static void amogus() {
     * System.out.println("-----------------⣠⣤⣤⣤⣤⣤⣤⣤⣤⣄⡀---------");
     * System.out.println("-------------⢀⣴⣿⡿⠛⠉⠙⠛⠛⠛⠛⠻⢿⣿⣷⣤--------");
     * System.out.println("-------------⣼⣿⠋-------⢀⣀⣀⠈⢻⣿⣿⡄------");
     * System.out.println("------------⣸⣿⡏---⣠⣶⣾⣿⣿⣿⠿⠿⠿⢿⣿⣿⣿⣄-----");
     * System.out.println("------------⣿⣿⠁--⢰⣿⣿⣯⠁-------⠈⠙⢿⣷⡄---");
     * System.out.println("------⣀⣤⣴⣶⣶⣿⡟---⢸⣿⣿⣿⣆----------⣿⣷----");
     * System.out.println("-----⢰⣿⡟⠋⠉⣹⣿⡇---⠘⣿⣿⣿⣿⣷⣦⣤⣤⣤⣶⣶⣶⣶⣿??----");
     * System.out.println("-----⢸⣿⡇--⣿⣿⡇----⠹⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿??----");
     * System.out.println("-----⣸⣿⡇--⣿⣿⡇-----⠉⠻⠿⣿⣿⣿⣿⡿⠿⠿⠛⢻???----");
     * System.out.println("-----⣿⣿⠁--⣿⣿⡇-----------------⢸⣿⣧----");
     * System.out.println("-----⣿⣿---⣿⣿⡇-----------------⢸⣿⣿----");
     * System.out.println("-----⣿⣿---⣿⣿⡇-----------------⢸⣿⣿----");
     * System.out.println("-----⢿⣿⡆--⣿⣿⡇-----------------⢸⣿⡇----");
     * System.out.println("-----⠸⣿⣧⡀-⣿⣿⡇-----------------⣿⣿⠃----");
     * System.out.println("------⠛⢿⣿⣿⣿⣿⣇-----⣰⣿⣿⣷⣶⣶⣶⣶⢠⣿⣿????----");
     * System.out.println("------------⣿⣿------⣿⣿⡇-⣽⣿⡏--⢸⣿?-----");
     * System.out.println("------------⣿⣿------⣿⣿⡇-⢹⣿⡆---⣸⣿⠇----");
     * System.out.println("------------⢿⣿⣦⣄⣀⣠⣴⣿⣿⠁--⠻⣿⣿⣿⣿⡿??-----");
     * System.out.println("------------⠈⠛⠻⠿⠿⠿⠿⠋⠁----------------");
     * 
     * }
     */
}

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class Server {
    public static <JSONArray> JSONArray ShowAsta(String oggetto)
    {
        ArrayList<String> asta = new ArrayList<String>();
        Connection con;
        String url = "jdbc:mysql://localhost:3306";
        final String schema = "asta";
        String user = "root";
        String password ="Cabumy26#";
        try {
            con = DriverManager.getConnection(url+"/"+schema,user, password);
            
            System.out.println("connessione effettuata");
            
            Statement stat = con.createStatement();
            
            ResultSet rs = stat.executeQuery("Select * from astab where oggetto="+oggetto);
            ResultSetMetaData md = rs.getMetaData();
            int numCols = md.getColumnCount();
            List<Object> colNames = IntStream.range(0, numCols)
                      .mapToObj(i -> {
                          try {
                              return md.getColumnName(i + 1);
                          } catch (SQLException e) {
                              e.printStackTrace();
                              return "?";
                          }
                      })
                      .collect(Collectors.toList());
            
            JSONArray result = new JSONArray();
            while (rs.next()) {
                JSONObject row = new JSONObject();
                colNames.forEach(cn -> {
                        row.put(cn, rs.getObject(cn));
                    
                });
                result.add(row);
            }
            
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return result;
	public static void main(String[] args) {
		ArrayList<String> asta = new ArrayList<String>();
		Scanner myObj = new Scanner(System.in);
		System.out.println("Inserisci");
		String oggetto ="'"+myObj.nextLine()+"'";
		asta = ShowAsta(oggetto);
		try {
			// 1 - pubblicare una ServerSocket, decidendo una porta NON RISERVATA
			ServerSocket serverSocket = new ServerSocket(8080);

			// 2 - mettersi in attesa di ricevere richieste dai client, ed accettarle
			// METODO BLOCCANTE
			
			System.out.println("Sono in attesa di accetare un client");
			
			Socket comunicationSocketFromServer = serverSocket.accept();

			System.out.println("Client accettato");

			InputStream inputStreamServer = comunicationSocketFromServer.getInputStream();

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStreamServer));

			System.out.println("Sono in attesa di un messaggio dal client");

			// METODO BLOCCANTE
			String messaggio = bufferedReader.readLine();
			
			System.out.println("Ricevuto dal client: " + messaggio);
			
			OutputStream outputStreamServer = comunicationSocketFromServer.getOutputStream();

			DataOutputStream dataOutputStream = new DataOutputStream(outputStreamServer);
			
			String risposta = messaggio;
			dataOutputStream.writeBytes(risposta);
			
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}

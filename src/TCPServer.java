import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mysql.cj.xdevapi.JsonParser;

public class TCPServer {
    private JSONObject jsonObject;


    static ServerSocket serverSocket = null;
    static Statement statement = null;

    public TCPServer(int port, Statement sqlstatement) throws Exception {
        try {
            statement = sqlstatement;
            serverSocket = new ServerSocket(port);
            System.out.println(">TCP Server started at localhost:" + port + "\n");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from " + clientSocket.getInetAddress());
                // TCP Thread, Runnable as Lambda
                new Thread(() -> {
                    try {
                        handleRequest(clientSocket);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    public void jsonToObj(JSONArray Ojson)
    {
        JsonParser parser = new JsonParser();
        Object obj = parser.parseArray(new JSONArray(Ojson));
        this.jsonObject = (JSONObject)obj;
    }
    public List<String> getRoba()
    {
        List<String> roba = new ArrayList<>();
        Iterator iter = this.jsonObject.keySet().iterator();
        while(iter.hasNext())
        {
            String robas = (String) iter.next();
            roba.add(robas);
        }
        
        return roba;
        
    }
    
    private static void handleRequest(Socket clientSocket) throws Exception {
        InputStream input = clientSocket.getInputStream();
        OutputStream output = clientSocket.getOutputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line = reader.readLine();
        if (line != null) {
            String[] tokens = line.split(" ");
            if (tokens.length >= 2) {
                String method = tokens[0];
                String path = tokens[1];

                String body = parseRequestBody(reader);
                
                System.out.println(line + " body:{" + body + "}");
                if (method.equalsIgnoreCase("GET")) {
                    handleGet(path, output);
                } else if (method.equalsIgnoreCase("POST")) {
                    handlePost(path, body, output);
                } else {
                    sendNotFound(output);
                }
            } else {
                sendNotFound(output);
            }
        } else {
            sendNotFound(output);
        }

        output.close();
        input.close();
        clientSocket.close();
    }

    private static void handleGet(String path, OutputStream output) throws Exception {
        if (path.equals("/")) {
            ResultSet rs = statement.executeQuery("Select * from items");
            String response = "{\"message\": \"" + parseResultSet(rs).toString() + "\"}";
            String headers = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: application/json\r\n" +
                    "Access-Control-Allow-Origin: *\r\n" +
                    "Content-Length: " + response.length() + "\r\n" +
                    "\r\n";
            output.write(headers.getBytes());
            output.write(response.getBytes());
        } else {
            sendNotFound(output);
        }
    }

    private static void handlePost(String path, String body, OutputStream output) throws Exception {
        if (path.equals("/")) {
            String response = "{\"message\": \"" + body + "\"}";
            String headers = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: application/json\r\n" +
                    "Access-Control-Allow-Origin: *\r\n" +
                    "Content-Length: " + response.length() + "\r\n" +
                    "\r\n";
            output.write(headers.getBytes());
            output.write(response.getBytes());
        } else if (path.equals("/getbyname")) {
            String query = "";
            if (isNumeric(body)) {
                query = "Select * from items where ItemID =" + body;
            } else {
                query = "Select * from items where Item_Name like '%" + body + "%'";
            }
            ResultSet rs = statement.executeQuery(query);
            String response = "{\"message\": \"" + parseResultSet(rs).toString() + "\"}";
            String headers = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: application/json\r\n" +
                    "Access-Control-Allow-Origin: *\r\n" +
                    "Content-Length: " + response.length() + "\r\n" +
                    "\r\n";
            output.write(headers.getBytes());
            output.write(response.getBytes());
        } else if (path.equals("/offerPage")) {
            if (!body.isEmpty() && isNumeric(body)) {
                ResultSet rs = statement.executeQuery("Select * from items where ItemID = " + body);
                String response = "{\"message\": \"" + parseResultSet(rs).toString() + "\"}";
                String headers = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: application/json\r\n" +
                        "Access-Control-Allow-Origin: *\r\n" +
                        "Content-Length: " + response.length() + "\r\n" +
                        "\r\n";
                output.write(headers.getBytes());
                output.write(response.getBytes());
            } else {
                sendNotFound(output);
            }
        } else {
            sendNotFound(output);
        }
    }

    private static void sendNotFound(OutputStream output) throws Exception {
        String response = "404 Not Found";
        String headers = "HTTP/1.1 404 Not Found\r\n" +
                "Content-Type: text/plain\r\n" +
                "Access-Control-Allow-Origin: *\r\n" +
                "Content-Length: " + response.length() + "\r\n" +
                "\r\n";
        output.write(headers.getBytes());
        output.write(response.getBytes());
    }

    private static String parseRequestBody(BufferedReader reader) throws Exception {
        // headers
        Map<String, String> headers = new HashMap<>();
        String headerLine;
        while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
            String[] headerTokens = headerLine.split(":");
            if (headerTokens.length == 2) {
                headers.put(headerTokens[0].trim(), headerTokens[1].trim());
            }
        }
        // body
        int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
        char[] buffer = new char[1024];
        StringBuilder bodyBuilder = new StringBuilder();
        int bytesRead;
        while ((bytesRead = reader.read(buffer, 0, Math.min(buffer.length, contentLength))) > 0) {
            bodyBuilder.append(buffer, 0, bytesRead);
            contentLength -= bytesRead;
            if (contentLength <= 0) {
                break;
            }
        }
        String body = bodyBuilder.toString();
        return body;
    }

    private static JSONArray parseResultSet(ResultSet resultSet) throws SQLException {
        ResultSetMetaData md = resultSet.getMetaData();
        int numCols = md.getColumnCount();
        List<String> colNames = IntStream.range(0, numCols)
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
        while (resultSet.next()) {
            JSONObject row = new JSONObject();
            colNames.forEach(cn -> {
                try {
                    row.put(cn, resultSet.getObject(cn));
                } catch (JSONException | SQLException e) {
                    e.printStackTrace();
                }
            });
            result.put(row);
        }
        return result;
    }

    private static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
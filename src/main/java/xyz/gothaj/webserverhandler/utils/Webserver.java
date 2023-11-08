package xyz.gothaj.webserverhandler.utils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import xyz.gothaj.webserverhandler.WebAPI;

import java.sql.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Webserver {

    private int port;

    private static WebAPI webAPI;

    private static String host = "mysql.hostify.cz";
    private static String user = "db_74345_gothajdb";
    private static String password = "Kokotka123";

    public Webserver(WebAPI webAPI,int port){
        this.webAPI = webAPI;
        this.port = port;
        this.setupHTTPServer();
    }

    public void setupHTTPServer() {
        HttpServer server = null;
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            webAPI.getLogger().info("[Website API] Website is not setuped, something went wrong!");
            return;
        }
        server.createContext("/validate", new ValidationHandler());
        ExecutorService executor = Executors.newFixedThreadPool(10);
        server.setExecutor(executor);
        webAPI.getLogger().info("[Website API] Website is running on port: " + port);
        server.start();
    }

    static class ValidationHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String query = httpExchange.getRequestURI().getQuery();

            ArrayList<Variabile> variabiles = VariabileHandler.handleArguments(query);
            if (httpExchange.getRequestURI().getPath().equals("/validate")) {

                String q = "select * from accounts where hwid='"+variabiles.get(0).getValue()+"'";

                String id = null;
                String name =null;
                String hwid =null;
                Connection connect = null;
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    connect = DriverManager
                            .getConnection("jdbc:mysql://"+host+":3306/"+user+"?"
                                    + "user="+user+"&password="+password);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                try {
                    Statement statement = connect.createStatement();
                    ResultSet resultSet = statement
                            .executeQuery(q);

                    while (resultSet.next()) {
                        id = resultSet.getString("id");
                        name = resultSet.getString("name");
                        hwid = resultSet.getString("hwid");
                    }

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                try {
                    connect.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                String response;
                if(variabiles == null || variabiles.size() != 1 || !variabiles.get(0).getName().equals("hwid") || connect == null || (id == null || name == null || hwid == null)) {
                    response = "{" +
                            "\"error\": \"Error\""+
                            "}";
                }else{


                    response = "{" +
                            "\"id\": \""+ id +"\","+
                            "\"name\": \""+ name +"\","+
                            "\"hwid\": \""+ hwid +"\""+
                            "}";
                }

                httpExchange.sendResponseHeaders(200, response.length());
                OutputStream os = httpExchange.getResponseBody();
                os.write(response.getBytes());
                os.close();

            } else {
                String response = "Invalid response";
                httpExchange.sendResponseHeaders(404, response.length());
                OutputStream os = httpExchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

}

package com.example.lab;
import java.sql.*;
public class DataBase{
    public static void main(String[] args){
        String url = "jdbc:postgresql://172.16.107.156:0612/test";
        String username = "Alex";
        String password = "1q2w3e4r";
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(url, username, password);
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("select version();");
            long avgtime = 0;
            int n = 10;
            for(int i=0;i<n;i++){
                long start = System.nanoTime();
                Connection connection = DriverManager.getConnection(url, username, password);
                long end = System.nanoTime() - start;
                System.out.println(end + "ns");
                connection.close();
                avgtime += end;
            }
            System.out.println("avg time: " + avgtime/n + "ns");
        }  catch (Exception e){
            e.printStackTrace();
        }
    }

}
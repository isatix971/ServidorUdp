/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverudp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author fvergarh
 */
public class ServerUdp {

    /**
     * @param args the command line arguments
     */
    private static Connection cnx = null;

    public static Connection obtener() throws SQLException, ClassNotFoundException {
        if (cnx == null) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                cnx = DriverManager.getConnection("jdbc:mysql://localhost/gpssace", "root", "lalola1415");
            } catch (SQLException ex) {
                throw new SQLException(ex);
            } catch (ClassNotFoundException ex) {
                throw new ClassCastException(ex.getMessage());
            }
        }
        return cnx;
    }

    public static void cerrar() throws SQLException {
        if (cnx != null) {
            cnx.close();
        }
    }

    public static void main(String[] args) throws Exception {
        DatagramSocket serverSocket = new DatagramSocket(9876);
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            String sentence = new String(receivePacket.getData());
            String[] fragmento = sentence.split(",");

//            for (int i = 0; i < fragmento.length; i++) {
//                System.out.println(i + ": " + fragmento[i]);
//            }
            Connection conex = obtener();

            Statement stmt = conex.createStatement();

            Float a1 = Float.valueOf(fragmento[2]);
            Float a2 = Float.valueOf(fragmento[4]);

            System.out.println(a1 + "-a-" + a2);
            int b1 = a1.intValue()/100;
            int b2 = a2.intValue()/100;

            System.out.println(b1 + "-b-" + b2);
            float c1 = a1.intValue() - (b1 * 100);
            float c2 = a2.intValue() - (b2 * 100);

            System.out.println(c1 + "-c-" + c2);
            float d1 = c1 / 60;
            float d2 = c2 / 60;
            System.out.println(d1 + "-d-" + d2);

            float e1 = (b1 + d1);
            float e2 = (b2 + d2);
            System.out.println(e1 + "-e-" + e2);

            String sql;
            sql = "INSERT INTO coordenadas ( x, y, comentario) VALUES ( " + e1 + ", " + e2 + ", '" + fragmento[0] + "')";
            stmt.execute(sql);
//System.out.println(rs);
            System.out.println("RECEIVED: " + sentence);
            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
            String capitalizedSentence = sentence.toUpperCase();
            sendData = capitalizedSentence.getBytes();
            DatagramPacket sendPacket
                    = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            serverSocket.send(sendPacket);
        }
    }

}

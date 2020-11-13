/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.utility.vimakdata;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Simon.waweru
 */
public class DBConnector {

    String pastDB = "jdbc:mysql://127.0.0.1:3306/newpaint_limsoft_db?useSSL=false&allowPublicKeyRetrieval=true&zeroDateTimeBehavior = CONVERT_TO_NULL";

    String currentDB = "jdbc:mysql://127.0.0.1:3306/smarthealth?useSSL=false&allowPublicKeyRetrieval=true&zeroDateTimeBehavior = CONVERT_TO_NULL";

    private final String USERNM = "smarthealth";
    private final String PASSWD = "Sm@rt_123";

    public Connection ConnectToPastDB() throws Exception {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connMain = DriverManager.getConnection(pastDB, USERNM, PASSWD);
            return connMain;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new Exception("Error connection ", e);
        }
    }

    public static void main(String[] args) {
        try {
            ResultSet rs = null;
            PreparedStatement pst = null;
            PreparedStatement pst2 = null;
            Connection conn = null;

            DBConnector dBConnector = new DBConnector();
            Connection connnnnn = dBConnector.msConnection();

            String fetchPatientData = "SELECT p.RegID, p.FileNo,FirstName,LastName,ParentsName FROM NewPaint_LimSoft_DB.dbo.limpatients AS p ";
            pst = connnnnn.prepareStatement(fetchPatientData);
            rs = pst.executeQuery();
            while (rs.next()) {
                System.out.println("rs.gggggggg " + rs.getString("RegID"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection msConnection() throws Exception {
        try {
            //        DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());   
//169.254.228.249
//            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//            Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            String dbURL = "jdbc:sqlserver://LP-TECH-4Z0Y\\SQLEXPRESS:1433;database=NewPaint_LimSoft_DB;user=sa;password=Admin@12345";
//            String dbURL = "jdbc:sqlserver://LIMSOFT-SERVER\\Limsoft:1433;database=NewPaint_LimSoft_DB;user=newuser;password=smart123";
            Connection conn = DriverManager.getConnection(dbURL);
            System.out.println("success");
            return conn;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new Exception("Error connection ", e);
        }
    }

    public Connection ConnectToCurrentDB() throws Exception {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connMain = DriverManager.getConnection(currentDB, USERNM, PASSWD);
            return connMain;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new Exception("Error connection ", e);
        }
    }
}

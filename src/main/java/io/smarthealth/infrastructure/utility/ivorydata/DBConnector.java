/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.utility.ivorydata;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Simon.waweru
 */
public class DBConnector {

    String pastDB = "jdbc:mysql://127.0.0.1:3306/hospitaldb?useSSL=false&allowPublicKeyRetrieval=true&zeroDateTimeBehavior = CONVERT_TO_NULL";

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
}

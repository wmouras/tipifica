package com.br.sos.lib;

import java.sql.*;
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

public class DbConnection {

    static Logger logger = Logger.getLogger(DbConnection.class);

    public static Connection connect(){

        BasicConfigurator.configure();
        try{
            Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/eventus", "root", "admin");
            return conn;
        }catch (SQLException se){
            logger.warn(se.getMessage());
        }
        return null;
    }

}

package database;

import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author krish
 */
public class connection {
    
    private static final String dbName = "U060iN";
    private static final String DB_URL = "jdbc:mysql://52.206.157.109/" + dbName;
    private static final String userName = "U060iN";
    private static final String password = "53688665828";
    private static final String driver = "com.mysql.jdbc.Driver";
    static Connection conn;
    
    public static void makeConnection() throws ClassNotFoundException, SQLException, Exception
    {
        
        Class.forName(driver);
        conn = (Connection) DriverManager.getConnection(DB_URL, userName, password);

    }
    
    public static Connection getConnection() throws ClassNotFoundException, SQLException{
        
        Class.forName(driver);
        conn = (Connection) DriverManager.getConnection(DB_URL, userName, password);
        return conn;
        
    }
    
    public static void closeConnection() throws ClassNotFoundException, SQLException, Exception
    {
        conn.close();
    }
    
}

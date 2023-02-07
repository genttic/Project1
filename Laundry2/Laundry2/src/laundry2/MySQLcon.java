/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package laundry2;
import java.sql.*;
import javax.swing.JOptionPane;
/**
 *
 * @author Gent
 */
public class MySQLcon {
    
    static private String url =  "jdbc:mysql://localhost:3306/laundry?useTimezone=true&serverTimezone=UTC";
    static private String user  = "root";
    static private String password = "Cerv@d1ku";
    private Connection m_con;
    private static MySQLcon instance = null;
    static public String useri;
    static public String email;
    
    
    private MySQLcon(){
       
        try
            {  
                //dynamically load the driver's class file into memory, which automatically registers it
                Class.forName("com.mysql.cj.jdbc.Driver"); 

                //Create connection to DB - world is database name, root is username and 1234 is the password 
                m_con=DriverManager.getConnection(url, user, password);           


            }
            catch(Exception e)
            { 
                
            }  
    }
    
    public static MySQLcon getInstance()
    {
        if (instance == null)
        {
            instance = new MySQLcon();
            
        }
        return instance;
    }
    
    public Connection getConnection()
    {
        return m_con;
    }
}

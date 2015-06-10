package retrylogicexample;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.microsoft.sqlserver.jdbc.*;

/**
 *
 * @author Luis Bosquez(lbosq)
 * @email lbosq@microsoft.com
 */
public class RetryLogicExample {
    // Database-side timeout value.
    static final int connectionTimeoutSeconds = 2;
    // Max number of times the program will retry to connect.
    static int maxCountTriesConnectAndQuery = 3;
    // Delay time between retries
    static int secondsBetweenRetries = 2;
    
    // Identified transient error codes.
    static int[] transientErrorCodes = {4060, 10928, 10929, 40197, 40501, 40544, 40549, 40550, 40551, 40552, 40553, 40613};
    
    // Connection URL obtained from the SQL DB portal.
    static String connectionUrl = "jdbc:sqlserver://{your_server}.database.windows.net:1433;" + 
                "database=test;"
                + "user={your_username}}@{your_server}};"
                + "password={your_password};"
                + "encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout="+ connectionTimeoutSeconds +""; 
    
    public static void main(String[] args) {
        connectAndQuery();
        System.out.println("End of execution.");
    }
    
    public static void connectAndQuery(){
        Connection con = null;
        long delayTime = 1;
        for (int cc = 1; cc <= maxCountTriesConnectAndQuery; cc++){
            try
            {
                // If the connection is unsuccessful, the program will follow the retry logic. Otherwise, the execution will end.
                establishConnection(con);
                if(con != null){
                    try { con.close(); } catch(Exception e) {}
                }
                System.out.println("Connection successful.");
                break;
            }
            catch (SQLException se)
            {
                // Print out error message.
                System.out.println(se.getMessage());
                
                // Boolean flag to identify transient errors.
                boolean isTransientError;
                
                // Transient error detection strategy.
                isTransientError = detectTransientError(se);
                
                // If a transient error wasn't detected, it means that the error is persistent and will not be sorted out by the retry logic.
                if(isTransientError == false){
                    System.out.println("Persistent error suffered. Error number: " + se.getErrorCode());
                    break;
                }
                
                System.out.println("Transient error encountered. Program will retry by itself. Error number: " + se.getErrorCode());
                System.out.println("" + cc + " attempts made so far.");
            }
            catch (Exception e)
            {
                System.out.println("Unexpected exception type caught. Will terminate.");
            }
            
            // Delay time increases exponentially.
            delayTime *= secondsBetweenRetries;
            
            try {
                // Execution is delayed before retrying.
                Thread.sleep(delayTime * 1000);
                System.out.println("Waiting " + secondsBetweenRetries + " seconds...");
            } catch (InterruptedException ex) {
                Logger.getLogger(RetryLogicExample.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        
    }
    
    public static void establishConnection(Connection con) throws SQLException, Exception{
        con = DriverManager.getConnection(connectionUrl);
        if (con != null){
            System.out.println("Connection established!");
        }
        else 
        {
            System.out.println("Connection unsuccessful");
            throw new Exception();
        }
    }
    
    public static boolean detectTransientError(SQLException se){
        int errorCode = se.getErrorCode();
        
        for(int i = 0; i<transientErrorCodes.length; i++){
            if (errorCode == transientErrorCodes[i]){
                return true;
            }
        }
        
        return false;
    }
}

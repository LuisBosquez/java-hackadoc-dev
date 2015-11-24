<properties 
	pageTitle="Retry Logic Java Sample" 
	description="Presents a Java code sample that uses retry logic to connect to Azure SQL DB."
	services="sql-database" 
	documentationCenter="" 
	authors="LuisBosquez" 
	manager="jeffreyg" 
	editor="genemi"/>

<tags 
	ms.service="sql-database" 
	ms.workload="data-management" 
	ms.tgt_pltfrm="na" 
	ms.devlang="java" 
	ms.topic="article" 
	ms.date="04/29/2015" 
	ms.author="lbosq"/>

# Retry Logic Java Sample #

This topic presents a Java code sample that you can use to connect to Azure SQL Database with .

## Requirements ##

- [Microsoft JDBC Driver for SQL Server - SQL JDBC 4](http://www.microsoft.com/download/details.aspx?displaylang=en&id=11774).
- Any operating system platform that runs [Java Development Kit 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).
- An existing database on SQL Azure. See the [Get Started topic](sql-database-get-started.md) to learn how to create a sample database and retrieve your connection string.

## Connecting to your SQL Database ##
In the following example, we connect to an existing database hosted in Azure. The application includes the code in the `main` function of the `SQLDatabaseTest` class. The naming conventions were selected in this way purely for demonstration purposes.

This example uses the following schema:

	CREATE TABLE Person
	(
		id INT PRIMARY KEY AUTO_INCREMENT,
		firstName VARCHAR(32),
		lastName VARCHAR(32),
		age INT
	);

For the connection, we use the connection string from the Azure Management Portal (See [Create your first Azure SQL Database](http://azure.microsoft.com/en-us/documentation/articles/sql-database-get-started/)) to generate a `Connection` object. **Please note** that you have to type your password into the generated connection string. 
 
 
	import java.sql.*;
	import com.microsoft.sqlserver.jdbc.*;

	public class SQLDatabaseTest {
	
	    public static void main(String[] args) {
	        String connectionUrl = "jdbc:sqlserver://server.database.windows.net:1433;" + 
	                "database=test;"
	                + "user=user@server;"
	                + "password={your_password_here};"
	                + "encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"; 
	        
			// Declare the JDBC objects.
	        Connection con = null;
	        Statement stmt = null;
	        ResultSet rs = null;
			// Prepared statement for INSERT operation.
			PreparedStatement pstmt = null;
	        // Prepared statement for TRANSACTION operation.
	        try {
	            con = DriverManager.getConnection(connectionUrl);
	
	            // Select a set of rows from the table.
	            // ...

				// Insert a row into the table.
				// ...
				
				// Create and execute transactions.
				// ...
	        } catch (Exception e) {
				// Catch and print any exceptions
	            e.printStackTrace();
	        }
	        finally {
				//Close the connections once the data has been handled.
	            if (rs != null) try { rs.close(); } catch(Exception e) {}
	            if (stmt != null) try { stmt.close(); } catch(Exception e) {}
				if (updateAge != null) try { updateAge.close(); } catch(Exception e) {}
				if (pstmt != null) try { pstmt.close(); } catch(Exception e) {}
	            if (con != null) try { con.close(); } catch(Exception e) {}
	        }
	    }	    
	}

## Executing SELECT operations ##
To execute a SELECT operation, we create a `Statement` object and pass the SQL instruction in the form of a string to its `executeQuery` method. This method will return a `ResultSet` object that can be iterated. 

The following code is called from inside the `//Select a set of rows from the table.` block of the above example:

	// Create and execute a SELECT SQL statement.
    String selectSql = "SELECT firstName, lastName, age FROM dbo.Person";
	stmt = con.createStatement();
    rs = stmt.executeQuery(selectSql);
	
	// Iterate through the result set and print the attributes.
	while (rs.next()) {
    	System.out.println(rs.getString(2) + " " + rs.getString(3));
	}

## Insert a row, pass parameters and retrieve the generated primary key ##
The following example uses a SQL INSERT statement that will insert a new entry in the table defined above. For this purpose, we create a `PreparedStatement` by calling the `prepareStatement` method of the `Connection` object and providing the SQL instruction as a string, as well as the `Statement.RETURN_GENERATED_KEYS` flag to return the values of the keys that were generated as a result. 

The following code is called from inside the `// Insert a row into the table` block in the first example: 

	// Create and execute an INSERT SQL prepared statement.
    String insertSql = "INSERT INTO Person (firstName, lastName, age) VALUES "
		+ "('Bill', 'Gates', 59),"
		+ "('Steve','Ballmer',59);";
    
    pstmt = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
    pstmt.execute();
    // Retrieve the generated key from the insert.
    rs = pstmt.getGeneratedKeys();
    // Iterate through the set of generated keys.
    while (rs.next()) {
        System.out.println("Generated: " + rs.getString(1));
    }

## Create and execute transactions ##
The following example executes a transaction to increase the age field for each entry of the Person table by one. To do this, we use a `PreparedStatement` object and provide a SQL instruction in the form of a string when we call the `prepareStatement` method of the `Connection` object.

After that, the transaction is executed through the `executeUpdate` method and then committed to the database through the `commit` method.

The following code is called from inside the `// Create and execute transactions.` block in the first example: 

    // Set AutoCommit value to false to execute a single transaction at a time.
    con.setAutoCommit(false);
    
    // Write the SQL Update instruction and get the PreparedStatement object.
    String transactionSql = "UPDATE Person SET Person.age = Person.age + 1;";
    updateAge = con.prepareStatement(transactionSql);
    
    // Execute the statement.
    updateAge.executeUpdate();
    
    //Commit the transaction.
    con.commit();
    
    // Return the AutoCommit value to true.
    con.setAutoCommit(true);
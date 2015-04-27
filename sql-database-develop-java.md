 # Using SQL Database by using Java with JDBC #

### Important Note ###
The following article assumes that the developer is including Microsoft proprietary APIs. If the developer uses APIs from other sources, the steps included in this article might not work properly.

## Getting a SQL Database ##

Refer to [Creating a new SQL Database](http://markdownpad.com)

## Requirements ##
### Linux, Unix, Windows 7, Windows 8, Windows 8.1, Windows Server 2008 R2, Windows Server 2012, Windows Vista ###

- A SQL Database running on Azure
- [Microsoft JDBC Driver for SQL Server](http://www.microsoft.com/en-us/download/details.aspx?displaylang=en&id=11774)

## Connect to your SQL Database ##
### 1. Build a connection string ###
Build your connection string by providing the following parameters:

- *URL*: Driver and location of host.
- *Username*: Database account username.
- *Database Name*: Name of the database schema to connect to.
- *Password*: Database account password.
- *Additional Parameters*: Optional parameters that include encryption, certificates, timeouts and other options.

Here is an example of a connection string:

	private static final String CONNECTION_STRING = "jdbc:sqlserver://database_server:port;" +
	            "database=yourDatabase;" +
	            "user=username@database_server;" +
	            "password=*******;";
	

### 2. Establish the connection ###
A connection is established by calling the `getConnection()` method of the `DriverManager` class.

	connection = DriverManager.getConnection(CONNECTION_STRING);


### 3. Execute a statement ###
After connecting to a database, a `statement` object has to be created by calling the `createStatement()` method of the `connection` object that was created in the previous step.

	statement = connection.createStatement();

A statement object will handle the execution of SQL statements.

## Execute an SQL SELECT ##

### 1. Connect to the database ###

Establish a connection to the existing database by using your connection string and the `getConnection()` method from the `Driver Manager` class. (See *Connecting to your SQL Database*).

The `DriverManager.getConnection();` method throws a **SQLException**.

### 2. Create a SQL statement ###

After the connection to the database is established, you need to create the query that will be executed. The query will be sent as a string parameter for the `statement.executeQuery();` method.

	String sql = "SELECT FirstName, LastName FROM table";

The `statement.executeQuery();` method throws a **SQLException** and a **SQLTimeoutException**.

### 3. Execute the statement ###

The query string will then be written as a parameter of the `executeQuery()` method:

	resultSet = statement.executeQuery(sql);

The method will return a result set over which the user can iterate.

### 4. Iterate the result set ###

The result set can be iterated using a `while` statement in the following way:

	while (resultSet.next())
	{
            System.out.println("-> " + resultSet.getString("FirstName") + " " + resultSet.getString ("LastName"));
    }


### 5. Close the connection ###

After the data has been retrieved, the connection can be closed with the following statements:

	if (connection != null) 
	{
		connection.close();
	}
    if (statement != null) 
	{
		statement.close();
	}

`connection.close();` and `statement.close();` both throw a **SQLException**.

## Insert a row, pass parameters and retrieve the generated primary key##


### 1. Connect to the database ###

Establish a connection to the existing database by using your connection string and the `getConnection()` method from the `Driver Manager` class. (See *Connecting to your SQL Database*).

The `DriverManager.getConnection();` method throws a **SQLException**.

### 2. Create a SQL statement ###

After the connection to the database is established, you need to create the query that will be executed. The query will be sent as a string parameter for the `statement.executeUpdate();` method that will be executed in the next step.

	String sql = "INSERT INTO table (FirstName, LastName) VALUES ('John', 'Doe'),('Casey','Karst');";

	statement = connection.createStatement();

The `statement.executeUpdate(sql);` method that will be executed next throws a **SQLException** and a **SQLTimeoutException**.

### 3. Execute the statement ###

The query string will then be written as a parameter of the `executeUpdate()` method:

	statement.executeUpdate(sql);

### 4. Get the generated keys from the statement ###
	
	statement.getGeneratedKeys();

### 5. Close the connection ###

After the data has been retrieved, the connection can be closed with the following statements:

	if (connection != null) 
	{
		connection.close();
	}
    if (statement != null) 
	{
		statement.close();
	}

`connection.close();` and `statement.close();` both throw a **SQLException**.

## Transactions ##

### 0. Connect to the database ###

Establish a connection to the existing database by using your connection string and the `getConnection()` method from the `Driver Manager` class. (See *Connecting to your SQL Database*).

The `DriverManager.getConnection();` method throws a **SQLException**.

### 1. Declare the prepared statements. ###

	PreparedStatement updateSales = null;

### 2. Prepare the query strings to be executed with the transaction. ###

The queries will be passed as arguments of the `connection.prepareStatement(string);` method in the next step.

	String updateString =
	        "update " + database + ".COFFEES " +
	        "set SALES = ? where COFFEE_NAME = ?";

### 3. Obtain the prepared statements from the connection. ###

	updateSales = connection.prepareStatement(updateString);


### 4. Run the update transactions ###

First, change the auto-commit mode to false:
 
	connection.setAutoCommit(false);

Set the values that will change:

	updateSales.setInt(1, 150);

Then by running the `executeUpdate();` method:

	updateSales.executeUpdate();

In the end, the changes have to be committed to the database:

	connection.commit();

All of the above methods throw a **SQLException**.

### 5. Close the connection. ###

	if (updateSales != null) 
	{
	    updateSales.close();
	}

And reset the auto-commit mode to true:

	connection.setAutoCommit(true);

package neo4jCRUD;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Exmpl2 {
	public static void main( String... args ) throws Exception
    {
		// Connecting
		try (Connection con = DriverManager.getConnection("jdbc:neo4j:bolt://localhost", "neo4j", "neo4jneo4j")) {

		    // Querying
		    String query = "MATCH (u:User)-[:FRIEND]-(f:User) WHERE u.name = {1} RETURN f.name, f.age";
		    try (PreparedStatement stmt = con.prepareStatement(query)) {
		        stmt.setString(1,"John");

		        try (ResultSet rs = stmt.executeQuery()) {
		            while (rs.next()) {
		                System.out.println("Friend: "+rs.getString("f.name")+" is "+rs.getInt("f.age"));
		            }
		        }
		    }
		}
		
		// Connect
		Connection con = DriverManager.getConnection("jdbc:neo4j:bolt://localhost");

		// Querying
		try (Statement stmt = con.createStatement()) {
		    ResultSet rs = stmt.executeQuery("MATCH (n:User) RETURN n.name");
		    while (rs.next()) {
		        System.out.println(rs.getString("n.name"));
		    }
		}
		con.close();
    }
}

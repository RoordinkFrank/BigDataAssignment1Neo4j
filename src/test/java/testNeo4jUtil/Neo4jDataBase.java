package testNeo4jUtil;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;

import neo4jUtil.Neo4jConnection;
import model.Aankoop;
import model.Filiaal;
import model.Klant;
import model.Product;
import testUtil.MockData;

public class Neo4jDataBase {
	public static void insertMockDataToNeo4jDB(MockData mockData, Neo4jConnection conn){
		System.out.println(" ");
		System.out.println("Insert Filialen in neo4j");
		for (Filiaal filiaal : mockData.mockFilialen){
			neo4jCRUD.FiliaalCRUD.insertFiliaal(conn.driver, filiaal);
		}
		System.out.println(" ");
		System.out.println("Insert Klanten in neo4j");
		for (Klant klant : mockData.mockKlanten){
			neo4jCRUD.KlantCRUD.insertKlant(conn.driver, klant);
		}
		System.out.println(" ");
		System.out.println("Insert Producten in neo4j");
		for (Product product : mockData.mockProducten){
			neo4jCRUD.ProductCRUD.insertProduct(conn.driver, product);
		}
		System.out.println(" ");
		System.out.println("Insert Aankopen in neo4j");

		for (Aankoop aankoop : mockData.mockAankopen){
			neo4jCRUD.AankoopCRUD.insertAankoop(conn.driver, aankoop);
		}
	}
	
	 public static void resetDatabase(Driver driver){
	    	try ( Session session = driver.session() )
	        {
	            String result = session.writeTransaction( new TransactionWork<String>()
	            {
	                @Override
	                public String execute( Transaction tx )
	                {
	                    StatementResult result = tx.run("MATCH (n) DETACH DELETE n");
	                    return null;
	                    //return result.single().get( 0 ).asString();
	                }
	            } );
	            System.out.println("resetDatabase has been carried out");
	        }
	    }
}
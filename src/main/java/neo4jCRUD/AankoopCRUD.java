package neo4jCRUD;

import static org.neo4j.driver.v1.Values.parameters;
import model.Aankoop;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;

public class AankoopCRUD {

	public static void insertAankoop(Driver driver, final Aankoop aankoop) {
		try ( Session session = driver.session() )
        {
            String confirmMessage = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                	StatementResult result = tx.run( "MATCH (k:Klant) WHERE k.key = \""+aankoop.klant.key+"\""
                			+ "MATCH (f:Filiaal) WHERE f.key = \""+aankoop.filiaal.key+"\""
        					+ "MATCH (p:Product) WHERE p.key = \""+aankoop.product.key+"\""
                			+ "CREATE (a:Aankoop), (a)-[:BOUGHT]->(k), (a)-[:BOUGHT]->(f),(a)-[:BOUGHTPRODUCT]->(p) SET a.key = $key SET a.datum = $datum SET a.aantal = $aantal "
                			+ "RETURN a.key + ', with id ' + id(a)",
                            parameters( "key", aankoop.key, "datum", aankoop.datum.toString(), "aantal", aankoop.aantal) );
                    return result.single().get( 0 ).asString();
                }
            } );
            System.out.println("Aankoop aangemaakt en gelinkt: "+ confirmMessage );
        }
	}
}

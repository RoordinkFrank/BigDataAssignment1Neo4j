package neo4jCRUD;

import static org.neo4j.driver.v1.Values.parameters;
import model.Filiaal;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;

public class FiliaalCRUD {

	public static void insertFiliaal(Driver driver, final Filiaal filiaal) {
		try ( Session session = driver.session() )
        {
            String confirmMessage = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    StatementResult result = tx.run( "CREATE (a:Filiaal) " +
                                                     "SET a.key = $key " +
                                                     "RETURN a.key + ', with id ' + id(a)",
                            parameters( "key", filiaal.key) );
                    return result.single().get( 0 ).asString();
                }
            } );
            System.out.println(confirmMessage);
        }
	}
}

package neo4jCRUD;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;

import static org.neo4j.driver.v1.Values.parameters;

public class Neo4jConnectionExample implements AutoCloseable
{
    public final Driver driver;

    public Neo4jConnectionExample()
    {
    	String uri = "bolt://localhost:7687";
    	String user = "neo4j";
    	String password = "neo4jneo4j";
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }

    @Override
    public void close() throws Exception
    {
        driver.close();
    }
    
    public void query(final String query){
    	try ( Session session = driver.session() )
        {
            String result = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    StatementResult result = tx.run(""+query);
                    return result.single().get( 0 ).asString();
                }
            } );
            System.out.println( result );
        }
    }

    private void printGreeting( final String message )
    {
        try ( Session session = driver.session() )
        {
            String greeting = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    StatementResult result = tx.run( "CREATE (a:Greeting) " +
                                                     "SET a.message = $message " +
                                                     "RETURN a.message + ', from node ' + id(a)",
                            parameters( "message", message ) );
                    return result.single().get( 0 ).asString();
                }
            } );
            System.out.println( greeting );
        }
    }

    public static void main( String... args ) throws Exception
    {
        try ( Neo4jConnectionExample greeter = new Neo4jConnectionExample(  ) )
        {
            greeter.printGreeting( "hello, world" );
        }
    }
}
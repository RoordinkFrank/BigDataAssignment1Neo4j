package neo4jUtil;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;

public class Neo4jConnection implements AutoCloseable
{
    public final Driver driver;

    public Neo4jConnection()
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
}
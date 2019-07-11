package neo4jCRUD;

import static org.neo4j.driver.v1.Values.parameters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import model.Klant;
import model.KlantPaar;
import model.KlantPaarExtension;
import model.Product;
import model.ProductPaar;
import model.ProductPaarExtension;
import neo4jUtil.Neo4jConnection;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;
import org.neo4j.driver.v1.Value;

public class ProductCRUD {
	
	public static void insertProduct(Driver driver, final Product product){
		try ( Session session = driver.session() )
        {
            String confirmMessage = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    StatementResult result = tx.run( "CREATE (a:Product) " +
                                                     "SET a.key = $key " +
                                                     "SET a.omschrijving = $omschrijving " +
                                                     "SET a.aantal = $aantal " +
                                                     "RETURN a.key + ', with id ' + id(a)",
                            parameters( "key", product.key, "omschrijving", product.omschrijving, "aantal", product.aantal ) );
                    return result.single().get( 0 ).asString();
                }
            } );
            System.out.println( confirmMessage );
        }
	}
	
	public static List<Product> getProducten(final String key) throws Exception{
	    Neo4jConnectionExample conn = new Neo4jConnectionExample();
		Driver driver = conn.driver;
		try ( Session session = driver.session() )
        {
			StatementResult result = session.readTransaction( new TransactionWork<StatementResult>()
            {
                @Override
                public StatementResult execute( Transaction tx )
                {
                	//MATCH (people:Person) RETURN people.name LIMIT 10
                	//MATCH (tom {name: "Tom Hanks"}) RETURN tom
                    StatementResult result = tx.run( "MATCH (a:Product) WHERE a.key = $key RETURN a;",
                            parameters( "key", key ));
                    return result;
                }
            } );
			
			List<Product> producten = new ArrayList<Product>();
			for (Record r : result.list()){
				Value v = r.get(0);
				Product product = new Product();
				product.key = v.get("key").toString();
				product.omschrijving = v.get("omschrijving").asString();
				product.aantal = v.get("aantal").asInt();
				producten.add(product);
			}
			conn.close();
            return producten;
        }
	}
	public static List<ProductPaar> selectMostBoughtProductPairs(
			Neo4jConnection conn) {
		Driver driver = conn.driver;
		HashMap<String ,ProductPaarExtension> productParen = new HashMap<String, ProductPaarExtension>();
		try (Session session = driver.session()) {
			StatementResult result = session
					.readTransaction(new TransactionWork<StatementResult>() {
						
						@Override
						public StatementResult execute(Transaction tx) {

							// WITH [k1, k2] as k 
							// Kees, ineke en ineke, kees komen nu als resultaten terug.
							// Lukte me niet om ze te sorteren bij gebruik aan LEATEST en GREATEST. Iets anders werkte bij mij ook niet goed.
							StatementResult result = tx
									.run("MATCH (p1:Product), (p2:Product), (a1:Aankoop), (a2:Aankoop) WHERE (a1)-[:BOUGHTPRODUCT]->(p1) AND (a2)-[:BOUGHTPRODUCT]->(p2) AND a1.datum = a2.datum AND p1 <> p2 RETURN p1, p2, count(*) as aantal ORDER BY aantal DESC");
							return result;
						}
					});

			//List<Product> producten = new ArrayList<Product>();
			System.out.println(result);
			for (Record r : result.list()) {
				Value p1 = r.get(0);
				Value p2 = r.get(1);
				Value aantal = r.get(2);
				System.out.println(p1.get("key").toString()+" "+p2.get("key").toString()+" "+ aantal.asInt());

				ProductPaarExtension productPaar = new ProductPaarExtension();
				//replaceAll is nodig omdat format in "text" ipv text terugkomt. Geen idee waarom.
				productPaar.firstProductKey = r.get(0).get("key").toString().replaceAll("\"", "");;
				productPaar.otherProductKey = r.get(1).get("key").toString().replaceAll("\"", "");;
				productPaar.aantal = r.get(2).asInt();
				productPaar.sortKlantenAlphabetical();
			    productParen.put(productPaar.firstProductKey+productPaar.otherProductKey+"",productPaar);
			}
		}
		List<ProductPaarExtension> productPaarList = new ArrayList<ProductPaarExtension>(productParen.values());
		//lijst klopt volledig maar is uit volgorde doordat LEAST, GREATEST niet kon en nu in java moet gebeuren.
		//betekend dat het weer opnieuw op aantal gesorteerd moet worden.
		Collections.sort(productPaarList);
		for (ProductPaar productpaar : productPaarList){
			System.out.println(productpaar);
		}
		List<? extends ProductPaar> bases = productPaarList;
		return (List<ProductPaar>) bases;
		//Hij vindt hem niet in de 
		//return (List<ProductPaar>) productPaarList;
		//zo wel.
	}
}

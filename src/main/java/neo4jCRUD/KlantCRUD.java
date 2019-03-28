package neo4jCRUD;

import static org.neo4j.driver.v1.Values.parameters;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import neo4jUtil.Neo4jConnection;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;
import org.neo4j.driver.v1.Value;

import model.Klant;
import model.KlantPaar;
import model.KlantPaarExtension;
import model.Product;

public class KlantCRUD {

	public static void insertKlant(Driver driver, Klant klant) {
		final Klant finalKlant = klant;
		try (Session session = driver.session()) {
			String greeting = session
					.writeTransaction(new TransactionWork<String>() {
						@Override
						public String execute(Transaction tx) {
							StatementResult result = tx.run("CREATE (a:Klant) "
									+ "SET a.key = $key "
									+ "SET a.naam = $key "
									+ "RETURN a.key + ', with id ' + id(a)",
									parameters("key", finalKlant.key));
							return result.single().get(0).asString();
						}
					});
			System.out.println(greeting);
		}
	}

	public static Klant getKlant(Driver driver, String key) {
		final String finalKey = key;
		try (Session session = driver.session()) {
			String greeting = session
					.readTransaction(new TransactionWork<String>() {
						@Override
						public String execute(Transaction tx) {
							// MATCH (people:Person) RETURN people.name LIMIT 10
							// MATCH (tom {name: "Tom Hanks"}) RETURN tom
							StatementResult result = tx
									.run("MATCH (people:Klant) RETURN people.key LIMIT 1",
											parameters("key", finalKey));
							return result.single().get(0).asString();
						}
					});
			System.out.println(greeting);
			return new Klant(key);
		}
	}

	public static Klant getKlanten(Driver driver, String key) {
		final String finalKey = key;
		try (Session session = driver.session()) {
			List<String> greeting = session
					.readTransaction(new TransactionWork<List<String>>() {
						@Override
						public List<String> execute(Transaction tx) {
							// MATCH (people:Person) RETURN people.name LIMIT 10
							// MATCH (tom {name: "Tom Hanks"}) RETURN tom
							StatementResult result = tx
									.run("MATCH (people:Klant) RETURN people.key LIMIT 10",
											parameters("key", finalKey));
							return result.keys();
						}
					});
			for (String g : greeting) {
				System.out.println(g);
			}

			return new Klant(key);
		}
	}

	public static List<KlantPaar> selectKlantparenMetZelfdeProducten(
			Neo4jConnection conn, int aantalDezelfdeProducten) {
		Driver driver = conn.driver;
		HashMap<String ,KlantPaar> klantParen = new HashMap<String, KlantPaar>();
		try (Session session = driver.session()) {
			StatementResult result = session
					.readTransaction(new TransactionWork<StatementResult>() {
						
						@Override
						public StatementResult execute(Transaction tx) {

							// WITH [k1, k2] as k 
							// Kees, ineke en ineke, kees komen nu als resultaten terug.
							// Lukte me niet om ze te sorteren bij gebruik aan LEATEST en GREATEST. Iets anders werkte bij mij ook niet goed.
							StatementResult result = tx
									.run("MATCH (k1:Klant), (k2:Klant), (a:Aankoop), (a2:Aankoop), (p:Product) WHERE (a)-[:BOUGHT]->(k1) AND (a)-[:BOUGHTPRODUCT]->(p) AND (a2)-[:BOUGHT]->(k2) AND (a2)-[:BOUGHTPRODUCT]->(p) AND k1.key <> k2.key RETURN k1, k2, SUM(a.aantal)");
							return result;
						}
					});

			System.out.println(result);
			for (Record r : result.list()) {
				Value k1 = r.get(0);
				Value k2 = r.get(1);
				Value aantal = r.get(2);
				System.out.println(k1.get("key").toString()+" "+k2.get("key").toString()+" "+ aantal.asInt());

				KlantPaarExtension kp = new KlantPaarExtension();
				kp.firstKlant = new Klant(r.get(0).get("key").toString());
				kp.otherKlant = new Klant(r.get(1).get("key").toString());
				kp.overeenkomstigeProducten = r.get(2).asInt();
				kp.sortKlantenAlphabetical();
				if (kp.overeenkomstigeProducten >= aantalDezelfdeProducten){
					klantParen.put(kp.firstKlant.key+kp.otherKlant.key+"",kp);
				}
			}
		}
		List<KlantPaar> list = new ArrayList<KlantPaar>(klantParen.values());
		for (KlantPaar k : list){
			System.out.println(k);
		}
		return list;
	}
}

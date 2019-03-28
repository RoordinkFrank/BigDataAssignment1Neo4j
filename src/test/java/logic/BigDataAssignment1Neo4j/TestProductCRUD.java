package logic.BigDataAssignment1Neo4j;

import static org.junit.Assert.assertEquals;

import java.util.List;

import model.ProductPaar;
import neo4jCRUD.ProductCRUD;
import neo4jUtil.Neo4jConnection;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import testNeo4jUtil.Neo4jDataBase;
import testUtil.MockData;

public class TestProductCRUD {
	

	static MockData mockData;
	static Neo4jConnection conn;
	
	@BeforeClass
	public static void setup() throws Exception{
		mockData = new MockData();
		conn = new Neo4jConnection();
		Neo4jDataBase.resetDatabase(conn.driver);
		Neo4jDataBase.insertMockDataToNeo4jDB(mockData, conn);
	}
	
	@Test
	public void testSelectMostBoughtProductPairs(){
		List<ProductPaar> productParen = ProductCRUD.selectMostBoughtProductPairs(conn);
		assertEquals(productParen.get(0).firstProductKey, "eend");
		assertEquals(productParen.get(0).otherProductKey, "kaas");
		//in mysql is het schaap met eend, komt omdat er 3 zijn die 5 (grootste) zijn.
		//hier is de lijst gesorteerd, schaap met eend staat 3de (ook met 5 aantal).
		assertEquals(productParen.get(0).aantal, 5);
	}
	

	@AfterClass
	public static void breakDown() throws Exception{
		conn.close();
	}
}

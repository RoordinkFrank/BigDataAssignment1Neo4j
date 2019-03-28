package logic.BigDataAssignment1Neo4j;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import model.KlantPaar;
import mysqlCRUD.KlantCRUD;
import neo4jUtil.Neo4jConnection;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import testNeo4jUtil.Neo4jDataBase;
import testUtil.DataBase;
import testUtil.MockData;
import util.Util;

public class TestKlantCRUD {
	
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
	public void testSelectKlantparenMetVierZelfdeProducten(){
		List<KlantPaar> klantParen = neo4jCRUD.KlantCRUD.selectKlantparenMetZelfdeProducten(conn, 4);
		assertEquals(klantParen.size(), 9);
	}
	
	@AfterClass
	public static void breakDown() throws Exception{
		conn.close();
	}
}
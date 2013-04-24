package helper;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;

import de.dis2011.data.DB2ConnectionManager;
import de.dis2011.data.Haus;
import de.dis2011.data.Kaufvertrag;
import de.dis2011.data.Makler;
import de.dis2011.data.Mietvertrag;
import de.dis2011.data.Person;
import de.dis2011.data.Wohnung;

public class DBManager {
	private Connection _con;
	
	public DBManager() {
		_con = DB2ConnectionManager.getInstance().getConnection();
	}
	
	/**
	 * Creates a DB-Scheme for the tables
	 * (makler, immobilie, haus, wohnung, person, vertrag, mietvertrag and kaufvertrag)
	 */
	public void createScheme() {
		try {
			Statement stmt = _con.createStatement();

			String maklerSQL = "CREATE TABLE makler " +
					"(id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE) PRIMARY KEY," +
					"name VARCHAR(50)," +
					"address VARCHAR(50)," +
					"login VARCHAR(50) NOT NULL UNIQUE," +
					"password VARCHAR(50) NOT NULL)";
			stmt.execute(maklerSQL);
			System.out.println("[DBManager] Successfully created Table 'makler'");
			
			String immoSQL = "CREATE TABLE immobilie " +
					"(id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE) PRIMARY KEY," +
					"verwalter INT NOT NULL REFERENCES makler(id)," +
					"ort VARCHAR(50)," +
					"plz INT," +
					"strasse VARCHAR(50)," +
					"hausnummer VARCHAR(50)," +
					"flaeche INT)";
			stmt.execute(immoSQL);
			System.out.println("[DBManager] Successfully created Table 'immobilie'");
			
			String hausSQL = "CREATE TABLE haus " +
					"(id INT UNIQUE NOT NULL REFERENCES immobilie(id)," +
					"stockwerke INT," +
					"preis INT," +
					"garten VARCHAR(5))";
			stmt.execute(hausSQL);
			System.out.println("[DBManager] Successfully created Table 'haus'");
			
			String wohnungSQL = "CREATE TABLE wohnung " +
					"(id INT UNIQUE NOT NULL REFERENCES immobilie(id)," +
					"stockwerk INT," +
					"preis INT," +
					"zimmer INT," +
					"balkon VARCHAR(5)," +
					"ebk VARCHAR(5))";
			stmt.execute(wohnungSQL);
			System.out.println("[DBManager] Successfully created Table 'wohnung'");
			
			String personSQL = "CREATE TABLE person " +
					"(id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE) PRIMARY KEY," +
					"vorname VARCHAR(50)," +
					"nachname VARCHAR(50)," +
					"adresse VARCHAR(50))";
			stmt.execute(personSQL);
			System.out.println("[DBManager] Successfully created Table 'person'");
			
			String vertragSQL = "CREATE TABLE vertrag " +
					"(nummer INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE) PRIMARY KEY," +
					"datum DATE," +
					"ort VARCHAR(50))";
			stmt.execute(vertragSQL);
			System.out.println("[DBManager] Successfully created Table 'vertrag'");
			
			String mietVertragSQL = "CREATE TABLE mietvertrag " +
					"(nummer INT UNIQUE NOT NULL REFERENCES vertrag(nummer)," +
					"wohnung INT UNIQUE NOT NULL REFERENCES wohnung(id)," +
					"person INT NOT NULL REFERENCES person(id)," +
					"beginn DATE," +
					"dauer INT," +
					"nebenkosten INT)";
			stmt.execute(mietVertragSQL);
			System.out.println("[DBManager] Successfully created Table 'mietvertrag'");
			
			String kaufVertragSQL = "CREATE TABLE kaufvertrag " +
					"(nummer INT UNIQUE NOT NULL REFERENCES vertrag(nummer)," +
					"haus INT UNIQUE NOT NULL REFERENCES haus(id)," +
					"person INT NOT NULL REFERENCES person(id)," +
					"anzahlRaten INT," +
					"ratenZins INT)";
			stmt.execute(kaufVertragSQL);
			System.out.println("[DBManager] Successfully created Table 'kaufvertrag'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Load fixtures
	 */
	public void loadFixtures() {
		// insert makler "Makler Zwei"
		Makler makler = new Makler();
		makler.setName("Makler Zwei");
		makler.setLogin("2makler");
		makler.setPassword("geheim");
		makler.setAddress("Vogt-Koelln-Strasse 30");
		makler.save();
		
		// insert wohnung "testwohnung"
		Wohnung wohnung = new Wohnung();
		wohnung.setVerwalter(makler);
		wohnung.setOrt("Hamburg");
		wohnung.setPlz(22527);
		wohnung.setStrasse("Vogt-Koelln-Strasse");
		wohnung.setHausnummer("30");
		wohnung.setFlaeche(70);
		wohnung.setStockwerk(2);
		wohnung.setPreis(800);
		wohnung.setZimmer(3);
		wohnung.setBalkon(true);
		wohnung.setEbk(false);
		wohnung.save();
		
		// insert haus
		Haus haus = new Haus();
		haus.setVerwalter(makler);
		haus.setOrt("Hamburg");
		haus.setPlz(22527);
		haus.setStrasse("Vogt-Koelln-Strasse");
		haus.setHausnummer("30");
		haus.setFlaeche(70);
		haus.setStockwerke(3);
		haus.setPreis(300000);
		haus.setGarten(false);
		haus.save();
		
		// insert person
		Person hans = new Person();
		hans.setVorname("Hans-Peter");
		hans.setNachname("Vogelschiss");
		hans.setAdresse("Tümpelpfad 33");
		hans.save();
		

		long currentTime = System.currentTimeMillis();
		
		// insert kaufvertrag
		Kaufvertrag kv = new Kaufvertrag();
		kv.setDatum(new Date(currentTime));
		kv.setOrt("Hamburg");
		kv.setPerson(hans);
		kv.setHaus(haus);
		kv.setAnzahlRaten(120);
		kv.setRatenZins(5);
		kv.save();
		
		// insert mietvertrag
		Mietvertrag mv = new Mietvertrag();
		mv.setDatum(new Date(currentTime));
		mv.setOrt("Hamburg");
		mv.setPerson(hans);
		mv.setWohnung(wohnung);
		mv.setBeginn(new Date(currentTime));
		mv.setDauer(12);
		mv.setNebenkosten(150);
		mv.save();
		
		System.out.println("[DBManager] Successfully loaded Fixtures.");
	}
	
	/**
	 * Drops the Tables
	 * (makler, immobilie, haus, wohnung, person, vertrag, mietvertrag and kaufvertrag)
	 */
	public void dropTables() {
		Statement stmt;
		try {
			stmt = _con.createStatement();
			try {
				String maklerSQL = "DROP TABLE makler";
				stmt.execute(maklerSQL);
				System.out.println("[DBManager] Successfully dropped table makler.");
			} catch (SQLException e) {
				System.out.println("[DBManager] SQLException: Tried to Drop Table 'makler' but failed. Usually this happens when there is no such table.");
			}
			try {
				String immoSQL = "DROP TABLE immobilie";
				stmt.execute(immoSQL);
				System.out.println("[DBManager] Successfully dropped table immobilie.");
			} catch (SQLException e) {
				System.out.println("[DBManager] SQLException: Tried to Drop Table 'immobilie' but failed. Usually this happens when there is no such table.");
			}
			try {
				String hausSQL = "DROP TABLE haus";
				stmt.execute(hausSQL);
				System.out.println("[DBManager] Successfully dropped table haus.");
			} catch (SQLException e) {
				System.out.println("[DBManager] SQLException: Tried to Drop Table 'haus' but failed. Usually this happens when there is no such table.");
			}
			try {
				String wohnungSQL = "DROP TABLE wohnung";
				stmt.execute(wohnungSQL);
				System.out.println("[DBManager] Successfully dropped table haus.");
			} catch (SQLException e) {
				System.out.println("[DBManager] SQLException: Tried to Drop Table 'wohnung' but failed. Usually this happens when there is no such table.");
			}
			try {
				String personSQL = "DROP TABLE person";
				stmt.execute(personSQL);
				System.out.println("[DBManager] Successfully dropped table person.");
			} catch (SQLException e) {
				System.out.println("[DBManager] SQLException: Tried to Drop Table 'person' but failed. Usually this happens when there is no such table.");
			}
			try {
				String vertragSQL = "DROP TABLE vertrag";
				stmt.execute(vertragSQL);
				System.out.println("[DBManager] Successfully dropped table vertrag.");
			} catch (SQLException e) {
				System.out.println("[DBManager] SQLException: Tried to Drop Table 'vertrag' but failed. Usually this happens when there is no such table.");
			}
			try {
				String mietVertragSQL = "DROP TABLE mietvertrag";
				stmt.execute(mietVertragSQL);
				System.out.println("[DBManager] Successfully dropped table mietvertrag.");
			} catch (SQLException e) {
				System.out.println("[DBManager] SQLException: Tried to Drop Table 'mietvertrag' but failed. Usually this happens when there is no such table.");
			}
			try {
				String kaufVertragSQL = "DROP TABLE kaufvertrag";
				stmt.execute(kaufVertragSQL);
				System.out.println("[DBManager] Successfully dropped table kaufvertrag.");
			} catch (SQLException e) {
				System.out.println("[DBManager] SQLException: Tried to Drop Table 'kaufvertrag' but failed. Usually this happens when there is no such table.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

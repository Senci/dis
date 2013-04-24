package de.dis2011.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.dis2011.data.DB2ConnectionManager;

/**
 * Wohnung-Bean
 * 
 * Beispiel-Tabelle:
 * CREATE TABLE wohnung
 * (id INT UNIQUE NOT NULL REFERENCES immobilie(id)
 * stockwerk INT
 * preis INT
 * zimmer INT
 * balkon VARCHAR(5)
 * ebk VARCHAR(5)
 * 
 */
public class Wohnung extends Immobilie{
	private int stockwerk;
	private int preis;
	private int zimmer;
	private boolean balkon;
	private boolean ebk;
	
	public int getStockwerk() {
		return stockwerk;
	}

	public void setStockwerk(int stockwerk) {
		this.stockwerk = stockwerk;
	}

	public int getPreis() {
		return preis;
	}

	public void setPreis(int preis) {
		this.preis = preis;
	}

	public int getZimmer() {
		return zimmer;
	}

	public void setZimmer(int zimmer) {
		this.zimmer = zimmer;
	}

	public boolean getBalkon() {
		return balkon;
	}

	public void setBalkon(boolean balkon) {
		this.balkon = balkon;
	}

	public boolean getEbk() {
		return ebk;
	}

	public void setEbk(boolean ebk) {
		this.ebk = ebk;
	}
	
	/**
	 * Lädt eine Wohnung aus der Datenbank
	 * @param id ID der zu ladenden Wohnung
	 * @return Wohnungs-Instanz
	 */
	public static Wohnung load(int id) {
		try {
			// Hole Verbindung
			Connection con = DB2ConnectionManager.getInstance().getConnection();

			// Erzeuge Anfrage
			String selectSQL = "SELECT * FROM wohnung JOIN immobilie ON immobilie.id = wohnung.id WHERE wohnung.id = ?";
			PreparedStatement pstmt = con.prepareStatement(selectSQL);
			pstmt.setInt(1, id);

			// Führe Anfrage aus
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				Wohnung ts = new Wohnung();
				ts.setId(id);
				ts.setVerwalter(Makler.load(rs.getInt("verwalter")));
				ts.setOrt(rs.getString("ort"));
				ts.setPlz(rs.getInt("plz"));
				ts.setStrasse(rs.getString("strasse"));
				ts.setHausnummer(rs.getString("hausnummer"));
				ts.setFlaeche(rs.getInt("flaeche"));
				ts.setStockwerk(rs.getInt("stockwerk"));
				ts.setPreis(rs.getInt("preis"));
				ts.setZimmer(rs.getInt("zimmer"));
				ts.setBalkon(rs.getString("balkon").equals("true"));
				ts.setEbk(rs.getString("ebk").equals("true"));

				rs.close();
				pstmt.close();
				return ts;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Speichert die Wohnung in der Datenbank. Ist noch keine ID vergeben
	 * worden, wird die generierte Id von DB2 geholt und dem Model übergeben.
	 */
	public void save() {
		// Hole Verbindung
		Connection con = DB2ConnectionManager.getInstance().getConnection();

		try {
			// FC<ge neues Element hinzu, wenn das Objekt noch keine ID hat.
			if (getId() == -1) {
				insertImmobilie(con);				
				
				String insertSQL = "INSERT INTO wohnung(id, stockwerk, preis, zimmer, balkon, ebk) VALUES (?, ?, ?, ?, ?, ?)";

				PreparedStatement pstmt = con.prepareStatement(insertSQL,
						Statement.RETURN_GENERATED_KEYS);

				String wBalkon = getBalkon() ? "true" : "false";
				String wEbk = getEbk() ? "true" : "false";

				// Setze Anfrageparameter und fC<hre Anfrage aus
				pstmt.setInt(1, getId());
				pstmt.setInt(2, getStockwerk());
				pstmt.setInt(3, getPreis());
				pstmt.setInt(4, getZimmer());
				pstmt.setString(5, wBalkon);
				pstmt.setString(6, wEbk);
				pstmt.executeUpdate();

				pstmt.close();
			} else {
				// Falls schon eine ID vorhanden ist, mache ein Update...
				updateImmobilie(con);
				
				String updateSQL = "UPDATE wohnung SET stockwerk = ?, preis = ?, zimmer = ?, balkon = ?, ebk = ? WHERE id = ?";
				PreparedStatement pstmt = con.prepareStatement(updateSQL);

				String wBalkon = getBalkon() ? "true" : "false";
				String wEbk = getEbk() ? "true" : "false";

				// Setze Anfrage Parameter
				pstmt.setInt(1, getStockwerk());
				pstmt.setInt(2, getPreis());
				pstmt.setInt(3, getZimmer());
				pstmt.setString(4, wBalkon);
				pstmt.setString(5, wEbk);
				pstmt.setInt(6, getId());
				pstmt.executeUpdate();

				pstmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

package de.dis2011.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.dis2011.data.DB2ConnectionManager;

/**
 * Haus-Bean
 * 
 * Beispiel-Tabelle:
 * CREATE TABLE haus
 * id INT UNIQUE NOT NULL REFERENCES immobilie(id)
 * stockwerke INT
 * preis INT
 * garten VARCHAR(5)
 * 
 */
public class Haus extends Immobilie{
	private int stockwerke;
	private int preis;
	private boolean garten;

	public int getStockwerke() {
		return stockwerke;
	}

	public void setStockwerke(int stockwerke) {
		this.stockwerke = stockwerke;
	}

	public int getPreis() {
		return preis;
	}

	public void setPreis(int preis) {
		this.preis = preis;
	}

	public boolean getGarten() {
		return garten;
	}

	public void setGarten(boolean garten) {
		this.garten = garten;
	}
	
	/**
	 * Lädt ein Haus aus der Datenbank
	 * @param id ID des zu ladenden Hauses
	 * @return Haus-Instanz
	 */
	public static Haus load(int id) {
		try {
			// Hole Verbindung
			Connection con = DB2ConnectionManager.getInstance().getConnection();

			// Erzeuge Anfrage
			String selectSQL = "SELECT * FROM haus JOIN immobilie ON immobilie.id = haus.id WHERE haus.id = ?";
			PreparedStatement pstmt = con.prepareStatement(selectSQL);
			pstmt.setInt(1, id);

			// Führe Anfrage aus
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				Haus ts = new Haus();
				ts.setId(id);
				ts.setVerwalter(Makler.load(rs.getInt("verwalter")));
				ts.setOrt(rs.getString("ort"));
				ts.setPlz(rs.getInt("plz"));
				ts.setStrasse(rs.getString("strasse"));
				ts.setHausnummer(rs.getString("hausnummer"));
				ts.setFlaeche(rs.getInt("flaeche"));
				ts.setStockwerke(rs.getInt("stockwerke"));
				ts.setPreis(rs.getInt("preis"));;
				ts.setGarten(rs.getString("garten").equals("true"));

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
	 * Speichert das Haus in der Datenbank. Ist noch keine ID vergeben
	 * worden, wird die generierte Id von DB2 geholt und dem Model übergeben.
	 */
	public void save() {
		// Hole Verbindung
		Connection con = DB2ConnectionManager.getInstance().getConnection();

		try {
			// FC<ge neues Element hinzu, wenn das Objekt noch keine ID hat.
			if (getId() == -1) {
				insertImmobilie(con);

				String insertSQL = "INSERT INTO haus(id, stockwerke, preis, garten) VALUES (?, ?, ?, ?)";

				PreparedStatement pstmt = con.prepareStatement(insertSQL,
						Statement.RETURN_GENERATED_KEYS);

				String wGarten = getGarten() ? "true" : "false";

				// Setze Anfrageparameter und fC<hre Anfrage aus
				pstmt.setInt(1, getId());
				pstmt.setInt(2, getStockwerke());
				pstmt.setInt(3, getPreis());
				pstmt.setString(4, wGarten);
				pstmt.executeUpdate();

				pstmt.close();
			} else {
				// Falls schon eine ID vorhanden ist, mache ein Update...
				updateImmobilie(con);
				
				String updateSQL = "UPDATE haus SET stockwerke = ?, preis = ?, garten = ? WHERE id = ?";
				PreparedStatement pstmt = con.prepareStatement(updateSQL);

				String wGarten = getGarten() ? "true" : "false";

				// Setze Anfrage Parameter
				pstmt.setInt(1, getStockwerke());
				pstmt.setInt(2, getPreis());
				pstmt.setString(3, wGarten);
				pstmt.setInt(4, getId());
				pstmt.executeUpdate();

				pstmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

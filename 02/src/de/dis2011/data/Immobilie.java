package de.dis2011.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Immobilie-Bean
 * 
 * Beispiel-Tabelle:
 * CREATE TABLE immobilie
 * id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE) PRIMARY KEY
 * verwalter INT NOT NULL REFERENCES makler(id)
 * ort VARCHAR(50)
 * plz INT
 * strasse VARCHAR(50)
 * hausnummer VARCHAR(50)
 * flaeche INT
 * 
 */
public abstract class Immobilie {
	private int id = -1;
	private Makler verwalter;
	private String ort;
	private int plz;
	private String strasse;
	private String hausnummer;
	private int flaeche;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public Makler getVerwalter() {
		return verwalter;
	}

	public void setVerwalter(Makler verwalter) {
		this.verwalter = verwalter;
	}

	public String getOrt() {
		return ort;
	}

	public void setOrt(String ort) {
		this.ort = ort;
	}

	public int getPlz() {
		return plz;
	}

	public void setPlz(int plz) {
		this.plz = plz;
	}

	public String getStrasse() {
		return strasse;
	}

	public void setStrasse(String strasse) {
		this.strasse = strasse;
	}

	public String getHausnummer() {
		return hausnummer;
	}

	public void setHausnummer(String hausnummer) {
		this.hausnummer = hausnummer;
	}

	public int getFlaeche() {
		return flaeche;
	}

	public void setFlaeche(int flaeche) {
		this.flaeche = flaeche;
	}
	
	/**
	 * 
	 * Inserts an "imobilie" into database 
	 * 
	 * @param con an Open SQL-Connection
	 */
	protected void insertImmobilie(Connection con) {
		String insertSQL = "INSERT INTO immobilie(verwalter, ort, plz, strasse, hausnummer, flaeche) VALUES (?, ?, ?, ?, ?, ?)";
		PreparedStatement pstmt;
		try {
			pstmt = con.prepareStatement(insertSQL,
					Statement.RETURN_GENERATED_KEYS);

			pstmt.setInt(1, getVerwalter().getId());
			pstmt.setString(2, getOrt());
			pstmt.setInt(3, getPlz());
			pstmt.setString(4, getStrasse());
			pstmt.setString(5, getHausnummer());
			pstmt.setInt(6, getFlaeche());
			pstmt.executeUpdate();

			// Hole die Id des engefC<gten Datensatzes
			ResultSet rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
				setId(rs.getInt(1));
			}

			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Updates an "imobilie" in database 
	 * 
	 * @param con an Open SQL-Connection
	 */
	protected void updateImmobilie(Connection con) {
		try {
			String updateISQL = "UPDATE immobilie SET verwalter = ?, ort = ?, plz = ?, strasse = ?, hausnummer = ?, flaeche = ? WHERE id = ?";
			PreparedStatement pstmt = con.prepareStatement(updateISQL);

			// Setze Anfrage Parameter
			pstmt.setInt(1, getVerwalter().getId());
			pstmt.setString(2, getOrt());
			pstmt.setInt(3, getPlz());
			pstmt.setString(4, getStrasse());
			pstmt.setString(5, getHausnummer());
			pstmt.setInt(6, getFlaeche());
			pstmt.setInt(7, getId());
			pstmt.executeUpdate();

			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

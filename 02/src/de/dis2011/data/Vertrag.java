package de.dis2011.data;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Vertrag-Bean
 * 
 * Beispiel-Tabelle:
 * CREATE TABLE vertrag
 * nummer INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE) PRIMARY KEY
 * datum DATE
 * ort VARCHAR(50)
 */
public abstract class Vertrag {
	private int nummer = -1;
	private Date datum;
	private String ort;
	
	public int getId() {
		return nummer;
	}
	
	public void setId(int id) {
		this.nummer = id;
	}
	
	public int getNummer() {
		return nummer;
	}
	
	public void setNummer(int id) {
		this.nummer = id;
	}

	public Date getDatum() {
		return datum;
	}

	public void setDatum(Date datum) {
		this.datum = datum;
	}
	
	public String getOrt() {
		return ort;
	}

	public void setOrt(String ort) {
		this.ort = ort;
	}
	
	/**
	 * 
	 * Inserts an "vertrag" into database 
	 * 
	 * @param con an Open SQL-Connection
	 */
	protected void insertVertrag(Connection con) {
		String insertSQL = "INSERT INTO vertrag(datum, ort) VALUES (?, ?)";
		PreparedStatement pstmt;
		try {
			pstmt = con.prepareStatement(insertSQL,
					Statement.RETURN_GENERATED_KEYS);
			
			pstmt.setDate(1, getDatum());
			pstmt.setString(2, getOrt());
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
	 * Updates an "vertrag" in database 
	 * 
	 * @param con an Open SQL-Connection
	 */
	protected void updateVertrag(Connection con) {
		try {
			String updateISQL = "UPDATE vertrag SET datum = ?, ort = ? WHERE id = ?";
			PreparedStatement pstmt = con.prepareStatement(updateISQL);

			// Setze Anfrage Parameters
			pstmt.setDate(1, getDatum());
			pstmt.setString(2, getOrt());
			pstmt.executeUpdate();

			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

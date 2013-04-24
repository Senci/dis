package de.dis2011.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.dis2011.data.DB2ConnectionManager;

/**
 * Kaufvertrag-Bean
 * 
 * Beispiel-Tabelle:
 * CREATE TABLE kaufvertrag
 * nummer INT UNIQUE NOT NULL REFERENCES vertrag(nummer)
 * haus INT UNIQUE NOT NULL REFERENCES haus(id)
 * person INT NOT NULL REFERENCES person(id)
 * anzahlRaten INT
 * ratenZins INT
 */
public class Kaufvertrag extends Vertrag {
	private Haus haus;
	private Person person;
	private int anzahlRaten;
	private int ratenZins;

	public Haus getHaus() {
		return haus;
	}

	public void setHaus(Haus haus) {
		this.haus = haus;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public int getAnzahlRaten() {
		return anzahlRaten;
	}

	public void setAnzahlRaten(int anzahlRaten) {
		this.anzahlRaten = anzahlRaten;
	}

	public int getRatenZins() {
		return ratenZins;
	}

	public void setRatenZins(int ratenZins) {
		this.ratenZins = ratenZins;
	}
	
	/**
	 * Lädt einen Kaufvertrag aus der Datenbank
	 * @param id ID (oder Nummer) des zu ladenden Kaufvertrags
	 * @return Kaufvertrag-Instanz
	 */
	public static Kaufvertrag load(int id) {
		try {
			// Hole Verbindung
			Connection con = DB2ConnectionManager.getInstance().getConnection();

			// Erzeuge Anfrage
			String selectSQL = "SELECT * FROM kaufvertrag JOIN vertrag ON kaufvertrag.nummer = vertrag.nummer WHERE vertrag.nummer = ?";
			PreparedStatement pstmt = con.prepareStatement(selectSQL);
			pstmt.setInt(1, id);

			// Führe Anfrage aus
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				Kaufvertrag ts = new Kaufvertrag();
				ts.setId(id);
				ts.setDatum(rs.getDate("datum"));
				ts.setOrt(rs.getString("ort"));
				ts.setPerson(Person.load(rs.getInt("person")));
				ts.setHaus(Haus.load(rs.getInt("haus")));
				ts.setAnzahlRaten(rs.getInt("anzahlRaten"));
				ts.setRatenZins(rs.getInt("ratenZins"));

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
	 * Speichert den Kaufvertrag in der Datenbank. Ist noch keine ID vergeben
	 * worden, wird die generierte Id von DB2 geholt und dem Model übergeben.
	 */
	public void save() {
		// Hole Verbindung
		Connection con = DB2ConnectionManager.getInstance().getConnection();

		try {
			// FC<ge neues Element hinzu, wenn das Objekt noch keine ID hat.
			if (getId() == -1) {
				insertVertrag(con);

				String insertSQL = "INSERT INTO kaufvertrag(nummer, person, haus, anzahlRaten, ratenZins) VALUES (?, ?, ?, ?, ?)";

				PreparedStatement pstmt = con.prepareStatement(insertSQL,
						Statement.RETURN_GENERATED_KEYS);

				// Setze Anfrageparameter und fC<hre Anfrage aus
				pstmt.setInt(1, getId());
				pstmt.setInt(2, getPerson().getId());
				pstmt.setInt(3, getHaus().getId());
				pstmt.setInt(4, getAnzahlRaten());
				pstmt.setInt(5, getRatenZins());
				pstmt.executeUpdate();

				pstmt.close();
			} else {
				// Falls schon eine ID vorhanden ist, mache ein Update...
				updateVertrag(con);
				
				String updateSQL = "UPDATE kaufvertrag SET person = ?, haus = ?, anzahlRaten = ?, ratenZins = ? WHERE id = ?";
				PreparedStatement pstmt = con.prepareStatement(updateSQL);

				// Setze Anfrage Parameter
				pstmt.setInt(1, getPerson().getId());
				pstmt.setInt(2, getHaus().getId());
				pstmt.setInt(3, getAnzahlRaten());
				pstmt.setInt(4, getRatenZins());
				pstmt.setInt(5, getId());
				pstmt.executeUpdate();

				pstmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

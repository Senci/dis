package de.dis2011.data;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.dis2011.data.DB2ConnectionManager;

/**
 * Mietvertrag-Bean
 * 
 * Beispiel-Tabelle:
 * CREATE TABLE mietvertrag
 * nummer INT UNIQUE NOT NULL REFERENCES vertrag(nummer)
 * wohnung INT UNIQUE NOT NULL REFERENCES wohnung(id)
 * person INT NOT NULL REFERENCES person(id)
 * beginn DATE
 * dauer INT
 * nebenkosten INT
 */
public class Mietvertrag extends Vertrag {
	private Wohnung wohnung;
	private Person person;
	private Date beginn;
	private int dauer;
	private int nebenkosten;

	public Wohnung getWohnung() {
		return wohnung;
	}

	public void setWohnung(Wohnung wohnung) {
		this.wohnung = wohnung;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Date getBeginn() {
		return beginn;
	}

	public void setBeginn(Date beginn) {
		this.beginn = beginn;
	}

	public int getDauer() {
		return dauer;
	}

	public void setDauer(int dauer) {
		this.dauer = dauer;
	}

	public int getNebenkosten() {
		return nebenkosten;
	}

	public void setNebenkosten(int nebenkosten) {
		this.nebenkosten = nebenkosten;
	}
	
	/**
	 * Lädt einen Mietvertrag aus der Datenbank
	 * @param id ID (oder Nummer) des zu ladenden Mietvertrags
	 * @return Mietvertrag-Instanz
	 */
	public static Mietvertrag load(int id) {
		try {
			// Hole Verbindung
			Connection con = DB2ConnectionManager.getInstance().getConnection();

			// Erzeuge Anfrage
			String selectSQL = "SELECT * FROM mietvertrag JOIN vertrag ON mietvertrag.nummer = vertrag.nummer WHERE vertrag.nummer = ?";
			PreparedStatement pstmt = con.prepareStatement(selectSQL);
			pstmt.setInt(1, id);

			// Führe Anfrage aus
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				Mietvertrag ts = new Mietvertrag();
				ts.setId(id);
				ts.setDatum(rs.getDate("datum"));
				ts.setOrt(rs.getString("ort"));
				ts.setPerson(Person.load(rs.getInt("person")));
				ts.setWohnung(Wohnung.load(rs.getInt("wohnung")));
				ts.setBeginn(rs.getDate("beginn"));
				ts.setNebenkosten(rs.getInt("nebenkosten"));

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

				String insertSQL = "INSERT INTO mietvertrag(nummer, person, wohnung, beginn, dauer, nebenkosten) VALUES (?, ?, ?, ?, ?, ?)";

				PreparedStatement pstmt = con.prepareStatement(insertSQL,
						Statement.RETURN_GENERATED_KEYS);

				// Setze Anfrageparameter und fC<hre Anfrage aus
				pstmt.setInt(1, getId());
				pstmt.setInt(2, getPerson().getId());
				pstmt.setInt(3, getWohnung().getId());
				pstmt.setDate(4, getBeginn());
				pstmt.setInt(5, getDauer());
				pstmt.setInt(6, getNebenkosten());
				pstmt.executeUpdate();

				pstmt.close();
			} else {
				// Falls schon eine ID vorhanden ist, mache ein Update...
				updateVertrag(con);
				
				String updateSQL = "UPDATE mietvertrag SET person = ?, wohnung = ?, beginn = ?, dauer = ?, nebenkosten = ? WHERE id = ?";
				PreparedStatement pstmt = con.prepareStatement(updateSQL);

				// Setze Anfrage Parameter
				pstmt.setInt(1, getPerson().getId());
				pstmt.setInt(2, getWohnung().getId());
				pstmt.setDate(3, getBeginn());
				pstmt.setInt(4, getDauer());
				pstmt.setInt(5, getNebenkosten());
				pstmt.setInt(6, getId());
				pstmt.executeUpdate();

				pstmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

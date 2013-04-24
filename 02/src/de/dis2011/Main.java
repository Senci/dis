package de.dis2011;

import java.util.LinkedList;

import helper.DBManager;
import de.dis2011.data.Makler;
import de.dis2011.data.Wohnung;

/**
 * Hauptklasse
 */
public class Main {
	/**
	 * Startet die Anwendung
	 */
	public static void main(String[] args) {
//		DBManager db = new DBManager();
//		db.dropTables();
//		db.createScheme();
//		db.loadFixtures();
		showMainMenu();
	}
	
	/**
	 * Zeigt das Hauptmen√º
	 */
	public static void showMainMenu() {
		//Men√ºoptionen
		final int MENU_MAKLER = 0;
		final int MENU_IMMO = 1;
		final int QUIT = 2;
		
		//Erzeuge Men√º
		Menu mainMenu = new Menu("Hauptmen√º");
		mainMenu.addEntry("Makler-Verwaltung", MENU_MAKLER);
		mainMenu.addEntry("Immo-Verwaltung", MENU_IMMO);
		mainMenu.addEntry("Beenden", QUIT);
		
		//Verarbeite Eingabe
		while(true) {
			int response = mainMenu.show();
			
			switch(response) {
				case MENU_MAKLER:
					showMaklerMenu();
					break;
				case MENU_IMMO:
					showImmoMenu();
					break;
				case QUIT:
					return;
			}
		}
	}
	
	/**
	 * Zeigt die Maklerverwaltung
	 */
	public static void showMaklerMenu() {
		//Men√ºoptionen
		final int NEW_MAKLER = 0;
		final int EDIT_MAKLER = 1;
		final int BACK = 2;
		
		//Maklerverwaltungsmen√º
		Menu maklerMenu = new Menu("Makler-Verwaltung");
		maklerMenu.addEntry("Neuer Makler", NEW_MAKLER);
		maklerMenu.addEntry("Makler Editieren", EDIT_MAKLER);
		maklerMenu.addEntry("Zur√ºck zum Hauptmen√º", BACK);
		
		//Verarbeite Eingabe
		while(true) {
			int response = maklerMenu.show();
			
			switch(response) {
				case NEW_MAKLER:
					newMakler();
					break;
				case EDIT_MAKLER:
					editMaklerMenu();
					break;
				case BACK:
					return;
			}
		}
	}
	
	/**
	 * Legt einen neuen Makler an, nachdem der Benutzer
	 * die entprechenden Daten eingegeben hat.
	 */
	public static void newMakler() {
		Makler m = new Makler();
		
		m.setName(FormUtil.readString("Name"));
		m.setAddress(FormUtil.readString("Adresse"));
		m.setLogin(FormUtil.readString("Login"));
		m.setPassword(FormUtil.readString("Passwort"));
		m.save();
		
		System.out.println("Makler mit der ID "+m.getId()+" wurde erzeugt.");
	}
	
	/**
	 * √ñffnet einen Dialog mit dem Makler editiert werden k√∂nnen.
	 */
	public static void editMaklerMenu() {
		final int BACK = 0;
		
		LinkedList<Makler> makler = new LinkedList<Makler>();
		Makler m = new Makler();
		for (int i = 1; m != null; i++) {
			m = Makler.load(i);
			if (m == null) {
				break;
			}
			makler.add(m);
		}
		
		Menu editMaklerMenu = new Menu("Makler-Verwaltung: Makler");
		
		for (Makler ma:makler) {
			editMaklerMenu.addEntry(ma.getLogin(), ma.getId());
		}
		
		editMaklerMenu.addEntry("Zur√ºck zum Makler-Verwaltungs Men√º", BACK);
		
		System.out.println("reached b4 while");
		//Verarbeite Eingabe
		while(true) {
			System.out.println("in while!");
			int response = editMaklerMenu.show();
			if (response == BACK) {
				return;
			} else if(response <= makler.size()) {
				editMakler(response);
			} else {
				System.out.println("Your input was not recognized! You will be returned to the previous menu.");
			}
		}
	}

	private static void editMakler(int makler_id) {
		Makler m = Makler.load(makler_id);
		
		String name = FormUtil.readString("Name"+"["+m.getName()+"]");
		m.setName(name.isEmpty() ? m.getName() : name);
		String address = FormUtil.readString("Adresse"+"["+m.getAddress()+"]");
		m.setAddress(address.isEmpty() ? m.getAddress() : address);
		String login = FormUtil.readString("Login"+"["+m.getLogin()+"]");
		m.setLogin(login.isEmpty() ? m.getLogin() : login);
		String password = FormUtil.readString("Passwort"+"["+m.getPassword()+"]");
		m.setPassword(password.isEmpty() ? m.getPassword() : password);
		m.save();
		
		System.out.println("Saved Makler \""+m.getLogin());
	}
	
	/**
	 * Zeigt das Immobilienmen¸
	 */
	public static void showImmoMenu() {

		final int NEW_IMMO = 0;
		final int EDIT_IMMO = 1;
		final int DELETE_IMMO = 2;
		final int BACK = 3;

		Menu immoMenu = new Menu("Immobilien-Verwaltung");
		immoMenu.addEntry("Neue Immobilie", NEW_IMMO);
		immoMenu.addEntry("Immobilie ‰ndern", EDIT_IMMO);
		immoMenu.addEntry("Immobilie lˆschen", DELETE_IMMO);
		immoMenu.addEntry("Zur¸ck zum Hauptmen¸", BACK);

		while (true) {
			int response = immoMenu.show();

			switch (response) {
			case NEW_IMMO:
				newWohnung();
				break;
			case EDIT_IMMO:
				int id = FormUtil.readInt("Zu editierende Wohnung (ID)");
				editWohnung(id);
				break;
			case DELETE_IMMO:
				deleteImmo();
				break;
			case BACK:
				return;
			}
		}
	}

	private static void newWohnung()
	{
		Wohnung w = new Wohnung();
		
		w.setVerwalter(Makler.load(FormUtil.readInt("Verwaltender Makler (ID)")));
		w.setOrt(FormUtil.readString("Ort"));
		w.setPlz(FormUtil.readInt("PLZ"));
		w.setStrasse(FormUtil.readString("Straﬂe"));
		w.setHausnummer(FormUtil.readString("HausNr"));
		w.setFlaeche(FormUtil.readInt("Fl‰che"));
		w.setStockwerk(FormUtil.readInt("Stockwerk"));
		w.setPreis(FormUtil.readInt("Preis"));
		w.setZimmer(FormUtil.readInt("Zimmer"));
		w.setBalkon((FormUtil.readString("Balkon t/f").equals("t") ? true : false));
		w.setEbk((FormUtil.readString("EBK t/f").equals("t") ? true : false));
		
		w.save();
		System.out.println("Saved Wohnung "+w.getId());
	}
	
	private static void editWohnung(int wohnung_id) {
		Wohnung w = Wohnung.load(wohnung_id);
		
		
		w.setVerwalter(Makler.load(FormUtil.readInt("Verwaltender Makler (ID)")));
		String ort = FormUtil.readString("Ort"+"["+w.getOrt()+"]");
		w.setOrt(ort.isEmpty() ? w.getOrt() : ort);
		Integer plz = FormUtil.readInt("PLZ"+"["+w.getPlz()+"]");
		w.setPlz(plz);
		String Strasse = FormUtil.readString("Straﬂe"+"["+w.getStrasse()+"]");
		w.setStrasse(Strasse.isEmpty() ? w.getStrasse() : Strasse);
		String hausnr = FormUtil.readString("HausNr"+"["+w.getHausnummer()+"]");
		w.setHausnummer(hausnr);
		Integer flaeche = FormUtil.readInt("Fl‰che"+"["+w.getFlaeche()+"]");
		w.setFlaeche(flaeche);
		Integer stockwerk = FormUtil.readInt("Stockwerk"+"["+w.getStockwerk()+"]");
		w.setStockwerk(stockwerk);
		Integer mietpreis = FormUtil.readInt("Mietpreis"+"["+w.getPreis()+"]");
		w.setPreis(mietpreis);
		Integer zimmer = FormUtil.readInt("Zimmer"+"["+w.getZimmer()+"]");
		w.setZimmer(zimmer);
		String balkon = FormUtil.readString("Balkon"+"["+w.getBalkon()+"] t/f");
		w.setBalkon(balkon.equals("t") ? true : false);
		String ebk = FormUtil.readString("EBK"+"["+w.getEbk()+"] t/f");
		w.setEbk(ebk.equals("t") ? true : false);
		
		
		
		w.save();
		
		System.out.println("Saved Wohnung \""+w.getId());
		
		

	}

	private static void editImmo() {
		// TODO Auto-generated method stub

	}

	private static void deleteImmo() {
		// TODO Auto-generated method stub

	}

}

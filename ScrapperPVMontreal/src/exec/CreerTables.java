package exec;

import java.sql.SQLException;

public class CreerTables {

	/*MÉTHODE DE CRÉATION DE LA TALBE AVEC INFORMATION SUR DOCUMENTS*/
	public static void tbDocs(){

		String requete = "CREATE TABLE IF NOT EXISTS pvCeDocs (" +
				"id INT, date_ce DATE, jurisdiction VARCHAR(30), ordinaire boolean, titre VARCHAR(150), url VARCHAR(200), " +
				"presences TEXT, absences TEXT, autres_presences TEXT, PRIMARY KEY(id))";
		
		String destruction = "DROP TABLE IF EXISTS pvCeDocs";
		String indexNoComp = "CREATE INDEX ixidDoc ON pvCeDocs (id)";

		try {
			util.Extraction.executerCreation(destruction);
			util.Extraction.executerCreation(requete);
			util.Extraction.executerCreation(indexNoComp);

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*MÉTHODE DE CRÉATION DE LA TALBE AVEC CONTENU DES DOCUMENTS*/
	public static void tbContenu(){
		
		String requete = "CREATE TABLE IF NOT EXISTS pvCeContenu (" +
				"id INT, date_ce DATE, jurisdiction VARCHAR(30), no_element VARCHAR(12), " +
				"txt_resolu TEXT, adopte_a TEXT, cash BOOLEAN, petit_chiffre VARCHAR(10), no_resolution VARCHAR(15))";
		String destruction = "DROP TABLE IF EXISTS pvCeContenu";
		String indexNoComp = "CREATE INDEX ixidCont ON pvCeContenu (no_element)";
		
		try {
			util.Extraction.executerCreation(destruction);
			util.Extraction.executerCreation(requete);
			//util.Extraction.executerCreation(indexNoComp);

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}

package exec;

import java.io.File;
import java.io.IOException;

public class BuildBD {

	/**
	 * @param args
	 */
	public static String separateur = "######";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		/*ALLER CHERCHER LES FICHIERS DES PV*/
		File[] listeF = Scrapper.fichier(CHEMIN_DES_FICHIERS);
		System.out.println(listeF.length);
		String date = "";
		
		/*CRÉATION DES TABLES*/
		CreerTables.tbDocs();
		CreerTables.tbContenu();
		
		try {
			/*SCRAPPING DE L'INFORMATION ET INSERTIONS DANS LES TABLES*/
			for(int i = 0; i < listeF.length; i++){
				System.out.println(listeF[i].getName() + "\t" + i);
				String texte = Scrapper.contenu(listeF[i]);
				date = Scrapper.headerFichier(texte, "____________________________", listeF[i].getName(), i);
				Scrapper.resolution(texte, "____________________________", date, i);
			}
			//
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

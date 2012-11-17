package exec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scrapper {

	/*Méthode pour aller chercher l'information qui ira dans la table d'information du document*/
	public static String headerFichier(String fichier, String sepPage, String path, int id){
		
		boolean continuer = true;
		int compteur = 0;
		String titre = "";
		String presence = "";
		String absence = "";
		String autrePresence = "";
		boolean ordinaire = true;
		String date = "";
		String url = "http://ville.montreal.qc.ca/documents/Adi_Public/CE/" + path.replace(".txt",".pdf");
		String ville = "MONTREAL";
		boolean bPres = false;
		boolean bAbs = false;
		boolean bAutPres = false;
		
		String[] ligne = fichier.split("\\n");
		int index = 0;
		
		
		/*Extraction de la date dans le titre du fichier*/
		if(path.toUpperCase().startsWith("CE") && !path.toUpperCase().startsWith("CE_PV_EXTRA") && !path.toUpperCase().startsWith("CE_PV_ORDI")){
			index = path.indexOf("-");
			date = path.substring(index+1, index + 5)+ "-" + path.substring(index + 5, index + 7) + "-" + path.substring(index + 7, index + 9);
			
		}else if(path.toUpperCase().startsWith("CE_PV_EXTRA")){
			index = path.indexOf("2");
			date = path.substring(index,index+10);
			
		}else if(path.toUpperCase().startsWith("CE_PV_ORDI")){
			index = path.indexOf("2");
			date = path.substring(index,index+10);
		}
		
		while(continuer){
			//System.out.println(ligne[compteur]);
			if(ligne[compteur].startsWith(sepPage)){
				continuer = false;
				bPres = false;
				bAbs = false;
				bAutPres = false;
			}
			if(ligne[compteur].toUpperCase().startsWith("SÉANCE")){
				titre = ligne[compteur];
			}
			if(ligne[compteur].toUpperCase().startsWith("PROCÈS-VERBAL")){
				if(ligne[compteur].toUpperCase().contains("EXTRAORDINAIRE")){
					ordinaire = false;
				}
			}
			if(ligne[compteur].toUpperCase().startsWith("SONT PRÉSENTS :") || ligne[compteur].toUpperCase().startsWith("PRÉSENCES :")){
				bPres = true;
				bAbs = false;
				bAutPres = false;
			}
			if(bPres){
				presence += ligne[compteur].replace("-","") + " ";
			}
			if(ligne[compteur].toUpperCase().startsWith("SONT ABSENTS :") || ligne[compteur].toUpperCase().startsWith("ABSENCES :")){
				bPres = false;
				bAbs = true;
				bAutPres = false;
			}
			if(bAbs){
				absence += ligne[compteur].replace("-","") + " ";
			}
			if(ligne[compteur].toUpperCase().startsWith("SONT AUSSI PRÉSENTS :") || ligne[compteur].toUpperCase().startsWith("AUTRES PRÉSENCES :")){
				bPres = false;
				bAbs = false;
				bAutPres = true;
			}
			if(bAutPres){
				autrePresence += ligne[compteur].replace("-","") + " ";
			}
			
			compteur += 1;
		}
		presence = presence.replace("SONT PRÉSENTS :", "").replace("PRÉSENCES :", "").replace("SONT AUSSI PRÉSENTS :","").replace("AUTRES PRÉSENCES :","").replace("SONT ABSENTS :","").replace("ABSENCES :","").replace("AUTRES","");
		absence = absence.replace("SONT PRÉSENTS :", "").replace("PRÉSENCES :", "").replace("SONT AUSSI PRÉSENTS :","").replace("AUTRES PRÉSENCES :","").replace("SONT ABSENTS :","").replace("ABSENCES :","").replace("AUTRES","");;
		autrePresence = autrePresence.replace("SONT PRÉSENTS :", "").replace("PRÉSENCES :", "").replace("SONT AUSSI PRÉSENTS :","").replace("AUTRES PRÉSENCES :","").replace("SONT ABSENTS :","").replace("ABSENCES :","").replace("AUTRES","");;
		
		String insert = "INSERT INTO pvCeDocs (" +
				"id, date_ce, jurisdiction, ordinaire, titre, url, presences, " +
				"absences, autres_presences) VALUES (";
		insert += Integer.toString(id) + ",";
		insert += "'" + date + "', '" + ville + "', "+ Boolean.toString(ordinaire) + ", '" + titre.trim().replaceAll("'","''") + "', '" + url.trim().replaceAll("'","''") + "', '" + presence.trim().replaceAll("'","''") + "', '" + absence.trim().replaceAll("'","''") + "', '" + autrePresence.trim().replaceAll("'","''") + "')";
		System.out.println(insert);
		
		try {
			util.Extraction.executerCreation(insert);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	/*Méthode qui sépare le document PV en résolution*/
	public static void resolution(String fichier, String sepPage, String date, int id){

		String[] enResolutions = fichier.split(sepPage);
		boolean resol = false;
		boolean cash = false;
		String ville = "MONTREAL";
		String noElement = "";
		String txtResolution = "";
		String noRes = "";
		String tiChiffre = "";
		String adopte = "";
		String insert = "";
		
		Pattern patternTC = Pattern.compile(".*\\d{2}\\.\\d{2}.*");
		Pattern pattern10 = Pattern.compile(".*[0-9]{10}");
		
		for(int i = 0; i < enResolutions.length; i++){

			noElement = "";
			txtResolution = "";
			noRes = "";
			tiChiffre = "";
			adopte = "";
			cash = false;
			
			/*Pseudo regex...*/
			String[] lignes = enResolutions[i].split("\n");
			for(int j = 0; j < lignes.length; j ++){
				Matcher matchTC = patternTC.matcher(lignes[j].trim());
				Matcher match10 = pattern10.matcher(lignes[j].trim());
				if(lignes[j].startsWith("CE0") || lignes[j].startsWith("CE1") || lignes[j].startsWith("CE2")
						|| lignes[j].startsWith("CE3") || lignes[j].startsWith("CE4") || lignes[j].startsWith("CE5")
						|| lignes[j].startsWith("CE6") || lignes[j].startsWith("CE7") || lignes[j].startsWith("CE8")
						|| lignes[j].startsWith("CE9")){
					noElement = lignes[j].trim();
					if(noElement.length() >= 12){
						noElement = noElement.substring(0,11);
					}
				}
				if(lignes[j].toUpperCase().startsWith("RÉSOLU :")){
					resol = true;
				}
				if(!lignes[j].toUpperCase().startsWith("RÉSOLU :") && resol && !lignes[j].toUpperCase().startsWith("ADOPT")){
					txtResolution += lignes[j] + "\t";
				}
				if(lignes[j].contains("$")){
					cash = true;
				}
				if(lignes[j].toUpperCase().startsWith("ADOPT")){
					resol = false;
					adopte = lignes[j].trim();
				}
				if(matchTC.matches()){
					resol = false;
					String[] no = lignes[j].split(" ");
					if(no.length == 1){
						tiChiffre = no[0];
					}else{
						for(int k = 0; k < no.length; k++){
							if(no[k].contains(".")){
								tiChiffre = no[k];
							}
						}
					}
				}
				if(match10.matches()){
					resol = false;
					String[] no = lignes[j].split(" ");
					if(no.length == 1){
						noRes = no[0];
					}else{
						for(int k = 0; k < no.length; k++){
							if(no[k].length() >= 10){
								noRes = no[k];
							}
						}
					}
				}	
			}
			/*Insertion dans la table*/
			if(!noElement.isEmpty()){
				insert = "INSERT INTO pvCeContenu (" +
				"id, date_ce, jurisdiction, no_element, txt_resolu, adopte_a, " +
				"cash, petit_chiffre, no_resolution) VALUES (";
				
				insert += Integer.toString(id) + ", '" + date + "', '" + ville + "', '"  + noElement.trim().replaceAll("'","''") + "', '" + txtResolution.trim().replaceAll("'","''").replaceAll("’","''") + "', '"  +
				adopte.trim().replaceAll("'","''") + "', " + Boolean.toString(cash) + ", '" + tiChiffre.trim() + "', '" + noRes.trim() + "')";
				
				System.out.println(insert);
				try {
					util.Extraction.executerCreation(insert);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static String contenu(File fichier) throws IOException{

		FileReader fich = new FileReader(fichier);
		BufferedReader buf = new BufferedReader(fich);

		String texte = "";
		String ligne = "";

		while((ligne = buf.readLine()) != null){
			if(!ligne.startsWith("Séance"))
				texte += ligne + "\n";
		}

		return texte;

	}

	public static File[] fichier(String chemin){
		File doss = new File(chemin);
		File[] liste = doss.listFiles();
		return liste;
	}
}

package audio;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import io.FileSource;

public class SNRFilter implements AudioFilter {
	
	private String emplacement1;
	private ArrayList<FileSource> tableauFichier;
	private double tableauSNR[];
	private FileSource file1 = null;
	
	
	/**
	 * Constructeur d'initialisation
	 * @param pEmplacement1
	 * @param pEmplacement2
	 */
	public SNRFilter(String pEmplacement){
		emplacement1 = pEmplacement;
		tableauFichier = new ArrayList<FileSource>();
		
		// TODO Auto-generated method stub
				
		try {
			file1 = new FileSource(emplacement1);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Ajouter un fichier dans le tableau de fichiers
	 * @param _emplacement 
	 */
	public void ajouterFichier(String _emplacement){
		try {
			tableauFichier.add(new FileSource(_emplacement));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void process() {
		tableauSNR = new double[tableauFichier.size()];
		double grandeur4 = 0;
		double grandeur3 = 0;
		double temp1 = 0;
		double temp2 = 0;
		double tableauBruit[] = new double[tableauSNR.length];
		file1.skipByte(44);
			
			
		for (int i=0; i<tableauSNR.length;i++){
			tableauFichier.get(i).skipByte(44);
		}
			while (temp1!= -1){
				temp1 =  (double)file1.readInt();
				grandeur4 += Math.pow(temp1, 2);
				for (int i=0; i<tableauSNR.length;i++){	
					temp2 =  (double)tableauFichier.get(i).readInt();
					
					
					grandeur3 = (temp1-temp2);
					tableauBruit[i] += Math.pow(grandeur3,2);
				}

				
			}
		for (int i=0; i<tableauSNR.length;i++){	
			tableauSNR[i] = Math.log10(grandeur4/tableauBruit[i]) *10;
		}
		
		classer();
		
		afficherValeur();	
	}
	
	
	/*
	 * Afficher les valeurs du tableauSNR
	 */
	private void afficherValeur(){
		for(int i=0; i < (int)tableauSNR.length;i++){
			System.out.print(tableauFichier.get(i).getNomFichier() + ": ");
			System.out.println(tableauSNR[i]);
		}
	}
	
	/*
	 * Classer par insertion les valeurs du tableau
	 */
	private void classer(){
		for (int j = 1;j < (int)tableauSNR.length;j++){
			double cle = tableauSNR[j];
			FileSource nom = tableauFichier.get(j);
			int i = j-1;
			while (i>=0 && tableauSNR[i]>cle){
				tableauSNR[i+1] = tableauSNR[i];
				tableauFichier.set(i+1, tableauFichier.get(i));
				i = i-1;
				
			}
			tableauSNR[i+1] =cle;
			tableauFichier.set(i+1, nom);
		}
	}
}

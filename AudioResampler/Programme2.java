import audio.SNRFilter;

public class Programme2 {

	/**
	 * Launch the application
	 * @param args This parameter is ignored
	 */
	public static void main(String args[]){
		System.out.println("Audio Resample project!");
		SNRFilter filtre = new SNRFilter("son/App2_Original_Mono8bits.wav");
		filtre.ajouterFichier("son/App2_Mod7_WhiteNoise4_Mono8bits.wav");
		filtre.ajouterFichier("son/App2_Mod1_AmpBase_Mono8bits.wav");
		filtre.ajouterFichier("son/App2_Mod2_30SamplesDelay_Mono8bits.wav");
		filtre.ajouterFichier("son/App2_Mod3_Echo1SecMono8bits.wav");
		filtre.process();
	}
}

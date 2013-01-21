import audio.WaveAudioFilter;



public class Programme1 {

	/**
	 * Launch the application
	 * @param args This parameter is ignored
	 */
	public static void main(String args[]) {
		System.out.println("Audio Resample project!");
		WaveAudioFilter filtreWave = new WaveAudioFilter(args[0], args[1]);
		filtreWave.process();
	}
}

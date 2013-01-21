package audio;

import java.io.FileNotFoundException;
import java.io.IOException;

import io.FileSink;
import io.FileSource;

/**
 * Votre mandat consiste à développer un filtre audio pour convertir des valeurs de 16 bits, 
 * à des valeurs de 8 bits. Le programme doit analyser un fichier « .wav » non compressé, 
 * transformer la valeur de 16 bits en une valeur de 8 bits et produire un nouveau fichier « .wav » à la sortie.
 * Le programme devra vérifier que le fichier à l’entrée utilise bien 16 bits pour représenter une valeur avant de faire la modification.
 * 
 * @author dannyboyer
 *
 */

public class WaveAudioFilter implements AudioFilter{

	private String nomFichierSource;
	private String nomFichierSortie;
	private final String EXTENSION_FICHIER = ".wav";	
	private FileSource fichierSource;
	private FileSink fichierSortie;
	private short numCanaux;
	private short nouveauBitsPerSample;
	private int nouveauByteRate;
	private short nouveauBlockAlign;
	private int nouveauSubChunk2size;
	private int nouveauChunckSize;
	private int tauxEchantillonnage;
	
	/**
	 * Constructeur principale.
	 * @param in
	 * @param out
	 */
	public WaveAudioFilter(String in, String out){
		nomFichierSource = in;
		nomFichierSortie = out;
	}
	
	/**
	 * Convertir un fichier WAVE qui possède des valeurs de 16 bits, à des valeurs de 8 bits.
	 * Complexité : O(n) = n*1 + n*1 + 35,en ommetant la complexité de convertirData().
	 */
	public void process() {
		try {
			//Recuperer un liens vers le fichier à traiter et le fichier de destination.
			fichierSource = new FileSource(nomFichierSource + EXTENSION_FICHIER);//-------------------------------------1			
			fichierSortie = new FileSink(nomFichierSortie  + EXTENSION_FICHIER);//--------------------------------------1
			
			//Récupérer et mémoriser les données du fichier source.
			byte[] chunckId = fichierSource.pop(4);//-------------------------------------------------------------------1
			fichierSource.skipByte(4);//--------------------------------------------------------------------------------1
			byte[] format = fichierSource.pop(4);//---------------------------------------------------------------------1			
			byte[] subChunk = fichierSource.pop(10);//------------------------------------------------------------------1
			byte[] numChannels = fichierSource.pop(2);//----------------------------------------------------------------1
			byte[] sampleRate = fichierSource.pop(4);//-----------------------------------------------------------------1
			fichierSource.skipByte(6);//--------------------------------------------------------------------------------1
			byte[] bitsPerSample = fichierSource.pop(2);//--------------------------------------------------------------1		
			byte[] subChunk2id = fichierSource.pop(4);//----------------------------------------------------------------1
			byte[] subChunk2size = fichierSource.pop(4);//--------------------------------------------------------------1
			
			//on refait les calculs pour la conversion 8bits.
			numCanaux = lireShort(numChannels);//-----------------------------------------------------------------------1
			tauxEchantillonnage = lireInt(sampleRate);//----------------------------------------------------------------1
			nouveauBitsPerSample = 8;//---------------------------------------------------------------------------------1
			nouveauByteRate = tauxEchantillonnage  * numCanaux * (nouveauBitsPerSample/8);//----------------------------1
			nouveauBlockAlign = (short) (numCanaux * (nouveauBitsPerSample/8));//---------------------------------------1
			nouveauSubChunk2size = lireInt(subChunk2size) / 2;//--------------------------------------------------------1
			nouveauChunckSize = 36 + nouveauSubChunk2size;//------------------------------------------------------------1
					
			//on redefini les valeurs dans des tableaux de byte.
			byte[] tabNouveauByteRate = intToByteArray(nouveauByteRate, false);//---------------------------------------1		
			byte[] tabNouveauBlockAlign = shortToByteArray(nouveauBlockAlign, false);//---------------------------------1
			byte[] tabEightBitsPerSample = shortToByteArray(nouveauBitsPerSample, false);//-----------------------------1
			byte[] tabSubCHunk2Size = intToByteArray(nouveauSubChunk2size, false);//------------------------------------1
			byte[] tabChunckSize = intToByteArray(nouveauChunckSize, false);//------------------------------------------1
			
			//Vérification du nombre de bits par échantillon.
			if (verification16bits(lireShort(bitsPerSample))){//------------------------------------------------------n*1
				//Consrtruire le fichier de sortie.
				fichierSortie.push(chunckId);//-------------------------------------------------------------------------1
				fichierSortie.push(tabChunckSize);//--------------------------------------------------------------------1
				fichierSortie.push(format);//---------------------------------------------------------------------------1
				fichierSortie.push(subChunk);//-------------------------------------------------------------------------1
				fichierSortie.push(numChannels);//----------------------------------------------------------------------1
				fichierSortie.push(sampleRate);//-----------------------------------------------------------------------1
				fichierSortie.push(tabNouveauByteRate);//---------------------------------------------------------------1
				fichierSortie.push(tabNouveauBlockAlign);//-------------------------------------------------------------1
				fichierSortie.push(tabEightBitsPerSample);//------------------------------------------------------------1
				fichierSortie.push(subChunk2id);//----------------------------------------------------------------------1
				fichierSortie.push(tabSubCHunk2Size);//-----------------------------------------------------------------1
				convertirData();//------------------------------------------------------------------------------------n*1
			}
			//sinon, on ne traite pas le fichier.
			else{
				System.out.println("La conversion est annulée, le fichier n'est pas composé de 16 bits.");
			}
					
			//On ferme l'acces au fichier source et au fichier de sortie.
			fichierSource.close();
			fichierSortie.close();
			
		} 
		catch (FileNotFoundException e) {
			System.out.println("Le fichier est introuvable, veuillez réessayer s.v.p. !");
		}
		catch (IOException e) {
			System.out.println("Une erreur de type I/O est survenue, le fichier est peut-être corrompue.");
		}
	}
	
	/**
	 * Convertit le data en 8bits.
	 * Complexité :O(n) = n*1 + n*1 + n*1 + n*1 + n*1 + n*1 + n*1 + n*1 + 19. 
	 * @throws IOException
	 */
	private void convertirData() throws IOException {
		for(int i=0; i<(nouveauSubChunk2size / numCanaux * 2); i++){//n*1
			//mono.
			if(numCanaux == 1){//------------------------------------n*1
				byte[] buffer = fichierSource.pop(2);//----------------1
				int valeur = lireShort(buffer);//----------------------1
			     
			    if (valeur >= 32767){//------------------------------n*1
			    	valeur= (valeur - 32767)/256;//--------------------1
			    }
			    else if (valeur < 32767){//--------------------------n*1
			        valeur= 128 + (valeur / 256);//--------------------1
			    }
			 
			    byte[] newData = {(byte) valeur};//--------------------1
			    fichierSortie.push(newData);//-------------------------1
			    i++;//-------------------------------------------------1
			}
			//stereo.
			else{
				byte[] buffer = fichierSource.pop(4);//----------------1
				int valeurG = lireIntCustom(buffer, true);//-----------1
				int valeurD = lireIntCustom(buffer, false);//----------1
				
				if (valeurG >= 32767){//-----------------------------n*1
					valeurG= (valeurG - 32767)/256;//------------------1
			    }
			    else if (valeurG < 32767){//-------------------------n*1
			    	valeurG= 128 + (valeurG / 256);//------------------1
			    }
			    else if (valeurD >= 32767){//------------------------n*1
			    	valeurD= (valeurD - 32767)/256;//------------------1
			    }
			    else if (valeurD < 32767){//-------------------------n*1
			    	valeurD= 128 + (valeurD / 256);//------------------1
			    }
			     
				byte[] newDataG = {(byte) valeurG};//------------------1
				byte[] newDataD = {(byte) valeurD};//------------------1
			    fichierSortie.push(newDataG);//------------------------1
			    fichierSortie.push(newDataD);//------------------------1
			    i++;//-------------------------------------------------1
			}
		}	     
	}
	
	/**************************************************************************************************************
	 * Méthodes utilitaires.
	 **************************************************************************************************************/
	
	/**
	 * Vérifier si le fichier source est bien composé de 16 bits.
	 * @param nbBits
	 * @return
	 */
	public boolean verification16bits(int nbBits){
		if(nbBits == 16)
			return true;
		else
			return false;
	}
		
	/**
	 * Convertir un tableau de 2 bits en int,
	 * pour le litle endian.
	 * 
	 * Inspirer en partie du tutorial:
	 * Reading and Writing a WAV File in Java
	 * https://thiscouldbebetter.wordpress.com/2011/08/14/reading-and-writing-a-wav-file-in-java/
	 */
	public short lireShort(byte[] bytesLittleEndian) throws IOException
    {      
        int valeurInt =
        (
            (bytesLittleEndian[0] & 0xFF)
            | ((bytesLittleEndian[1] & 0xFF) << 8 )
        );
        return (short)valeurInt;
    }
	
	/**
	 * Convertir un tableau de 4 bits en int,
	 * pour le litle endian.
	 * 
	 * Inspirer en partie du tutorial:
	 * Reading and Writing a WAV File in Java
	 * https://thiscouldbebetter.wordpress.com/2011/08/14/reading-and-writing-a-wav-file-in-java/
	 */
	public int lireInt(byte[] bytesLittleEndian) throws IOException
    {
        long returnValueAsLong =
        (
            (bytesLittleEndian[0] & 0xFF)
            | ((bytesLittleEndian[1] & 0xFF) << 8 )
            | ((bytesLittleEndian[2] & 0xFF) << 16)
            | ((bytesLittleEndian[3] & 0xFF) << 24)
        );

        return (int)returnValueAsLong;
    }
	
	/**
	 * Retourne la valeur de la moitié d'un tableau de bits représentant un int.
	 * Inspirer en partie du tutorial:
	 * Reading and Writing a WAV File in Java
	 * https://thiscouldbebetter.wordpress.com/2011/08/14/reading-and-writing-a-wav-file-in-java/
	 * 
	 * @param bytesLittleEndian
	 * @param left
	 * @return returnValueAsLong
	 * @throws IOException
	 */
	public int lireIntCustom(byte[] bytesLittleEndian, boolean left) throws IOException
	{
		long returnValueAsLong;		
		if (left){
			 returnValueAsLong =
			        (
			            (bytesLittleEndian[0] & 0xFF)
			            | ((bytesLittleEndian[1] & 0xFF) << 8 )
			        );
		}
		else{
			 returnValueAsLong =
			        (
		        		((bytesLittleEndian[2] & 0xFF) << 16)
		                | ((bytesLittleEndian[3] & 0xFF) << 24)
			        );
		}
	    return (int)returnValueAsLong;
	}
	
	/**
	* Convertir un entier en tableau de bits.
	* Inspirer en partie du tutorial:
	* Reading and Writing a WAV File in Java
	* https://thiscouldbebetter.wordpress.com/2011/08/14/reading-and-writing-a-wav-file-in-java/
	* 
	* @param piValueToConvert
	* @param pbBigEndian
	* @return
	*/
   public static byte [] intToByteArray ( int piValueToConvert, boolean pbBigEndian ) 
   { 
     byte [] aRetVal = new byte [ 4 ] ; 

     byte iLowest; 
     byte iLow; 
     byte iMid; 
     byte iHigh; 

     iLowest = ( byte )( piValueToConvert & 0xFF ) ; 
     iLow = ( byte )(( piValueToConvert >> 8 ) & 0xFF ) ; 
     iMid = ( byte )(( piValueToConvert >> 16 ) & 0xFF ) ; 
     iHigh = ( byte )(( piValueToConvert >> 24 ) & 0xFF ) ; 

     if ( pbBigEndian ) 
     { 
       aRetVal [ 3 ] = iLowest; 
       aRetVal [ 2 ] = iLow; 
       aRetVal [ 1 ] = iMid; 
       aRetVal [ 0 ] = iHigh; 
     } 
     else 
     { 
       aRetVal [ 0 ] = iLowest; 
       aRetVal [ 1 ] = iLow; 
       aRetVal [ 2 ] = iMid; 
       aRetVal [ 3 ] = iHigh; 
     } 

     return aRetVal; 
   }
   
   /**
	* Convertir un short en tableau de bits.
	* Inspirer en partie du tutorial:
	* Reading and Writing a WAV File in Java
	* https://thiscouldbebetter.wordpress.com/2011/08/14/reading-and-writing-a-wav-file-in-java/
	* 
	* @param piValueToConvert
	* @param pbBigEndian
	* @return
	*/
   public static byte [] shortToByteArray ( short piValueToConvert, boolean pbBigEndian ) 
   { 
     byte [] aRetVal = new byte [ 2 ] ; 

     byte iLowest; 
     byte iLow; 
     byte iMid; 
     byte iHigh; 

     iLowest = ( byte )( piValueToConvert & 0xFF ) ; 
     iLow = ( byte )(( piValueToConvert >> 8 ) & 0xFF ) ; 
     iMid = ( byte )(( piValueToConvert >> 16 ) & 0xFF ) ; 
     iHigh = ( byte )(( piValueToConvert >> 24 ) & 0xFF ) ; 

     if ( pbBigEndian ) 
     {  
       aRetVal [ 1 ] = iMid; 
       aRetVal [ 0 ] = iHigh; 
     } 
     else 
     { 
       aRetVal [ 0 ] = iLowest; 
       aRetVal [ 1 ] = iLow; 
     } 

     return aRetVal; 
   } 	
}

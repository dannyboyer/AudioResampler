GTI310
======

Structures de données multimédias

## Contexte

Votre mandat consiste à développer un filtre audio pour convertir des valeurs de 16 bits, à des valeurs de 8 bits. Le programme doit analyser un fichier « .wav » non compressé, transformer la valeur de 16 bits en une valeur de 8 bits et produire un nouveau fichier « .wav » à la sortie. Le programme devra vérifier que le fichier à l’entrée utilise bien 16 bits pour représenter une valeur avant de faire la modification.

Par ailleurs, la compagnie aimerait aussi avoir une idée de la qualité de fichiers modifiés dont elle dispose. Vous devrez évaluer le rapport signal à bruit (RSB) des fichiers modifiés par rapport au fichier original (ces fichiers vous sont fournis, vous n’avez pas à les créer). Pour ce faire, vous devrez développer un second filtre audio pour calculer le SNR (Signal to Noise Ratio). Le fichier original servira de référence dans le calcul (voir notes de cours pour la formule du SNR). Vous aurez donc deux petits programmes : un premier qui traite les fichiers originaux, et un second qui évalue le SNR des fichiers modifiés par rapport à l’original.

## Crédits

* École de technologie supérieure 
* Département de génie logiciel et des Professeur TI
* Automne 2012 
* Jean-François Franche


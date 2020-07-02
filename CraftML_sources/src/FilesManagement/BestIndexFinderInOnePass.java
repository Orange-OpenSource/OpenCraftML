package FilesManagement;

import Algorithm.Displayer;

//compléments de doc : PhP,2016/01/26Ma.
/*
	TODO 2016/01/27-Me, PhP - PB DES ERREURS / EXCEPTION (undef top etc).
	On peut toujours clasher le prog comme erreur interne...
	Mais l'ennui est... que ça fait aussi planter l'Interface Graphique,
	et qu'il est dommage de l'arrêter alors qu'elle fait d'autres choses...

*/
/**
	* Classe utilitaire pour retenir le TOP N de scores (ou de similarités,...)
	* d'une série d'ID (lignes ou colonnes) qui arrivent à volée ;
	*
	* sert pour avoir les lignes/colonnes les plus denses,
	* et pour le calcul des K-plus-proches-voisins et des TOP N (ex: Precision) ;
	*
	* ATTENTION, quelques précautions à prendre(*).
	*
	*<P>(*) PRECAUTIONS.
	*<DIR>
	*	<P>ADD's - REDONDANCES -
	*		ATTENTION on suppose que chaque ID n'est vu qu'une fois :
	*		si un ID est vu plusieurs fois il pourra être mémorisé
	*		plusieurs fois dans la liste...
	*	<P>ADD's - ARGS VALIDES -
	*		le prog ne contrôle pas la validité des arguments du ADD : chaîne vide, null...
	*		la chose devra être garantie en amont... Ce choix est fait pour des raisons pratiques :
	*		minimiser la redondance des opérations de contrôle, ne pas jeter Exception, etc...
	*	<P>COMPLETUDE DES TOP-K -
	*		Il faut prendre garde que les Top-K soient complets en fin d'opération ;
	*		c'est à dire, au delà des problèmes d'ID's vides etc (pb des args du Add) :
	*		que toutes les valeurs associées à un ID soient remplacées,
	*		étant donné qu'au départ, les Top-K sont indéfinis (vides ou similairement),
	*		et que seuls les Add's sauront les remplir...
	*		Mais il ne suffit pas pour cela que le nombre d'observations (Add's)
	*		soit supérieur ou égal à K : il faut aussi que les Add's véhiculent
	*		suffisamment de propriétés (au moins K donc) pour remplir les Top-K...
	*		Ainsi par exemple : 1000 Add(PROPID,value,...)'s mais de la même propriété (PROPID)
	*		ne rempliront jamais la table des Top-K	(sauf si K=1), mais une seule case...
	*		En pratique, les données usuellement fournies ne devraient pas poser de problème,
	*		toutefois il faut en toute rigueur tenir compte de la chose ;
	*		un compteur est implémenté en ce but et permet de savoir.
	*	    <BR><BR>
	*       ATTENTION ! Pour avoir les TopK tous définis, il faut suffisamment de variables
	*       pour remplir les K cellules... mais comme ce ne sont pas les mêmes variables (Props)
	*       qui viendront en TopK pour tous les Items (lignes, ou symétriquement : Props,Cols),
	*       cela signifie que même pour Seulement K TopK's, même peu grand, même partiel sur la ligne (sym: col),
	*       il faut potentiellement calculer TOUS les scores de la LogMatrice...
	*   <P>INITIALISATION ET REMPLACEMENTS -
	*   	La <U>Table des Top-Scores</U> est initialisée à -Infinity (la Table des Top-Value's aussi, du reste).
	*   	<U>S'il y reste des -Infinity à la fin, c'est que les cellules de ces positions n'ont
	*          jamais été remplacées, faute de candidats, même très faibles</U>...
	*   	D'abord parce que les candidats sont tous définis en principe,
	*   	et parce que de toutes façons, pour entrer dans les Top-K,
	*   	il faut être strictement supérieur au min des Top-K,
	*   	or il n'y a aucun nombre plus petit que Infinity,
	*   	donc même si des candidats (scores) étaient -Infinity, ils n'entreraient pas (...).
	*
	*</DIR>
	*
	* ATTENTION - prendre garde que le nombre d'observations soit supérieur ou égal à N dans Top N.
	*
	*<P>PRINCIPE :<DIR>
	*	On a un ensemble total dont on veut les K meilleurs (ex: K=ordre de la précision) ;<BR>
	*  les éléments de l'ensemble total arrivent au fur et à mesure (à la volée) pour candidater au bac :<BR>
	*  on garde en effet (en continu) un "bac" des K meilleurs éléments (Top K) ;<BR>
	*  quand le nouvel arrivant est meilleur que le plus mauvais du bac, il prend sa place (et l'autre est éjecté) ;<BR>
	*  à la fin, le bac contient forcément les meilleurs éléments ;<BR>
	*  pour finir, on trie le bac, et on a les K meilleurs dans l'ordre (décroissant).</DIR>
	*
	* @author ofmu6285
	*
 */
public class BestIndexFinderInOnePass
{
	//TODO: le cas de cases du Top-K restant vides... (cas de données peu nombreuses)

		/**
		 * La Table[K] des ID's des éléments du Top-K.
		 */
	public int[] bestID;

		/**
		 * La Table[K] des Valeurs des éléments du Top-K ; ce sont des Scores.
		 */
	public float[] bestScore;

		/**
		 * Comme bestScore, sauf que ce sont des Valeurs Réelles ;
		 * elles n'interviennent que passivement, en restant
		 * parallèlement et continuement associées aux Scores.
		 *<P>NB - l'ID associé au score étant connu, il est vrai
		 *   qu'on pourrait retrouver la présente valeur associée via l'ID, 
		 *   mais cela suppose d'avoir une table à clefs vers ces valeurs - pas forcément le cas, 
		 *   ensuite même en l'ayant, serait-ce plus "rentable", de la créer puis d'y accéder ???
		 *   Noter qu'il faudrait une table pour chaque Elément (tq Item) dont on cherche le Top-K : 
		 *   il se peut notamment qu'on travaille directement en Fichier sans ItemSet (etc)...   
		 */
	public float[] bestRealValue=null; //ajout PhP 2016/01/26-ma.

		/**
		 * L'ordre K du Top-K.
		 */
	int size;

		/**
		 * (<I>aux</I>) Position (indice), dans le Top-K, du plus petit élément du Top-K courant.
		 */
	int lowerValueIndexCache;

		/**
		 * (<I>aux</I>) Valeur du plus petit élément du Top-K courant.
		 */
	float lowerValueCache;

		/**
			* Nom affiché pour ID (alias PropID) Vide ; ce cas, qui est une ERREUR,
			* se présente lorsque les Add's n'ont pas apporté toutes les occurrences
			* nécessaires et suffisamment variées en ID's pour combler les Top-K,
			* et un ID vide est donc le signe d'incomplétude des ID's...
			*<P>Noter que les Add's ne peuvent pas contrôler la chose,
			* puisqu'il ne s'agit pas de Add's avec des ID's indéfinis,
			* mais de Add's directement absents...
			*/
	String mEmptyID="?";//ou ""

		/**	Nombre de Top-K définis (ça doit valoir K).
		 *<P>Nombre de Cellules(*) remplacées dans les tables du Top-K (au fil des Add's) ;
		 * <U>il doit valoir K</U> ; sinon, cela signifie que les Add's n'ont pas apporté
		 * une assez grande variété d'ID's (alias PropID's), ce qui signifie,
		 * qu'il n'y a pas assez d'observations, et/ou, plus insidieusement,
		 * que les Propriétés ne sont pas toutes définies (cf doc de classe).
		 *<P>*Nb Cells - ne tient évidemment pas compte du nombre de tables[K]...
		 */
	int mNbTopsDefined=0; //ajout PhP 2016/01/27-me
	//
	public int getNbTopsDefined(){return mNbTopsDefined;}
	public int getNbTopsUnDefined(){return (size-mNbTopsDefined);}
	public boolean isTopKDefined(){return (mNbTopsDefined==size);}

		/**
		 * Nombre de Adds (tout compris, qu'ils soient ou non mis dans les Top-K).
		 */
	int mNbAddsTotal=0;

	public int getNbAddsTotal(){return mNbAddsTotal;}

	/**
	 * Constructeur avec en arg le nombre K de Top-K à retenir (opération 1 sur 3, unique) ;
	 * au sortir du Constructeur, toutes les tables et autres variables sont initialisées,
	 * en sorte que le module est prêt à recevoir les Add's et ce qui s'ensuit.
	 * @param size  Le K du Top-K.
	 */
	public BestIndexFinderInOnePass(int size)
	{
		this.bestID=new int[size];
		this.bestScore=new float[size];
		this.bestRealValue=new float[size];//ajout PhP 2016/01/26-ma.

		this.size=size;

		for (int i=0;i<size;i++)
		{
			this.bestID[i]=-1; //modif PhP 2016/01/27-Me (empty value)
			this.bestScore[i]=Float.NEGATIVE_INFINITY;
			this.bestRealValue[i]=Float.NEGATIVE_INFINITY;//ajout PhP 2016/01/26-ma.
		}
		this.lowerValueIndexCache=0;
		this.lowerValueCache=Float.NEGATIVE_INFINITY;

		mNbTopsDefined=0; //ajout PhP 2016/01/27-me
	}

		/** (<I>aux</I>) cherche le plus petit élément du Top-K (son indice).
		 * @return indice du plus petit élément du Top-K courant ; nota :
		 *         comparaison avec candidat inférieur strict à min courant,
		 *         donc, notamment, en cas d'égalité, c'est statu quo...
		 *         Pour cette raison, notamment, le -Infinity d'initialisation
		 *         ne saurait être remplacé, car les candidats sont tous définis
		 *         (et s'il l'était quand-même (inf ou égal), ce serait par lui-même)...
		 */
	public int getLowerScoreIndex() {
		int index=0;
		float lowerValue=this.bestScore[0];
		for (int i=1;i<size;i++) {
			if (this.bestScore[i]<lowerValue) {
				lowerValue=this.bestScore[i];
				index=i;
			}
		}
		return index;
	}

		/**  (<I>aux</I>) Mise à jour de la position du plus petit élément du Top-K courant.
			 */
	public void updateCache() {
		lowerValueIndexCache=getLowerScoreIndex();
		lowerValueCache=this.bestScore[lowerValueIndexCache];
	}

	/**
	 * Addition d'un élément ; on donne son ID et sa Valeur (opération 2 sur 3, répétitive) ;
	 * après l'addition, le Top-K restera le Top-K, car l'élément n'aura été accepté
	 * dans le Top-K que s'il était meilleur que le plus mauvais du Top-K avant le add.
	 * @param id     ID de l'élément candidat au Top-K.
	 * @param value  Valeur de l'élément.
	 */
	public void add(int id, float value)
	{
		mNbAddsTotal++;

			//ne remplacer que si la Value est PLUS GRANDE STRICT QUE MIN TOPK - modif PhP, 2016/01/29-Ve
				//notamment évite des déplacements a priori non utiles (=>garde plutôt les prems colonnes).
				//en général dans un tri, on ne prend que les meilleurs stricts!!!
				//il est vrai que sur des valeurs aussi diversifiées que les Scores, 
				//cela devrait changer - le plus souvent - peu de choses...
				//
		  if (value<=lowerValueCache) //candidat: être plus grand strictement que le plus petit (strict) des Top-K.
		//if (value<lowerValueCache) //OLD MODE - modif PhP, 2016/01/29-Ve
		{
			// nothing
		} else {
			this.bestID[lowerValueIndexCache]=id;
			this.bestScore[lowerValueIndexCache]=value;
			updateCache();
		}

		/**
		for (int i=0;i<size;i++) {
			if (bestScore[i]<value) {
				this.bestID[i]=id;
				this.bestScore[i]=value;
				break;
			}
		}
		**/
	}
	public void add(int id, float score, float value_real)
	{
		mNbAddsTotal++;

			//ne remplacer que si la Value est PLUS GRANDE STRICT QUE MIN TOPK - modif PhP, 2016/01/29-Ve
				//notamment évite des déplacements a priori non utiles (=>garde plutôt les prems colonnes).
				//en général dans un tri, on ne prend que les meilleurs stricts!!!
				//il est vrai que sur des valeurs aussi diversifiées que les Scores, 
				//cela devrait changer - le plus souvent - peu de choses...
				//
		  if (score<=lowerValueCache) //candidat: être plus grand strictement que le plus petit (strict) des Top-K.
		//if (score<lowerValueCache) //OLD MODE - modif PhP, 2016/01/29-Ve
		{
			// nothing
		} else {
			//REPLACE!!!
			//mais d'abord (et pas après) un petit test de remplissage...
			if(this.bestScore[lowerValueIndexCache]==Float.NEGATIVE_INFINITY)
				{mNbTopsDefined++;} //ça pourra être <K mais pas >K...
			this.bestID[lowerValueIndexCache]=id;
			this.bestScore[lowerValueIndexCache]=score;
			this.bestRealValue[lowerValueIndexCache]=value_real;//ajout PhP 2016/01/26-ma.
			updateCache();
		}

		/**
		for (int i=0;i<size;i++) {
			if (bestScore[i]<value) {
				this.bestID[i]=id;
				this.bestScore[i]=value;
				break;
			}
		}
		**/
	}

		/** Printe le Top-K.*/
	public void print() {
		for (int i=0;i<size;i++) {
			Displayer.displayText("'"+bestID[i]+"'"+"="+bestScore[i]+"\t");
			//System.out.print(bestID[i]+", "+bestScore[i]+"\t");
		}
		Displayer.displayText("");
	}

	public void print2() //ajout PhP 2016/01/26-ma. =>bestRealValue
	{
		Displayer.displayText("bestID"+"\t"+"bestScore"+"\t"+"bestRealValue"+"\n");

		for (int i=0;i<size;i++)
		{
			Displayer.displayText(bestID[i]+"\t"+bestScore[i]+"\t"+bestRealValue[i]+"\n");
		}
		Displayer.displayText("");
	}

	/**
	 * Tri des Top-K par ordre décroissant (opération 3 sur 3, unique) ;
	 * en effet, les add's entretiennent le Top-K en continu,
	 *   mais pas dans l'ordre.
	 *<P>NB - L'algo de tri, peu performant, fait l'affaire pour les Top-K,
	 *   dans la mesure où K est petit et que le tri n'est pas trop fréquent
	 *   (recherches successives de Top-K's).
	 */
	public void bubbleSort()
	{
		boolean cont=true;

		while (cont)
		{
			cont=false;

			for (int i=1;i<this.bestScore.length;i++)
			{
				if (this.bestScore[i-1]<this.bestScore[i])
				{
					// SWAP
					float tmp;
					float tmp_real;
					int tmpID;

					tmp=this.bestScore[i-1];
					tmpID=this.bestID[i-1];
					tmp_real=this.bestRealValue[i-1];//ajout PhP 2016/01/26-ma.

					this.bestScore[i-1]=this.bestScore[i];
					this.bestID[i-1]=this.bestID[i];
					this.bestRealValue[i-1]=this.bestRealValue[i];//ajout PhP 2016/01/26-ma.

					this.bestID[i]=tmpID;
					this.bestScore[i]=tmp;
					this.bestRealValue[i]=tmp_real;//ajout PhP 2016/01/26-ma.

					cont=true;
				}
			}
		}
	}


	/** Exemples.*/
	public static void main(String[] p) {

		System.out.println("Best 10 on 0-99");
		BestIndexFinderInOnePass myBest=new BestIndexFinderInOnePass(10);

		for (int i=0; i<100;i++) {
			myBest.add(i+i, i);
		}

		myBest.print();
		System.out.println("SORTING....");

		myBest.bubbleSort();

		myBest.print();
		System.out.println();


		System.out.println("generating 1000+");
		for (int i=1000; i>0;i--) {
			myBest.add(+i, i);
		}


		myBest.print();
		System.out.println("SORTING....");

		myBest.bubbleSort();
		myBest.print();
		System.out.println();


		System.out.println("generating 100 random(1000), keep 10 best ones");
		myBest = new BestIndexFinderInOnePass(10);

		for (int i=0; i<100;i++) {
			int random = (int) (Math.random()*1000);
			myBest.add(i+random, random);
		}

		myBest.print();

		System.out.println("SORTING....");

		myBest.bubbleSort();

		myBest.print();
	}//main
}//class


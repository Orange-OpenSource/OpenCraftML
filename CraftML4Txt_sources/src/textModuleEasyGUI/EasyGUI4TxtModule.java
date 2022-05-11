package textModuleEasyGUI;

//MiniCraftIHMEtude2PPSwing.java, 2020/10/03-Sa, PhP.



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout.Alignment;

import textModule.CraftML4Text_API;
import textModule.CraftML4Text_API_ScriptInterpretor;
import textModule.CraftML4Text_Params;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;


/**
 * Thanks to Philippe Porretta, Orange Labs 2020
 * 
 * Little Easy Graphic User Interface for the Text Module of CraftML4Txt
 * 
 * ----------------------------------------------------  French comments --------------------------
 * 
 * Petite IHM pour MiniCraftML, programmée en Java/Swing..
 * <P>Structure et Procédé</P>
 * <P>NB - <I>Les mots mis entre Guillemets sont notamment des mots à prendre dans le sens général,
 * 	et qui ne correspondent pas forcément à une classe en Java, ou pas à la classe que le nom évoque
 * (ex: ContentPane n'est pas une classe Java, bien qu'il en fasse partie du Vocabulaire ;
 * 	"Composant" n'est pas un {@link Component}, mais un composant graphique en général)</I>.
 * </P>
 * <UL>
 * <P><LI>Le <B>"Panneau" Principal de la Fenêtre</B> (fenêtre : {@link JFrame} ) de l'IHM, qui est nommé "ContentPane" est un {@link Container} ;
 * 		<BR>CF: Container {@link JFrame}.getContentPane() et {@link JFrame}.setContentPane( {@link Container} ).
 * 		<BR>NOTA - Le Container est le "Composant" de plus haut niveau contenant d'autres composants (cad avec un ADD("Composant")) :
 * 		un {@link Component} ne supporte pas le Add.
 * 		<BR>ATTENTION : <I>ne pas confondre {@link Component} avec {@link JComponent}, ce dernier supporte Add,
 * 			et {@link JPanel} en dérive </I>.
 * </LI></P>
 *
 * <P><LI>La <B>"ContentPane"</B> est organisée en <B>GRILLE</B>, selon un <B>{@link GridBagLayout}</B>, qui se contrôle par un {@link GridBagConstraints}.
 * 		Ca donne donc du genre "ContentPane".add("Composant", {@link GridBagConstraints} ).
 * 		C'est une chose très peu maniable...  Cf <I>GridBagDemo</I> dans Tutorial Java.  </LI></P>
 *
 * <P><LI>Cette Grille est de dimension <B>GRILLE [NB_PANNEAUX_AJOUTES] [1]</B> : une seule colonne.</LI></P>
 *
 * <P><LI><B>Chaque ligne de la grille contient un "Panneau"</B>, plus précisément : un {@link Container} - (la grille est mono-colonne).
 * 		<BR>Autrement dit, l'idée est d'ajouter un composant qui encapsule ce qu'on veut,
 * 		<BR>donc typiquement ajouter un {@link JPanel} (extends ... {@link JComponent} ... / ... {@link Container}) , mais pas un JButton par exemple, ce dernier sera encapsulé dans le JPanel.
 * 	</LI></P>
 *
 * <P><LI>
 * 		Le programmeur peut ajouter tous les panneaux qu'il veut à la grille.
 * 		<BR>Ceci se passe dans <B>buildinPart1LayoutPart3_ADD_COMPONENTS_TO_GRID()</B>.
 * </LI></P>
 * <P><LI>Un <B>Panneau Prédéfini</B> est le <B>{@link RequestPanel}</B>.
 * 		<BR>C'est une structure pour organiser certains composants (FileButton, FilePath, ActionButton).
 * 		<BR>Le RequestPanel prend en paramètres ces objets, qui sont créés par le programmeur dans le buildin Part Layout.
 * 		<BR>Il ne fait que les disposer dans un Panel.
 * </LI></P>
 * <P><LI><B>Bref : tous les panneaux ajoutés sont - sauf le RequestPanel - intégralement créés par le programmeur,
 * 		<BR>et sinon, pour le RequestPanel, tous les composants de ce dernier sont créés par le programmeur,
 * 				mais pas le RequestPanel lui-même, qui ne fait que les absorber</B>.
 * 		<BR>Ceci se passe dans <B>buildin Part Layout</B>, appelée par buildin(), appelée par le CSTR.
 * </LI></P>
 *
 * <P><LI></LI></P>
 *
 * </UL>
 *
 * <P>NOTA - DEV - <I>Le Ma 06/10/2020, Version à peu près propre et câblée - et documentée.
 * 		Quelques aménagements, ergonomiques notamment pour maintenir le Prog, peuvent rester à faire</I>...
 *
 * @author PhP.20201003Sa
 *
 */
public class EasyGUI4TxtModule extends JFrame implements ActionListener
{

	public CraftML4Text_API myAPI= new CraftML4Text_API();


	String globalPath=null;   //
	static final String modelName4txt="CraftTXT";


	static final String titleFrame="Easy PROTOTYPE Interface for CRAFTML4Txt version 3.2 ";
	//final private String        mmInfos                     ="  Default paramaters: 50 Trees, BranchFactor 10, ngramSize=4 (words & chars). Use Scripts to change defaults.";
	//final private String        mmInfos                     =" Version Spéciale F. Walsh. Paramaters: 100 Trees, BranchFactor 10, ngramSize=3 (words & chars), ONLY 3 PREDICTIONS. Use Scripts to change defaults.";



	/**
	 * Compteur du nombre de "Panneaux" ajoutés à la Grille de la "ContentPane" (le "Panneau" principal de l'IHM) ;
	 * c'est aussi l'indice (base Zero) de la ligne dans la Grille.
	 * <P>Plus précisément, on ajoute des {@link Container} 's à la "ContentPane", qui est organisé en grille par un {@link GridBagLayout}.
	 *
	 */
	private int                      mcNbComponentsAddedToContentPane       = 0;
	private final GridBagConstraints mg_GRIDBAG_CONSTRAINTS_FOR_CONTENTPANE = new ContentPaneGridBagConstraints();



	final private JTextArea     mgMessage                     = new JTextArea(); // Messages retours de l'API

	//
	//TODO éventuellement faire des classes spec pour Titre/InfoPanel, si comportements spécifiques communes répétitifs...
	//
	private JPanel              mgTitrePanel                  = null        ;   // panel haut pour le titre

	private RequestPanel        mgLearnRequestPanel           = null        ;    // panel pour le learn
	private RequestPanel        mgModelRequestPanel           = null        ;    // panel pour le Modèle
	private JPanel              mgInfoPanel                   = null        ;    // panel info dont modèle dispo oui/non	 //%%%TODO: VOIR  et aussi LABEL !!!

	private RequestPanel        mgPredictionRequestPanel      = null        ;    // panel pour prédiction
	private RequestPanel        mgEvaluationRequestPanel      = null        ;    // panel pour évaluation
	private RequestPanel        mgScriptRequestPanel          = null        ;    // panel pour script

	//  on fait quoi avec ça ?
	//	final private JPanel        mgBas                     = new JPanel();    // panel mgBas pour système à tenue de charge
	//	final private JPanel        mgPieds                   = new JPanel();    // pour zone message


	//détail panneaux

	//-- Bloc 0 : Titres
	final private String        mgLabelTextTitre              =  titleFrame;
	final private JLabel        mgLabelTitre                  = new JLabel(mgLabelTextTitre);

	//--- Bloc 1 : Learn
	final private ButtonFile    mgButtonSelectLearnFile       = new ButtonFile     ("set Learn File (I)");
	final private String        mgLabelTextLoadLearnFile      =                     "learn file is C:/myData/myFolder/...				" ;
	final private ButtonAction  mgButtonLearn                 = new ButtonAction   ("EXECUTE LEARN");


	//--- Bloc 2 : Model
	final private ButtonFile    mgButtonSelectModelPath       = new ButtonFile     ("set Model Path (I/O)");
	final private String        mgLabelTextModelPath          =                     "model file c:/myData/myFolder/..." ;
	final private ButtonAction  mgButtonModelLoad             = new ButtonAction   ("LOAD MODEL");
	final private ButtonAction  mgButtonModelSave             = new ButtonAction   ("SAVE MODEL");


	//--- Bloc 3 : infos diverses
	final private String        mgLabelTextInfo               = "infos diverses à afficher..." ; // à compléter
	 private JTextArea     mgLabelInfo                   = null; //new JTextArea(mgLabelTextInfo); // à compléter
//		final private JLabel        mgLabelInfo                   = new JLabel(mgLabelTextInfo); // à compléter

	//--- Bloc 4 : Prediction
	final private ButtonFile    mgButtonSelectPredictionFile  = new ButtonFile     ("set Prediction file (O)");
	final private String        mgLabelTextPreditionFilePath  =                     "prediction file c:/myData/myFolder/..." ;
	final private ButtonAction  mgButtonPrediction            = new ButtonAction   ("EXECUTE PREDICT");


	//--- Bloc 5 : Evaluation
	final private ButtonFile    mgButtonSelectEvaluationFile  = new ButtonFile     ("set Evaluation File (O)");
	final private String        mgLabelTextEvaluationFilePath =                     "Evaluation file c:/myData/myFolder/..." ;
	final private ButtonAction  mgButtonEvaluation            = new ButtonAction   ("EXECUTE EVAL");


	//--- Bloc 6 : Script
	final private ButtonFile    mgButtonSetScriptFile         = new ButtonFile     ("set script File (I)");
	final private String        mgLabelTextScriptFilePath     =                     "Script file c:/myData/myFolder/..." ;
	final private ButtonAction  mgButtonExecScript            = new ButtonAction   ("EXECUTE SCRIPT");

	//A faire plutôt dans buildin(), cause personalisation de choix.
	//

	///////////////////////////////////////////////////////////////////////
	//DIRECTORY INITIALE ET COURANTE POUR SUIVI DES FICHIERS @ FILEDIALOG//
	///////////////////////////////////////////////////////////////////////
	//
	private File mf_1_DefaultFileInitDir=null; //Dir "User" par exemple => A faire dans buildin().
	private File mf_2_CurrentFileInitDir=null; //mf_1_DefaultFileInitDir au départ.
	//à initialiser dès le départ à mf_1_DefaultFileInitDir une fois celle-ci connue.
	//Puis à la dir du dernier fichier ouvert par exemple
	//

	////////////////////////////////////
	//FICHIERS DES BOUTONS SELECT FILE//
	////////////////////////////////////
	private File mfLearnFilePath      =null;
	private File mfModelFilePath      =null;
	private File mfPredictionFilePath =null;
	private File mfEvaluationFilePath =null;
	private File mfScriptFileFilePath =null;


	final  private    JFileChooser mgFileChooser    = new JFileChooser();


	////////////////////////////////////////
	//DETAILS DE DIMENSIONNEMENT, LOOK ETC//
	////////////////////////////////////////
	/**
	 * Classe STATIC -
	 * REGLAGES de dimensions et autres, à caractère récurrent (ex : DIMENSIONs des composants d'un RequestPanel) ;
	 * NOTA - la taille de la fenêtre (et des composants) est (ici) réglée par le bas : d'où définition des tailles de composants inférieurs ;
	 * NOTA - contrairement à JavaFX, on ne peut pas régler séparément Hauteur/Largeur ;
	 * NOTA - les vars de réglages sont mises static, sinon elles ne passent pas toujours à l'utilisation.
	 */
	private static class Settings
	{
		//NB - si on ne les met pas static, elles ne passent pas. Bah...
		//
		private static int ms_Titles_WID=600;  //ça doit être pour les titres ...
		private static int ms_Titles_HIG= 60;

		private static int ms_ButtonFile_WID=200;
		private static int ms_ButtonFile_HIG= 50;

		private static int ms_FilePathField_WID=800;
		private static int ms_FilePathField_HIG= 30;
		
		/////////////
	//INFOPANEL//
	/////////////

	private static int INFOPANEL_SCROLLPANE_WID=Settings.ms_FilePathField_WID + Settings.ms_ButtonFile_WID;
	private static int INFOPANEL_SCROLLPANE_HIG=100;
	private static int INFOPANEL_TEXTAREA_NBCOLS=200;
	private static int INFOPANEL_TEXTAREA_NBROWS= 50;
	}

	/**
	 * Constructeur - Voir buildin(), appelée par CSTR qui fait le gros/détail des opérations.
	 */
	public  EasyGUI4TxtModule()
	{
		super();

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		CraftML4Text_Params paramDef= new CraftML4Text_Params();
		
		String mmInfo=" (default params: nbTrees:"+paramDef.numberOfTrees+", branchFactor:"+paramDef.branchFactor+", ngramWords/Chars:"+paramDef.maxWordNgram+"/"+paramDef.maxCharNgram;
		mmInfo=mmInfo+", sparsity:"+paramDef.sparsity+", nbPredictedLabels:"+paramDef.topNLabels;
		mmInfo=mmInfo+") => Use Scripts to use other params";
		setTitle("CraftML4Text "+mmInfo);
		//////////
		buildin();
		//////////
		pack();
		setVisible(true);

	}//CSTR

	/**
	 * Appelée par CSTR, la présente fonction buildin fait le gros/détail des opérations ;
	 * elle fait quelques calculs elle-même et appelle directement ou indirectement
	 * les fonctions buldinPart*** ; de même en cascade.
	 */
	private void buildin()
	{

		mf_1_DefaultFileInitDir=new File("./").getAbsoluteFile(); //ou Canonical ?   (subtile différence)
		mf_2_CurrentFileInitDir=mf_1_DefaultFileInitDir;

		buildinPart1Layout();
		buildinPart2Listeners();

	}//buildin

	private void buildinPart1Layout()
	{
		//buildinPart1LayoutMode02PP();  //layout général etc, et crée certains "composants à la main" ou spéciaux
		//
		buildinPart1LayoutPart1General();
		buildinPart1LayoutPart2SpecialComponents();
		//
		buildinPart1LayoutPart3_ADD_COMPONENTS_TO_GRID();
	}

	private void buildinPart1LayoutPart1General() //from ex-buildinPart1LayoutMode02PP
	{
		Container CONTENTPANE=null;

		CONTENTPANE=this.getContentPane();
		//JPanel CONTENTPANE=new JPanel();
		//setContentPane(CONTENTPANE);

		CONTENTPANE.setBackground(Color.gray); //pour

		GridBagLayout GBL=new GridBagLayout();
		CONTENTPANE.setLayout(GBL);

		//ceci intervient avec CONTENTPANE.add(item, GridBagConstraints);
		//GridBagConstraints GRIDBAG_CONSTRAINTS = new GridBagConstraints();
		//GridBagConstraints GRIDBAG_CONSTRAINTS = GBL.getConstraints(CONTENTPANE); //ou new GridBagConstraints();


	}//buildinPart1LayoutPartGeneral

	private void buildinPart1LayoutPart2SpecialComponents()  //from ex-buildinPart1LayoutMode02PP
	{
		//TODO éventuellement faire des classes spec pour Titre/InfoPanel, si comportements spécifiques communes répétitifs...

		//TITRE PANEL//
		mgLabelTitre.setOpaque(true);
		mgLabelTitre.setAlignmentX(Component.CENTER_ALIGNMENT); //sans effet : porter sur qui ???
		mgLabelTitre.setPreferredSize(new Dimension(Settings.ms_Titles_WID,Settings.ms_Titles_HIG));

		mgTitrePanel=new JPanel();
		mgTitrePanel.setLayout(new FlowLayout()); //ou Bx/X, voir
		mgTitrePanel.add(mgLabelTitre);

		//INFO PANEL//
	/*	mgLabelInfo.setOpaque(true);
		mgLabelInfo.setAlignmentX(Component.CENTER_ALIGNMENT); //sans effet : porter sur qui ???
		mgLabelInfo.setPreferredSize(new Dimension(Settings.ms_Titles_WID,Settings.ms_Titles_HIG));

		mgInfoPanel=new JPanel();
		mgInfoPanel.setLayout(new FlowLayout()); //ou Bx/X, voir
		mgInfoPanel.add(mgLabelInfo);  */

		//////////////
		//INFO PANEL//
	//////////////
	mgInfoPanel=new JPanel();
	mgInfoPanel.setLayout(new FlowLayout()); //ou Bx/X, voir
	//ATT C'EST UNE JTEXTAREA MAINTENANT !!! => VOIR VARS DE CLASSE. 
	mgLabelInfo=new JTextArea(Settings.INFOPANEL_TEXTAREA_NBROWS, Settings.INFOPANEL_TEXTAREA_NBCOLS);  //L,C ! (et non C,L)
	//JScrollPane scrollPane = new JScrollPane(mgLabelInfo);
	JScrollPane scrollPane = new JScrollPane(mgLabelInfo, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS); //c'est par défait, mais là c'est +sûr !
	scrollPane  .setPreferredSize(new Dimension(Settings.INFOPANEL_SCROLLPANE_WID, Settings.INFOPANEL_SCROLLPANE_HIG));
	mgInfoPanel.add(scrollPane, BorderLayout.CENTER);
	}//buildinPart1LayoutPartSpecialComponents


	/**
	 * La FONCTION CENTRALE, qui ajoute tous les composants (essentiellement les RequestPanel's)
	 * à la Grille de la ContentPane (alias Panneau Principal) ;
	 * ATTENTION IMPORTANT - <B>les RequestPanel's sont créés ici dans la présente fonction
	 * (ie: leurs vars de classe définies ici) ;
	 * MAIS les composants qu'on donne au RequestPanel,
	 * ainsi que d'autres composants ajoutés à la grille
	 * sont déjà fabriqués
	 * ailleurs dans le buildin (et stockés en vars de classe)</B>.
	 * <P>VOIR : this::(RequestPanel addToGridNewRequestPanel(ButtonFile aFileButton, String aInitialFileFieldText, ButtonAction... aButtonActionAppendedList)).
	 *
	 */
	private void buildinPart1LayoutPart3_ADD_COMPONENTS_TO_GRID()  //TODO remplace buildinPartAddRequestPanels
	{
		//=>on remettra ça dans les variables d'origine ... voir plus bas.
		RequestPanel mg_RQT_LEARN  =null;
		RequestPanel mg_RQT_MODEL  =null;
		RequestPanel mg_RQT_PRED   =null;
		RequestPanel mg_RQT_EVAL   =null;
		RequestPanel mg_RQT_SCRIPT =null;

		//NB : certains panels sont déjà définis (ce ne sont pas des RequestPanel's).
		//=} mgTitrePanel, mgInfoPanel ... @ buildin...().

		//TITRE//
		addToGridSomeContainer(mgTitrePanel);   //déjà défini (dans un buildLayout...).


		//LEARNFILE//
		mg_RQT_LEARN  = addToGridNewRequestPanel(mgButtonSelectLearnFile      , mgLabelTextLoadLearnFile        , mgButtonLearn);

		//MODEL//
		mg_RQT_MODEL  = addToGridNewRequestPanel(mgButtonSelectModelPath      , mgLabelTextModelPath            , mgButtonModelLoad, mgButtonModelSave);


		//INFO//
		addToGridSomeContainer(mgInfoPanel);   //déjà défini (dans un buildLayout...).


		//PREDICTION//
		mg_RQT_PRED   = addToGridNewRequestPanel(mgButtonSelectPredictionFile , mgLabelTextPreditionFilePath    , mgButtonPrediction);

		//EVALUATION//
		mg_RQT_EVAL   = addToGridNewRequestPanel(mgButtonSelectEvaluationFile , mgLabelTextEvaluationFilePath   , mgButtonEvaluation);

		//SCRIPT//
		mg_RQT_SCRIPT = addToGridNewRequestPanel(mgButtonSetScriptFile        , mgLabelTextScriptFilePath       , mgButtonExecScript);

		//=>on remet ça dans les variables d'origine ...
		mgLearnRequestPanel          = mg_RQT_LEARN        ;    // panel pour le learn
		mgModelRequestPanel          = mg_RQT_MODEL        ;    // panel pour le Modèle
		mgPredictionRequestPanel     = mg_RQT_PRED         ;    // panel pour prédiction
		mgEvaluationRequestPanel     = mg_RQT_EVAL         ;    // panel pour évaluation
		mgScriptRequestPanel         = mg_RQT_SCRIPT       ;    // panel pour script

	}//</buildinPartAddRequestPanels>

	/**
	 * Ajoute les Listeners aux Composants concernés.
	 */
	private void buildinPart2Listeners()
	{
		//  éléments activables : TODO A COMPLETER

		buildinPart2ListenersPart1File();
		buildinPart2ListenersPart2Action();

	}//buildinPartListener

	/**
	 * Ajoute les Listeners aux Boutons Select File.
	 */
	private void buildinPart2ListenersPart1File()
	{
		//  éléments activables : TODO A COMPLETER

		mgButtonSelectLearnFile      .addActionListener(this);
		mgButtonSelectModelPath      .addActionListener(this);
		mgButtonSelectPredictionFile .addActionListener(this);
		mgButtonSelectEvaluationFile .addActionListener(this);
		mgButtonSetScriptFile        .addActionListener(this);

	}//buildinPartListener

	/**
	 * Ajoute les Listeners aux Boutons d'Action (Execute Learn etc).
	 */
	private void buildinPart2ListenersPart2Action()
	{
		//  éléments activables : TODO A COMPLETER

		mgButtonLearn      .addActionListener(this);
		mgButtonModelLoad  .addActionListener(this);
		mgButtonModelSave  .addActionListener(this);
		mgButtonPrediction .addActionListener(this);
		mgButtonEvaluation .addActionListener(this);
		mgButtonExecScript .addActionListener(this);

	}//buildinPartListener


	/**
	 * FABRIQUE+RETOURNE UN RequestPanel A PARTIR DES COMPOSANTS ET AUTRES ELEMENTS DEJA CALCULES AILLEURS DANS LE buildin() ;
	 * il ne restera plus qu'à l'ajouter à la Grille de la ContentPane (alias Panneau Principal),
	 * via la fonction <I>buildinPart1LayoutPart3_ADD_COMPONENTS_TO_GRID()</I>.
	 *
	 * @param aFileButton Bouton FILE.
	 * @param aInitialFileFieldText  Texte du FILE pour la Bande de Texte à cet effet.
	 * @param aButtonActionAppendedList Liste "OPEN", "NON DELIMITEE" (APPEND), des Boutons ACTION ( <I>add(butFile, text, butAction1, ButAction2, ...)</I> ).
	 * @return
	 */
	private RequestPanel addToGridNewRequestPanel(ButtonFile aFileButton, String aInitialFileFieldText, ButtonAction... aButtonActionAppendedList)
	{
		RequestPanel RQT=new RequestPanel();

		RQT.addFileButton(aFileButton);
		RQT.setFilePathText(aInitialFileFieldText);

		for(ButtonAction BUT : aButtonActionAppendedList)  //LISTE "OPEN" DES Boutons Action.
		{
			RQT.addActionButton(BUT);
		}

		addRequestPanel(RQT);

		return RQT;
	}

	/**
	 * <B>Pour ajouter un {@link Container} (Conteneur, ex: JPanel) à la Grille[NROWS=(open)][NCOLS=1]</B> (un {@link GridBagLayout} en l'occurrence) de la ContentPane ;
	 * NB - le Container est la classe la plus haute pouvant contenir des composants ;
	 * typiquement ce sera un JPanel, mais pas un Button ou ce genre (composant final) ; =&gt; donc : pas de addComponent ;
	 * <B>l'idée est que : on ajoutera à la Grille des "Panneaux" qui contiendront ce qu'il faut, tq JPanel, mais pas de "composants" terminaux tq Button ;
	 * les {@link GridBagConstraints} utilisées sont les mêmes pour tous les {@link Container}'s ajoutés</B>.
	 * @param aContainer
	 */
	private void addToGridSomeContainer(Container aContainer)  //component, Container ???
	{
		int GP_INDICE_ROW=mcNbComponentsAddedToContentPane;
		mg_GRIDBAG_CONSTRAINTS_FOR_CONTENTPANE.gridy=GP_INDICE_ROW;
		this.getContentPane().add(aContainer, mg_GRIDBAG_CONSTRAINTS_FOR_CONTENTPANE);
		mcNbComponentsAddedToContentPane++;
		//return aComponent; //desfois qu'on factorise ?  //sinon, laisser void.
	}

	/**
	 * <B>Ajoute nommément un RequestPanel</B> ; en fait, fait juste
	 * un renvoi vers =&gt; <B>addToGridSomeContainer(Container)</B>.
	 */

	private void addRequestPanel(RequestPanel aRequestPanel)   //TODO => addToGridSomeComponent ?
	{
		addToGridSomeContainer(aRequestPanel); //en fait on ne fait rien de spécial pour le RequestPanel !

		//int GP_INDICE_ROW=mcNbComponentsAddedToContentPane;
		//mg_GRIDBAG_CONSTRAINTS_FOR_CONTENTPANE.gridy=GP_INDICE_ROW;
		//this.getContentPane().add(aRequestPanel, mg_GRIDBAG_CONSTRAINTS_FOR_CONTENTPANE);
		//mcNbComponentsAddedToContentPane++;
	}


	//===================================     Aiguillage général des EVENTS =================================

	// TODO juste préparé et testé le learn et l'éval pour le moment


	/**
	 * Fonction des Réponses aux Boutons FILE et ACTION (et autres s'il en est) ;
	 * pour les boutons FILE, c'est le fichier correspondant (var de classe, ex: mfLearnFilePath) qui est défini via le JFileChooser ;
	 * pour les boutons ACTION, les fonctions d'exécution concernées sont appelées (ex: doLearn(learnfile), etc).
	 * <P>NOTA - les réponses dirigent les résultats en fonction du bouton concerné ;
	 * par exemple si la source de Event est le Bouton Select LearnFile (var de classe mgButtonSelectLearnFile) ,
	 * alors, la clause concernée if(Event::getSource()==buttonfile)  dirige automatiquement
	 * pour actualiser le fichier LearnFile (var de classe mfLearnFilePath).
	 */
	public void actionPerformed(ActionEvent EVT)   //obligatoirement public !
	{
		//Object x=e.getSource();

		if(false)
		{}
		////////
		//FILE//
		////////

		else if (EVT.getSource()==mgButtonSelectLearnFile      ) {File FILE=doGetFileByDialog("LEARN FILE         ".trim(), mgLearnRequestPanel      ) ; System.out.println("***FILE=["+FILE+"]"); if(FILE!=null){ mfLearnFilePath      = FILE; mgLearnRequestPanel      .setFilePathText(mfLearnFilePath      );}}
		else if (EVT.getSource()==mgButtonSelectModelPath      ) {File FILE=doGetFileByDialog("MODEL FILE         ".trim(), mgModelRequestPanel      ) ; System.out.println("***FILE=["+FILE+"]"); if(FILE!=null){ mfModelFilePath      = FILE; mgModelRequestPanel      .setFilePathText(mfModelFilePath      );}}
		else if (EVT.getSource()==mgButtonSelectPredictionFile ) {File FILE=doGetFileByDialog("PREDICTION FILE    ".trim(), mgPredictionRequestPanel ) ; System.out.println("***FILE=["+FILE+"]"); if(FILE!=null){ mfPredictionFilePath = FILE; mgPredictionRequestPanel .setFilePathText(mfPredictionFilePath );}}
		else if (EVT.getSource()==mgButtonSelectEvaluationFile ) {File FILE=doGetFileByDialog("EVALUATION FILE    ".trim(), mgEvaluationRequestPanel ) ; System.out.println("***FILE=["+FILE+"]"); if(FILE!=null){ mfEvaluationFilePath = FILE; mgEvaluationRequestPanel .setFilePathText(mfEvaluationFilePath );}}
		else if (EVT.getSource()==mgButtonSetScriptFile        ) {File FILE=doGetFileByDialog("SCRIPT FILE        ".trim(), mgScriptRequestPanel     ) ; System.out.println("***FILE=["+FILE+"]"); if(FILE!=null){ mfScriptFileFilePath = FILE; mgScriptRequestPanel     .setFilePathText(mfScriptFileFilePath );}}

		//////////
		//ACTION//
		//////////

		else if (EVT.getSource()==mgButtonLearn      ) {doLearn      (mfLearnFilePath      );}
		else if (EVT.getSource()==mgButtonModelLoad  ) {doLoadModel  (mfModelFilePath      );}
		else if (EVT.getSource()==mgButtonModelSave  ) {doSaveModel  (mfModelFilePath      );}
		else if (EVT.getSource()==mgButtonPrediction ) {doPredict    (mfPredictionFilePath );}
		else if (EVT.getSource()==mgButtonEvaluation ) {doEval       (mfEvaluationFilePath );}
		else if (EVT.getSource()==mgButtonExecScript ) {doExecScript (mfScriptFileFilePath );}

		//
		else{
			System.out.println("MiniCraftIHMEtude2PPSwing.actionPerformed(EVT) - UNKNOWN SOURCE (EVT="+EVT+")");
		}
	}

	//================================= fonctions IHM  ======================================


	/**
	 * Ramène un fichier et gère le "suivi" des fichiers pour ne pas recommencer les recherches à Zero (cf mf_2_CurrentFileInitDir) ;
	 * la fonction ets utilisée dans this::actionPerformed(ActionEvent), qui se charge d'attribuer les fichiers en fonction du bouton concerné ;
	 * bref,il est inutile de définir une fonction doGetFileByDialog spécifique pour chaque "REQUÊTE (RequestPanel).
	 * @param aSelectionTitle
	 * @param aRequestPanel
	 * @return
	 */

	public File doGetFileByDialog(String aSelectionTitle, RequestPanel aRequestPanel)
	{
		//NOTA - la fonction (qui est assez générale) pourrait être static modulo quelques révisions du prog.

		System.out.println("select "+aSelectionTitle);  //exemple

		mgFileChooser.setDialogTitle(aSelectionTitle); //TODO FileChooser comme var de classe est inutile.

		mgFileChooser.setSelectedFile(mf_2_CurrentFileInitDir);  //en espérant Dir/EndFile indifférent !

		File SELECTED_FILE=null;
		 //absolute path...  //=>à faire plutôt en dehors selon besoin => anecdotique ici

		int returnVal = mgFileChooser.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) //=>Selected File supposé non Null !!!
		{
			SELECTED_FILE = mgFileChooser.getSelectedFile();
			globalPath=SELECTED_FILE.getAbsolutePath();  //anecdotique ici.

			System.out.println("Selecting=>[" + globalPath+"]");
		} else {
			System.out.println("select "+aSelectionTitle+" cancelled by user.");
		}

		///SUIVI DE FICHIER - on pourrait raffiner le suivi par famille de fichier =>+compliqué !
		//Si les fichiers ne sont pas dans la même dir, alors, le suivi rique d'apporter assez peu ...
		//mais on peut supposer que ce sera "toujours" mieux que de repartir de zero ...
		//
		if(SELECTED_FILE != null){
			if(SELECTED_FILE.isDirectory())
			{mf_2_CurrentFileInitDir=SELECTED_FILE;}
			else {mf_2_CurrentFileInitDir=SELECTED_FILE.getParentFile();}
		}else {}

		return SELECTED_FILE; //qui peut être null !!!

	}//</doGetFileByDialog>



	// ================================================  boutons lançant des action de l'API ==========================


/*	
	MESSAGE DE FRANK
	------------------------

	EN FAIT : il faudrait charger les paramètres plutôt depuis les champs textes, qui sont remplis par les file sélecteurs, mais qui peuvent aussi être saisis à la main
	 certains seront saisis à la main : les modèles notamment
*/
	 
	private void doLearn(File pathData          ) 	
	{
		//System.out.println("doLearn      FILE=["+getFileSmartPath(mfLearnFilePath      )+"]  ---   A cabler vers CraftML            ".trim());
		//String learnFilePath=getFileSmartPath(mfLearnFilePath);

		//String learnFilePathFromClassVar=null;
		
		//if(mfLearnFilePath!=null)
			//{learnFilePathFromClassVar=mfLearnFilePath.getAbsolutePath();}


		// learnFilePath=    je cherche le champ texte
		
		String learnFilePathFromTextField=mgLearnRequestPanel.getFilePathTextFromTextField();
		
		//System.out.println("CTL - MiniCraftIHMEtude2PPSwing::doLearn(filepath) => FILEPATH FROM CLASSVAR  : learnFilePathFromClassVar  ="+learnFilePathFromClassVar  +";");
		//System.out.println(".................................................. => FILEPATH FROM TEXTFIELD : learnFilePathFromTextField ="+learnFilePathFromTextField +";");
		String result=myAPI.file_learnModelFromFile(learnFilePathFromTextField);

		System.out.println("Trace : learning with dataset: "+learnFilePathFromTextField);
		
		mgLabelInfo.setText(result);
	}


	private void doPredict (File pathDataToPredic  ) {
		String result;
		if (myAPI.myModel!=null) {
		String predictFilePath=mgPredictionRequestPanel.getFilePathTextFromTextField();
		System.out.println("Trace : predict on:"+predictFilePath);
		
		result=myAPI.file_predictOnInteractiveFile(predictFilePath);
		} else {
			result="NO MODEL AVAILABLE: LEARN OR LOAD A MODEL FIRST";
		}
		
		mgLabelInfo.setText(result);
	}

	private void doLoadModel (File pathModel         ) {
		String modelFilePath=mgModelRequestPanel.getFilePathTextFromTextField();
		System.out.println("Trace : loading model: "+modelFilePath);
		//System.out.println("Faut récupérer la chaine, car le fichier n'existe pas sous une forme unique, on rentre que son début");
		String suffix="_parameters.txt"+"\n";
		modelFilePath=modelFilePath+"\n";
		if (!modelFilePath.endsWith(suffix)) {
			mgLabelInfo.setText("please select a parameters.txt file,\n current is <"+modelFilePath+">");
			return;
		}
		modelFilePath=modelFilePath.replaceAll(suffix, "");
		
		suffix=myAPI.modelForTextExtension;
		
		modelFilePath=modelFilePath.replaceAll(suffix, "");
		
		String result=myAPI.file_loadModel(modelFilePath);
		mgLabelInfo.setText(result);
	}

	private void doSaveModel (File pathModel         ) {
		String modelFilePath=mgModelRequestPanel.getFilePathTextFromTextField();
		System.out.println("Trace: saving model:"+modelFilePath);
		//System.out.println("Faut récupérer la chaine, car le fichier n'existe pas sous une forme unique, on rentre que son début");
		String result=myAPI.file_saveModel(modelFilePath);
		mgLabelInfo.setText(result);
	}

	private void doEval  (File pathEvalFile      ) {
		String result;
		if (myAPI.myModel!=null) {
		String evalFilePath=mgEvaluationRequestPanel.getFilePathTextFromTextField();
		System.out.println("Trace : eval on:"+evalFilePath);
		result=myAPI.file_eval_precision(evalFilePath);
		} else {
			result="NO MODEL AVAILABLE: LEARN OR LOAD A MODEL FIRST";
		}
		mgLabelInfo.setText(result);
	}

	private void doExecScript  (File pathScriptFile    ) {
		String execFilePath=mgScriptRequestPanel.getFilePathTextFromTextField();
		System.out.println("Trace : exec script file: "+execFilePath);
		CraftML4Text_API_ScriptInterpretor myInterpret= new CraftML4Text_API_ScriptInterpretor();
		String result=myInterpret.interpretor(execFilePath);
		mgLabelInfo.setText(result);
		
	}


	/* FILE SERAIT MIEUX !!!
		private void doLearn           (String pathData          ) {System.out.println("doLearn  ---   A cabler vers CraftML            ".trim());}
		private void doPredict         (String pathDataToPredic  ) {System.out.println("doPredict  ---   A cabler vers CraftML          ".trim());}
		private void doLoadModel       (String pathModel         ) {System.out.println("doLoadModel  ---   A cabler vers CraftML        ".trim());}
		private void doSaveModel       (String pathModel         ) {System.out.println("doLoadModel  ---   A cabler vers CraftML        ".trim());}
		private void doEval            (String pathEvalFile      ) {System.out.println("doEval  ---   A cabler vers CraftML             ".trim());}
		private void doExecScript      (String pathScriptFile    ) {System.out.println("doExecScript  ---   A cabler vers CraftML       ".trim());}
	 */


	public static String getFileSmartPath(File FILE)
	{
		String TXT=null;

		if(false){}
		else if(FILE==null){TXT=null;} //TODO mieux ?
		else if(FILE.isDirectory())  {TXT=""+FILE;}
		else if(FILE.isFile())  {TXT="[<"+FILE.getName()+"> @ <"+FILE.getParentFile()+"> ]";}
		else{
			//ça ne devrait pas se produire !!! En principe (cf javadoc(File), tout ce qui n'est pas Directory est File (EndFile) !!!
			//Toutefois, pour un fichier "abstrait" (au sens de non existant), ce n'est pas évident !!!
			//puisque le système ne sait pas encore ce que c'est comme type de fichier réellement !
			TXT+=FILE;
		}
		return TXT;
	}

	////////
	//MAIN//
	////////
	public static void main (String[] p)
	{
		
			
		new EasyGUI4TxtModule();
	}

	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	//DEB - CLASSES INTERNES
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	//
	private class ContentPaneGridBagConstraints extends GridBagConstraints
	{
		public ContentPaneGridBagConstraints() {super(); buildin();}

		private void buildin()
		{
			//VOIR : GridBagLayoutDemo : ils arrivent à contenir toutes les rangées en haut, sauf la dernière volontairement sur le bas...
			//
			this.fill = GridBagConstraints.HORIZONTAL;
			//this.gridx = 0;  //CASE/X //indices : base zero
			//this.gridy = 0;  //CASE/Y //indices : base zero
			this.ipadx = 12; //effet ??? none !
			this.ipady = 12; //effet ??? none !
			//this.anchor = GridBagConstraints.PAGE_START; //sans effet
			this.weighty = 0.5; //0.5; //bah ça semble accrocher en haut !!! (0.5) ou 1 //0: les RQT restent collés.
		}
	}//</class ContentPaneGridBagConstraints>

	/**
	 * Bouton pour Select File ; ses spécificités sont juste de Display/Layout ;
	 * notamment il est dimensionné à la base, pour des raisons d'alignement régulier ;
	 * la classe permet aussi une identification plus serrée de cet Item.
	 */
	private class ButtonFile extends JButton
	{
		private Color      mgColor = Color.green;
		private Dimension  mgSize  = new Dimension(Settings.ms_ButtonFile_WID,Settings.ms_ButtonFile_HIG); //W,H //il s'en fiche avec GridLayout Moed Initial !

		public ButtonFile(String aTitle)
		{
			super(aTitle);
			//
			setBackground(mgColor);
			setPreferredSize(mgSize);
			setMinimumSize(mgSize);
			setMaximumSize(mgSize);
		}//cstr
	}

	/**
	 * Cette classe permet uniquement une identification plus serrée de cet Item ;
	 * à part cela, le bouton n'a aucune spécificité (autre donc que sa classe) ;
	 * son dimensionnement n'est pas nécessaire (pas de contrainte d'alignement global).
	 */
	private class ButtonAction extends JButton
	{
		//pas de Dim préimposée, pas besoin d'alignement vertical ! de plus, centrés !!!

		//Dimension BUT_EXEC_SIZE=new Dimension(100,30);

		public ButtonAction(String aTitle){
			super(aTitle);
			//setPreferredSize(BUT_EXEC_SIZE);
			//setMinimumSize(BUT_EXEC_SIZE);
			//setMaximumSize(BUT_EXEC_SIZE);
		}
	}

	/**
	 * La Bande de Texte qui contient le FILE sélectionné par le Bouton FILE ;
	 * sa seule spécificité est d'être dimensionnée pour cause d'alignement visuel global.
	 */
	private class FilePathField extends JTextField
	{
		public FilePathField() {
			super();
			buildin();
		}
		private void buildin() {
			setBackground(Color.white);
			setOpaque(true);
			Dimension SIZE_RGT=new Dimension(Settings.ms_FilePathField_WID, Settings.ms_FilePathField_HIG);
			setPreferredSize(SIZE_RGT);
			setMinimumSize(SIZE_RGT);
			setMaximumSize(SIZE_RGT);
		}
	}

	/**
	 * Le RequestPanel ou Panneau de Requête, item principal de la grille,
	 * composé d'un bouton Select File, d'une bande de texte indiquant le fichier,
	 * et de un ou plusieurs Boutons d'Action (autre que setfile) (Tq Execute Learn etc) ;
	 * c'est un JPanel spécifique, élément récurrent principal de la grille ;
	 * NOTA - les composants "actifs" du RequestPanel sont exogènes (ex : Bouton) (*1)
	 *<P>(*1) Composants et autres éléments EXOGENES (fabriqués dans le buildin de la classe principale).
	 * <UL>
	 * 	  <LI>Le Bouton FILE est amené de l'extérieur.</LI>
	 * 	  <LI>Le Texte de la Bande de Texte est évidemment exogène (mais pas le JTextField ou similaire).</LI>
	 * 	  <LI><Les Boutons "Action" sont amenés de l'extérieur./LI>
	 *</UL>
	 * <P>NOTA - La structure du RequestPanel est calculée pour réussir à aligner proprement les panneaux,
	 *    en conjonction avec un GridBagLayout, qui n'est pas une chose très maniable.
	 */
	private class RequestPanel extends JPanel
	{
		//RequestPanel RQT004=RequestPanel.this;

		private LFTPanel LFT=null;
		private RGTPanel RGT=null;

		/**
		 * CSTR - à A vide - Nota : <I>il pourrait contenir directement tous les éléments exogènes (Button File etc) ;
		 * toutefois dans la classe principale, on a une fonction qui revient au même
		 * (RequestPanel addToGridNewRequestPanel(ButtonFile, String, ButtonAction...)) ;
		 * TODO - ceci peut si besoin faire l'objet d'une révision du prog, ce qui supprimerait des fonctions</I>...
		 */
		public RequestPanel()
		{
			super();
			buildin();
		}

		private void buildin()
		{
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			LFT=new LFTPanel();
			RGT=new RGTPanel();
			add(LFT);
			add(RGT);
		}

		public void addFileButton(ButtonFile aButtonFile) {LFT.add(aButtonFile);}

		public void setFilePathText(String aFilePathText) {RGT.setFilePathText(aFilePathText);}
		public void setFilePathText(File aFilePath) {
			String TEXT=""+aFilePath;  //TODO trouver mieux  (get path, absolute path canonical ...)
			setFilePathText(TEXT);
		}
		
			/**
			 * <B>Retourne le Texte (trimé) du TextField de FilePath</B>.
			 * @return
			 */
		public String getFilePathTextFromTextField() {return RGT.getFilePathText().trim();}  //déjà trimé en fait

		public void addActionButton(ButtonAction aActionButton) {RGT.addActionButton(aActionButton);}

		//public void addFileButton(String aTitle) {
		//	ButtonFile BUT=new ButtonFile(aTitle);
		//	LFT.add(BUT);
		//}

		private class LFTPanel extends JPanel
		{
			public LFTPanel(){
				super();
			}
		}

		private class RGTPanel extends JPanel
		{
			private JPanel TOP=new JPanel();
			JPanel BOT=new JPanel();

			private FilePathField FPT=new FilePathField();

			public void setFilePathText(String aFilePathText) {FPT.setText(aFilePathText.trim());}
			public void addActionButton(ButtonAction aActionButton) {BOT.add(aActionButton);}
			
			public String getFilePathText() {return FPT.getText().trim();}

			public RGTPanel(){
				super();
				setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
				//BOT.setLayout(new BoxLayout(BOT,BoxLayout.X_AXIS)); //pourrait éviter de replier la liste ds boutons.
				BOT.setLayout(new FlowLayout()); //ceci suffit et centre les boutons.
				TOP.add(FPT); //le JTextField est encapsulé !
				add(TOP);
				add(BOT);
			}

		}
	}//</class RequestPanel>

	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	//FIN - CLASSES INTERNES
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	//


}//</class MiniCraftIHMEtude2PPSwing>


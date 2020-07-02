package textModule;

import java.util.ArrayList;

import FilesManagement.SmallItem;
import utils.MessManager;

public class SmallParserOLD {


	public int wordNgramSize=3 ; // default size

	public int letterNgramSize=0; // default size, 0 for no letterNgram

	//public boolean addLetterNgram=true; // d�fault

	public boolean caseSensitive=false; // default

	public boolean deletePunctuation=false; // default


	/**
	 * insert blank between classical separator EXCEPT underscore
	 * TODO : do not do this for number (digit left and digit right)
	 * TODO : note that [] and {} are not segmented (not classical separators)
	 * @param input
	 * @return
	 */

	public String getSeparatorSegmentation(String input) {
		String result=input;
		result=result.replace("!", " ! ");
		result=result.replace("?", " ? ");

		result=result.replace(";", " ; ");
		result=result.replace(",", " , ");  
		result=result.replace(":", " : ");
		result=result.replace(".", " . ");

		result=result.replace("(", " ( ");
		result=result.replace(")", " ) ");
		result=result.replace("[", " [ ");
		result=result.replace("]", " ] ");
		result=result.replace("{", " { ");
		result=result.replace("}", " } ");

		result=result.replace("=", " = ");
		result=result.replace("+", " + ");
		result=result.replace("*", " * ");
		result=result.replace("-", " - ");
		result=result.replace("/", " / ");

		result=result.replace("\"", " \" ");
		result=result.replace("\'", " \' ");

		return result;

	}


	public String getWithoutSeparator(String input) {
		String result=input;
		result=result.replace("!", " ");
		result=result.replace("?", " ");

		result=result.replace(";", " ");
		result=result.replace(",", " ");  
		result=result.replace(":", " ");
		result=result.replace(".", " ");

		result=result.replace("(", " ");
		result=result.replace(")", " ");
		result=result.replace("[", " ");
		result=result.replace("]", " ");
		result=result.replace("{", " ");
		result=result.replace("}", " ");

		result=result.replace("=", " ");
		result=result.replace("+", " ");
		result=result.replace("*", " ");
		result=result.replace("-", " ");
		result=result.replace("/", " ");

		result=result.replace("\"", " ");
		result=result.replace("\'", " ");

		return result;

	}

	/**
	 * Replace forbidden characters (separators used for saving/loading models)
	 * TABS becomes invisible (replaced bu space)
	 */
	public String replaceForbiddenChars(String s) {
		String result=s.replace("=", " equals ");
		result=result.replace("\t", " ");
		result=result.replace(";", " semicolon ");
		return result;
	}



	/**
	 * returns null if input is null or empty
	 * @param input
	 * @return
	 */
	public String[] getWordSequence(String input) {
		if (input==null) {
			return null;
		}
		if (input.length()<1) {
			return null;
		}
		input=getNormalizedStringWithoutDoubleSpace(input);
		String[] result=input.split(" ");
		return result;
	}

	/**
	 * returns null if input is null or empty
	 * @param input
	 * @return
	 */
	public String[] getLetterSequence(String input) {
		if (input==null) {
			return null;
		}
		if (input.length()<1) {
			return null;
		}
		input=getNormalizedStringWithoutDoubleSpace(input);
		input=input.replace(" ", "_");             //====================  spaces are replaced with underscores
		String[] result= new String[input.length()];
		for (int i=0;i<input.length();i++) {
			result[i]=input.substring(i, i+1);
		}

		return result;
	}

	/**
	 * The normalized String has no double space, 
	 *    and has separators for words (separated by UNDERSCORE)
	 *  Manage case sensitive Y/N and punctuation deletion Y/N
	 *  Replace forbidden characters (used as separator for saving/loading models)
	 * returns null if input is null or empty
	 * @param input
	 * @return
	 */
	public String getNormalizedStringWithoutDoubleSpace(String s) {
		if (s==null) {
			return null;
		}
		if (s.length()<1) {
			return null;
		}
		if (!caseSensitive) {
			s=s.toLowerCase();
		}
		if (deletePunctuation) {
			s=getWithoutSeparator(s);
		} else {
			s=getSeparatorSegmentation(s);
		}
		s=replaceForbiddenChars(s);
		while (s.contains("  ")) {
			s=s.replace("  ", " ");
		}
		s=s.trim();
		
		return s;
	}


	public static void printSeq(String[] seq) {
		for (int i=0;i<seq.length;i++) {
			System.out.println(i+" ["+seq[i]+"]");
		}
	}

	/**
	 * Return the ngrams of a sequence ; the junction character is the underscore in the case of words
	 * @param sequence
	 * @param sizeMax
	 * @return
	 */
	public ArrayList<String> getNgramsFromSequence(String[] sequence, int sizeMax, boolean wordJunction) {
		if (sequence==null) {
			MessManager.sayError("getNgrams sequence null");
		}
		if (sequence.length<1) {
			MessManager.sayError("getNgrams sequence size<1");
		}
		if ((sizeMax<1) || (sizeMax>10)) {
			MessManager.sayError("getNgrams sizeMax ngram <1 or >10, sizeMax:"+sizeMax);
		}
		ArrayList<String> result=new ArrayList<String>();
		for (int i=0;i<sequence.length;i++) {
			String ngram=sequence[i];
			result.add(ngram);
			for (int j=2; j<=sizeMax;j++) {
				int indexToAdd=i+j-1;
				//if (indexToAdd<sequence.length-1) {
				if (indexToAdd<sequence.length) {
					if (wordJunction) {
						ngram=ngram+"_"+sequence[indexToAdd];
					} else {
						ngram=ngram+sequence[indexToAdd];
					}
				}
				result.add(ngram);
			}
		}
		return result;
	}



	public SmallItem getNgramParsing(String s) {
		if (s==null) {
			MessManager.sayError("getNgramParsing s null");
		}
		
		// encapsulation //
		s="[B] "+s+" [E]";
		
		
		
		if (s.length()<1) {
			MessManager.sayWarning("getNgramParsing s size<1");
		}
		ArrayList<String> ngramsWords= new ArrayList<String>();
		String[] sequenceW=getWordSequence(s);
		// ===========  System.out.println("word seq: ");
		// ===========  printSeq(sequenceW);
		ngramsWords=getNgramsFromSequence(sequenceW, wordNgramSize,true);

		if (letterNgramSize>=1) {
			ArrayList<String> ngramsLetters= new ArrayList<String>();
			String[] sequenceL=getLetterSequence(s);
			// =========  System.out.println("letterSeq: ");
			// =========  printSeq(sequenceL);
			ngramsLetters=getNgramsFromSequence(sequenceL, letterNgramSize,false);
			ngramsWords.addAll(ngramsLetters);
		}
		SmallItem result= new SmallItem();
		for (int i=0;i<ngramsWords.size();i++) {
			result.putKeyValue(ngramsWords.get(i), 1);
		}
		return result;
	}


	/**
	 * return an array of labels separated by usuals separators 
	 *  comma
	 *  space
	 *  tabulation
	 *  semicolon
	 *  check is case sensitive
	 * @param s
	 * @return
	 */
	public String[] getListOfSeparatedLabels(String s) {

		if (s==null) {
			return null;
		}
		if (!caseSensitive) {
			s=s.toLowerCase();
		}
		s=s.replace("\t", " ");
		s=s.replace(";"," ");
		s=s.replace(","," ");
		while (s.contains("  ")) {
			s=s.replace("  ", " ");
		}
		s=s.trim();
		String[] result;
		result=s.split(" ");
		if (result.length<1) {
			return null;
		}
		for (int i=0; i<result.length;i++) {
			result[i]=result[i].trim();
		}
		return result;
	}

	/**
	 * Assumes that the array is not null 
	 * @param as
	 * @return
	 */

	public SmallItem convertArrayToSmallItem(String[] as) {
		SmallItem result= new SmallItem();
		for (int i=0;i<as.length;i++) {
			result.putKeyValue(as[i], 1f);
		}
		return result;
	}

	public static void main(String[] args) {

		SmallParser myParser= new SmallParser();

		/*
		String s="  bonjour,                       je suis bien content: demain (il parait)     je dois peut-�tre avoir un cadeau !! !, c'esty pas cool avec 34.8 francs 6 sous 3+9+8 //youpy ;o) ?";

		String result;
		result=myParser.separatorSegmentation(s);

		System.out.println(result);

		String[] wrds=myParser.getWordSequence(result);

		printSeq(wrds);
		 */


		//String t="1 2  3   4     5      6       7        8         9          10           11            12";
		//String t="1 2  3   4             5";

		String t=" a = b ; et c= d; Le chat  mange La souris (et l'oiseau), {mais pas la vache}... [ni le héron]";

		//String[] list=myParser.getLetterSequence(t);
		//printSeq(list);

		System.out.println("===========================");
		SmallItem ss=myParser.getNgramParsing(t);
		System.out.println(ss.getLinetext("\n", "="));
		System.out.println();
		System.out.println();
		String s="label1, LAbel2 label3 ; label4		label5  label6";
		String[] mesLabels=myParser.getListOfSeparatedLabels(s);
		for (int i=0;i<mesLabels.length;i++) {
			System.out.println(i+" "+mesLabels[i]);
		}



	}







}

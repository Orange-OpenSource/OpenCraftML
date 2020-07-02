package utils;

import FilesManagement.SmallItem;
import textModule.SmallParser;

public class PerformanceEvaluator {

	final int N=10;  // NUMBER "K"-1

	long numberOfRecordEvaluated=0;

	//=========  for accuracy evaluation with a threshold  ===

	long numberTotalOfTrueLabel=0;
	double averageTrueLabelPerRecord=0;

	long numberTotalOfLabelPredicted=0;
	double averageLabelPredictedPerRecord=0;

	long numberOfTrueLabelPredictedTrue=0;
	long numberOfTrueLabelNotPredicted=0;
	long numberOfFalseLabelPredictedTrue=0;

	// errorRate IS SIMPLIFIED   EACH RECORD IS EITHER TOTALLY CORRECT (0 local error) OR INCORRECT (1 localError)
	// errorRate is the mean of localErrorRate
	double localErrorRateAccumulator;
	double errorRate=0;

	// accuracy :  1-errorRate
	double accuracy=0;


	//==========   FOR PRECISION AT K  ============


	double[] precisionAtKAccumulator=new double[N+1];


	public void addEvalExampleForPrecisionAtK(String[] yTruth, SmallItem yPredicted) {


		assert (yTruth!=null);
		assert (yTruth.length>0);
		assert (yPredicted!=null);
		//assert (k>0);
		//System.out.println();
		//System.out.println("addEvalExample, k="+k);
		//System.out.println("size of yTruth: "+yTruth.length);
		//System.out.println("true labels in brackets:");
		//for (int i=0; i<yTruth.length;i++ ) {
		//	//System.out.println("   <"+yTruth[i]+">");
		//}
		//System.out.println("y predicted :");
		//System.out.println(yPredicted.getLinetext(";", "="));

		numberOfRecordEvaluated++;

		
		for (int prec=1; prec<=N; prec++) {

			int matchCount=0;
			float localPrecision;

			//System.out.println("LOOP FOR PREC="+prec);
			int k=prec;
			if (k>yTruth.length) {
				k=yTruth.length;    // cannot be more precise

			}

			//===if (k>yPredicted.getSize()) {
			//	k=yPredicted.getSize();
			//}
			//System.out.println("k after adjusting size ypredicted: "+k);

			// if no prediction, Local precision = 0
			if (yPredicted.getSize()==0) {
				localPrecision=0;
			}  else {
				// get the small item predicted for the best k
				String[] yPredictedOrdered=yPredicted.getBestKeysDecreasingOrder(k);
				//SmallItem syPred=new SmallItem();
				//syPred.initViaListOfKeys(yPredictedOrdered);

				// get the small item groung truth
				SmallItem syTruth=new SmallItem();
				syTruth.initViaListOfKeys(yTruth);
				//System.out.println("syTruth : "+syTruth.getLinetext(";", "="));

				for (int i=0;i<yPredictedOrdered.length;i++) {
					//System.out.println("test existence de :<"+yPredictedOrdered[i]+">");
					if (syTruth.hasKey(yPredictedOrdered[i])) {
						matchCount++;
						//System.out.println("match");
					} else {
						//System.out.println("no match");
					}
				}
				//System.out.println("matchCount: "+matchCount);
				assert (matchCount<=yTruth.length);
				localPrecision=(float) matchCount/ (float) yPredictedOrdered.length;
				//System.out.println("  matchCount="+matchCount);
				//System.out.println("  length y predicted ordered:"+yPredictedOrdered.length);
				//System.out.println("  localPrecision="+localPrecision);
			}

			//System.out.println("local precision:"+localPrecision);
			//System.out.println();
			precisionAtKAccumulator[prec]=precisionAtKAccumulator[prec]+localPrecision;
		}

	}


	public double getMeanPrecision(int k) {
		if (numberOfRecordEvaluated==0) {
			System.out.println("WARNING : NO RECORD EVAL DONE");
			return 0;
		}
		return (precisionAtKAccumulator[k]/numberOfRecordEvaluated);
	}


	public String getEvalPrecisionInfo() {
		if (numberOfRecordEvaluated==0) {
			return "no record evaluated";
		}
		String result="";
		result=result+"number of record evaluated:\t"+numberOfRecordEvaluated+"\n";
		for (int k=1;k<=N;k++) {
			result=result+"mean precision@"+k+"\t"+getMeanPrecision(k)+"\n";
		}
		return result;

	}


	private void addEvalRecordForPrecisionAtK_forDevTest_only(String yTruth, String yPredicted) {

		SmallParser myParser=new SmallParser();

		yTruth=myParser.getNormalizedStringWithoutDoubleSpace(yTruth);
		System.out.println("normalized ytruth: "+yTruth);
		System.out.println("VUE LABELS Ytruth:"+yTruth);
		String[] yTruthList=yTruth.split(" ");

		yPredicted=yPredicted.toLowerCase();
		System.out.println("normalized ypredicted: "+yPredicted);
		//yPredicted=yPredicted.replaceAll("_", " ");
		SmallItem syPredictedItem= new SmallItem();
		syPredictedItem.initViaLineIndexValue(yPredicted, ";", "=");
		System.out.println("syPredicted interne après parsing: "+syPredictedItem.getLinetext(" ; ", "="));
		System.out.println("init done");

		addEvalExampleForPrecisionAtK(yTruthList, syPredictedItem);

	}



	public static void main(String[] params) {
		String labelsTrue1=" A   B   C  D  E   F";  String labelsPredicted1=" AA=0.001 ; A=1 ;  B=0.3 ;  C=0.1 ; cc=0.002";

		String labelsTrue2=" A   B   X Y Z E F";  String labelsPredicted2=" A=1 ;  B=0.3 ; Y=0.1 ; T=2;  F=5;  U=2;";

		String labelsTrue3=" A   B   C ";  String labelsPredicted3=" A=0.1 ;  B=0.3 ; C=0.1 "; // ; X=0.9 ; Y=0.95 ; Z=0.7";


		PerformanceEvaluator myEval= new PerformanceEvaluator();

		int maxPrec=4;

		myEval.addEvalRecordForPrecisionAtK_forDevTest_only(labelsTrue1, labelsPredicted1);
		myEval.addEvalRecordForPrecisionAtK_forDevTest_only(labelsTrue2, labelsPredicted2);
		myEval.addEvalRecordForPrecisionAtK_forDevTest_only(labelsTrue3, labelsPredicted3);

		for (int k=1;k<=maxPrec;k++) {
			System.out.println("mean precision: @"+k+" "+myEval.getMeanPrecision(k));
		}

		System.out.println(myEval.getEvalPrecisionInfo());


	}





}

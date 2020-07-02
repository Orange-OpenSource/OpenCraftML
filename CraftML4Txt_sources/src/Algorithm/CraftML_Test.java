package Algorithm;

import java.util.ArrayList;
import java.util.List;

import FilesManagement.CraftMLPredictionWriter;
import FilesManagement.LibsvmFileReader;
import FilesManagement.OnlinePerformanceCumulator;

public class CraftML_Test {
	
	public static void main(String[] args) {
		
		
		for(int i = 0; i < 20; i++) {
			
		
		
		CraftML forest = new CraftML();
		
		//String trainFile = "C:/benchWissam2016_2017/test_craftml/Eurlex/eurlex_train.txt";
		//String testFile = "C:/benchWissam2016_2017/test_craftml/Eurlex/eurlex_test.txt";
		

		String trainFile = "C:\\XML_data\\WikiLSHTC325k\\WikiLSHTC325k_train.txt";
		String testFile = "C:\\XML_data\\WikiLSHTC325k\\WikiLSHTC325k_test.txt";
		
		forest.dimReductionX = 10000;
		forest.dimReductionY = 10000;
		forest.sizeReservoirKmeans = 10000;
		forest.setMode("SXDY");
		forest.nbThread = 5;
		forest.setNbTrees(30);
		forest.sparsity = 1000;
		
		/*
		forest.setNbTrees(10);
		forest.setMode("SXSY");
		forest.setOptimizeMemory(false);
		forest.setAllTreesTogether(false);*/
		
		
		
		LibsvmFileReader readerTrain = new LibsvmFileReader();
		
		readerTrain.setFile(trainFile);
		
		forest.trainAlgoOnFile(readerTrain);
		
		//forest.saveModel("C:/benchWissam2016_2017/cal_dataset/", "model");		
		
		//forest.loadModel("C:/benchWissam2016_2017/cal_dataset/", "model");
		
		LibsvmFileReader readerTest = new LibsvmFileReader();
		
		readerTest.setFile(testFile);
		
		List<String> performances = new ArrayList<String>();
		performances.add("Pat1");
		performances.add("Pat3");
		performances.add("Pat5");
		
		CraftMLPredictionWriter predictionWriter = new CraftMLPredictionWriter("C:/benchWissam2016_2017/WikiLSHTC325k/predictions/predictions_" + Integer.toString(i) + ".txt");
		
		//forest.predictOnFile(readerTest, predictionWriter, 10000);
		
		forest.mesurePerformanceOnFile(readerTest,performances,false,null, true,predictionWriter, 10000);
		
		/*
		CraftMLPredictionWriter predictionWriter = new CraftMLPredictionWriter("C:/benchWissam2016_2017/cal_dataset/predictions.txt");	
		forest.mesurePerformanceOnFile(readerTest,performances,true,"C:/benchWissam2016_2017/cal_dataset/perfo.txt", true, predictionWriter,10); */
		
		
		//forest.predictOnFile(readerTest, false, null);
		
		//forest.auditForest("C:/benchWissam2016_2017/cal_dataset/model_audit2.txt");
		}
		
	}
	
}

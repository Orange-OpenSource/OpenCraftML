package Benchmark;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Algorithm.CraftML;
import FilesManagement.LibsvmFileReader;

/**
 *  Les fichiers de donn�es sont sur l-vipe au chemin C:/data_XML/
 *	Les mesures de performances sont �crites dans un dossier dat� dans C:/data_XML/bench_results/ 
 *
 * @author XPGT4620
 *
 */

public class Bench_XML {
	
	public static void main(String[] args) {
		
		long t1, t2;
		t1=System.currentTimeMillis();
		
		List<String> datasets = new ArrayList<String>();
		
		//datasets.add("Amazon670k");
		datasets.add("Delicious200k");
		//datasets.add("Eurlex4k");
		//datasets.add("Wiki1031k");
		//datasets.add("AmazonCat13k");
		//datasets.add("WikiLSHTC325k");
		

		String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());		
		
		String trainFile;
		String testFile;
		
		List<String> performances = new ArrayList<String>();
		performances.add("Pat1");
		performances.add("Pat3");
		performances.add("Pat5");
		
		for(int i = 0; i < datasets.size(); i++){
			
			String currentDataset = datasets.get(i);
			CraftML forest = new CraftML();	
			
			forest.dimReductionX = 10000;
			forest.dimReductionY = 10000;
			forest.sizeReservoirKmeans = 10000;
			forest.setMode("DXDY");
			forest.nbThread = 2;
			forest.setNbTrees(10);
			forest.sparsity = 1000;
			
			trainFile = "C:/data_XML/" + currentDataset + "/" + currentDataset +  "_train.txt";
			testFile = "C:/data_XML/" + currentDataset + "/" + currentDataset +  "_test.txt";
			
			LibsvmFileReader readerTrain = new LibsvmFileReader();
			readerTrain.setFile(trainFile);
			
			forest.trainAlgoOnFile(readerTrain);
			
			LibsvmFileReader readerTest = new LibsvmFileReader();
			readerTest.setFile(testFile);
			
		
			forest.mesurePerformanceOnFile(readerTest,performances,true,"C:/data_XML/bench_results/bench_" + date + "/" + currentDataset + "_performances.txt", false, null,10);
			
			forest = null;
		}
		t2=System.currentTimeMillis();
		System.out.println("TOTAL EXECUTION TIME (s):"+(t2-t1)/1000);
		
	}
}

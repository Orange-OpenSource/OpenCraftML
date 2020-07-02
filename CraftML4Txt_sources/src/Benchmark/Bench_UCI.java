package Benchmark;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import Algorithm.CraftML;
import FilesManagement.CraftMLPredictionWriter;
import FilesManagement.RecordTextReader;
import FilesManagement.TabularFileReader;

public class Bench_UCI {

	public static void main(String[] args) {
		
		
		long t1, t2;
		t1=System.currentTimeMillis();
		
		
		List<String> datasets = new ArrayList<String>();
		datasets.add("zoo");
		datasets.add("iris");
		datasets.add("balance-scale");
		datasets.add("ecoli");
		datasets.add("breast-cancer");
		datasets.add("house-votes-84");		
		datasets.add("car");
		datasets.add("primary-tumor");
		datasets.add("soybean");
		datasets.add("letter");
		datasets.add("pendigits");
		datasets.add("optdigits");
		datasets.add("audiology");

		
		String trainFile;
		String testFile;
		
		List<String> performances = new ArrayList<String>();
		performances.add("Pat1");
		
		for(int i = 0; i < datasets.size(); i++){
			String currentDataset = datasets.get(i);
			
			for(int j = 1; j <6;j++){
				CraftML forest = new CraftML();
				forest.setNbTrees(100);
				forest.setBranchFactor(10);
				forest.setMinInst(10);
				
				trainFile = "C:/data_UCI/" + currentDataset + "/out/" + currentDataset +  "_train" + Integer.toString(j) + ".txt";
				testFile = "C:/data_UCI/" + currentDataset + "/out/" + currentDataset +  "_test" + Integer.toString(j) + ".txt";
				
				TabularFileReader readerTrain = new TabularFileReader();
				
				readerTrain.setFile(trainFile);
				
				int nbFeature = readerTrain.getNbVariables();				
				
				System.out.println("DimX = " + nbFeature);
				
				forest.setDimReductionX(nbFeature);
				forest.sparsity = nbFeature;
						
				forest.trainAlgoOnFile(readerTrain);
				
				TabularFileReader readerTest = new TabularFileReader();
				readerTest.setFile(testFile);
				
				
				forest.mesurePerformanceOnFile(readerTest,performances,true,"C:/data_UCI/results_multiclass/" + currentDataset + "_fold" + Integer.toString(j) + ".txt", false, null,10);
				
				
				
				//CraftMLPredictionWriter predictionWriter = new CraftMLPredictionWriter("C:/benchWissam2016_2017/results_multiclass/predictions/" + currentDataset + "_fold" + Integer.toString(j) + ".txt");	
						
				//forest.mesurePerformanceOnFile(readerTest,performances,true,"C:/benchWissam2016_2017/results_multiclass/" + currentDataset + "_fold" + Integer.toString(j) + ".txt", true, predictionWriter,10);
				
			}
			
		}
		
		
		String output = "";
		
		RecordTextReader mrReader = new RecordTextReader();
		
		for(int i = 0; i < datasets.size(); i++){
			String currentDataset = datasets.get(i);
			float[] accu = new float[5];
			for(int j = 1; j <6;j++){
				mrReader.openFile("C:/data_UCI/results_multiclass/" + currentDataset + "_fold" + Integer.toString(j) + ".txt");
				String[] record = mrReader.readLine().split("\t");
				accu[j-1] = Float.parseFloat(record[1]);				
				mrReader.closeFile();
			}
			output = output + currentDataset + ": " + calculateMean(accu) + "+-" + calculateSD(accu) + "\n";	
		}
		
		System.out.println(output);
		
		t2=System.currentTimeMillis();
		System.out.println("TOTAL EXECUTION TIME (s):"+(t2-t1)/1000);
		
	}
	
	public static float calculateMean(float numArray[])
    {
		float sum = 0.0f;

        for(float num : numArray) {
            sum += num;
        }

        float mean = sum/numArray.length;
        
        return mean;
    }
	
	public static float calculateSD(float numArray[])
    {
		float sum = 0.0f, standardDeviation = 0.0f;

        for(float num : numArray) {
            sum += num;
        }

        float mean = sum/numArray.length;

        for(float num: numArray) {
            standardDeviation += Math.pow(num - mean, 2);
        }

        return (float) Math.sqrt(standardDeviation/numArray.length);
    }
	
	
}

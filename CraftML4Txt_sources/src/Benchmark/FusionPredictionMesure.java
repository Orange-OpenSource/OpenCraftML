package Benchmark;

import java.util.ArrayList;
import java.util.List;

import Algorithm.Displayer;
import FilesManagement.LibsvmFileReader;
import FilesManagement.OnlinePerformanceCumulator;
import FilesManagement.RecordTextReader;
import FilesManagement.SmallItem;

public class FusionPredictionMesure {
	
	public static void main(String[] args) {
		
		
		int nombreFichierPred = 20;
		String debutNomFichier = "C:\\benchWissam2016_2017\\Amazon\\predictions\\predictions_";
		
		LibsvmFileReader fileReader = new LibsvmFileReader();

		String testFile = "C:\\XML_data\\Amazon670k\\Amazon670k_test.txt";
		
		fileReader.setFile(testFile);
		fileReader.openFile();
		
		SmallItem x;
		SmallItem y;

		SmallItem ypred, yclass;
		
		List<String> performances = new ArrayList<String>();
		performances.add("Pat1");
		performances.add("Pat3");
		performances.add("Pat5");
		
		OnlinePerformanceCumulator performanceCumulator = new OnlinePerformanceCumulator(performances);
		
		int indexCurrent = 0;

		Displayer.displayText("\n Starting aggregation and evaluation on test file... \n");
		
		
		RecordTextReader[] readers = new RecordTextReader[nombreFichierPred];
		for(int i = 0; i < nombreFichierPred;i++) {
			readers[i] = new RecordTextReader();
			readers[i].openFile(debutNomFichier + Integer.toString(i) + ".txt");
		}
		
		
		boolean continueRead = fileReader.readNext();

		while (continueRead) {
			
			indexCurrent++;
						
			y = fileReader.getY();		
			
			ypred = new SmallItem();
			
			for(int i = 0; i < nombreFichierPred;i++) {
				String line = readers[i].readLine();
				SmallItem ycurrent = new SmallItem();
				ycurrent.initViaLineIndexValue(line, ";", "=");
				for (String key : ycurrent.getKeySet()) {
					if (ypred.hasKey(key)) {
						ypred.putKeyValue(key, ypred.getValue(key) + ycurrent.getValue(key));
					} else {
						ypred.putKeyValue(key, ycurrent.getValue(key));
					}
				}
			}			
			
			yclass = new SmallItem();
			
			performanceCumulator.addError(y, ypred, yclass);
			
			if(indexCurrent % 5000 == 5) {
				Displayer.displayText("Testing performances computed on " + indexCurrent + " instances.");
				for (int i = 0; i < performances.size(); i++) {
					Displayer.displayText(performances.get(i) + "\t" + performanceCumulator.getPerformance(performances.get(i)));
				}	
			}
			
			continueRead = fileReader.readNext();
		}
		
		Displayer.displayText("Testing performances computed on " + indexCurrent + " instances.");
		for (int i = 0; i < performances.size(); i++) {
			Displayer.displayText(performances.get(i) + "\t" + performanceCumulator.getPerformance(performances.get(i)));
		}		
		
	}
	
}

package FilesManagement;

import java.util.concurrent.ThreadLocalRandom;

public class GenerateContexte {
	int nbMot = 1000;
	int nbMotNonNuls = 150;
	
	int nbTrain = 4500;
	int nbHeldOut = 500;
	int nbTest = 1000;
	
	public void generateFile(String pathAndRoot) {
		
		String trainFile = pathAndRoot + ".train";
		String testFile = pathAndRoot + ".test";
		String heldoutFile = pathAndRoot + ".test";
		
		String trainFileFull = pathAndRoot + "2.train";
		
		RecordTextWriter mWriter = new RecordTextWriter();
		RecordTextWriter mWriter2 = new RecordTextWriter();
		
		mWriter.openFile(trainFile);
		mWriter2.openFile(trainFileFull);
		
		for(int i=0;i<nbTrain;i++){
			String yPart = "";
			String xPart = "";
			boolean plage1 = Math.random() > 0.5;
			int nbNonZeros = ThreadLocalRandom.current().nextInt(3, nbMotNonNuls + 1);
			if(plage1){
				xPart = "1:1.0";				
				for(int j=0;j<nbNonZeros;j++){
					
					int indexFeatures;
					switch (j) {
					case 0:
						indexFeatures = ThreadLocalRandom.current().nextInt(2, ((nbMot+2)/2));
						break;
					case 1:
						indexFeatures = ThreadLocalRandom.current().nextInt(((nbMot+2)/2), nbMot + 1);
						break;
					default:
						indexFeatures = ThreadLocalRandom.current().nextInt(2, nbMot + 1);
						break;
					}
					
					xPart = xPart + " " + Integer.toString(indexFeatures) + ":1.0";
					if(indexFeatures < ((nbMot+2)/2)){
						if(yPart.equals("")){
							yPart = Integer.toString(indexFeatures-2);
						}else{
							yPart = yPart + "," + Integer.toString(indexFeatures-2);
						}
					}
				}
				mWriter.writeLine(yPart + " " + xPart);
				mWriter2.writeLine(yPart + " " + xPart);
			}else{
				for(int j=0;j<nbNonZeros;j++){
					
					int indexFeatures;
					switch (j) {
					case 0:
						indexFeatures = ThreadLocalRandom.current().nextInt(2, ((nbMot+2)/2));
						break;
					case 1:
						indexFeatures = ThreadLocalRandom.current().nextInt(((nbMot+2)/2), nbMot + 1);
						break;
					default:
						indexFeatures = ThreadLocalRandom.current().nextInt(2, nbMot + 1);
						break;
					}
					
					xPart = xPart + " " + Integer.toString(indexFeatures) + ":1.0";
					if(indexFeatures >= ((nbMot+2)/2)){
						if(yPart.equals("")){
							yPart = Integer.toString(indexFeatures-2);
						}else{
							yPart = yPart + "," + Integer.toString(indexFeatures-2);
						}
					}
				}
				mWriter.writeLine(yPart + xPart);
				mWriter2.writeLine(yPart + xPart);
			}
		}
		
		mWriter.closeFile();		
		
		mWriter.openFile(heldoutFile);
		
		for(int i=0;i<nbHeldOut;i++){
			String yPart = "";
			String xPart = "";
			boolean plage1 = Math.random() > 0.5;
			int nbNonZeros = ThreadLocalRandom.current().nextInt(3, nbMotNonNuls + 1);
			if(plage1){
				xPart = "1:1.0";				
				for(int j=0;j<nbNonZeros;j++){
					
					int indexFeatures;
					switch (j) {
					case 0:
						indexFeatures = ThreadLocalRandom.current().nextInt(2, ((nbMot+2)/2));
						break;
					case 1:
						indexFeatures = ThreadLocalRandom.current().nextInt(((nbMot+2)/2), nbMot + 1);
						break;
					default:
						indexFeatures = ThreadLocalRandom.current().nextInt(2, nbMot + 1);
						break;
					}
					
					xPart = xPart + " " + Integer.toString(indexFeatures) + ":1.0";
					if(indexFeatures < ((nbMot+2)/2)){
						if(yPart.equals("")){
							yPart = Integer.toString(indexFeatures-2);
						}else{
							yPart = yPart + "," + Integer.toString(indexFeatures-2);
						}
					}
				}
				mWriter.writeLine(yPart + " " + xPart);
				mWriter2.writeLine(yPart + " " + xPart);
			}else{
				for(int j=0;j<nbNonZeros;j++){
					
					int indexFeatures;
					switch (j) {
					case 0:
						indexFeatures = ThreadLocalRandom.current().nextInt(2, ((nbMot+2)/2));
						break;
					case 1:
						indexFeatures = ThreadLocalRandom.current().nextInt(((nbMot+2)/2), nbMot + 1);
						break;
					default:
						indexFeatures = ThreadLocalRandom.current().nextInt(2, nbMot + 1);
						break;
					}
					
					xPart = xPart + " " + Integer.toString(indexFeatures) + ":1.0";
					if(indexFeatures >= ((nbMot+2)/2)){
						if(yPart.equals("")){
							yPart = Integer.toString(indexFeatures-2);
						}else{
							yPart = yPart + "," + Integer.toString(indexFeatures-2);
						}
					}
				}
				mWriter.writeLine(yPart + xPart);
				mWriter2.writeLine(yPart + xPart);
			}
		}
		
		mWriter.closeFile();
		mWriter2.closeFile();
		
		
		mWriter.openFile(testFile);
		
		for(int i=0;i<nbTest;i++){
			String yPart = "";
			String xPart = "";
			boolean plage1 = Math.random() > 0.5;
			int nbNonZeros = ThreadLocalRandom.current().nextInt(3, nbMotNonNuls + 1);
			if(plage1){
				xPart = "1:1.0";				
				for(int j=0;j<nbNonZeros;j++){
					
					int indexFeatures;
					switch (j) {
					case 0:
						indexFeatures = ThreadLocalRandom.current().nextInt(2, ((nbMot+2)/2));
						break;
					case 1:
						indexFeatures = ThreadLocalRandom.current().nextInt(((nbMot+2)/2), nbMot + 1);
						break;
					default:
						indexFeatures = ThreadLocalRandom.current().nextInt(2, nbMot + 1);
						break;
					}
					
					xPart = xPart + " " + Integer.toString(indexFeatures) + ":1.0";
					if(indexFeatures < ((nbMot+2)/2)){
						if(yPart.equals("")){
							yPart = Integer.toString(indexFeatures-2);
						}else{
							yPart = yPart + "," + Integer.toString(indexFeatures-2);
						}
					}
				}
				mWriter.writeLine(yPart + " " + xPart);
			}else{
				for(int j=0;j<nbNonZeros;j++){
					
					int indexFeatures;
					switch (j) {
					case 0:
						indexFeatures = ThreadLocalRandom.current().nextInt(2, ((nbMot+2)/2));
						break;
					case 1:
						indexFeatures = ThreadLocalRandom.current().nextInt(((nbMot+2)/2), nbMot + 1);
						break;
					default:
						indexFeatures = ThreadLocalRandom.current().nextInt(2, nbMot + 1);
						break;
					}
					
					xPart = xPart + " " + Integer.toString(indexFeatures) + ":1.0";
					if(indexFeatures >= ((nbMot+2)/2)){
						if(yPart.equals("")){
							yPart = Integer.toString(indexFeatures-2);
						}else{
							yPart = yPart + "," + Integer.toString(indexFeatures-2);
						}
					}
				}
				mWriter.writeLine(yPart + xPart);
			}
		}
		
		mWriter.closeFile();	
		
		
		
		
	}
	
	

	public static void main(String[] args) {
		GenerateContexte contexte = new GenerateContexte();
		contexte.generateFile("C:/benchWissam2016_2017/contexte/contexte");
	}
	
}

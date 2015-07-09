package bayesANDjess;


import java.util.ArrayList;

public class Decider {
	public static Value[] sortValues(Value [] toSort){
		Value [] sortedArray = toSort;
		for(int i=0; i<sortedArray.length-1;i++){
			for(int j=i+1; j<sortedArray.length;j++){
				if(sortedArray[i].getProbability() < sortedArray[j].getProbability()){
					Value temp = sortedArray[i];
					sortedArray[i] = sortedArray[j];
					sortedArray[j] = temp;
				}
			}
		}
		return sortedArray;
	}
	
	public static Value scegli(Value p1, Value p2, Value p3){
		Value toReturn = null;
		Value toSort[] = {p1,p2,p3};
		Value[] sorted = sortValues(toSort);
		//sorted[0] contiene il massimo
		double launch = Math.random();
		if(launch >= (1-sorted[0].getProbability())){
			toReturn = sorted[0];
		}else{
			//non � stato scelto sorted 0
			double constNorm = sorted[1].getProbability()+sorted[2].getProbability();
			double p2Norm = sorted[1].getProbability()/constNorm; //p2 � maggiore
			double p3Norm = sorted[2].getProbability()/constNorm;
			
			double launch2 = Math.random();
			if(launch2 >= (1-p2Norm)){
				toReturn = sorted[1];
			}else
				toReturn = sorted[2];
		}
		
		return toReturn;
	}
	
	
	public Value choose(Value [] values){
		values = sortValues(values);
		ArrayList<Value> valuesList = new ArrayList<Value>();
		for(int i=0;i<values.length;i++){
			valuesList.add(new Value(values[i].getOutcome(),values[i].getProbability()));
			//System.out.println(values[i].getOutcome()+" p="+values[i].getValue());
		}
		//System.out.println("---------------------------------");
		while(valuesList.size() > 2){
			double launch = Math.random();
			if(launch >= (1-valuesList.get(0).getProbability())){
				//System.out.println("Value trovato: "+valuesList.get(0).getOutcome()+" p="+valuesList.get(0).getValue());
				return valuesList.get(0);
			}else{
				//System.out.println("Scartato value "+valuesList.get(0).getOutcome());
				//calcolo costante normalizzazione
				double normConst = 0;
				for(int i=1;i<valuesList.size();i++){
					normConst+= valuesList.get(i).getProbability();
				}
				//System.out.println("Costante di norm: "+normConst);
				//normalizzo i restanti
				for(int j=1; j<valuesList.size(); j++){
					double oldValue = valuesList.get(j).getProbability();
					valuesList.get(j).setProbability(oldValue/normConst);
					//System.out.println("Outcome "+valuesList.get(j).getOutcome()+" new value: "+valuesList.get(j).getValue());
				}
				//rimuovo il primo
				valuesList.remove(0);
				//System.out.println("Iterazione terminata "+valuesList.size());
				//System.out.println("------------------------------------");
			}
		}
		//Sono rimasti due elementi
		double lastLaunch = Math.random();
		if(lastLaunch >= (1-valuesList.get(0).getProbability())){
			return valuesList.get(0);
		}
		return valuesList.get(1);
	}
	
	/*public void demo(){
		Value p1 = new Value (1, 0.8);
		Value p2 = new Value (2, 0.12);
		Value p3 = new Value (3, 0.08);
		
		int count = 10000000;
		int p1Choosen = 0;
		int p2Choosen = 0;
		int p3Choosen = 0;
		while(count > 0){
			Value obtained = scegli(p1,p2,p3);
			if(obtained.getId() == 1)
				p1Choosen++;
			if(obtained.getId() == 2)
				p2Choosen++;
			if(obtained.getId() == 3)
				p3Choosen++;
			count--;
		}
		System.out.println("Scelto 1: "+p1Choosen+" volte");
		System.out.println("Scelto 2: "+p2Choosen+" volte");
		System.out.println("Scelto 3: "+p3Choosen+" volte");
	}*/
}

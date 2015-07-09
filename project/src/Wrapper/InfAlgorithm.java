package Wrapper;
/**
 * Types of algorithms available
 * @author Andre
 */
public enum InfAlgorithm {
	EXACT("Inferenza esatta (Junction tree)"), 
	REJECT("Campionamento con Rigetto"), 
	LIKELI("Campionamento con Likelihood Weighted");
	
	private String description;
	InfAlgorithm(String description){
		this.description=description;
	}
	
	public String getDescription(){
		return description;
	}
}

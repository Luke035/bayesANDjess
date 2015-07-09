package Wrapper;

public class JayesException extends Exception {
	private final String message;
	public JayesException(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	private static final long serialVersionUID = 3232489028547230366L;
}
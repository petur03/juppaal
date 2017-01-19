package uppaal;

public class UppaalException extends Exception {

	private static final long serialVersionUID = -5492620726034305672L;

	public UppaalException(String message) {
		super(message);
	}

	public UppaalException(String message, Throwable cause) {
		super(message, cause);
	}

	public UppaalException(Throwable cause) {
		super(cause);
	}
}

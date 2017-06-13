package storage;

public class StorageException extends RuntimeException {
	private static final long serialVersionUID = 9164610006074881070L;

	public StorageException(String message) {
		super(message);
	}

	public StorageException(String message, Throwable cause) {
		super(message, cause);
	}
}

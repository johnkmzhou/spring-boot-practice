package storage;

public class StorageFileNotFoundException extends StorageException {
	private static final long serialVersionUID = -551812247454324277L;

	public StorageFileNotFoundException(String message) {
		super(message);
	}

	public StorageFileNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
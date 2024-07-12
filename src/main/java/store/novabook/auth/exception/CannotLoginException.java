package store.novabook.auth.exception;

public class CannotLoginException extends NovaException {
	public CannotLoginException(ErrorCode errorCode) {
		super(errorCode);
	}
}

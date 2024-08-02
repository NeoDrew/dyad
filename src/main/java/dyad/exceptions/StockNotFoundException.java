package dyad.exceptions;

public class StockNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 5016812401135889608L;

	private long id;

	public StockNotFoundException(long id) {
		super("Could not find stock " + id);

		this.id = id;
	}

	public long getId() {
		return id;
	}
}

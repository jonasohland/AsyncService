package de.hsmainz.iiwa.AsyncService.functional;

public interface Consumer<T> {
	public void accept(T input);
}

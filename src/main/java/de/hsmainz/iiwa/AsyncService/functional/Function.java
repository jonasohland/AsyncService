package de.hsmainz.iiwa.AsyncService.functional;

public interface Function <T,R> {
	public R apply(T input);
}

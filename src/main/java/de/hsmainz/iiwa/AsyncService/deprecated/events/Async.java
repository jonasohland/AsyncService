package de.hsmainz.iiwa.AsyncService.deprecated.events;

import de.hsmainz.iiwa.AsyncService.functional.BiConsumer;
import de.hsmainz.iiwa.AsyncService.functional.BiFunction;
import de.hsmainz.iiwa.AsyncService.functional.Consumer;
import de.hsmainz.iiwa.AsyncService.functional.Function;

import de.hsmainz.iiwa.AsyncService.functional.Supplier;

public class Async {
	
	public static AsyncTask makeAsync(Runnable r)
	{
		return new AsyncRunnable(r);
	}
	
	
	public static <T> AsyncSupplier<T> makeAsync(Supplier<T> function)
	{
		return new AsyncSupplier<T>(function);
	}
	
	/**
	 * Make an AsyncTask from an Consumer Object
	 * @param in1 input argument to the Consumer
	 * @param function supplier to perform with the event
	 * @param <T> consumer input type
	 * @return new AsyncConsumer
	 */
	public static <T> AsyncTask makeAsync(T in1, Consumer<T> function)
	{
		return new AsyncConsumer<T>(in1, function);
	}

	/**
	 * Make an AsyncTask from an Consumer Object
	 * @param function supplier to perform with the event
	 * @param <T> consumer input type
	 * @return new AsyncConsumer
	 */
	public static <T> AsyncTask makeAsync(Consumer<T> function)
	{
		return new AsyncConsumer<T>(function);
	}

	/**
	 * Make an AsyncTask from an BiConsumer Object
	 * @param <T> BiConsumer input 1 type
	 * @param <U> BiConsumer input 2 type
	 * @param function BiConsumer to perform with the AsyncTask
	 * @return new AsyncBiConsumer
	 */
	public static <T, U> AsyncTask makeAsync(BiConsumer<T,U> function)
	{
		return new AsyncBiConsumer<T, U>(function);
	}
	
	/**
	 * Make an AsyncTask from an BiConsumer Object
	 * @param in1 input arg 1 to the BiConsumer
	 * @param in2 input arg 2 to the BiConsumer
	 * @param <T> BiConsumer input 1 type
	 * @param <U> BiConsumer input 2 type
	 * @param function BiConsumer to perform with the AsyncTask
	 * @return new AsyncBiConsumer
	 */
	public static <T, U> AsyncTask makeAsync(T in1, U in2, BiConsumer<T,U> function)
	{
		return new AsyncBiConsumer<T, U>(in1, in2, function);
	}
	
	/**
	 * Make an AsyncTask from an Function Object
	 * @param in1 input arg1 to the Function
	 * @param <T> Function input type
	 * @param <U> Function return type
	 * @param function function to perform with the event
	 * @return new AsyncFunction
	 */
	public static <T, U> AsyncTask makeAsync(T in1, Function<T,U> function)
	{
		return new AsyncFunction<T, U>(in1, function);
	}

	/**
	 * Make an AsyncTask from an Function Object
	 * @param <T> Function input type
	 * @param <U> Function return type
	 * @param function function to perform with the event
	 * @return new AsyncFunction
	 */
	public static <T, U> AsyncTask makeAsync(Function<T,U> function)
	{
		return new AsyncFunction<T, U>(function);
	}

	/**
	 * make an AsyncTask from a BiFunction Object
	 * @param function BiFunction to perform with the AsyncTask
	 * @param <T> function input 1 type
	 * @param <U> function input 2 type
	 * @param <R> function return type
	 * @return new AsyncBiFunction
	 */
	public static <T,U,R> AsyncTask makeAsync(BiFunction<T,U,R> function)
	{
		return new AsyncBiFunction<T,U,R>(function);
	}
	
	/**
	 * make an AsyncTask from a BiFunction Object
	 * @param function BiFunction to perform with the AsyncTask
	 * @param in1 first input to function
	 * @param in2 second input to function
	 * @param <T> function input 1 type
	 * @param <U> function input 2 type
	 * @param <R> function return type
	 * @return new AsyncBiFunction
	 */
	public static <T,U,R> AsyncTask makeAsync(T in1, U in2, BiFunction<T,U,R> function)
	{
		return new AsyncBiFunction<T,U,R>(in1, in2, function);
	}



	public static void run(AsyncTask task){
		AsyncService.post(task);
	}

	public static void run(Runnable runnable){
		AsyncService.post(makeAsync(runnable));
	}

	public static <T> void run(Consumer<T> consumer){
		AsyncService.post(makeAsync(consumer));
	}

	public static <T> void run(T in, Consumer<T> consumer){
		AsyncService.post(makeAsync(in, consumer));
	}

	public static <T> void run(Supplier<T> supplier){
		AsyncService.post(makeAsync(supplier));
	}

	public static <T, R> void run(T in, Function<T, R> function){
		AsyncService.post(makeAsync(in, function));
	}

	public static <T, U> void run(BiConsumer<T, U> biconsumer){
		AsyncService.post(makeAsync(biconsumer));
	}

	public static <T, U> void run(T in1, U in2, BiConsumer<T, U> biconsumer){
		AsyncService.post(makeAsync(in1, in2, biconsumer));
	}

	public static <T, U, R> void run(BiFunction<T, U, R> bifunction){
		AsyncService.post(makeAsync(bifunction));
	}

	public static <T, U, R> void run(T in1, U in2, BiFunction<T, U, R> bifunction){
		AsyncService.post(makeAsync(in1, in2, bifunction));
	}

}

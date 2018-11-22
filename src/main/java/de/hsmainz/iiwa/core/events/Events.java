package de.hsmainz.iiwa.core.events;

import de.hsmainz.iiwa.core.functional.BiConsumer;
import de.hsmainz.iiwa.core.functional.BiFunction;
import de.hsmainz.iiwa.core.functional.Consumer;
import de.hsmainz.iiwa.core.functional.Function;

import de.hsmainz.iiwa.core.functional.Supplier;

public class Events {
	
	public static Event makeEvent(Runnable r)
	{
		return new RunnableEvent(r);
	}
	
	
	public static <T> SupplierEvent<T> makeEvent(Supplier<T> function)
	{
		return new SupplierEvent<T>(function);
	}
	
	/**
	 * Make an Event from an Consumer Object
	 * @param in1 input argument to the Consumer
	 * @param function supplier to perform with the event
	 * @param <T> consumer input type
	 * @return new ConsumerEvent
	 */
	public static <T> Event makeEvent(T in1, Consumer<T> function)
	{
		return new ConsumerEvent<T>(in1, function);
	}
	
	/**
	 * Make an Event from an BiConsumer Object
	 * @param in1 input arg 1 to the BiConsumer
	 * @param in2 input arg 2 to the BiConsumer
	 * @param <T> BiConsumer input 1 type
	 * @param <U> BiConsumer input 2 type
	 * @param function BiConsumer to perform with the Event
	 * @return new BiConsumerEvent
	 */
	public static <T, U> Event makeEvent(T in1, U in2, BiConsumer<T,U> function)
	{
		return new BiConsumerEvent<T, U>(in1, in2, function);
	}
	
	/**
	 * Make an Event from an Function Object
	 * @param in1 input arg1 to the Function
	 * @param <T> Function input type
	 * @param <U> Function return type
	 * @param function function to perform with the event
	 * @return new FunctionEvent
	 */
	public static <T, U> Event makeEvent(T in1, Function<T,U> function)
	{
		return new FunctionEvent<T, U>(in1, function);
	}
	
	/**
	 * make an Event from a BiFunction Object
	 * @param function BiFunction to perform with the Event
	 * @param in1 first input to function
	 * @param in2 second input to function
	 * @param <T> function input 1 type
	 * @param <U> function input 2 type
	 * @param <R> function return type
	 * @return new BiFunctionEvent
	 */
	public static <T,U,R> Event makeEvent(T in1, U in2, BiFunction<T,U,R> function)
	{
		return new BiFunctionEvent<T,U,R>(in1, in2, function);
		
	}
}

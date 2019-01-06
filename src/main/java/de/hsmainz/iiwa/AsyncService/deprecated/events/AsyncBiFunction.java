package de.hsmainz.iiwa.AsyncService.deprecated.events;

import de.hsmainz.iiwa.AsyncService.functional.BiFunction;

@Deprecated
public class AsyncBiFunction<Tin1, Tin2, Tout> implements AsyncTask {
	
	private Tin1 arg1;
	private Tin2 arg2;
	
	private LazyAllocatedListenableFuture<Tout> future = new LazyAllocatedListenableFuture<>();

	public BiFunction<Tin1,Tin2,Tout> function;
	
	private AsyncTimer timer;
	
	public AsyncBiFunction(Tin1 __arg1, Tin2 __arg2, BiFunction<Tin1, Tin2, Tout> __func)
	{
		arg1 = __arg1;
		arg2 = __arg2;
		function = __func;
		future.prepare(this);
	}
	
	public AsyncBiFunction(BiFunction<Tin1, Tin2, Tout> __func){
		function = __func;
		future.prepare(this);
	}
	
	public AsyncBiFunction(BiFunction<Tin1, Tin2, Tout> __func, ListenableFuture<Tout> __future){
		function = __func;
		future.attach_if_alloc(__future);
		future.prepare(this);
	}


	@Override
	public void execute() {
		Tout result = function.apply(arg1, arg2);
		if(future != null)
			future.fire(result);
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public ListenableFuture<Tout> getFuture() {
		return future.get();
	}



	@SuppressWarnings("unchecked")
	@Override
	public <K> void __set__arg_(K arg) {
		arg1 = (Tin1) arg;
	}



	@SuppressWarnings("unchecked")
	@Override
	public <L> void __set__sec__arg_(L arg) {
		arg2 = (Tin2) arg;
	}

	@Override
	public LazyAllocatedListenableFuture<Tout> getFutureLazy() {
		return future;
	}


	@Override
	public void fire() {
		AsyncService.post(this);
	}
	
	public void fire(Tin1 in1, Tin2 in2) {
		
		__set__arg_(in1);
		__set__sec__arg_(in2);
		
		AsyncService.post(this);
		
	}
	
	@Override
	public AsyncTask copy() {
		return new AsyncBiFunction<Tin1, Tin2, Tout>(arg1, arg2, function);
	}

	@Override
	public void attachTimer(AsyncTimer t) {
		timer = t;
	}

	@Override
	public boolean hasTimer() {
		return timer != null;
	}

	@Override
	public AsyncTimer getTimer() {
		return timer;
	}

}

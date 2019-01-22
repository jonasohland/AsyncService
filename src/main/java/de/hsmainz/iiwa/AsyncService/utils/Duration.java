package de.hsmainz.iiwa.AsyncService.utils;

public class Duration {

    private long _nanos;

    public Duration(long nanos){
        _nanos = nanos;
    }

    public static Duration ofNanos(long nanos){
        return new Duration(nanos);
    }

    public static Duration ofMicros(long micros){
        return new Duration(micros * 1000);
    }

    public static Duration ofMillis(long millis){
        return new Duration(millis * 1000000);
    }

    public static Duration ofSeconds(long secs){
        return new Duration(secs * 1000000000);
    }

    public static Duration ofMins(long mins){
        return new Duration(mins * 1000000000 * 60);
    }

    public long toNanos(){
        return _nanos;
    }

    public long toMicros(){
        return _nanos / 1000;
    }

    public long toMillis(){
        return _nanos / 1000000;
    }

    public long toSecs(){
        return _nanos / 1000000000;
    }

    public long toMins(){
        return _nanos / 60000000000L;
    }
}

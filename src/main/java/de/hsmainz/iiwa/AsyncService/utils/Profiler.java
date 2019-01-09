package de.hsmainz.iiwa.AsyncService.utils;

import de.hsmainz.iiwa.AsyncService.executor.layer.ExecutorLayer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Profiler {

    private final ConcurrentHashMap<String, ProfilerChannel> channels = new ConcurrentHashMap<>();

    public ProfilerChannel newChannel(String name, ExecutorLayer layer){

        ProfilerChannel new_channel = new ProfilerChannel(layer, name);
        channels.put(name, new_channel);

        return new_channel;

    }

    public void printStats(){

        System.out.println("---------- Profiler report: ---------- ");

        for(Map.Entry<String, ProfilerChannel> entry: channels.entrySet()){
            System.out.println(entry.getKey()
                    + ": avg: " + entry.getValue().average().toMillis()
                    + ", min: " + entry.getValue().min().toMillis()
                    + ", max: " + entry.getValue().max().toMillis());
        }

        System.out.println("------------- end report ------------- ");
    }


}

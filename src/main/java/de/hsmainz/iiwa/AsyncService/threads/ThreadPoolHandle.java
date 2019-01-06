package de.hsmainz.iiwa.AsyncService.threads;

import java.util.UUID;
import java.nio.ByteBuffer;

@Deprecated
public class ThreadPoolHandle {

    private long timeout;
    private Executable job;
    private UUID id;

    ThreadPoolHandle(Executable ex) {
        long thread_num = ThreadPool.registerNewThread();
        id = UUID.nameUUIDFromBytes(longToBytes(thread_num));
        job = ex;
    }

    public int getId() {
        return id.hashCode();
    }

    Executable getExecutable() {
        return job;
    }

    public ThreadPoolHandle(long _timeout) {
        timeout = _timeout;
    }

    public long getTimeout() {
        return timeout;
    }

    private byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

}

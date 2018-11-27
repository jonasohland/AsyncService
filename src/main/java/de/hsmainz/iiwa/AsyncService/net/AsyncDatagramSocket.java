package de.hsmainz.iiwa.AsyncService.net;

import de.hsmainz.iiwa.AsyncService.events.DualListenableFuture;
import de.hsmainz.iiwa.AsyncService.events.ListenableFuture;
import de.hsmainz.iiwa.AsyncService.threads.ThreadPoolJob;
import de.hsmainz.iiwa.AsyncService.threads.ThreadPoolJobLite;
import de.hsmainz.iiwa.AsyncService.utils.Result;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.*;

public class AsyncDatagramSocket {

    private DatagramSocket socket;

    public DualListenableFuture<String, InetAddress> onMessage = new DualListenableFuture<String, InetAddress>();
    public ListenableFuture<Result<SocketException>> onConnect = new ListenableFuture<>();
    public ListenableFuture<Result<SocketException>> onBind = new ListenableFuture<>();

    public void bind(SocketAddress addr) {

        ThreadPoolJobLite binding_job = ThreadPoolJobLite.makeJob(() -> {

            System.out.println("binding to socketaddr: " + addr.toString());

            try {

                if(socket == null){
                    socket = new DatagramSocket(null);
                    socket.bind(addr);
                }

                onBind.fire(new Result<>());

            } catch (SocketException ex){

                onBind.fire(new Result<>(ex));
            }

            System.out.println("binding job end");
        });

        binding_job.start();
    }

    public void connect(SocketAddress addr){

        ThreadPoolJobLite connection_job = ThreadPoolJobLite.makeJob(() -> {

            try {
                socket.connect(addr);
                onConnect.fire(new Result<>());

            } catch (SocketException ex){
                onConnect.fire(new Result<>(ex));
            }
        });

        connection_job.start();

    }

    public void send(String str) throws IOException {
        byte[] buf = str.getBytes();
        socket.send(new DatagramPacket(buf, buf.length));
    }

    public void close(){
        socket.close();
    }

    public void disconnect(){
        socket.disconnect();
    }

    public AsyncDatagramSocket(){
        super();
    }

    public Void perform() {

        byte[] buf = new byte[1024];

        DatagramPacket packet = new DatagramPacket(buf, 1024);

        try {

            socket.receive(packet);
            onMessage.fire(packet.toString(), packet.getAddress());

        } catch (IOException e) {

            e.printStackTrace();

        }

        return null;
    }




}

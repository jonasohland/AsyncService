package de.hsmainz.iiwa.AsyncService.net;
/*
import de.hsmainz.iiwa.AsyncService.executor.DualListenableFuture;
import de.hsmainz.iiwa.AsyncService.executor.ListenableFuture;
import de.hsmainz.iiwa.AsyncService.utils.Completion;

import java.io.IOException;
import java.net.*;

public class AsyncDatagramSocket {

    private DatagramSocket socket;

    public DualListenableFuture<String, InetAddress> onMessage = new DualListenableFuture<String, InetAddress>();
    public ListenableFuture<Completion<SocketException>> onConnect = new ListenableFuture<>();
    public ListenableFuture<Completion<SocketException>> onBind = new ListenableFuture<>();

    public void bind(SocketAddress addr) {

        ThreadPool.fromRunnable(() -> {

            System.out.println("binding to socketaddr: " + addr.toString());

            try {

                if(socket == null){
                    socket = new DatagramSocket(null);
                    socket.bind(addr);
                }

                onBind.fire(new Completion<>());

            } catch (SocketException ex){

                onBind.fire(new Completion<>(ex));
            }

            System.out.println("binding job end");
        }).start();
    }

    public void connect(SocketAddress addr){

        ThreadPoolJobLite connection_job = ThreadPoolJobLite.makeJob(() -> {

            try {
                socket.connect(addr);
                onConnect.fire(new Completion<>());

            } catch (SocketException ex){
                onConnect.fire(new Completion<>(ex));
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
*/
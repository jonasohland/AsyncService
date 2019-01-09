package de.hsmainz.iiwa.AsyncService.net;

import de.hsmainz.iiwa.AsyncService.future.DualListenableFuture;
import de.hsmainz.iiwa.AsyncService.future.ListenableFuture;
import de.hsmainz.iiwa.AsyncService.listenable.Event2;
import de.hsmainz.iiwa.AsyncService.utils.Completion;

import java.io.IOException;
import java.net.*;

public class AsyncDatagramSocket {

    private DatagramSocket socket;

    public DualListenableFuture<String, InetAddress> onMessage = new DualListenableFuture<String, InetAddress>();
    public ListenableFuture<Completion<SocketException>> onConnect = new ListenableFuture<>();
    public ListenableFuture<Completion<SocketException>> onBind = new ListenableFuture<>();

    public void asyncBind(SocketAddress addr) {

        new Thread(() -> {

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

    public void asyncConnect(SocketAddress addr){

        new Thread(() -> {

            try {
                socket.connect(addr);
                onConnect.fire(new Completion<>());

            } catch (SocketException ex){
                onConnect.fire(new Completion<>(ex));
            }
        }).start();

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

    public void beginRead(){
        new Thread(() -> {
        }).start();
    }

    public void perform() {

        DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);

        try {

            socket.receive(packet);
            onMessage.fire(packet.toString(), packet.getAddress());

        } catch (IOException e) {

            e.printStackTrace();

        }
    }

}
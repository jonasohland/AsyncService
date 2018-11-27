package de.hsmainz.iiwa.AsyncService.test;

import de.hsmainz.iiwa.AsyncService.events.Async;
import de.hsmainz.iiwa.AsyncService.events.AsyncService;
import de.hsmainz.iiwa.AsyncService.net.AsyncDatagramSocket;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class DatagramTests {


    AsyncDatagramSocket sock = new AsyncDatagramSocket();

    @Test
    public void bind_test(){

        AsyncService.init();

        sock.onBind.addListener((result) -> {

            if(result.failed()){
                System.out.println(result.getException().getMessage());
            } else {
                System.out.println("success");
            }

        });


        AsyncService.post(Async.makeAsync(() -> {
            sock.bind(null);
        }));


        AsyncService.run();

        AsyncService.exit();

    }
}

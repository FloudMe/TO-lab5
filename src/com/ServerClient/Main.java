package com.ServerClient;

import com.ServerClient.Server.Server;
import com.ServerClient.Client.MessangerClient;

public class Main {
    public static void main(String[] args) throws Exception {
        Thread t1 = new Thread(){
            @Override
            public void run()
            {
                Server server = new Server();
            }
        };
        Thread t2 = new Thread () {
            @Override
            public void run()
            {
                MessangerClient observer = new MessangerClient();
                int port = 5657;
                observer.connectToSocket(port);
            }
        };
        t1.run();
        t2.run();
    }
}

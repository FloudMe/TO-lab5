package com.ServerClient.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends SubjectAbstract {
    private ArrayList<ServerClient> observers = new ArrayList<ServerClient>();

    public Server()
    {
        this.start();
    }

    @Override
    public void addObserver(ServerSocket serverSocket) {
        try {
            Socket socket = serverSocket.accept();
            ServerClient serverClientThread = new ServerClient(socket, this);
            serverClientThread.start();
            observers.add(serverClientThread);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeObserver(ServerClient serverClient) {
        observers.remove(serverClient);
    }

    @Override
    public void notifyObserver(byte[] string) {
        for(ServerClient observer: observers)
            observer.messagePropagation(string);
    }

    public int getObserverIndex(ServerClient serverClient)
    {
        return observers.indexOf(serverClient);
    }

    public void run()
    {
        try(ServerSocket serverSocket = new ServerSocket(5657))
        {
            System.out.println("Server start");
            while(true)
            {
                addObserver(serverSocket);
                System.out.println("Client has joined");
            }
        }
        catch (IOException e)
        {
            System.out.println("Closing");
            this.stop();
        }
    }
}

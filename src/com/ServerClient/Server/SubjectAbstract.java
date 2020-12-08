package com.ServerClient.Server;

import java.net.ServerSocket;

public abstract class SubjectAbstract extends Thread {
    public abstract void addObserver(ServerSocket serverSocket);
    public abstract void removeObserver(ServerClient serverClient);
    public abstract void notifyObserver(byte[] string);
}

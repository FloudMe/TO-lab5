package com.ServerClient.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ServerClient extends Thread {
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private Server server;
    private boolean messageShown = false;

    public ServerClient(Socket socket, Server server) throws IOException
    {
        this.socket = socket;
        inputStream = new DataInputStream(this.socket.getInputStream());
        outputStream = new DataOutputStream(this.socket.getOutputStream());
        this.server = server;
    }

    public void run()
    {
        byte[] received;
        int sizeOfMessage;
        ArrayList<Byte> message = new ArrayList<Byte>();
        String name = "User " + server.getObserverIndex(this);

        while(true)
        {
            sizeOfMessage = 0;
            try
            {
                if(!messageShown)
                {
                    outputStream.writeUTF(name);
                    outputStream.writeUTF("You've joined the chat!\n" +
                            "To send a message type path\n\n");
                    messageShown = true;
                }

                while(inputStream.available() != 0)
                {
                    sizeOfMessage = inputStream.available();
                    received = new byte[sizeOfMessage];
                    inputStream.read(received);

                    for(int i = 0; i < sizeOfMessage; i++)
                        message.add(received[i]);
                }

                if(sizeOfMessage != 0)
                {
                    received = new byte[message.size()];

                    for(int i = 0; i < message.size(); i++)
                        received[i] = message.get(i);

                    if(received[0] == -1)
                    {
                        System.out.println("Client " + this.socket + " sends exit...\n" +
                                "Closing conection.");
                        this.socket.close();
                        System.out.println("Connection closed");
                        server.removeObserver(this);
                        break;
                    }
                    else if(isTypeOfMessageKnown(new byte[]{0, 1, 2, 3, 4}, received[0]))
                    {
                        server.notifyObserver(received);
                    }
                    else
                    {
                        throw new IOException("Uknown type of data");
                    }

                    message.clear();
                }

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        try
        {
            this.inputStream.close();
            this.outputStream.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    private boolean isTypeOfMessageKnown(byte[] types, byte messageType)
    {
        for( byte type: types)
            if(type == messageType)
                return true;

        return false;
    }

    public void messagePropagation(byte[] message)
    {
        try
        {
            outputStream.write(message);
            outputStream.flush();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}

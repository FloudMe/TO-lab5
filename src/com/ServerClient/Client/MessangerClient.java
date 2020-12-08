package com.ServerClient.Client;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.FocusManager;
import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MessangerClient extends JFrame implements iChatable, ActionListener {
    private boolean welcomed = false;
    private DataInputStream input;
    private DataOutputStream output;
    private Socket socket;
    private String nickname;

    static boolean runReadThread = true;

    private JTextArea tMessages = new JTextArea();
    private TextAreaWithPrompt tNewMessage = new TextAreaWithPrompt();
    private JButton bSendImage = new JButton("Send image");
    private JButton bSendMusic = new JButton("Send music");
    private JButton bSend = new JButton("Send");


    private JDialog errorDialog = new JDialog();
    private JTextPane tError = new JTextPane();
    private JButton bOkError = new JButton("Ok");

    public MessangerClient()
    {
        setSize(400,525);
        setTitle("Messenger");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(null);
        setResizable(false);

        tMessages.setSize(390, 400);
        tMessages.setEditable(false);
        tMessages.setLineWrap(true);
        tMessages.setVisible(true);

        JScrollPane scroll = new JScrollPane(tMessages);
        scroll.setSize(390, 400);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVisible(true);
        this.add(scroll);


        tNewMessage.setBounds(0, 400, 395, 50);
        tNewMessage.setLineWrap(true);
        add(tNewMessage);

        bSendMusic.setBounds(0, 450, 133, 50);
        bSendMusic.addActionListener(this);
        add(bSendMusic);

        bSendImage.setBounds(133, 450, 133, 50);
        bSendImage.addActionListener(this);
        add(bSendImage);

        bSend.setBounds(266, 450, 133, 50);
        bSend.addActionListener(this);
        add(bSend);

        // Wrong path dialog settings
        errorDialog.setBounds(200, 200, 200, 200);
        GridLayout grid = new GridLayout();
        grid.setColumns(1);
        grid.setRows(2);
        errorDialog.setLayout(grid);

        SimpleAttributeSet attribs = new SimpleAttributeSet();
        StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_CENTER);
        StyleConstants.setSpaceAbove(attribs, 30);
        tError.setParagraphAttributes(attribs, true);
        tError.setEditable(false);
        errorDialog.add(tError);

        bOkError.addActionListener(this);

        errorDialog.add(bOkError);
        errorDialog.setVisible(false);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int i = JOptionPane.showConfirmDialog(null, "Do you want to leave?");
                if (i == 0)
                    System.out.println("Closing this connection : " + socket);
                int premabula = -1;
                try {
                    output.write(prepareBytes("exit".getBytes(), (byte) premabula));
                } catch (IOException | NullPointerException ex) {
                    System.out.println("Closing app. There was problem with connection");
                    runReadThread = false;
                    ex.printStackTrace();
                    System.exit(0);
                }
                System.out.println("Connection closed");
                runReadThread = false;
                System.exit(0);

            }
        });

        setVisible(true);
    }

    public class TextAreaWithPrompt extends JTextArea {

        @Override
        protected void paintComponent(java.awt.Graphics g) {
            super.paintComponent(g);

            if (getText().isEmpty() && !(FocusManager.getCurrentKeyboardFocusManager().getFocusOwner() == this)) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setBackground(Color.gray);
                g2.setFont(getFont().deriveFont(Font.ITALIC));
                g2.drawString("Wpisz wiadomosc lub scieżke pliku", 5, 10); //figure out x, y from font's FontMetrics and size of component.
                g2.dispose();
            }
        }

    }

    public void connectToSocket(int port)
    {
        try
        {
            InetAddress ip = InetAddress.getByName("localhost");
            socket = new Socket(ip, port);
            input = new DataInputStream(this.socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

            receiveMessages();
        }
        catch (UnknownHostException ex)
        {
            System.out.println("Server unavaible: " + ex.getMessage());
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    private byte[] prepareBytes(byte[] bytes, byte preambula)
    {
        byte[] bytesToSend = new byte[bytes.length + 1];

        bytesToSend[0] = preambula;
        System.arraycopy(bytes, 0, bytesToSend, 1, bytes.length);

        return bytesToSend;
    }

    @Override
    public void receiveMessages() {
        Thread reciveMessageThread = new Thread(new Runnable() {
            @Override
            public void run() {
                ChainElement textElement = new TextElement((byte) 0);
                ChainElement imageElement = new Image((byte) 1);
                ChainElement audioElement = new AudioElement((byte) 2);
                ChainElement audioFormatElement = new AudioFormatElement((byte) 3);
                ChainElement audioFile = new AudioFile((byte) 4);

                textElement.addNextElement(imageElement);
                imageElement.addNextElement(audioElement);
                audioElement.addNextElement(audioFormatElement);
                audioFormatElement.addNextElement(audioFile);

                AudioFormat audioFormat = null;
                AudioFileFormat.Type audioFileFormat = null;

                while(runReadThread)
                {
                    try
                    {
                        byte[] receivedMessage;
                        int messageSize = 0;

                        if(!welcomed)
                        {
                            nickname = input.readUTF();
                            welcomed = true;
                        }
                        else
                        {
                            ArrayList<Byte> message = new ArrayList<Byte>();

                            while(input.available() !=0 )
                            {
                                messageSize = input.available();
                                receivedMessage = new byte[messageSize];
                                input.read(receivedMessage);

                                for(int i = 0; i < messageSize; i++)
                                    message.add(receivedMessage[i]);
                            }

                            if(messageSize != 0)
                            {
                                receivedMessage = new byte[message.size()];

                                for(int i = 0; i < message.size(); i++)
                                    receivedMessage[i] = message.get(i);

                                MessageTemplate messageTemplate = new MessageTemplate(receivedMessage);

                                Object handledMessage = textElement.handleReqest(messageTemplate);
                                MessageHandler messageHandler = new MessageHandler(handledMessage);

                                if(handledMessage instanceof String){
                                    messageHandler.handleText(tMessages);
                                } else if(handledMessage instanceof BufferedImage){
                                    int imageSize = messageTemplate.getBytes().length / 1024;
                                    messageHandler.handleImage(imageSize);
                                } else if(handledMessage instanceof AudioFileFormat.Type){
                                    audioFileFormat = (AudioFileFormat.Type) handledMessage;
                                } else if(handledMessage instanceof AudioFormat){
                                    audioFormat = (AudioFormat) handledMessage;
                                } else if(handledMessage instanceof byte[]){
                                    messageHandler.handleAudio(handledMessage, audioFormat, audioFileFormat);
                                    audioFormat = null;
                                }
                                message.clear();
                            }
                        }
                    }
                    catch (EOFException ex)
                    {
                        System.out.println("Receiving thred is closed");
                    }
                    catch (IOException ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object p = e.getSource();
        if(p == bSend)
        {
            try
            {
                String messageToSend = nickname + ": " + tNewMessage.getText();
                if(!messageToSend.equals(nickname + ": "))
                    if(messageToSend.equals("exit"))
                    {
                        System.out.println("Closing this conection: " + socket);
                        int premabula = -1;
                        output.write(prepareBytes(messageToSend.getBytes(), (byte) premabula));
                        System.out.println("Conection closed");
                        runReadThread = false;
                    }
                    else
                    {
                        int premabula = 0;
                        output.write(prepareBytes(messageToSend.getBytes(), (byte) premabula));
                    }
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
            tNewMessage.setText("");
        }
        else if(p == bSendImage)
        {
            try
            {
                String path = tNewMessage.getText();

                if(!path.equals(""))
                {
                    File file = new File(path);

                    BufferedImage img = ImageIO.read(file);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    ImageIO.write(img, "jpg", outputStream);

                    int premabula = 1;
                    output.write(prepareBytes(outputStream.toByteArray(), (byte) premabula));
                }
                else
                {
                    tError.setText("Podano zla sciezke");
                    errorDialog.setVisible(true);
                }
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
            tNewMessage.setText("");
        }
        else if(p == bSendMusic)
        {
            try
            {
                String path = tNewMessage.getText();

                if(!path.equals(""))
                {
                    File file = new File(path);

                    InputStream inputStream = AudioSystem.getAudioInputStream(file);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                    AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
                    AudioFileFormat.Type targetFileType = fileFormat.getType();
                    AudioFormat audioFormat = fileFormat.getFormat();

                    String[] tempAudioFormatArray = audioFormat.toString().split(" ");
                    boolean bigEndian = tempAudioFormatArray[tempAudioFormatArray.length - 1].equals("big-endian");

                    String audioFormatString = audioFormat.getEncoding().toString() + " " +
                            audioFormat.getSampleRate() + " " +
                            audioFormat.getSampleSizeInBits() + " " +
                            audioFormat.getChannels() + " " +
                            audioFormat.getFrameSize() + " " +
                            audioFormat.getFrameRate() + " " +
                            bigEndian;

                    int preambula = 3;
                    output.write(prepareBytes(audioFormatString.getBytes(), (byte) preambula));

                    String audioFileFormatTypeString = targetFileType.toString();

                    preambula = 4;
                    output.write(prepareBytes(audioFileFormatTypeString.getBytes(), (byte) preambula));

                    int read;
                    byte[] buff = new byte[1024];
                    while((read = inputStream.read(buff)) > 0)
                        outputStream.write(buff, 0, read);

                    outputStream.flush();
                    byte[] audioBytes = outputStream.toByteArray();

                    preambula = 2;

                    output.write(prepareBytes(audioBytes, (byte) preambula));
                }
                else
                {
                    tError.setText("Podano zla sciezke");
                    errorDialog.setVisible(true);
                }
            }
            catch (IOException | UnsupportedAudioFileException ex)
            {
                ex.printStackTrace();
            }
            tNewMessage.setText("");
        }
        else if(p == bOkError)
        {
            errorDialog.setVisible(false);
            tNewMessage.setText("");
        }
    }
}

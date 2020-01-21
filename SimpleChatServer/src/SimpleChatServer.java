import java.io.*;
import java.net.*;
import java.util.*;

public class SimpleChatServer {
  ArrayList<PrintWriter> clientOutputStreams;

  class ClientHandler implements Runnable {
    BufferedReader reader;
    Socket socket;

    public ClientHandler(Socket clientSocket){
      try {
        socket = clientSocket;
        InputStreamReader isReader = new InputStreamReader(socket.getInputStream());
        reader = new BufferedReader(isReader);
      } catch (Exception ex) {ex.printStackTrace();}
    } // close constructor

    public void run() {
      String message;
      try {
        while((message = reader.readLine()) != null) {
          System.out.println("message received: " + message);
          broadcastMessage(message);
        } // close while
      } catch (Exception ex) {ex.printStackTrace();}
    } // close Run
  } // close inner class

  public static void main(String[] args) {
    SimpleChatServer server = new SimpleChatServer();
    server.start();
  } // close main

  public void start() {
    clientOutputStreams = new ArrayList<>();
    try {
      ServerSocket serverSocket = new ServerSocket(5000);

      while(true) {
        Socket clientSocket = serverSocket.accept();
        PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
        clientOutputStreams.add(writer);

        Thread t = new Thread(new ClientHandler(clientSocket));
        t.start();
        System.out.println("Connected...");
      }
    } catch(Exception ex) {
      ex.printStackTrace();
    }
  } // close go

  public void broadcastMessage(String message) {
    Iterator it = clientOutputStreams.iterator();
    while(it.hasNext()) {
      try {
        PrintWriter writer = (PrintWriter) it.next();
        writer.println(message);
        writer.flush();
      } catch(Exception ex) {
        ex.printStackTrace();
      }
    } // end while
  } // close tellEveryone
} //close class

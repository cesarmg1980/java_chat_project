import jdk.nashorn.internal.scripts.JO;

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;


public class SimpleChatClient {

  private JTextArea incoming;
  private JTextField outgoing;
  private BufferedReader reader;
  private PrintWriter writer;
  private Socket socket;
  private final String user = System.getProperty("user.name");

  public static void main(String[] args) {
    SimpleChatClient client = new SimpleChatClient();
    client.start();
  }

  public void start() {
    // Window Frame
    JFrame frame = new JFrame("Simple Chat Client");
    // Panel to put the TextBox and Button
    JPanel mainPanel = new JPanel();
    // TextBox for the incoming messages
    incoming = new JTextArea(15, 50);

    // Configuration for the incoming TextBox
    incoming.setLineWrap(true);
    incoming.setWrapStyleWord(true);
    incoming.setEditable(false);

    // Adding a Scroller to the incoming TextBox
    JScrollPane qScroller = new JScrollPane(incoming);
    qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    // Outgoing TextBox
    outgoing = new JTextField(20);

    //Send Button
    JButton sendButton = new JButton("Send Message");
    //Registering the button as an ActionListener
    sendButton.addActionListener(new SendButtonListener());

    //Adding Scroller, outgoing and button to the main panel
    mainPanel.add(qScroller);
    mainPanel.add(outgoing);
    mainPanel.add(sendButton);

    //Adding the mainPanel to the Frame
    frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
    frame.setSize(600, 350);
    frame.setVisible(true);

    // Request for the server's IP
    String serverIP = JOptionPane.showInputDialog("Please enter the Server's IP Address");
    //Setting up the Network configuration
    setUpNetworkDetails(serverIP, frame);

    //Creating a new Thread worker with an IncomingReader Job
    Thread readerThread = new Thread(new IncomingReader());
    readerThread.start();

  } //close go

  private void setUpNetworkDetails(String serverIP, JFrame f) {
    try {
      socket = new Socket(serverIP, 5000);
      InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
      reader = new BufferedReader(streamReader);
      writer = new PrintWriter(socket.getOutputStream());
      System.out.println("networking established.");
    } catch (ConnectException ex) {
      JOptionPane.showMessageDialog(f, "IP Address not Valid or Server is not running, cannot start Chat Client!");
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  } // close setUpNetworking

  class SendButtonListener implements ActionListener {

    public void actionPerformed(ActionEvent ev) {
      DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
      LocalDateTime now = LocalDateTime.now();

      try {
        writer.println("On " + dateTimeFormat.format(now) + " " + user + " said: " + outgoing.getText());
        writer.flush();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      outgoing.setText("");
      outgoing.requestFocus();
    }
  } // close SendButtonListener class

  class IncomingReader implements Runnable {
    public void run() {
      String message;
      try {
        while ((message = reader.readLine()) != null) {
          System.out.println("read " + message);
          incoming.append(message + "\n");
        } //close while
      } catch (NullPointerException ex) {
        System.out.println("Error! Server appears to not be available!");
      } catch (SocketException ex) {
        System.out.println("Connection with the server was lost!");
        incoming.append("Connection with the server was lost!");
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    } // close run

  } // close inner class IncomingReader

} // close SimpleChatClient class

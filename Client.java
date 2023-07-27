import java.net.*; //Package is used for networking in Java, providing classes to work with URLs, sockets, and other network-related functionality.

import javax.swing.BorderFactory; //Package is used to create borders for GUI elementsto provide visual distinction and organization in user interfaces.
import javax.swing.ImageIcon; //To use image in Windows Frames

//Swing package is used for GUI
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.awt.BorderLayout; //Package to layout the Border of Window Frame
import java.awt.Font; //Package for Fonts
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener; //To listen the input 
import java.io.*; //Package for Input & Output

public class Client extends JFrame { // By extending JFrame, the "Client" class inherits all the properties and
                                     // methods of JFrame,
                                     // allowing you to customize and create your own window-based application using
                                     // Swing components.

    // Variable of Client
    Socket socket;

    // To read and write
    BufferedReader br;
    PrintWriter out;

    // Declare GUI Components
    private JLabel heading = new JLabel("Client Area"); // Heading of GUI
    private JTextArea messageArea = new JTextArea(); // Area where message will be shown
    private JTextField messageInput = new JTextField(); // Field where input can be given
    private Font font = new Font("Roboto", Font.PLAIN, 20);

    // Client Constructor
    public Client() {

        try {

            System.out.println("Sending request to Server");
            socket = new Socket("127.0.0.1", 7775);
            System.out.println("Connection done.");

            // to read
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // to write
            out = new PrintWriter(socket.getOutputStream());

            // To create GUI
            createGUI();

            // To handle occuring events
            handleEvents(); // Instead of startWriting()

            startReading();
            // startWriting();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void createGUI() {

        // GUI code
        // this means Windows frame
        this.setTitle("Client Messager"); // Heading or Title shown on Windows Frame
        this.setSize(600, 600); // Defining Height and Width of the Window Frame
        this.setLocationRelativeTo(null); // Sets Window Frame to center of the screen
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // When clicked on cross, the window frame will close

        // Coding for component - to set Font for GUI component
        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);

        // To set the heading component
        heading.setIcon(new ImageIcon("cc.png")); // To show logo in Frame
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setHorizontalAlignment(SwingConstants.CENTER); // To Show "Client Area" Text in the center of the frame
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // BorderFactory is a class which has many
                                                                            // lists of borders

        messageArea.setEditable(false);
        // To set the messageInput component
        messageInput.setHorizontalAlignment(SwingConstants.LEFT);

        // To set layout of the Window Frame
        this.setLayout(new BorderLayout());
        this.add(heading, BorderLayout.NORTH);
        JScrollPane jScrollPane = new JScrollPane(messageArea); //For Scrollable view
        this.add(jScrollPane, BorderLayout.CENTER);
        this.add(messageInput, BorderLayout.SOUTH);

        this.setVisible(true); // To make Window Frame visible
    }

    private void handleEvents() {

        messageInput.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

                // System.out.println("key released" + e.getKeyCode());

                if (e.getKeyCode() == 10) {

                    // System.out.println("You have pressed Enter button");

                    String contentToSend = messageInput.getText(); // Assigns the text content entered in GUI input
                                                                   // field (messageInput) to a String var
                                                                   // i.e.contentToSend
                    messageArea.append("Me: " + contentToSend + "\n");
                    out.println(contentToSend); // To print the input
                    out.flush(); // It flushes the output stream, ensuring that any buffered data is immediately
                                 // sent to its destination, typically the console or file
                    messageInput.setText(""); // Sets the text of the GUI input field (messageInput) to an empty string,
                                              // effectively clearing its content
                    messageInput.requestFocus(); // Sets the focus on the GUI input field (messageInput), making it
                                                 // ready to receive user input or keyboard events
                }
            }

        });
    }

    public void startReading() {

        // Thread which will read
        Runnable r1 = () -> {

            System.out.println("Reader started!");

            try {

                // To read infinite time or as much time as user gives input
                while (true) {

                    // To read input
                    String msg = br.readLine();

                    // If user gives input as "exit"
                    if (msg.equals("exit")) {

                        System.out.println("Server has terminated the chat!");

                        JOptionPane.showMessageDialog(this, "Server terminated the chat"); // Displays a dialog box with
                                                                                           // the message "Server
                                                                                           // terminated the chat,"
                        // and the "this" parameter refers to the current GUI window or component where
                        // the dialog should be shown.
                        messageInput.setEnabled(false); // Disables the GUI input field, making it non-editable and
                                                        // prevents user from entering any text or interacting with it.

                        socket.close(); // To Terminate the connection
                        break;
                    }

                    // To print
                    // System.out.println("Server: " + msg);
                    messageArea.append("Server: " + msg + "\n");
                }

            } catch (Exception e) {

                // e.printStackTrace();
                System.out.println("Connection closed!");
            }
        };

        // To start the r1 thread
        new Thread(r1).start();
    }

    public void startWriting() {

        // Thread which will take input from user and will send to client

        Runnable r2 = () -> {

            System.out.println("Writer started!");

            try {

                // To write or print infinite time
                while (!socket.isClosed()) {

                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));

                    // To read input
                    String content = br1.readLine();

                    // To print
                    out.println(content);
                    out.flush(); // `out.flush()` is a method used to force any buffered data in the output
                                 // stream to be written immediately to its destination,
                                 // ensuring that the data is not held in the buffer and is made available for
                                 // consumption or storage.

                    if (content.equals("exit")) {

                        socket.close();
                        break;
                    }
                }

            } catch (Exception e) {

                // e.printStackTrace(); // e.printStackTrace(); // e.printStackTrace() is a
                // method in Java that is used to print the stack trace of an exception.
                // It is commonly used for debugging purposes to identify the cause of an
                // exception and to trace the sequence of method calls that led to the
                // exception.

                System.out.println("Connection closed!");
            }
        };

        // To start the r2 thread
        new Thread(r2).start();

    }

    public static void main(String[] args) {

        System.out.println("This is Client");
        new Client();
    }
}

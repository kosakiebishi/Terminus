import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * Klasa Glowna TerminusGUIServer dzidziczaca po klasie JFrame
 * @author greg
 */
public class TerminusGUIServer extends JFrame {

        
	private JButton buttonRun, buttonStop;
	private JPanel jPanel;
	private JTextField jPort;
	private JTextArea jMessages;
        private JList jUsers;
        

	private int portNumber = 11023;
	private boolean isRun = false;
	private Vector<Connection> myClients = new Vector<Connection>();
        private Vector<String> usersVector = new Vector<String>();
        
        private DefaultListModel listOfThread;
        

        /**
         * Konstruktor Klasy TerminusGUIServer
         * jego zadaniem jest ustawienia wartosci zmiennych
         * odpowiadajacych za wyswietlanie okien graficznych
         */
	public TerminusGUIServer() {
		super("Terminus - Server - GUI");
		setSize(850,500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		jPanel = new JPanel(new FlowLayout());
		jMessages = new JTextArea();
		jMessages.setLineWrap(true);
		jMessages.setEditable(false);
                
                listOfThread = new DefaultListModel();
                
                jUsers = new JList(listOfThread);
                jUsers.setFixedCellWidth(120);
		
		jPort = new JTextField(new Integer(portNumber).toString(), 8);
		buttonRun = new JButton("Uruchom");
		buttonStop = new JButton("Zatrzymaj");
		buttonStop.setEnabled(false);

		TerminusActionListener obsluga = new TerminusActionListener();

		buttonRun.addActionListener(obsluga);
		buttonStop.addActionListener(obsluga);
                

		jPanel.add(new JLabel("Port: "));
		jPanel.add(jPort);
		jPanel.add(buttonRun);
		jPanel.add(buttonStop);

		add(jPanel, BorderLayout.NORTH);
		add(new JScrollPane(jMessages, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
                add(new JScrollPane(jUsers, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.SOUTH);

		setVisible(true);
                
                

                MouseListener mouseListener = new MouseAdapter() {
                    /**
                    * Metoda odpowiadajaca za stworzenie listenera na JList 
                    * dwukrotne klikniecie lewym przyciskiem myszy powoduje akcje
                    */
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) {

                                String selectedItem = (String) jUsers.getSelectedValue();
                                sendLog(" ! You have selected: " + selectedItem + "\n");

                         }
                    }
                };
                
                jUsers.addMouseListener(mouseListener);
	
        
        }

        
        /**
         * Klasa TerminusActionListener implementujacy interfejsc ActionListener
         * odpowiada z akcje zwiaze z klikniecie na przycisk uruchom, oraz zatrzymaj
         */
	private class TerminusActionListener implements ActionListener {

		private MyServer srv;

                /**
                 * Metoda actionPerformed wykonujaca akcje w sytuacji klijniecia na przycisk uruchom, lub zakoncz
                 * @param e 
                 */
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("Uruchom")) {
				srv = new MyServer();
				srv.start();
				isRun = true;
				buttonRun.setEnabled(false);
				buttonStop.setEnabled(true);
				jPort.setEnabled(false);
				repaint();
			}
		
			if (e.getActionCommand().equals("Zatrzymaj")) {
				srv.kill();
				isRun = false;
				buttonStop.setEnabled(false);
				buttonRun.setEnabled(true);
				jPort.setEnabled(true);
				repaint();
			}
		}
                
                
	}

        /**
         * Klasa MyServer rozszerzajaca klase Thread
         * Klasa odpowiada za nawiazanie polaczenia z clientem
         */
	private class MyServer extends Thread {

		private ServerSocket server;

                /**
                 * Metoda kill odpowiadajaca za zatrzymanie serwera
                 */
		public void kill() {
			try {
				server.close();

				for (Connection client : myClients) {
					try { 
						client.out.println(" ! Server stoped");
						client.socket.close();
					} catch (IOException e) { }
				}

				sendLog(" ! All connection has been closed. \n");
			} catch (IOException e) { }
		}

                /**
                 * Metoda run odpowiadajaca za uruchomienie serwera
                 */
		public void run() {
			try {
				server = new ServerSocket(new Integer(jPort.getText()));
				sendLog(" ! Server runs on port: " + jPort.getText() + "\n");
				
				while (isRun) {
					Socket socket = server.accept();
					sendLog(" ! New connection. \n");
                                        

                                        
                                        // !!!!!!!!!!! new connection !!!!!!!!!!!
					new Connection(socket).start();
				}
			} catch (SocketException e) {
			} catch (Exception e) {
				sendLog(e.toString());
			} finally {
                                try {
                                    if(server != null) server.close();
                                } catch (IOException e) {
                                    sendLog(e.toString());
                                }
                        }
                        sendLog(" ! Server has been stoped \n\n");
                        usersVector.removeAllElements();
                        listOfThread.removeAllElements();
		}
		
	}


    /**
     * Klasa prywatna Connection dziedziczaca po klasie Thread
     * odpowiada za uruchomienie watku w sytuacji podlaczenia klienta
     */
    private class Connection extends Thread {

            private BufferedReader in;
            private PrintWriter out;
            private Socket socket;
            private String thread;
            private String line;

            /**
             * Konstruktor klasy Connection przyjmujaca jeden parametr socket
             * @param socket parametr przekazywany w chwili zestawienia polaczenia
             */
            public Connection(Socket socket) {
                    this.socket = socket;

                    synchronized(myClients) {
                            myClients.add(this);
                    }
            }

            /**
             * Metoda run uruchamiajaca sesje klienta
             */
            public void run() {

                    try { 
                            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                            InetAddress addr = socket.getInetAddress();
                            thread = this.toString();
                            
                            // socket to string to listOfThreads
                            //listOfThread.addElement(thread);
                            listOfThread.addElement(socket.toString());
                            
                            System.out.println("socket::: " + socket.toString());
                            sendLog(" ## New client has joined: " + thread + " /// " + socket.toString() + "\n");
                            
                            TerminusGame terminus = new TerminusGame(thread);
                            out.println(terminus.introduction() + "\n\r> ");


                            while (isRun && !(line = in.readLine()).equalsIgnoreCase("exit")) {
                                    out.println(terminus.nextCommand(line) + "\n\r> ");
                            }

                            out.println("Zegnaj\n");
                            usersVector.remove(thread);

                            synchronized(myClients) {
                                    myClients.remove(this);                                   
                                    System.out.println(this);
                            }

                    } catch (Exception e) {
                    } finally {
                            try {
                                    in.close();
                                    out.close();
                                    socket.close();
                                    
                                    sendLog(" ! Connection has been stoped\n");
                                    
                                    //listOfThread.removeElement(thread);
                                    listOfThread.removeElement(socket.toString());
                                    
                            } catch (IOException e) { }
                    }
            }
    }

            /**
             * Metoda sendLog zwracajaca logi do pola tekstowego serwera
             * @param tekst 
             */
            private void sendLog(String tekst) {
                            jMessages.append(tekst);
                            jMessages.setCaretPosition(jMessages.getDocument().getLength());
            }
            
            /**
             * Metoda glowna main tworzaca obiekt klasy TerminusGUIServer
             * @param args 
             */
            public static void main(String[] args) {
                            new TerminusGUIServer();
            }


    }
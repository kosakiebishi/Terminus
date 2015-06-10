import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class TerminusGUIServer extends JFrame {

	private JButton buttonRun, buttonStop;
	private JPanel jPanel;
	private JTextField jPort;
	private JTextArea jMessages;
        private JTextArea jUsers;

	private int portNumber = 11023;
	private boolean isRun = false;
	private Vector<Connection> myClients = new Vector<Connection>();
        private Vector<String> usersVector = new Vector<String>();
        


	public TerminusGUIServer() {
		super("Terminus - Server - GUI");
		setSize(650,500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		jPanel = new JPanel(new FlowLayout());
		jMessages = new JTextArea();
		jMessages.setLineWrap(true);
		jMessages.setEditable(false);
                
                jUsers = new JTextArea();
                jUsers.setLineWrap(true);
                jUsers.setEditable(false);
                //jUsers.setColumns(20);
                jUsers.setRows(10);
                
		
		jPort = new JTextField(new Integer(portNumber).toString(), 8);
		buttonRun = new JButton("Uruchom");
		buttonStop = new JButton("Zatrzymaj");
		buttonStop.setEnabled(false);

		ObslugaZdarzen obsluga = new ObslugaZdarzen();

		buttonRun.addActionListener(obsluga);
		buttonStop.addActionListener(obsluga);

		jPanel.add(new JLabel("Port: "));
		jPanel.add(jPort);
		jPanel.add(buttonRun);
		jPanel.add(buttonStop);

		add(jPanel, BorderLayout.NORTH);
		add(new JScrollPane(jMessages), BorderLayout.CENTER);
                add(new JScrollPane(jUsers), BorderLayout.SOUTH);

		setVisible(true);
	}

	private class ObslugaZdarzen implements ActionListener {

		private MyServer srv;

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

	private class MyServer extends Thread {

		private ServerSocket server;

		public void kill() {
			try {
				server.close();

				for (Connection klient : myClients) {
					try { 
						klient.wyjscie.println("Serwer przestal dzialac!");
						klient.socket.close();
					} catch (IOException e) { }
				}

				sendLog("Wszystkie Polaczenie zastaly zakonczone. \n");
			} catch (IOException e) { }
		}

		public void run() {
			try {
				server = new ServerSocket(new Integer(jPort.getText()));
				sendLog("Serwer uruchomiony na porcie: " + jPort.getText() + "\n");
				
				while (isRun) {
					Socket socket = server.accept();
					sendLog("Nowe polaczenie. \n");
                                        
                                        
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
                        sendLog("Serwer zatrzymany");
                        usersVector.removeAllElements();
                        updateUsers();
		}
		
	}





    private class Connection extends Thread {

            private BufferedReader wejscie;
            private PrintWriter wyjscie;

            private Socket socket;
            private String nick;

            private String linia;

            public Connection(Socket socket) {
                    this.socket = socket;

                    synchronized(myClients) {
                            myClients.add(this);
                    }
            }

            public void run() {

                    try { 
                            wejscie = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            wyjscie = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

                            wyjscie.println("\n\r\n\rWitam w grze Terminus.\nJest to gra przygodowa z elementami RPG.");
                            //wyjscie.println("\n\rPodaj swe imie dzielny poszukiwaczu przygod: ");
                            //nick = wejscie.readLine();
                            
                            InetAddress addr = socket.getInetAddress();
                            
                            nick = addr.getHostAddress() + " - "+ this;
                            

                            sendLog(" > Join: " + nick + "\n");
                            
                            usersVector.add(nick);
                            updateUsers();
                            
                            TerminusGame terminus = new TerminusGame(nick);
                            wyjscie.println(terminus.introduction());

                            while (isRun && !(linia = wejscie.readLine()).equalsIgnoreCase("exit")) {
                                    //sendLog(linia);
                                    wyjscie.println(terminus.nextCommand(linia));
                            }

                            wyjscie.println("Zegnaj\n");
                            usersVector.remove(nick);
                            updateUsers();

                            synchronized(myClients) {
                                    myClients.remove(this);
                            }

                            sendLog("Polaczenie zostalo zakonczone\n");

                    } catch (Exception e) {
                    } finally {
                            try {
                                    wejscie.close();
                                    wyjscie.close();
                                    socket.close();
                            } catch (IOException e) { }
                    }
            }
    }


            private void sendLog(String tekst) {
                            jMessages.append(tekst);
                            jMessages.setCaretPosition(jMessages.getDocument().getLength());
            }
            
            private void updateUsers() {
                
                jUsers.setText("");
                
                System.out.println(usersVector.toString());
                
                for (int i = 0; i < usersVector.size(); i++) {
                    jUsers.append(usersVector.get(i) + "\n");
                }
            }
            
            
            public static void main(String[] args) {
                            new TerminusGUIServer();
            }


    }
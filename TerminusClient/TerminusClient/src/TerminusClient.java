import java.io.*;
import java.net.*;
import java.util.*;

public class TerminusClient {
        static private Socket socket;
        static private final int PORT = 11023;
        static private final String SERVER = "localhost";
        static private final String PROMPT = "> ";
        public static void main(String[] args) {

                Scanner keyboard = null;
                Scanner in = null;
                PrintWriter out = null;
                try {
                        System.out.println("Łącze się z serwerem na porcie " + PORT);
                        socket = new Socket(SERVER, PORT);
                        out = new PrintWriter(socket.getOutputStream(), true);
                        in = new Scanner(socket.getInputStream());
                        keyboard = new Scanner(System.in);

                } catch (UnknownHostException e) { return;
                } catch (IOException e) { return; }

                System.out.println(in.nextLine());
                boolean koniec = false;

                while ( !koniec ) { 
                        System.out.print(PROMPT);
                        out.println(keyboard.next());
                        System.out.println(in.nextLine());
                           
                }   

                System.out.println ("Połączenie zostało zakończone.");

                try {
                        socket.close();
                } catch (IOException e) { } 
        }   
}
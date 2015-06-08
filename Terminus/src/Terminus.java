import java.io.*;
import java.util.*;
import java.net.*;

        


public class Terminus {
    
    private static final int PORT = 2345;
    private ServerTerminus srv;
    
    public Terminus() {
        
        System.out.println("Terminus server: \t inicialization");
        
        srv = new ServerTerminus();
        srv.start();
        
    }
    
    private class ServerTerminus extends Thread {
        
        private ServerSocket server;
        
        public void run() {
            System.out.println("Therad");
        }
        
    }
    
    
    public static void main(String[] args) {
        new Terminus();
    }
    
}

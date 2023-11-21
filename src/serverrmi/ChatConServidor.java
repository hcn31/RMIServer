package serverrmi;

import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;

/**
 *
 * @author Efraim Rodrigues
 */
public class ChatConServidor {

    public ChatConServidor() {
        try {
            
            System.setProperty("java.rmi.server.hostname", "localhost");
            
            LocateRegistry.createRegistry(1099);
            
            ChatConInterface server = new ChatCon();
            Naming.bind("ServidorChat", (Remote) server);
            System.out.println("Server online.");
        } catch (Exception e) {
            System.out.println("Trouble: " + e.toString());
        }
    }

    public static void main(String args[]) {
        new ChatConServidor();
    }
}
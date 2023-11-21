//HCN
package serverrmi;

import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;

public class ChatConServidor {

    public ChatConServidor() {
        try {
            
            System.setProperty("java.rmi.server.hostname", "localhost");
            
            LocateRegistry.createRegistry(1099);
            
            ChatConInterface server = new ChatCon();
            Naming.bind("myServer", (Remote) server);
            System.out.println("Server running...");
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    public static void main(String args[]) {
        new ChatConServidor();
    }
}
//HCN
package server;

import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;

public class Server {

    public Server() {
        try {
            
            System.setProperty("java.rmi.server.hostname", "localhost");
            
            LocateRegistry.createRegistry(1099);
            
            Forum server = new ForumImp();
            Naming.bind("myServer", (Remote) server);
            System.out.println("Server running...");
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    public static void main(String args[]) {
        new Server();
    }
}
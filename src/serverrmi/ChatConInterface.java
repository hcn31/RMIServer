//HCN
package serverrmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ChatConInterface extends Remote {
	abstract public ArrayList<String> getUsers() throws RemoteException;
    abstract public String getPublicKey(String username) throws RemoteException;
    abstract public boolean isOnline(String username) throws RemoteException;
    abstract public void supprimerUser(String username) throws RemoteException;
	abstract public void envoyerMessage(String user, String mensagem) throws RemoteException;
    abstract public ChatConUserInterface ajouterUser(String username, String publicKey) throws RemoteException;
        
}
//HCN
package serverrmi;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ChatConUserInterface extends Remote,Serializable {
    abstract void envoyerMessage(String mensagem) throws RemoteException;
    abstract ArrayList<String> getMessages() throws RemoteException;
    abstract String getPublicKey() throws RemoteException;
    abstract Integer getCounter() throws RemoteException;
}
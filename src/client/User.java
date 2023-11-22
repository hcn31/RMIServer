package client;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface User extends Remote,Serializable {
    abstract void envoyerMessage(String user,String message) throws RemoteException;
    abstract ArrayList<String> getMessages() throws RemoteException;
    abstract String getPublicKey() throws RemoteException;
    abstract Integer getCounter() throws RemoteException;
}
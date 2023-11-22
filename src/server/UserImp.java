package server;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

// La classe UserImp implémente les interfaces Serializable et User
public class UserImp extends UnicastRemoteObject implements Serializable, User {

    private final String publicKey; // Stocke la clé publique de l'utilisateur
    private Integer counter; // Garde une trace du nombre de messages envoyés
    private final ArrayList<String> messages; // Stocke les messages envoyés par l'utilisateur
    
    // Constructeur
    UserImp(String publicKey) throws RemoteException {
        super(); // Appelle le constructeur de la superclasse (UnicastRemoteObject)
        messages = new ArrayList<>(); // Initialise la liste des messages
        counter = 0; // Initialise le compteur
        this.publicKey = publicKey; // Définit la clé publique de l'utilisateur
    }

    // Méthode pour envoyer un message
    @Override
    public void envoyerMessage(String user,String message) throws RemoteException {
        synchronized (messages) {
            this.counter++; // Incrémente le compteur de messages
            messages.add(message); // Ajoute le message à la liste
            System.out.println(user+": "+message); // Affiche le message dans la console
        }
    }
    
    // Méthode pour récupérer la liste des messages
    @Override
    public ArrayList<String> getMessages() throws RemoteException {
        return messages; // Retourne la liste des messages
    }

    // Méthode pour récupérer la clé publique de l'utilisateur
    @Override
    public String getPublicKey() throws RemoteException {
        return publicKey; // Retourne la clé publique de l'utilisateur
    }

    // Méthode pour récupérer le compteur de messages
    @Override
    public Integer getCounter() throws RemoteException {
        return counter; // Retourne le compteur de messages
    }
}

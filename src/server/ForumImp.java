package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

// La classe ForumImp implémente l'interface Forum
public class ForumImp extends UnicastRemoteObject implements Forum {

    //pour stocker les utilisateurs du forum
    private final HashMap<String, User> users;

    // Constructeur
    public ForumImp() throws RemoteException {
        super();
        this.users = new HashMap<String, User>();
    }

    // Méthode pour envoyer un message d'un utilisateur aux autres utilisateurs
    public void envoyerMessage(String user, String message) throws RemoteException {
        users.get(user).envoyerMessage(user,message);
    }

    // Méthode pour ajouter un utilisateur au forum
    @Override
    public User ajouterUser(String username, String publicKey) throws RemoteException {
        User user = new UserImp(publicKey); // Crée un nouvel utilisateur avec la clé publique fournie
        this.users.put(username, user); // Ajoute l'utilisateur à la liste des utilisateurs du forum
        return user; // Retourne l'utilisateur ajouté
    }

    // Méthode pour supprimer un utilisateur du forum
    @Override
    public void supprimerUser(String username) throws RemoteException {
        this.users.remove(username); // Supprime l'utilisateur de la liste des utilisateurs du forum
        System.out.println(username + " bye, exit\n"); // Affiche un message indiquant que l'utilisateur a quitté
    }
    
    // Méthode pour récupérer la liste des utilisateurs du forum
    @Override
    public ArrayList<String> getUsers() throws RemoteException {
        ArrayList<String> ret = new ArrayList<String>();
        // Ajoute chaque nom d'utilisateur à la liste de ret
        users.keySet().stream().forEach((key) -> {
            ret.add(key);
        });
        return ret; // Retourne la liste des utilisateurs
    }
    
    // Méthode pour récupérer la clé publique d'un utilisateur donné
    @Override
    public String getPublicKey(String username) throws RemoteException {
        return users.get(username).getPublicKey(); // Retourne la clé publique de l'utilisateur spécifié
    }

    // Méthode pour vérifier si un utilisateur est en ligne
    @Override
    public boolean isOnline(String username) throws RemoteException {
        return users.containsKey(username); // Retourne true si l'utilisateur est en ligne, sinon false
    }
}

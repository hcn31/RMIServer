package server;
import java.io.Serializable;
import java.math.BigInteger;
import java.rmi.*;
import java.util.ArrayList;
import java.rmi.RemoteException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.LinkedList;
import java.util.Queue;
import javafx.application.Platform;

import javax.crypto.Cipher;
// La classe Chat étend Thread et implémente Serializable
public class Chat extends Thread implements Runnable, Serializable {

    private String nom; // Nom de l'utilisateur
    private static Forum forum; // Instance du forum
    private static User user; // Instance de l'utilisateur

    private KeyPair keyPair; // Paire de clés pour le chiffrement RSA

    private static final Queue<String> messages = new LinkedList<>(); // File d'attente pour les messages
    // Constructeur
    public Chat() {
        nom = "";
        try {
            if (this.forum == null) {
                this.forum = (Forum) Naming.lookup("rmi://localhost:1099/myServer"); // Recherche du serveur RMI
            }
        } catch (Exception ex) {
           System.err.println(ex.getStackTrace());
        }
    }

    // Méthode pour obtenir le nom de l'utilisateur
    public String getNom() {
        return nom;
    }
    // Méthode pour définir le nom de l'utilisateur
    public void setNom(String name) {
        this.nom = name;
    }
    // Méthode pour obtenir le dernier message de la file d'attente
    public String getNewMessage() {
        String ret = "";
        if (messages.size() > 0) {
            ret = messages.poll();
            System.out.println(ret);
        }
        return ret;
    }

 // Méthode exécutée par le Thread pour traiter les nouveaux messages
    @Override
    public void run() {
        try {
            int cont = user.getCounter(); // Obtient le compteur de messages actuel de l'utilisateur

            int contDiff = 0;
            ArrayList<String> msgArray = null;

            while (true) {
                Thread.sleep(100); // Pause de 100 millisecondes pour éviter une utilisation excessive du processeur

                synchronized (forum) {
                    contDiff = user.getCounter(); // Obtient le compteur de messages mis à jour
                    msgArray = user.getMessages(); // Obtient la liste des messages de l'utilisateur
                }

                // Parcours des nouveaux messages depuis la dernière vérification du compteur
                for (; contDiff > cont; cont++) {
                    // Déchiffre le message et le stocke dans la file d'attente
                    this.stockMessage(decryptMessage(msgArray.get(msgArray.size() - (contDiff - cont))));
                }
            }
        } catch (Exception ex) {
            exit(); // Gestion d'erreur : ferme l'application en cas d'exception
            System.err.println(ex.getStackTrace());
        }
    }

    // Méthode pour envoyer un message
    public void envoyerMessage(String msg) {
        try {
            for (String user : forum.getUsers()) {
                forum.envoyerMessage(user, encryptMessage(nom + ": " + msg, getKeyFromString(forum.getPublicKey(user))));
            }
        } catch (Exception ex) {
            exit();
            System.err.println(ex.getStackTrace());
        }
    }

    // Méthode pour stocker un message dans la file d'attente
    private void stockMessage(String msg) {
        synchronized (messages) {
            if (!msg.equals("exit")) {
                messages.add(msg);
            }
        }
    }

    // Méthode pour obtenir la liste des utilisateurs en ligne
    public ArrayList<String> getUsersOnline() {
        ArrayList<String> ret = new ArrayList<>();
        try {
            synchronized (forum) {
                ArrayList<String> temp = forum.getUsers();

                for (String user : temp) {
                    ret.add(user);
                }
            }
        } catch (RemoteException ex) {
            exit();
            System.err.println(ex.getStackTrace());
        }

        return ret;
    }

    // Méthode pour vérifier si un utilisateur est en ligne
    public boolean isOnline(String username) {
        boolean ret = false;
        try {
            ret = forum.isOnline(username);
        } catch (RemoteException ex) {
            exit();
            System.err.println(ex.getStackTrace());
        }

        return ret;
    }

    // Méthode pour se connecter avec un nom d'utilisateur
    public void login(String nom) {
        try {
            setNom(nom);

            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(1024);
            keyPair = generator.genKeyPair();

            KeyFactory fact = KeyFactory.getInstance("RSA");

            RSAPublicKey publicKey = (RSAPublicKey)keyPair.getPublic();

            String pKey = publicKey.getModulus().toString() + "|" + publicKey.getPublicExponent().toString();

            user = forum.ajouterUser(nom, pKey);

            envoyerMessage("Je suis connecté !");
        } catch (Exception ex) {
             System.err.println(ex.getStackTrace());
        }
    }

    // Méthode pour supprimer un utilisateur
    private void supprimerUser(String nom) {
        try {
            forum.supprimerUser(nom);
        } catch (RemoteException ex) {
             System.err.println(ex.getStackTrace());
        }
    }

    // Méthode pour quitter l'application
    public void exit() {
        envoyerMessage("Je suis déconnecté !");
        supprimerUser(getNom());
        Platform.exit();
        System.exit(0);
    }

    // Méthode pour déchiffrer un message
    public String decryptMessage(String encryptedMessage) {
        String ret = "";
        try {
            Cipher cipher = Cipher.getInstance("RSA");

            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());

            ret = new String(cipher.doFinal(Base64.getDecoder().decode(encryptedMessage.getBytes())));

        } catch (Exception ex) {
             System.err.println(ex.getStackTrace());
        }
        return ret;
    }

    // Méthode pour chiffrer un message
    public String encryptMessage(String message, RSAPublicKey key) {
        String ret = "";
        try {
            Cipher cipher = Cipher.getInstance("RSA");

            cipher.init(Cipher.ENCRYPT_MODE, key);

            ret = Base64.getEncoder().encodeToString(cipher.doFinal(message.getBytes("UTF-8")));

        } catch (Exception ex) {
             System.err.println(ex.getStackTrace());
        }

        return ret;
    }
    // Méthode pour obtenir une clé publique depuis une chaîne
    private RSAPublicKey getKeyFromString(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String [] partes = key.split("\\|");

        RSAPublicKeySpec spec = new RSAPublicKeySpec(new BigInteger(partes[0]), new BigInteger(partes[1]));

        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    // Méthode appelée lors de la destruction de l'objet
    @Override
    protected void finalize() {
        try {
            exit();
        } catch (Throwable ex) {
             System.err.println(ex.getStackTrace());
        }
    }
}

/// Mehdi 
package chatrmi;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.rmi.*;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.rmi.RemoteException;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import serverrmi.ChatConInterface;
import serverrmi.ChatConUserInterface;


public class ChatConCliente extends Thread implements Runnable, Serializable {

    private String nom;
    private static ChatConInterface chat;
    private static ChatConUserInterface user;

    private String encAlgo = "AES";
    private static Key key;
    private byte[] mdp = new String("seasideseasideSS").getBytes();
    
    private KeyPair keyPair;

    private static final Queue<String> messages = new LinkedList<>();
 
   
    public ChatConCliente() {
        nom = "";

        try {
            if (this.chat == null) {
                this.chat = (ChatConInterface) Naming.lookup("rmi://localhost:1099/myServer");
            }

            key = new SecretKeySpec(mdp, encAlgo);

        } catch (Exception ex) {
           System.err.println(ex.getStackTrace());
        }

    }

   
    public String getNom() {
        return nom;
    }

    public void setNom(String name) {
        this.nom = name;
    }

   
    public String getNewMessage() {
        String ret = "";
        if (messages.size() > 0) {
            ret = messages.poll();
            System.out.println(ret);
        }

        return ret;
    }

    @Override
    @SuppressWarnings("empty-statement")
    public void run() {
        try {
            int cont = user.getCounter();

            int contDiff = 0;

            ArrayList<String> msgArray = null;

            while (true) {

                Thread.sleep(100);

                synchronized (chat) {
                    contDiff = user.getCounter();

                    msgArray = user.getMessages();
                }

                for (; contDiff > cont; cont++) {
                    this.stockMessage(decryptMessage(msgArray.get(msgArray.size() - (contDiff - cont))));
                }
            }

        } catch (Exception ex) {
            exit();
            System.err.println(ex.getStackTrace());
        }
    }
    
 
    public void envoyerMessage(String msg) {
        try {
                for (String user : chat.getUsers()) {
                    chat.envoyerMessage(user, encryptMessage(nom + ": " + msg,getKeyFromString(chat.getPublicKey(user)))); //THIS WILL BE ENCRYPTED WITH PUBLIC KEY FROM USER
                }
            
        } catch (Exception ex) {
            exit();
            System.err.println(ex.getStackTrace());
        }
    }

    private void stockMessage(String msg) {
        synchronized (messages) {
            if (!msg.equals("exit")) {
                messages.add(msg);
            }
        }
    }

    
    public ArrayList<String> getUsersOnline() {
        ArrayList<String> ret = new ArrayList<>();
        try {
            synchronized (chat) {
                ArrayList<String> temp = chat.getUsers();

                for (String user : temp) {
                    ret.add(decrypt(user));
                }
            }
        } catch (RemoteException ex) {
            exit();
            System.err.println(ex.getStackTrace());
        }

        return ret;
    }

    public boolean isOnline(String username) {
        boolean ret = false;
        try {
            ret = chat.isOnline(encrypt(username));
        } catch (RemoteException ex) {
            exit();
            System.err.println(ex.getStackTrace());
        }

        return ret;
    }

    public void login(String nom) {
        try {
            setNom(nom);

            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(1024);
            keyPair = generator.genKeyPair();
            
            KeyFactory fact = KeyFactory.getInstance("RSA");
            
            RSAPublicKey publicKey = (RSAPublicKey)keyPair.getPublic();
            
            String pKey = publicKey.getModulus().toString() + "|" + publicKey.getPublicExponent().toString();
            
            user = chat.ajouterUser(encrypt(nom), encrypt(pKey)); // Le deuxième champ devrait être la clé publique de cet utilisateur.

            envoyerMessage("Je suis connecté !");
        } catch (Exception ex) {
        	 System.err.println(ex.getStackTrace());
        }
    }

    private void removeUsuario(String nom) {
        try {
            chat.supprimerUser(encrypt(nom));
        } catch (RemoteException ex) {
        	 System.err.println(ex.getStackTrace());
        }
    }

    public void exit() {
        envoyerMessage("Je suis deconnecté !");

        removeUsuario(getNom());

        Platform.exit();
        System.exit(0);
    }

  
    public String decrypt(String encryptedMessage) {
        String ret = "";

        try {
            Cipher cipher = Cipher.getInstance(encAlgo);

            cipher.init(Cipher.DECRYPT_MODE, key);

            ret = new String(cipher.doFinal(Base64.getDecoder().decode(encryptedMessage.getBytes())), Charset.forName("UTF8"));

        } catch (Exception ex) {
        	 System.err.println(ex.getStackTrace());
        }

        return ret;
    }

  
    public String encrypt(String message) {
        String ret = "";

        try {
            Cipher cipher = Cipher.getInstance(encAlgo);

            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encryptedMessage = cipher.doFinal(message.getBytes());

            ret = new String(Base64.getEncoder().encode(encryptedMessage), Charset.forName("UTF8"));

        } catch (Exception ex) {
        	 System.err.println(ex.getStackTrace());
        }

        return ret;
    }


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
    
    private RSAPublicKey getKeyFromString(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String [] partes = decrypt(key).split("\\|");
        
        RSAPublicKeySpec spec = new RSAPublicKeySpec(new BigInteger(partes[0]), new BigInteger(partes[1]));
        
        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);
    }
    
    @Override
    protected void finalize() {
        try {
            exit();
            super.finalize();
        } catch (Throwable ex) {
        	 System.err.println(ex.getStackTrace());
        }

    }

}

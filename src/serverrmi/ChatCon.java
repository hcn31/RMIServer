//HCN
package serverrmi;

import java.nio.charset.Charset;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class ChatCon extends java.rmi.server.UnicastRemoteObject implements ChatConInterface {

    private final HashMap<String, ChatConUserInterface> users;

    public ChatCon() throws RemoteException {
        super();
        this.users = new HashMap<String, ChatConUserInterface>();
    }

    public void envoyerMessage(String user, String message) throws RemoteException {
        users.get(decrypt(user)).envoyerMessage(message);
    }

    @Override
    public ChatConUserInterface ajouterUser(String username, String publicKey) throws RemoteException {
        ChatConUserInterface user = new ChatConUser(decrypt(publicKey));
        this.users.put(decrypt(username), user);
        System.out.print(decrypt(username) + " Hey, I am  here\n");
        return user;
    }

    @Override
    public void supprimerUser(String username) throws RemoteException {
        this.users.remove(decrypt(username));
        System.out.println(username + " bye, exit\n");
    }
    
    @Override
    public ArrayList<String> getUsers() throws RemoteException {
        ArrayList<String> ret = new ArrayList<String>();
        users.keySet().stream().forEach((key) -> {
            ret.add(encrypt(key));
        });
        return ret;
    }
    
    @Override
    public String getPublicKey(String username) throws RemoteException {
        return encrypt(users.get(decrypt(username)).getPublicKey());
    }

    @Override
    public boolean isOnline(String username) throws RemoteException {
        return users.containsKey(decrypt(username));
    }

    private String decrypt(String text) {
        String ret = "";
        try {
            byte[] mdp = new String("seasideseasideSS").getBytes();

            Key key = new SecretKeySpec(mdp, "AES");

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);

            ret = new String(cipher.doFinal(Base64.getDecoder().decode(text.getBytes())), Charset.forName("UTF8"));
        } catch (Exception ex) {
            System.err.println(ex.getStackTrace());
        }
        return ret;
    }

    private String encrypt(String text) {
        String ret = "";
        try {
            byte[] mdp = new String("seasideseasideSS").getBytes();
            
            Key key = new SecretKeySpec(mdp, "AES");
            
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            
            byte[] encryptedText = cipher.doFinal(text.getBytes());
            
            ret = new String(Base64.getEncoder().encode(encryptedText),Charset.forName("UTF8"));
        } catch (Exception ex) {
          System.out.println(ex.getStackTrace());
        } 
        return ret;
    }

}

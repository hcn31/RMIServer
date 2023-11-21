//HCN
package serverrmi;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class ChatConUser extends java.rmi.server.UnicastRemoteObject implements Serializable, ChatConUserInterface {

    private final String publicKey;
    private Integer counter;
    private final ArrayList<String> messages;
    
    ChatConUser (String publicKey) throws RemoteException {
        super();
        messages = new ArrayList<>();
        counter = 0;
        this.publicKey = publicKey;
    }

    @Override
    public void envoyerMessage(String mensagem) throws RemoteException {
        synchronized (messages) {
            this.counter++;
            messages.add(mensagem);
            System.out.println(mensagem);
        }
    }
    
    @Override
    public ArrayList<String> getMessages() throws RemoteException {
        return messages;
    }

    @Override
    public String getPublicKey() throws RemoteException {
        return publicKey;
    }

    @Override
    public Integer getCounter() throws RemoteException {
        return counter;
    }

    

}

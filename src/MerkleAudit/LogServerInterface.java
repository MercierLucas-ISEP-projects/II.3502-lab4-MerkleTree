package MerkleAudit;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface LogServerInterface extends Remote
{
    List<byte[]> genPath(int eventID) throws RemoteException;
    List<byte[]> genProof(int events) throws RemoteException;

    boolean verifyEvent(int eventID) throws RemoteException;

    void appendEvent(String[] events) throws RemoteException;
    void appendEvent(String event) throws RemoteException;

    int getCurrentEventsSize() throws RemoteException;
    byte[] getRootHash() throws RemoteException;
}

package MerkleAudit;

import java.io.InputStream;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class LogServer extends UnicastRemoteObject implements LogServerInterface
{
    private MerkleTree _tree;

    public LogServer() throws RemoteException
    {
        super();
        _tree = new MerkleTree(getFileContentToByte("/logs.txt"));
        //genPath(4);
        //genProof(8);
        //appendEvent(new String[]{"Test1","Test2"});
    }

    public static void main(String args[])
    {
        try
        {
            LogServer server = new LogServer();
            java.rmi.registry.LocateRegistry.createRegistry(1234);
            Naming.rebind("rmi://localhost:1234/LogServer",server);
            System.out.println("Server started");
        }
        catch (Exception e)
        {
            System.out.println("An error occured while receiving connection: "+e.getMessage() );
        }
    }

    private byte[][] getFileContentToByte(String path)
    {
        String result = getFileContent(path);
        String[] eventsToHash = result.split("\n");
        System.out.println(eventsToHash.length+" event to log");
        return  Helper.stringToByteArray(eventsToHash);
    }

    public List<byte[]> genPath(int eventID)
    {
        if(eventID > _tree.getNLeaves())
        {
            System.out.println("Incorrect id");
            return null;//throw new Exception("The id of the desired event must be lower than number of events in the tree");
        }

        List<byte[]> path = _tree.getPathToLeaf(eventID);
        System.out.println("Found path to event"+eventID+" in "+path.size()+" elements");

        return path;
    }

    public List<byte[]> genProof(int events)
    {
        List<byte[]> hashes = new ArrayList<>();
        MerkleTree extendedTree = new MerkleTree(getFileContentToByte("/newLogs.txt"));
        byte[][] baseTreeRoots = _tree.getRootChildsHash();

        hashes.add(baseTreeRoots[0]);
        hashes.add(baseTreeRoots[1]);
        hashes.add(extendedTree.getRoot().getRightNode().getHash());

        return hashes;
    }

    @Override
    public boolean verifyEvent(int eventID) throws RemoteException
    {
        return false;
    }

    @Override
    public int getCurrentEventsSize() throws RemoteException
    {
        return _tree.getNLeaves();
    }

    @Override
    public void appendEvent(String event) throws RemoteException
    {
        appendEvent(new String[]{event});
    }

    @Override
    public void appendEvent(String[] events) throws RemoteException
    {
        String result = getFileContent("/logs.txt");
        String[] eventsToHash = result.split("\n");
        String[] totalEvents = Helper.concatArrays(eventsToHash,events);

        Helper.showArray(totalEvents);

        _tree = new MerkleTree(Helper.stringToByteArray(totalEvents));
    }

    @Override
    public byte[] getRootHash() throws RemoteException
    {
        return _tree.getCurrentRootHash();
    }

    private String getFileContent(String fileName)
    {
        try
        {
            System.out.println();
            InputStream stream = getClass().getResourceAsStream(fileName);
            StringBuilder builder = new StringBuilder();

            int i = 0;
            while ((i = stream.read()) != -1)
            {
                builder.append((char)i);
            }

            return  builder.toString();
        }
        catch (Exception ex)
        {
            System.out.println("Exception while getting file "+ex);
        }

        return null;
    }
}

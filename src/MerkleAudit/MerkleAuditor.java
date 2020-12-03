package MerkleAudit;

import java.rmi.Naming;

public class MerkleAuditor
{
    public MerkleAuditor()
    {
        connectToServer();
    }

    private void connectToServer()
    {
        System.out.println("Starting audit...");

        try
        {
            LogServerInterface server = (LogServerInterface) Naming.lookup("rmi://localhost:1234/LogServer");
            System.out.println("Connected to server");

            int nLeaves = server.getCurrentEventsSize();
            byte[] rootHash = server.getRootHash();

            System.out.println("Found "+nLeaves+" events on the server");
            System.out.println("Root hash is");

            Helper.showArray(rootHash);

        }
        catch (Exception e)
        {
            System.out.println("An error occured while trying to connect: "+e.getMessage());
        }
    }
}

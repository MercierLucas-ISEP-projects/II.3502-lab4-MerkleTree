package com.company;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LogServer
{
    private MerkleTree _tree;

    public LogServer()
    {
        String result = getFileContent("/logs.txt");
        String[] eventsToHash = result.split("\n");
        System.out.println(eventsToHash.length+" event to log");
        _tree = new MerkleTree(Helper.stringToByteArray(eventsToHash));

        genPath(4);

        //appendEvent(new String[]{"Test1","Test2"});
    }

    public List<byte[]> genPath(int eventID)
    {

        if(eventID > _tree.getNLeaves())
        {
            return null;//throw new Exception("The id of the desired event must be lower than number of events in the tree");
        }

        List<byte[]> path =_tree.getPathToLeave(eventID);
        System.out.println("Found path to event"+eventID+" in "+path.size()+" elements");

        return path;
    }

    public void appendEvent(String event)
    {
        appendEvent(new String[]{event});
    }

    public void appendEvent(String[] events)
    {
        String result = getFileContent("/logs.txt");
        String[] eventsToHash = result.split("\n");
        String[] totalEvents = Helper.concatArrays(eventsToHash,events);

        Helper.showArray(totalEvents);

        _tree = new MerkleTree(Helper.stringToByteArray(totalEvents));
    }

    private byte[] getRootHash()
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

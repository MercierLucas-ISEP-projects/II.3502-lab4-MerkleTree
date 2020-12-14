package MerkleAudit;

import java.security.MessageDigest;

public class Node
{
    private Node _leftNode;
    private Node _rightNode;
    private Node _parent;

    private int _startIndex;
    private int _endIndex;

    private boolean _isLeave;

    private final byte LEAF_HEADER = 0x00;
    private final byte NODE_HEADER = 0x01;

    private byte[] _hash;

    public Node(Node leftNode,Node rightNode,int startIndex, int endIndex)
    {
        _leftNode = leftNode;
        _rightNode = rightNode;
        _startIndex = startIndex;
        _endIndex = endIndex;
        _isLeave = false;
        byte[] nodeContent = Helper.concatArrays(Helper.concatArrays(new byte[]{NODE_HEADER},leftNode.getHash()), rightNode.getHash());
        computeHash(nodeContent);
    }

    public Node(byte[] dataToHash,int startIndex, int endIndex)
    {
        _startIndex = startIndex;
        _endIndex = endIndex;
        _isLeave = true;

        byte[] leafContent = Helper.concatArrays(new byte[]{LEAF_HEADER},dataToHash);
        computeHash(leafContent);
    }

    private void computeHash(byte[] dataToHash)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            _hash = digest.digest(dataToHash);
        }
        catch (Exception e)
        {
            System.out.println("No such algorithm "+e.getMessage());
        }
    }

    public boolean containsIndex(int index)
    {
        //System.out.println(getStartIndex()+" "+index+" "+getEndIndex());
        //System.out.println(_startIndex >= index && index <= _endIndex);
        return index >=_startIndex && index <= _endIndex;
    }

    public byte[] getHash()
    {
        return _hash;
    }

    public int getStartIndex() {
        return _startIndex;
    }

    public int getIndexRange()
    {
        if(_isLeave)
            return _startIndex;

        return Helper.tryParseToInt(_startIndex+""+_endIndex);
    }

    public int getEndIndex() {
        return _endIndex;
    }

    public Node getLeftNode() {
        return _leftNode;
    }

    public Node getRightNode() {
        return _rightNode;
    }
}

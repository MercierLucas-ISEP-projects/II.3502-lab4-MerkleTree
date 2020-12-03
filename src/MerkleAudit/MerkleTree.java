package MerkleAudit;

import java.util.ArrayList;
import java.util.List;

public class MerkleTree
{
    List<List<Node>> _tree;
    private int _nLeaves;

    public MerkleTree(byte[][] events)
    {
        _nLeaves = events.length;
        _tree = constructTree(events);
        //showTree();
    }

    private void showTree()
    {
        for(List<Node> layers : _tree)
        {
            String indexes = "";

            for(Node node : layers)
            {
                indexes+=node.getIndexRange()+" ";
            }

            System.out.println(indexes);
        }
    }

    public List<List<Node>> constructTree(byte[][] events)
    {
        List<List<Node>> tree = new ArrayList<>();

        boolean canContinue = true;
        int currentLayer = 0;
        int n = 0;

        List<Node> leaves = new ArrayList<>();

        for(int eventID = 0; eventID < events.length; eventID++)
        {
            leaves.add(new Node(events[eventID],eventID+1,eventID+1));
        }
        tree.add(leaves);

        while (canContinue && n < 100)
        {
            n++;
            //System.out.println("Layer: "+currentLayer);

            if(tree.get(currentLayer).size() <= 1)
            {
                //System.out.println("Root reached ");
                canContinue = false;
                break;
            }
            List<Node> nodes = new ArrayList<>();

            for(int nodeID = 0; nodeID < tree.get(currentLayer).size(); nodeID+=2)
            {
                //System.out.println("Selecting nodes "+nodeID+" and"+(nodeID+2));

                Node n1 = tree.get(currentLayer).get(nodeID);
                Node node = null;

                if(tree.get(currentLayer).size() > nodeID+1)
                {
                    Node n2 = tree.get(currentLayer).get(nodeID+1);
                    node = new Node(n1,n2,n1.getStartIndex(),n2.getEndIndex());
                }
                else
                {
                    node = n1;
                }
                nodes.add(node);
            }
            tree.add(nodes);
            currentLayer+=1;
        }

        return tree;
    }

    public byte[] getCurrentRootHash()
    {
        return getRoot().getHash();
    }

    public Node getRoot()
    {
        return _tree.get(_tree.size()-1).get(0);
    }

    public byte[][] getRootChildsHash()
    {
        byte[][] rootChilds = new byte[2][];
        rootChilds[0] = getRoot().getLeftNode().getHash();
        rootChilds[1] = getRoot().getRightNode().getHash();

        return rootChilds;
    }

    public List<byte[]> getPathToLeave(int leafIndex)
    {
        List<byte[]> result = new ArrayList<>();
        Node nextNode = getRoot(); // start from the root

        for(int layerIdx = _tree.size() - 1; layerIdx >= 1; layerIdx--)
        {
            //System.out.println("Searching at layer "+layerIdx+" with node "+nextNode.getIndexRange());
            Node leftNode = nextNode.getLeftNode();

            if(leftNode.containsIndex(leafIndex))
            {
                nextNode = leftNode;
                result.add(nextNode.getRightNode().getHash());
                //System.out.println("Moved to the left");
            }
            else
            {
                nextNode = nextNode.getRightNode();
                result.add(leftNode.getHash());
                //System.out.println("Moved to the right");
            }
        }

        return result;
    }


    public int getNLeaves() {
        return _nLeaves;
    }
}

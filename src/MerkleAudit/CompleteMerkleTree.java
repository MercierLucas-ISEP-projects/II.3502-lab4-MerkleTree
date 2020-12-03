package MerkleAudit;

import java.util.ArrayList;
import java.util.List;

public class CompleteMerkleTree
{
    Node[][] _layers;

    private int _nLeaves;

    public CompleteMerkleTree(byte[][] events)
    {
        _nLeaves = events.length;

        int nLayers = (int)(Math.ceil(_nLeaves/4d)) + 2 ;

        System.out.println("Tree with L=" +_nLeaves +" H="+nLayers);
        showTree(constructCorrectTree(events));
        //initTree(nLayers,events);
        //buildTree(events, nLayers);
        //showTree();
    }

    private void showTree(List<List<Node>> tree)
    {
        for(List<Node> layers : tree)
        {
            String indexes = "";

            for(Node node : layers)
            {
                indexes+=node.getIndexRange()+" ";
            }

            System.out.println(indexes);
        }
    }

    public List<List<Node>> constructCorrectTree(byte[][] events)
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
            System.out.println("Layer: "+currentLayer);

            if(tree.get(currentLayer).size() <= 1)
            {
                System.out.println("Root reached ");
                canContinue = false;
                break;
            }
            List<Node> nodes = new ArrayList<>();

            for(int nodeID = 0; nodeID < tree.get(currentLayer).size(); nodeID+=2)
            {
                System.out.println("Selecting nodes "+nodeID+" and"+(nodeID+2));

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
        return _layers[_layers.length-1][0].getHash();
    }

    public Node getRoot()
    {
        return _layers[0][0];
    }

    public byte[][] getRootChildsHash()
    {
        byte[][] rootChilds = new byte[2][];
        rootChilds[0] = getRoot().getLeftNode().getHash();
        rootChilds[1] = getRoot().getRightNode().getHash();

        return rootChilds;
    }

    public List<byte[]> getPathToLeave(int leaveIndex)
    {
        List<byte[]> result = new ArrayList<>();
        Node nextNode = getRoot(); // start from the root

        for(int layerIdx = _layers.length - 1; layerIdx >= 1; layerIdx--)
        {
            //System.out.println("Searching at layer "+layerIdx+" with node "+nextNode.getIndexRange());
            Node leftNode = nextNode.getLeftNode();

            if(leftNode.containsIndex(leaveIndex))
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

    private void buildTree(byte[][] events ,int nLayers)
    {
        // leaves
        for(int leaveIdx = 0; leaveIdx < _layers[nLayers-1].length; leaveIdx++)
        {
            Node node = new Node(events[leaveIdx],leaveIdx+1,leaveIdx+1);
            _layers[nLayers-1][leaveIdx] = node;
        }

        for(int layerIdx = nLayers - 2; layerIdx >= 0; layerIdx --)
        {
            System.out.println("Building layer "+layerIdx);
            int childIdx = 0;

            for(int nodeIdx = 0; nodeIdx < _layers[layerIdx].length ; nodeIdx++)
            {
                System.out.println("Layer "+(layerIdx+1)+" node:"+childIdx);

                Node node;

                if(_layers[layerIdx+1].length <= childIdx)
                {
                    node = _layers[layerIdx][_layers[layerIdx].length-2]; // duplicate last node
                    System.out.println("Duplicating previous node");
                }
                else
                {
                    Node previousLeftNode = _layers[layerIdx+1][childIdx];

                    if(previousLeftNode == null)
                    {
                        node = _layers[layerIdx][_layers[layerIdx].length-2]; // duplicate last node
                    }
                    else
                    {
                        childIdx +=1;
                        System.out.println("Layer "+(layerIdx+1)+" node:"+childIdx);
                        Node previousRightNode;

                        //if(previousRightNode == null)
                        if(_layers[layerIdx+1].length <= childIdx)
                        {
                            previousRightNode = previousLeftNode;
                            System.out.println("Copied left to right");
                        }
                        else
                        {
                            previousRightNode = _layers[layerIdx+1][childIdx];
                            if(previousRightNode == null)
                            {
                                previousRightNode = previousLeftNode;
                            }
                        }
                        childIdx +=1;

                        node = new Node(previousLeftNode,previousRightNode,previousLeftNode.getIndexRange(),previousRightNode.getIndexRange());
                        System.out.println("Creating node"+nodeIdx+" from node"+previousLeftNode.getIndexRange()+" and node"+previousRightNode.getIndexRange());
                    }

                }

                _layers[layerIdx][nodeIdx] = node;
            }
        }
    }

    private void showTree()
    {
        for(int layerIdx = _layers.length - 1; layerIdx >= 0; layerIdx --)
        {
            String layer = "";
            for(int nodeIdx = 0; nodeIdx < _layers[layerIdx].length; nodeIdx++)
            {
                if(_layers[layerIdx][nodeIdx] != null)
                    layer+= "H"+_layers[layerIdx][nodeIdx].getIndexRange()+" ";
            }
            System.out.println(layer);
        }
    }

    private void initTree(int nLayers, byte[][] events)
    {
        _layers = new Node[nLayers][];


        for(int layerIdx = 0; layerIdx < nLayers-1 ; layerIdx++)
        {
            _layers[layerIdx] = new Node[(int)Math.pow(2,layerIdx)];
            System.out.println("Created layer "+layerIdx +" with "+(int)Math.pow(2,layerIdx)+" nodes");
        }

        _layers[nLayers - 1] = new Node[events.length];
        System.out.println("Created layer "+(nLayers - 1) +" with "+events.length+" nodes");
    }

    public int getNLeaves() {
        return _nLeaves;
    }
}

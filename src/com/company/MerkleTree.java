package com.company;

import java.util.ArrayList;
import java.util.List;

public class MerkleTree
{
    Node[][] _layers;
    private int _nLeaves;

    public MerkleTree(byte[][] events)
    {
        _nLeaves = events.length;

        int nLayers = (int)(Math.ceil(_nLeaves/4d)) + 2 ;

        System.out.println("Tree with L=" +_nLeaves +" H="+nLayers);
        initTree(nLayers,events);
        buildTree(events, nLayers);
        showTree();
    }

    public byte[] getCurrentRootHash()
    {
        return _layers[_layers.length-1][0].getHash();
    }

    public List<byte[]> getPathToLeave(int leaveIndex)
    {
        List<byte[]> result = new ArrayList<>();
        Node nextNode = _layers[0][0]; // start from the root

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
            System.out.println("Buildint layer "+layerIdx);
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

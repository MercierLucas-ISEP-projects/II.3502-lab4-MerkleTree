import MerkleAudit.Helper;
import MerkleAudit.MerkleTree;

import java.util.List;

public class MerkeTreeTest
{
    public static void main(String[] args)
    {
        assertTreeSize(5);
        assertSizeAfterAppend();
        assertPathToLeafNotNullAndCorrectSize(4);

        System.out.println("All tests passed");
    }

    private static void assertPathToLeafNotNullAndCorrectSize(int size)
    {
        MerkleTree tree = new MerkleTree(getDummies(size));
        List<byte[]> path = tree.getPathToLeaf(1);
        assert path != null;
        assert  path.size() > 0;
        assert path.size() == tree.getNLayers() - 1 ;
    }

    private static void assertTreeSize(int size)
    {
        MerkleTree tree = new MerkleTree(getDummies(size));
        assert tree.getNLeaves() == size;
    }

    private static void assertSizeAfterAppend()
    {
        String[] events = Helper.concatArrays(new String[]{"test1","test2"},new String[]{"test3","test4","test5"});

        MerkleTree tree = new MerkleTree(Helper.stringToByteArray(events));

        assert tree.getNLeaves() == 5;
    }

    private static byte[][] getDummies(int dummies)
    {
        String[] events = new String[dummies];

        for(int i =0; i < dummies;i++)
        {
            events[i] = "Dummy";
        }

        return Helper.stringToByteArray(events);
    }

}

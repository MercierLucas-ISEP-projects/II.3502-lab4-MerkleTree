package MerkleAudit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Helper
{
    public static byte[][] stringToByteArray(String[] text)
    {
        byte[][] result = new byte[text.length][];

        for (int i = 0; i < text.length; i++)
        {
            try
            {
                result[i] = text[i].getBytes("UTF-8");
            }
            catch (Exception e)
            {
                System.out.println("Unexpected charset");
            }
        }

        return result;
    }

    public static int tryParseToInt(String string)
    {
        try
        {
            return Integer.parseInt(string);
        }
        catch (Exception e)
        {
            return  0;
        }
    }

    public static void showArray(String[] a)
    {
        for(String txt : a)
        {
            System.out.println(txt);
        }
    }
    public static void showArray(byte[] a)
    {
        StringBuilder builder = new StringBuilder();
        for(byte element : a)
        {
            builder.append(element+" ");
        }
        System.out.println(builder.toString());
    }

    public static String[] concatArrays(String[] a, String[] b)
    {
        String[] result = new String[a.length + b.length];

        for(int i = 0; i < a.length; i++)
        {
            result[i] = a[i];
        }

        for(int i = 0; i < b.length; i++)
        {
            result[a.length+i] = b[i];
        }
        return result;
    }

    // Primitives doesn't support byte
    public static byte[] concatArrays(byte[] a, byte[] b)
    {
        byte[] result = new byte[a.length + b.length];

        for(int i = 0; i < a.length; i++)
        {
            result[i] = a[i];
        }

        for(int i = 0; i < b.length; i++)
        {
            result[a.length+i] = b[i];
        }
        return result;
    }


}

package application;

import java.util.ArrayList;
import java.util.HashMap;

// The algorithm treat every one byte as a symbol,
// and the input data is a byte array.
public class LzwCoding {

    public byte[] data;
    public String outputString;
    public ArrayList<Byte> compressedData;
    public HashMap<String, Integer> dictionary;
    public int nextCode;

    public LzwCoding(byte[] data) {
        this.data = data;
        outputString = "";
        compressedData =  new ArrayList<>();
        dictionary = new HashMap<>();

        // Main compression procedure.
        initializeDic();
        encode();
        getEncoded();
    }

    // Build the initial dictionary that consists of only 256 entries, for all single symbols.
    public void initializeDic() {
        for(int i = 0; i < 256; i++)
            dictionary.put(Character.toString((char)(i-128)), i);
        nextCode = 256;
    }

    public void encode() {
        // Retrieve first byte and convert into string format as a symbol.
        String s = "" + (char)data[0];
        for(int i = 1; i < data.length; i++) {
            // Retrieve next symbol.
            char c = (char)data[i];
            // Check if s + c exists in the dictionary.
            if(dictionary.containsKey(s + c))
                s = s + c;
            else {
                outputCode(s);
                // Expand the dictionary with s + c.
                dictionary.put(s + c, nextCode++);
                s = "" + c;
            }
        }
        outputCode(s);
    }

    // Store the output code of a symbol in binary string format.
    public void outputCode(String s) {
        int code = dictionary.get(s);
        // Convert the integer code into binary string.
        String codeword = String.format("%8s", Integer.toBinaryString(code & 0xFFF))
                .replace(' ', '0');
        outputString += codeword;
    }

    // Retrieve the compressed data in bytes from the code.
    public void getEncoded() {
        int i;
        // Extract every 8 literals in the code string as one byte.
        for(i = 0; i < outputString.length()-8; i += 8) {
            byte num = (byte)Integer.parseInt(outputString.substring(i, i+8), 2);
            compressedData.add(num);
        }
        compressedData.add((byte)Integer.parseInt(outputString.substring(i), 2));
    }

    public double getCompressRatio() {
        return (double)data.length / compressedData.size();
    }
}

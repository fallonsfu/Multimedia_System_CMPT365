package application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

// The algorithm treats every two bytes(16 bits) as a symbol
// and the input data is an integer array, in which every
// integer representing two bytes is one symbol.
public class HuffmanCoding {

    public int[] data;
    public ArrayList<Byte> compressedData;
    public HashMap freqs, codeMap;
    public PriorityQueue<Node> PQueue;
    public Node binaryTree;

    public HuffmanCoding(int[] data) {
        this.data = data;
        compressedData =  new ArrayList<>();
        freqs = new HashMap();
        codeMap = new HashMap();
        PQueue = new PriorityQueue <Node>(new NodeComparator());

        // Main compression procedure.
        computeFreq(data);
        initializeQueue();
        buildTree();
        encode(binaryTree, "");
        getEncoded();
    }

    // Calculate the occurrence frequency of every symbol.
    public void computeFreq(int[] data) {
        for(int elem: data) {
            int freq = 1;
            if(freqs.containsKey(elem))
                freq = (int)freqs.get(elem) + 1;
            freqs.put(elem, freq);
        }
    }

    // Create a leaf node for each symbol and add it to the priority queue.
    public void initializeQueue() {
        for(Object elem: freqs.keySet()) {
            int freq = (int)freqs.get(elem);
            PQueue.add(new Node(freq, (int)elem, null, null));
        }
    }

    // Construct the Huffman Tree from the queue.
    public void buildTree() {
        while(PQueue.size() > 1) {
            // Remove the two nodes of lowest frequency from the queue.
            Node min1 = PQueue.poll();
            Node min2 = PQueue.poll();
            // Create a new internal node with these two nodes as children
            // and with frequency equal to the sum of the two nodes' frequencies.
            Node subtree = new Node(min1.freq + min2.freq,
                    0, min1, min2);
            // Add the new node to the queue.
            PQueue.add(subtree);
        }
        // The remaining node is the root node and the tree is complete.
        binaryTree = PQueue.poll();
    }

    // Assign code backward from the root with empty code string initially.
    public void encode(Node tree, String code) {
        // If a node has no child, it is a leaf node. Encode this node.
        if(tree.leftChild == null)
            codeMap.put(tree.symbol, code);
        // If a node is an internal node, expand the code string.
        else {
            encode(tree.leftChild, code + "0");
            encode(tree.rightChild, code + "1");
        }
    }

    // Retrieve the compressed data in bytes from the code.
    public void getEncoded() {
        String codeString = "";
        // Translate the entire data file into binary code string.
        for(int elem: data)
            codeString += (String)codeMap.get(elem);
        int i;
        // Extract every 8 literals in the code string as one byte.
        for(i = 0; i < codeString.length()-8; i += 8) {
            byte num = (byte)Integer.parseInt(codeString.substring(i, i+8),2);
            compressedData.add(num);
        }
        compressedData.add((byte)Integer.parseInt(codeString.substring(i),2));
    }

    public double getCompressRatio() {
        return (double)data.length*2 / compressedData.size();
    }
}

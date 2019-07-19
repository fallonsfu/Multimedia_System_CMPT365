package application;

import java.util.Comparator;

public class Node {

    public int freq;
    public int symbol;
    public Node leftChild, rightChild;

    public Node(int freq, int symbol, Node left, Node right) {
        this.freq = freq;
        this.symbol = symbol;
        leftChild = left;
        rightChild = right;
    }
}

class NodeComparator implements Comparator<Node>{

    // Overriding compare()method of Comparator
    // for descending order of freq
    public int compare(Node s1, Node s2) {
        if (s1.freq > s2.freq)
            return 1;
        else if (s1.freq < s2.freq)
            return -1;
        return 0;
    }
}

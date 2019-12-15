public class Node {
    public NodeType nodeType;
    public Node left, right;
    public String value;
    public NodeType evalType;
    public String interimCode;

    Node() {
        this.nodeType = null;
        this.left = null;
        this.right = null;
        this.value = null;
    }
    Node(NodeType node_type, Node left, Node right, String value) {
        this.nodeType = node_type;
        this.left = left;
        this.right = right;
        this.value = value;
    }
    public static Node make_node(NodeType nodetype, Node left, Node right) {
        return new Node(nodetype, left, right, "");
    }
    public static Node make_node(NodeType nodetype, Node left) {
        return new Node(nodetype, left, null, "");
    }
    public static Node make_leaf(NodeType nodetype, String value) {
        return new Node(nodetype, null, null, value);
    }
}
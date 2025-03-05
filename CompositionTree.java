import java.io.PrintStream;
import java.util.ArrayList;


public class CompositionTree{
    public enum TreeType{
        S, P, Q
    }
    //-------------------------------------------//
    //          START INNER CLASS "NODE"         //
    //-------------------------------------------//
    protected class Node{
        private Node parent = null, leftChild = null, rightChild = null;
        private TreeType type;


        private Node(TreeType type){
            this.type = type;
        }


        //------------------------------//
        //          GET METHODS         //
        //------------------------------//
        private Node getSibling(){
            if(this.parent == null)
                return null;
            if(this.isLeftChild())
                return this.parent.rightChild;
            return this.parent.leftChild;
        }


        //--------------------------------//
        //          CHECK METHODS         //
        //--------------------------------//
        public boolean isS(){
            if(this.type == TreeType.S)
                return true;
            return false;
        }

        public boolean isP(){
            if(this.type == TreeType.P)
                return true;
            return false;
        }

        public boolean isQ(){
            if(this.type == TreeType.Q)
                return true;
            return false;
        }

        private boolean isLeftChild(){
            if(this.parent.leftChild.equals(this))
                return true;
            return false;
        }

        private boolean isRightChild(){
            if(this.parent.rightChild.equals(this))
                return true;
            return false;
        }

        private boolean isAtLeastOneChildSeries(){
            if(this.leftChild.type == TreeType.S || this.rightChild.type == TreeType.S)
                return true;
            return false;
        }

        private boolean isAtLeastOneChildParallel(){
            if(this.leftChild.type == TreeType.P || this.rightChild.type == TreeType.P)
                return true;
            return false;
        }

        private boolean areChildrenLeaves(){
            if(this.leftChild.type == TreeType.Q && this.rightChild.type == TreeType.Q)
                return true;
            return false;
        }
        

        //------------------------------//
        //          SET METHODS         //
        //------------------------------//
        private void setParent(Node newParent){
            this.parent = newParent;
        }

        private void setLeftChild(Node newLeftChild){
            this.leftChild = newLeftChild;
            newLeftChild.setParent(this);
        }

        private void setRightChild(Node newRightChild){
            this.rightChild = newRightChild;
            newRightChild.setParent(this);
        }

        private void setChildren(Node newLeftChild, Node newRightChild){
            this.setLeftChild(newLeftChild);
            this.setRightChild(newRightChild);
        }
    }
    //-----------------------------------------//
    //          END INNER CLASS "NODE"         //
    //-----------------------------------------//


    private ArrayList<Node> binaryTree, series, parallels, leaves;
    private ArrayList<Node> nodesToVisit, leavesToRemove, possibleEdges, fromSeriesToParallel, visited;
    private Node root = null;
    private int graphVertexes = 0, number = 0;


    public CompositionTree(int vertexesNumber){
        this.initializeCompositionTree(vertexesNumber);
        this.addNodes(vertexesNumber);
        this.removeDoubleQRecursive(root);
        this.removeRedundantEdges();
        //this.checkWhereEdgesCanBeAdded();
        //this.addNewEdgesInParallel();
        //this.checkWhereSeriesCanBecomeParallels();
        //this.changeAllSeriesToParallels();
        //this.removeParallels();
        this.print(System.out);
        //this.printDatas();
        //reverse();
        //recursive();
        if(leaves.size() != graphVertexes - 1)
            System.out.println("ERROR: " + leaves.size());
    }


    //------------------------------//
    //          GET METHODS         //
    //------------------------------//
    public Node getNode(int index){
        return this.binaryTree.get(index);
    }

    public int getRootIndex(){
        return this.binaryTree.indexOf(this.root);
    }

    public int getLeftChildIndex(int index){
        return this.binaryTree.indexOf(this.binaryTree.get(index).leftChild);
    }

    public int getRightChildIndex(int index){
        return this.binaryTree.indexOf(this.binaryTree.get(index).rightChild);
    }

    private Node getRandomLeaf(){
        return leaves.get(Graph.getRandomGenerator().nextInt(leaves.size()));
    }

    private Node getLeafInSeriesWithAnotherLeaf(){
        int randomIndex = Graph.getRandomGenerator().nextInt(this.leaves.size());
        Node leaf = this.leaves.get(randomIndex);
        while (!(leaf.parent.isS() && leaf.getSibling().isQ())) {
            randomIndex = Graph.getRandomGenerator().nextInt(this.leaves.size());
            leaf = this.leaves.get(randomIndex);
        }
        return leaf;
    }
    

    //--------------------------------//
    //          START METHODS         //
    //--------------------------------//
    private void initializeArrayLists(){
        this.binaryTree = new ArrayList<>();
        this.series = new ArrayList<>();
        this.parallels = new ArrayList<>();
        this.leaves = new ArrayList<>();
        this.nodesToVisit = new ArrayList<>();
        this.leavesToRemove = new ArrayList<>();
        this.possibleEdges = new ArrayList<>();
        this.fromSeriesToParallel = new ArrayList<>();
    }

    private void initializeCompositionTree(int vertexesNumber){
        this.initializeArrayLists();
        this.binaryTree.add(new Node(TreeType.Q));
        this.root = this.binaryTree.get(0);
        this.leaves.add(root);
        this.graphVertexes = 2;
    }


    //-------------------------------------------//
    //          ADD/REMOVE NODES METHODS         //
    //-------------------------------------------//
    private void addNode(Node node){
        this.binaryTree.add(node);
        if (node.isS())
            this.series.add(node);
        else if (node.isP())
            this.parallels.add(node);
        else
            this.leaves.add(node);
    }

    private void removeNode(Node node){
        node.leftChild = node.rightChild = node.parent = null;
        this.binaryTree.remove(node);
        if (node.isS())
            this.series.remove(node);
        else if (node.isP())
            this.parallels.remove(node);
        else
            this.leaves.remove(node);
    }

    private void addNodes(int vertexesNumber){
        while(this.graphVertexes < vertexesNumber){
            if(Graph.getRandomGenerator().nextBoolean()){
                this.addTwoEdgesAsChildren(TreeType.S);
                this.graphVertexes++;
            } else
                this.addTwoEdgesAsChildren(TreeType.P);
        }
    }

    private void addTwoEdgesAsChildren(TreeType type){
        Node randomNode = this.getRandomLeaf();
        randomNode.type = type;
        if(type == TreeType.S)
            this.series.add(randomNode);
        else
            this.parallels.add(randomNode);
        this.leaves.remove(randomNode);

        Node leftChild = new Node(TreeType.Q);
        Node rightChild = new Node(TreeType.Q);
        randomNode.setChildren(leftChild, rightChild);
        this.addNode(leftChild);
        this.addNode(rightChild);
    }

    private void addNewEdgeInSeriesOrParallel(TreeType type, Node node){
        Node newSeriesOrParallel = new Node(type);
        Node newLeaf = new Node(TreeType.Q);
        newSeriesOrParallel.setLeftChild(newLeaf);
        newLeaf.setParent(newSeriesOrParallel);
        this.addNode(newSeriesOrParallel);
        this.addNode(newLeaf);

        if (!node.equals(root)) {
            if (node.isLeftChild())
                node.parent.leftChild = newSeriesOrParallel;
            else
                node.parent.rightChild = newSeriesOrParallel;
            newSeriesOrParallel.parent = node.parent;
        } else
            this.root = newSeriesOrParallel;
        newSeriesOrParallel.rightChild = node;
        node.parent = newSeriesOrParallel;
    }

    private void removeLeafAndParent(Node leaf){
        Node originalParent = leaf.parent;
        Node originalSibling = leaf.getSibling();
        Node originalGrandparent = originalParent.parent;
        if(originalParent.equals(root)){
            this.root = originalSibling;
            originalSibling.setParent(null);
            //this.removeNode(originalGrandparent);
        } else{
            originalSibling.setParent(originalGrandparent);
            if(originalParent.isRightChild())
                originalGrandparent.rightChild = originalSibling;
            else
                originalGrandparent.leftChild = originalSibling;
        }
        this.removeNode(originalParent);
        this.removeNode(leaf);
    }


    //-------------------------------------------------//
    //          REMOVE REDUNDANT EDGES METHODS         //
    //-------------------------------------------------//
    private void removeDoubleQRecursive(Node node){
        if(!node.leftChild.isQ())
            this.removeDoubleQRecursive(node.leftChild);
        if(!node.rightChild.isQ())
            this.removeDoubleQRecursive(node.rightChild);
        
        if(node.isP() && node.areChildrenLeaves())
            this.removeLeafAndParent(node.leftChild);
    }

    private void removeRedundantEdges(){
        this.nodesToVisit.add(root);
        while(!nodesToVisit.isEmpty())
            this.checkIfLeafHasToBeRemoved(nodesToVisit.remove(0));

        while(!leavesToRemove.isEmpty())
            this.removeLeafAndParent(leavesToRemove.remove(0));
    }

    private void checkIfLeafHasToBeRemoved(Node node){
        if(node.isS() || (node.isP() && node.isAtLeastOneChildSeries())){
            this.nodesToVisit.add(node.leftChild);
            this.nodesToVisit.add(node.rightChild);
        } else if(node.isP() /*&& (node.areChildrenParallels() || node.isOneChildLeaf() && node.isOneChildParallel())*/){
            this.checkIfNodeHasPQDescendant(node.leftChild);
            this.checkIfNodeHasPQDescendant(node.rightChild);
        }
    }

    private void checkIfNodeHasPQDescendant(Node node){
        if(node.isQ())
            this.leavesToRemove.add(node);
        else if(node.isP()){
            this.checkIfNodeHasPQDescendant(node.leftChild);
            this.checkIfNodeHasPQDescendant(node.rightChild);
        } else // node.type == TreeType.S
            this.nodesToVisit.add(node);
    }


    //------------------------------------------------//
    //          ADD EDGES IN PARALLEL METHODS         //
    //------------------------------------------------//
    private void checkWhereEdgesCanBeAdded(){
        this.nodesToVisit.add(root);
        while (!this.nodesToVisit.isEmpty())
            this.checkIfEdgeInParallelCanBeAdded(this.nodesToVisit.remove(0));
    }

    private void checkIfEdgeInParallelCanBeAdded(Node node){
        if (node.isS()) {
            this.possibleEdges.add(node);
            this.nodesToVisit.add(node.leftChild);
            this.nodesToVisit.add(node.rightChild);
        } else if (node.isP() && !this.checkIfNodeHasPQDescendant2(node))
            this.possibleEdges.add(node);
    }

    private boolean checkIfNodeHasPQDescendant2(Node node){
        if (node.isQ())
            return true;
        else if (node.isP())
            return this.checkIfNodeHasPQDescendant2(node.leftChild) | this.checkIfNodeHasPQDescendant2(node.rightChild);
        else{ // node.type == TreeType.S
            this.nodesToVisit.add(node.leftChild);
            this.nodesToVisit.add(node.rightChild);
            return false;
        }
    }

    private void addNewEdgesInParallel(){
        while (!possibleEdges.isEmpty()) {
            Node node = possibleEdges.remove(0);
            this.addNewEdgeInSeriesOrParallel(TreeType.P, node);
        }
    }


    //-----------------------------------------------------//
    //          CHANGE SERIES TO PARALLELS METHODS         //
    //-----------------------------------------------------//
    private void checkWhereSeriesCanBecomeParallels(){
        this.nodesToVisit.add(root);
        while (!this.nodesToVisit.isEmpty())
            this.checkIfSeriesCanBecomeParallel(this.nodesToVisit.remove(0));
    }

    private void checkIfSeriesCanBecomeParallel(Node node){
        if(node.isP()){
            this.nodesToVisit.add(node.leftChild);
            this.nodesToVisit.add(node.rightChild);
        } else if(node.isS() && node.isAtLeastOneChildSeries()){
            this.fromSeriesToParallel.add(node);
            this.nodesToVisit.add(node.leftChild);
            this.nodesToVisit.add(node.rightChild);
        } else if(node.isS() && node.isAtLeastOneChildParallel()){
            boolean leftLeafFound = this.checkIfNodeHasPQDescendant3(node.leftChild);
            boolean rightLeafFound = this.checkIfNodeHasPQDescendant3(node.rightChild);
            if(!(leftLeafFound && rightLeafFound))
                this.fromSeriesToParallel.add(node);
        }
    }

    private boolean checkIfNodeHasPQDescendant3(Node node){
        if (node.isQ())
            return true;
        else if (node.isP())
            return this.checkIfNodeHasPQDescendant3(node.leftChild) | this.checkIfNodeHasPQDescendant3(node.rightChild);
        else{ // node.type == TreeType.S
            this.nodesToVisit.add(node);
            return false;
        }
    }

    private void changeAllSeriesToParallels(){
        while (!fromSeriesToParallel.isEmpty()) {
            Node node = fromSeriesToParallel.remove(0);
            this.addNewEdgeInSeriesOrParallel(TreeType.S, node);
            node.type = TreeType.P;
            this.series.remove(node);
            this.parallels.add(node);
            if(!this.checkIfNodeHasPQDescendant4(node))
                fromSeriesToParallel.add(node.parent);
        }
    }

    private boolean checkIfNodeHasPQDescendant4(Node node){
        if (node.isQ())
            return true;
        else if (node.isP())
            return this.checkIfNodeHasPQDescendant4(node.leftChild) | this.checkIfNodeHasPQDescendant4(node.rightChild);
        else // node.type == TreeType.S
            return false;
    }


    //-----------------------------------------------------//
    //          CHANGE PARALLELS TO SERIES METHODS         //
    //-----------------------------------------------------//
    private void removeParallels(){
        while (!this.parallels.isEmpty()) {
            int randomIndex = Graph.getRandomGenerator().nextInt(this.parallels.size());
            Node randomParallelToChange = this.parallels.remove(randomIndex);
            randomParallelToChange.type = TreeType.S;
            this.series.add(randomParallelToChange);

            Node randomLeafToRemove = this.getLeafInSeriesWithAnotherLeaf();
            Node originalLeafGrandparent = randomLeafToRemove.parent.parent;
            Node originalLeafUncle = randomLeafToRemove.parent.getSibling();
            this.removeLeafAndParent(randomLeafToRemove);
            if(originalLeafGrandparent.isP() && this.checkIfNodeHasPQDescendant5(originalLeafUncle)){ // o togli tutti i P-Q o metti questa condizione per vedere quali S-QQ poter usare
                //System.out.println(this.leavesToRemove.size());
                this.removeLeafAndParent(this.leavesToRemove.remove(0));
            }
        }
    }

    private boolean checkIfNodeHasPQDescendant5(Node node){
        if (node.isQ()){
            this.leavesToRemove.add(node);
            return true;
        } else if (node.isP())
            return this.checkIfNodeHasPQDescendant5(node.leftChild) | this.checkIfNodeHasPQDescendant5(node.rightChild);
        else // node.type == TreeType.S
            return false;
    }


    /*
     *  se P:
     *      vai ai figli
     *  se S && almeno 1 figlio S:
     *      OK
     *      vai ai figli
     *  se S && almeno 1 figlio P:
     *      se entrambi discendenti Q:
     *          NO
     *          vai ai nodi S, se ci sono
     *      altrimenti:
     *          OK
     *          vai ai nodi S, se ci sono
     */

     private void printDatas(){
        int edges = 0, series = 0, parallels = 0;
        for(Node i: binaryTree){
            if(i.type == TreeType.S)
                series++;
            else if(i.type == TreeType.P)
                parallels++;
            else if(i.type == TreeType.Q)
                edges++;
        }
        int total = series + parallels + edges;
        System.out.println("Composition: \t\t" + series + " " + parallels + " "+ edges + " " + total);
        System.out.println("CompositionArrayLists: \t\t" + this.series.size() + " " + this.parallels.size() + " "+ this.leaves.size() + " " + this.binaryTree.size());
    }

    private void recursive(){
        this.recursive(root);
        System.out.println("Nodes visited from root to leaves: " + number);
    }

    private void recursive(Node node){
        number++;
        if(node.type != TreeType.Q){
            recursive(node.leftChild);
            recursive(node.rightChild);
        }
    }

    private void reverse(){
        visited = new ArrayList<>();
        for(Node node: leaves)
            reverseRecursive(node);
        System.out.println("Nodes visited from leaves to root: " + visited.size());
    }

    private void reverseRecursive(Node node){
        if(!visited.contains(node))
            visited.add(node);
        if(!node.equals(root))
            reverseRecursive(node.parent);
    }

    public String traversePreOrder(Node root) {
        if (root == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(root.type);

        String pointerRight = "└──";
        String pointerLeft = (root.rightChild != null) ? "├──" : "└──";

        traverseNodes(sb, "", pointerLeft, root.leftChild, root.rightChild != null);
        traverseNodes(sb, "", pointerRight, root.rightChild, false);

        return sb.toString();
    }

    public void traverseNodes(StringBuilder sb, String padding, String pointer, Node node, boolean hasRightSibling) {
        if (node != null) {
            sb.append("\n");
            sb.append(padding);
            sb.append(pointer);
            sb.append(node.type);

            StringBuilder paddingBuilder = new StringBuilder(padding);
            if (hasRightSibling) {
                paddingBuilder.append("│  ");
            } else {
                paddingBuilder.append("   ");
            }

            String paddingForBoth = paddingBuilder.toString();
            String pointerRight = "└──";
            String pointerLeft = (node.rightChild != null) ? "├──" : "└──";

            traverseNodes(sb, paddingForBoth, pointerLeft, node.leftChild, node.rightChild != null);
            traverseNodes(sb, paddingForBoth, pointerRight, node.rightChild, false);
        }
    }

    public void print(PrintStream os) {
        os.print(traversePreOrder(root));
    }
}
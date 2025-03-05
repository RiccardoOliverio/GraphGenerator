import java.util.Stack;


public class Halin extends Graph{
    protected Halin(){
        super(4);
        for(int i=1; i<4; i++)
            this.addEdgeWithoutCheck(this.getFirstVertex(), this.getVertex(i));
    }


    //---------------------------------//
    //           GET METHODS           //
    //---------------------------------//
    public static int getMaxNumberOfEdges(int vertexesNumber){
        if(vertexesNumber % 2 == 0)
            return 3 * vertexesNumber / 2;
        else
            return (3 * vertexesNumber + 1) / 2;
    }


    //---------------------------------//
    //       OVERRIDDEN METHODS        //
    //---------------------------------//
    @Override
    protected void addVertex(){
        Vertex oldVertex = this.getRandomNonLeafVertex();
        super.addVertex();
        int newVertexPosition = Graph.getRandomGenerator().nextInt(this.getVertexLinkedList(oldVertex).size()) + 1;

        int oldVertexIndex = this.getVertexIndex(oldVertex);
        int newVertexIndex = this.getLastIndex();
        this.getAdjacencyList().get(oldVertexIndex).add(newVertexPosition, this.getLastVertex());
        this.getAdjacencyList().get(newVertexIndex).add(oldVertex);
    }

    @Override
    protected void addVertex(Vertex vertex){
        super.addVertex();
        Vertex adjacent = this.getRandomAdjacent(vertex);
        Vertex firstVertex = vertex, secondVertex = adjacent;
        if(this.getVertexIndex(adjacent) == Math.min(this.getVertexIndex(vertex), this.getVertexIndex(adjacent))){
            firstVertex = adjacent;
            secondVertex = vertex;
        }

        // replace adjacents
        int secondVertexRelativeIndex = this.getAdjacentRelativeIndex(firstVertex, secondVertex);
        int firstVertexRelativeIndex = this.getAdjacentRelativeIndex(secondVertex, firstVertex);
        this.getVertexLinkedList(secondVertex).set(firstVertexRelativeIndex, this.getLastVertex());
        this.getVertexLinkedList(firstVertex).set(secondVertexRelativeIndex, this.getLastVertex());

        // create middle vertex adjacents
        Vertex middleVertex = this.getLastVertex();
        if(this.getNumberOfAdjacents(firstVertex) > 1 && this.getNumberOfAdjacents(secondVertex) > 1)
            this.setMiddleVertexAdjacents(middleVertex, firstVertex, secondVertex);
        else if(this.getNumberOfAdjacents(firstVertex) > 1 && this.getNumberOfAdjacents(secondVertex) == 1)
            this.setMiddleVertexAdjacents(middleVertex, firstVertex, secondVertex);
        else if(this.getNumberOfAdjacents(firstVertex) == 1 && this.getNumberOfAdjacents(secondVertex) > 1)
            this.setMiddleVertexAdjacents(middleVertex, secondVertex, firstVertex);
    }

    private void setMiddleVertexAdjacents(Vertex middleVertex, Vertex firstAdjacent, Vertex otherAdjacent){
        this.getVertexLinkedList(middleVertex).add(firstAdjacent);
        if(Graph.getRandomGenerator().nextBoolean()){
            this.getVertexLinkedList(middleVertex).add(otherAdjacent);
            super.addVertex();
            this.addEdgeWithoutCheck(middleVertex, this.getLastVertex());
        } else{
            super.addVertex();
            this.addEdgeWithoutCheck(middleVertex, this.getLastVertex());
            this.getVertexLinkedList(middleVertex).add(otherAdjacent);
        }
    }


    //--------------------------------------//
    //     OUTER-EDGES-RELATED METHODS      //
    //--------------------------------------//
    private void createOuterBorder(){
        Vertex firstVertex = this.getRandomNonLeafVertex();
        Stack<Vertex> leaves = new Stack<>();
        this.createOuterBorderRecursive(firstVertex, null, leaves);
        this.createBorderEdges(leaves);
    }

    private void createOuterBorderRecursive(Vertex currentVertex, Vertex previousVertex, Stack<Vertex> leaves){
        for(int i=1; i<this.getVertexLinkedList(currentVertex).size(); i++){
            Vertex nextVertex = this.getVertexLinkedList(currentVertex).get(i);
            if(this.getNumberOfAdjacents(nextVertex) == 1)
                leaves.add(nextVertex);
            else if(nextVertex != previousVertex){
                Vertex startVertex = this.getVertexLinkedList(currentVertex).getFirst();
                this.createOuterBorderRecursive(nextVertex, startVertex, leaves);
            }
        }
    }

    private void createBorderEdges(Stack<Vertex> leaves){
        Vertex lastAdded = leaves.peek();
        int originalStackSize = leaves.size()-1;
        for(int i=0; i<originalStackSize; i++){
            Vertex removedLeaf = leaves.peek();
            this.addEdge(leaves.pop(), leaves.peek());
            if(i != 0)
                this.reverseAdjacents(removedLeaf);
        }
        Vertex lastLeaf = leaves.peek();
        this.addEdge(leaves.pop(), lastAdded);
        this.reverseAdjacents(lastLeaf);
    }


    //---------------------------------//
    //          STATIC METHODS         //
    //---------------------------------//
    public static Halin createHalinGraph(int numberOfVertexes, int numberOfEdges){
        Halin halinGraph = new Halin();
        if(numberOfEdges == -1)
            numberOfEdges = chooseRandomNumberOfEdges(numberOfVertexes);
        int[] results = getHowManyTimesAddVertexMethodsHaveToBeUsed(numberOfVertexes, numberOfEdges);
        int oneVertexes = results[0], twoVertexes = results[1];


        while(halinGraph.getNumberOfVertexes() < numberOfVertexes){
            if(oneVertexes > 0 && Graph.getRandomGenerator().nextBoolean()){
                halinGraph.addVertex();
                oneVertexes--;
            } else if(twoVertexes > 0){
                halinGraph.addVertex(halinGraph.getRandomNonLeafVertex());
                twoVertexes--;
            }
        }
        halinGraph.createOuterBorder();
        return halinGraph;
    }

    private static int chooseRandomNumberOfEdges(int numberOfVertexes){
        if(numberOfVertexes % 2 == 0)
            return Graph.getRandomGenerator().nextInt(3*numberOfVertexes/2, 2*numberOfVertexes - 1);
        else
            return Graph.getRandomGenerator().nextInt((3*numberOfVertexes + 1)/2, 2*numberOfVertexes - 1);
    }

    private static int[] getHowManyTimesAddVertexMethodsHaveToBeUsed(int numberOfVertexes, int numberOfEdges){
        int vertexesToAdd = numberOfVertexes - 4, currentEdges = 6;
        int oneVertexes = 0, twoVertexes = 0;

        if(vertexesToAdd % 2 == 0){
            twoVertexes = vertexesToAdd / 2;
            currentEdges += twoVertexes * 3;
        } else{
            oneVertexes++;
            currentEdges += 2;
            twoVertexes = (vertexesToAdd - 1) / 2;
            currentEdges += twoVertexes * 3;
        }
        while(currentEdges < numberOfEdges){
            twoVertexes--;
            oneVertexes += 2;
            currentEdges++;
        }

        int[] results = {oneVertexes, twoVertexes};
        return results;
    }

    /*
     * while(halinGraph.getNumberOfVertexes() < numberOfVertexes){
            if(Graph.getRandomGenerator().nextBoolean())
                halinGraph.addVertex();
            else
                halinGraph.addVertex(halinGraph.getRandomNonLeafVertex());
            if(halinGraph.getNumberOfVertexes() == numberOfVertexes-1)
                halinGraph.addVertex();
        }
        halinGraph.createOuterBorder();
        return halinGraph;
     */
}

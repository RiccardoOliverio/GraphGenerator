import java.util.ArrayList;


public class Outerplanar extends Graph{
    ArrayList<Vertex> vertexWithInnerEdges = new ArrayList<>(), nonLeaves = new ArrayList<>();


    private Outerplanar(){
        super(3);
        this.makeGraphComplete();
        this.reverseAdjacents(this.getVertex(1));
    }


    //---------------------------------//
    //           GET METHODS           //
    //---------------------------------//
    public static int getMaxNumberOfEdges(int vertexesNumber){
        return 3 + 2*(vertexesNumber - 3);
    }

    private Vertex getFirstOrLastAdjacent(Vertex vertex){
        int vertexIndex = this.getVertexIndex(vertex);
        if(Graph.getRandomGenerator().nextBoolean())
            return this.getAdjacencyList().get(vertexIndex).get(1);
        else
            return this.getAdjacencyList().get(vertexIndex).getLast();
    }

    private Vertex getRandomVertexWithInnerEdges(){
        int randomIndex = Graph.getRandomGenerator().nextInt(vertexWithInnerEdges.size());
        return vertexWithInnerEdges.get(randomIndex);
    }

    private Vertex getRandomInnerAdjacent(Vertex vertex){
        int randomInnerIndex = Graph.getRandomGenerator().nextInt(2, this.getNumberOfAdjacents(vertex));
        return this.getAdjacent(vertex, randomInnerIndex);
    }


    //--------------------------------//
    //          CHECK METHODS         //
    //--------------------------------//
    private boolean checkIfVertexHasInnerEdges(Vertex vertex){
        if(this.getNumberOfAdjacents(vertex) >= 3)
            return true;
        return false;
    }

    private boolean checkIfVertexIsLeaf(Vertex vertex){
        if(this.getNumberOfAdjacents(vertex) == 1)
            return true;
        return false;
    }


    //-------------------------------------//
    //          OVERRIDDEN METHODS         //
    //-------------------------------------//
    @Override
    protected void addVertex(){
        Vertex vertex = this.getRandomVertex();
        super.addVertex();
        Vertex adjacent = this.getFirstOrLastAdjacent(vertex);
        this.addEdgesInOrder(vertex, adjacent);
        this.correctAddedVertexPlanarEmbedding();
    }

    @Override
    protected void addEdgeWithoutCheck(Vertex firstVertex, Vertex secondVertex){
        int i = this.getVertexIndex(firstVertex);
        int j = this.getVertexIndex(secondVertex);
        this.getAdjacencyList().get(i).add(1, secondVertex);
        this.getAdjacencyList().get(j).add(firstVertex);
    }

    @Override
    protected Vertex getRandomNonLeafVertex(){
        int randomIndex = Graph.getRandomGenerator().nextInt(nonLeaves.size());
        return nonLeaves.get(randomIndex);
    }


    //----------------------------------------//
    //          EDGES-RELATED METHODS         //
    //----------------------------------------//
    private void addEdgesInOrder(Vertex vertex, Vertex adjacent){
        int vertexIndex = this.getVertexIndex(vertex);
        int adjacentIndex = this.getVertexIndex(adjacent);
        if(adjacentIndex < vertexIndex)
            this.addEdges(adjacent, vertex);
        else
            this.addEdges(vertex, adjacent);
    }

    private void addEdges(Vertex firstVertex, Vertex hisAdjacent){
        int edgeNumber = this.getAdjacentRelativeIndex(firstVertex, hisAdjacent);
        if(edgeNumber == this.getNumberOfAdjacents(firstVertex)){
            super.addEdgeWithoutCheck(firstVertex, this.getLastVertex());
            this.addEdgeWithoutCheck(hisAdjacent, this.getLastVertex());
        }
        else{
            this.addEdgeWithoutCheck(firstVertex, this.getLastVertex());
            super.addEdgeWithoutCheck(hisAdjacent, this.getLastVertex());
        }
    }

    private boolean removeRandomEdge(boolean isBiconnected){
        Vertex randomVertex, randomAdjacent;
        if(isBiconnected){
            randomVertex = this.getRandomVertexWithInnerEdges();
            randomAdjacent = this.getRandomInnerAdjacent(randomVertex);
            if(this.removeEdgeMaintainingConnection(randomAdjacent, randomVertex)){
                if(!this.checkIfVertexHasInnerEdges(randomVertex))
                    vertexWithInnerEdges.remove(randomVertex);
                if(!this.checkIfVertexHasInnerEdges(randomAdjacent))
                    vertexWithInnerEdges.remove(randomAdjacent);
                return true;
            }
        }
        else{
            randomVertex = this.getRandomNonLeafVertex();
            randomAdjacent = this.getRandomAdjacent(randomVertex);
            if(this.removeEdgeMaintainingConnection(randomAdjacent, randomVertex)){
                if(this.checkIfVertexIsLeaf(randomVertex))
                    nonLeaves.remove(randomVertex);
                if(this.checkIfVertexIsLeaf(randomAdjacent))
                    nonLeaves.remove(randomAdjacent);
                return true;
            }
        }
        return false;
    }


    //----------------------------------------------//
    //          INNER EDGES-RELATED METHODS         //
    //----------------------------------------------//
    private void labelVertexes(){
        for(int i=0; i<this.getNumberOfVertexes(); i++){
            Vertex currentVertex = this.getVertex(i);
            nonLeaves.add(currentVertex);
            if(this.checkIfVertexHasInnerEdges(currentVertex))
                vertexWithInnerEdges.add(currentVertex);
        }
    }


    //-------------------------------------------//
    //          PLANAR EMBEDDING METHODS         //
    //-------------------------------------------//
    private void correctAddedVertexPlanarEmbedding(){
        Vertex firstAdjacent = this.getLastVertexLinkedList().get(1);
        Vertex secondAdjacent = this.getLastVertexLinkedList().get(2);
        int adjacentRelativeIndex = this.getAdjacentRelativeIndex(firstAdjacent, secondAdjacent);
        Vertex nextAdjacent = this.getVertexLinkedList(firstAdjacent).get(adjacentRelativeIndex + 1);
        if(nextAdjacent != this.getLastVertex())
            this.reverseAdjacents(this.getLastVertex());
    }


    //---------------------------------//
    //          STATIC METHODS         //
    //---------------------------------//
    public static Outerplanar createOuterplanarGraph(int numberOfVertexes, int numberOfEdges, boolean isBiconnected){
        Outerplanar outerplanar = new Outerplanar();
        for(int i=4; i<=numberOfVertexes; i++)
            outerplanar.addVertex();

        int edgesToKeep = Graph.getRandomGenerator().nextInt(numberOfVertexes - 1, outerplanar.getNumberOfEdges() + 1);
        if(isBiconnected)
            edgesToKeep = Graph.getRandomGenerator().nextInt(numberOfVertexes, outerplanar.getNumberOfEdges() + 1);
        if(numberOfEdges != -1)
            edgesToKeep = numberOfEdges;
        outerplanar.labelVertexes();
        int currentNumberOfEdges = outerplanar.getNumberOfEdges();
        while(currentNumberOfEdges > edgesToKeep){
            if(outerplanar.removeRandomEdge(isBiconnected))
                currentNumberOfEdges--;
        }
        return outerplanar;
    }
}

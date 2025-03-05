public class PlanarThreeTree extends KTree{
    private PlanarThreeTree(){
        super(3);
        this.correctInitialPlanarEmbedding();
    }


    //--------------------------------//
    //          CHECK METHODS         //
    //--------------------------------//
    private boolean checkIfAdjacentsAreFirstAndLast(Vertex vertex, int firstAdjacentRelativeIndex, int secondAdjacentRelativeIndex){
        return Math.min(firstAdjacentRelativeIndex, secondAdjacentRelativeIndex) != 1 &&
               Math.max(firstAdjacentRelativeIndex, secondAdjacentRelativeIndex) != this.getNumberOfAdjacents(vertex);
    }
    

    //-------------------------------------//
    //          OVERRIDDEN METHODS         //
    //-------------------------------------//
    @Override
    protected void addVertex(){
        super.addVertex();
        this.correctAddedVertexPlanarEmbedding();
        this.getCliques().remove(0);
    }

    @Override
    protected void addEdgeWithoutCheck(Vertex newVertex, Vertex cliqueVertex){
        Graph clique = this.getCliques().get(0);

        Vertex cliqueVertexFirstAdjacent = clique.getVertexLinkedList(cliqueVertex).get(1);
        Vertex cliqueVertexSecondAdjacent = clique.getVertexLinkedList(cliqueVertex).get(2);
        int firstAdjacentRelativeIndex = this.getAdjacentRelativeIndex(cliqueVertex, cliqueVertexFirstAdjacent);
        int secondAdjacentRelativeIndex = this.getAdjacentRelativeIndex(cliqueVertex, cliqueVertexSecondAdjacent);

        if(this.checkIfAdjacentsAreFirstAndLast(cliqueVertex, firstAdjacentRelativeIndex, secondAdjacentRelativeIndex))
            super.addEdgeWithoutCheck(newVertex, cliqueVertex);
        else{
            this.getVertexLinkedList(cliqueVertex).add(Math.max(firstAdjacentRelativeIndex, secondAdjacentRelativeIndex), newVertex);
            this.getVertexLinkedList(newVertex).add(cliqueVertex);
        }
    }


    //-------------------------------------------//
    //          PLANAR EMBEDDING METHODS         //
    //-------------------------------------------//
    private void correctInitialPlanarEmbedding(){
        this.reverseAdjacents(this.getVertex(1));
        this.reverseAdjacents(this.getLastVertex());
    }

    private void correctAddedVertexPlanarEmbedding(){
        Graph temporary = this.getTemporaryGraphFromCurrentCliqueAndNewVertex();
        Vertex firstVertex = temporary.getFirstVertex();
        Vertex firstAdjacentInTemporary = temporary.getVertexLinkedList(firstVertex).get(1);
        Vertex secondAdjacentInTemporary = temporary.getVertexLinkedList(firstVertex).get(2);

        int firstAdjacentRelativeIndex = this.getAdjacentRelativeIndex(firstVertex, firstAdjacentInTemporary);
        int firstVertexLinkedListSize = this.getVertexLinkedList(firstVertex).size();
        Vertex nextAdjacent = this.getVertexLinkedList(firstVertex).get((firstAdjacentRelativeIndex + 1) % firstVertexLinkedListSize);
        while(!temporary.checkIfContainsVertex(nextAdjacent) || nextAdjacent.equals(firstVertex))
            nextAdjacent = this.getVertexLinkedList(firstVertex).get((++firstAdjacentRelativeIndex + 1) % firstVertexLinkedListSize);
        
        if(nextAdjacent.equals(secondAdjacentInTemporary))
            this.switchFirstAndLastAdjacents(this.getLastVertex());
    }

    private Graph getTemporaryGraphFromCurrentCliqueAndNewVertex(){
        Graph temporary = new Graph();
        Graph currentClique = this.getCliques().get(0);
        for(int i=0; i<currentClique.getNumberOfVertexes(); i++)
            temporary.addVertex(currentClique.getVertex(i));
        temporary.addVertex(this.getLastVertex());
        for(int i=1; i<temporary.getNumberOfVertexes(); i++)
            temporary.addEdgeWithoutCheck(temporary.getFirstVertex(), temporary.getVertex(i));
        return temporary;
    }


    //---------------------------------//
    //          STATIC METHODS         //
    //---------------------------------//
    public static PlanarThreeTree createPlanarThreeTree(int numberOfVertexes){
        PlanarThreeTree planarThreeTree = new PlanarThreeTree();
        for(int i=4; i<numberOfVertexes; i++)
            planarThreeTree.addVertex();
        return planarThreeTree;
    }
}
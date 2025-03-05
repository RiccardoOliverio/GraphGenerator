import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Stack;


public class Graph{
    //-------------------------------------------//
    //         START INNER CLASS "VERTEX"        //
    //-------------------------------------------//
    protected class Vertex{
        private static int GLOBAL_ID = 0;
        private int id;


        public Vertex(){
            this.id = GLOBAL_ID++;
        }


        public int getId(){
            return this.id;
        }
    }
    //-------------------------------------------//
    //          END INNER CLASS "VERTEX"         //
    //-------------------------------------------//

    
    private static Random randomGenerator = new Random();
    private ArrayList<LinkedList<Vertex>> adjacencyList;
    protected boolean isConnected = false;


    protected Graph(){
        this.adjacencyList = new ArrayList<>();
    }

    protected Graph(int k){
        this.adjacencyList = new ArrayList<>();
        for(int i=0; i<k; i++){
            this.adjacencyList.add(new LinkedList<>());
            this.adjacencyList.get(getNumberOfVertexes()-1).add(new Vertex());
        }
    }

    protected Graph(ArrayList<Vertex> vertexes){
        this.adjacencyList = new ArrayList<>();
        for(int i=0; i<vertexes.size(); i++){
            this.adjacencyList.add(new LinkedList<>());
            this.adjacencyList.get(getNumberOfVertexes()-1).add(vertexes.get(i));
        }
    }

    
    //---------------------------------//
    //          STATIC METHODS         //
    //---------------------------------//
    public static void setGeneratorSeed(long seed){
        randomGenerator.setSeed(seed);
    }

    public static int getMaxNumberOfEdges(int vertexesNumber){
        return vertexesNumber*(vertexesNumber-1)/2;
    }

    public static Random getRandomGenerator(){
        return randomGenerator;
    }


    //-------------------------------------------------//
    //           GET GRAPH PROPERTIES METHODS          //
    //-------------------------------------------------//
    protected ArrayList<Double> getDegreeDistribution(){
        ArrayList<Double> degrees = new ArrayList<>();
        for(int i=0; i<this.getNumberOfVertexes(); i++){
            int currentVertexDegree = this.getNumberOfAdjacents(this.getVertex(i));
            if(currentVertexDegree >= degrees.size())
                while(degrees.size() <= currentVertexDegree)
                    degrees.add(0.0);
            degrees.set(currentVertexDegree, degrees.get(currentVertexDegree) + 1);
        }
        return degrees;
    }

    protected double getGraphDiameter(){
        double diameter = 0;
        for(int i=0; i<this.getNumberOfVertexes(); i++){
            double currentEccentricity = this.breadthFirstSearchForEccentricity(this.getVertex(i));
            if(currentEccentricity > diameter)
                diameter = currentEccentricity;
        }
        return diameter;
    }

    protected double getGraphClusteringCoefficient(){
        double sum = 0;
        int numberOfVertexes = this.getNumberOfVertexes();
        for(int i=0; i<numberOfVertexes; i++)
            sum += this.getVertexClusteringCoefficient(this.getVertex(i));
        return sum / numberOfVertexes;
    }

    protected double getVertexClusteringCoefficient(Vertex vertex){
        double numberOfAdjacents = this.getNumberOfAdjacents(vertex);
        if(numberOfAdjacents <= 1)
            return 0;
        double edgesOnAdjacents = 0;
        for(int i=1; i<numberOfAdjacents; i++){
            Vertex currentAdjacent = this.getAdjacent(vertex, i);
            for(int j=i+1; j<=numberOfAdjacents; j++){
                if(this.checkIfEdgeExists(currentAdjacent, this.getAdjacent(vertex, j)))
                    edgesOnAdjacents += 1;
            }
        }
        return (2 * edgesOnAdjacents) / (numberOfAdjacents * (numberOfAdjacents - 1));
    }


    //-------------------------------------//
    //           GET LIST METHODS          //
    //-------------------------------------//
    protected ArrayList<LinkedList<Vertex>> getAdjacencyList(){
        return this.adjacencyList;
    }

    protected LinkedList<Vertex> getVertexLinkedList(Vertex vertex){
        return this.adjacencyList.get(this.getVertexIndex(vertex));
    }

    protected LinkedList<Vertex> getLastVertexLinkedList(){
        return this.adjacencyList.get(this.getNumberOfVertexes()-1);
    }


    //-----------------------------------------//
    //           GET VERTEXES METHODS          //
    //-----------------------------------------//
    protected Vertex getVertex(int index){
        return this.adjacencyList.get(index).getFirst();
    }

    protected Vertex getFirstVertex(){
        return this.adjacencyList.get(0).getFirst();
    }

    protected Vertex getLastVertex(){
        return this.adjacencyList.get(this.getNumberOfVertexes()-1).getFirst();
    }

    protected Vertex getRandomVertex(){
        return this.getVertex(randomGenerator.nextInt(this.getNumberOfVertexes()));
    }

    protected Vertex getRandomNonLeafVertex(){
        Vertex randomVertex = this.getRandomVertex();
        while(this.getNumberOfAdjacents(randomVertex) == 1)
            randomVertex = this.getRandomVertex();
        return randomVertex;
    }

    protected Vertex getRandomVertexWithAdjacents(){
        Vertex randomVertex = this.getRandomVertex();
        while(this.getNumberOfAdjacents(randomVertex) == 0)
            randomVertex = this.getRandomVertex();
        return randomVertex;
    }

    protected Vertex getRandomVertexWithMaxAdjacents(int num){
        Vertex randomVertex = this.getRandomVertex();
        while(this.getNumberOfAdjacents(randomVertex) > num+1)
            randomVertex = this.getRandomVertex();
        return randomVertex;
    }

    protected Vertex getAdjacent(Vertex vertex, int adjacentIndex){
        return this.getVertexLinkedList(vertex).get(adjacentIndex);
    }

    protected Vertex getFirstAdjacent(Vertex vertex){
        return this.getVertexLinkedList(vertex).get(1);
    }

    protected Vertex getLastAdjacent(Vertex vertex){
        return this.getVertexLinkedList(vertex).getLast();
    }

    protected Vertex getRandomAdjacent(Vertex vertex){
        if(this.getNumberOfAdjacents(vertex) == 0)
            return null;
        int randomAdjacentRelativeIndex = randomGenerator.nextInt(this.getNumberOfAdjacents(vertex)) + 1;
        return this.getVertexLinkedList(vertex).get(randomAdjacentRelativeIndex);
    }

    //------------------------------------------//
    //           GET NUMBER OF METHODS          //
    //------------------------------------------//
    protected int getNumberOfVertexes(){
        return this.adjacencyList.size();
    }

    protected int getNumberOfEdges(){
        int numberOfEdges = 0;
        for(int i=0; i<this.getNumberOfVertexes(); i++){
            for(int j=1; j<this.adjacencyList.get(i).size(); j++)
                numberOfEdges++;
        }
        return numberOfEdges/2;
    }

    protected int getNumberOfAdjacents(Vertex vertex){
        return this.adjacencyList.get(this.getVertexIndex(vertex)).size()-1;
    }

    //-----------------------------------------//
    //            GET INDEXES METHODS          //
    //-----------------------------------------//
    protected int getVertexIndex(Vertex vertex){
        for(int i=0; i<this.getNumberOfVertexes(); i++){
            if(this.getVertex(i).equals(vertex))
                return i;
        }
        return -1;
    }

    protected int getLastIndex(){
        return this.adjacencyList.size()-1;
    }

    protected int getAdjacentRelativeIndex(Vertex vertex, Vertex adjacent){
        int index = this.getVertexIndex(vertex);
        for(int i=1; i<this.getNumberOfAdjacents(vertex)+1; i++){
            if(this.adjacencyList.get(index).get(i).equals(adjacent))
                return i;
        }
        return -1;
    }


    //---------------------------------//
    //          CHECK METHODS          //
    //---------------------------------//
    protected boolean checkIfEdgeExists(Vertex firstVertex, Vertex secondVertex){
        if(this.getVertexLinkedList(firstVertex).contains(secondVertex))
            return true;
        return false;
    }

    protected boolean checkIfThereIsIsolatedVertex(){
        for(int i=0; i<this.getNumberOfVertexes(); i++)
            if(this.getNumberOfAdjacents(this.getVertex(i)) == 0)
                return true;
        return false;
    }

    protected boolean checkIfContainsVertex(Vertex vertex){
        if(this.getVertexIndex(vertex) == -1)
            return false;
        return true;
    }            

    protected boolean checkIfGraphIsConnected(){
        Stack<Vertex> foundVertexes = this.breadthFirstSearchForVertexes(this.getFirstVertex());
        if(foundVertexes.size() == this.getNumberOfVertexes())
            return true;
        return false;
    }


    //---------------------------------//
    //     VERTEX-RELATED METHODS      //
    //---------------------------------//
    protected void addVertex(){
        this.adjacencyList.add(new LinkedList<>());
        this.getLastVertexLinkedList().add(new Vertex());
    }

    protected void addVertex(Vertex vertex){
        this.adjacencyList.add(new LinkedList<>());
        this.getLastVertexLinkedList().add(vertex);
    }

    protected void removeVertex(Vertex vertexToRemove){
        int vertexIndex = this.getVertexIndex(vertexToRemove);
        this.adjacencyList.get(vertexIndex).clear();
        this.adjacencyList.remove(vertexIndex);
        for(int j=0; j<this.getNumberOfVertexes(); j++)
            this.adjacencyList.get(j).remove(vertexToRemove);
    }

    protected boolean removeVertexMaintainingConnection(Vertex vertexToRemove){
        int vertexIndex = this.getVertexIndex(vertexToRemove);
        Vertex[] adjacents = new Vertex[this.getNumberOfAdjacents(vertexToRemove)];
        int[] adjacentsIndex = new int[adjacents.length];
        for(int i=0; i<adjacents.length; i++){
            adjacents[i] = this.getAdjacent(vertexToRemove, i+1);
            adjacentsIndex[i] = this.getAdjacentRelativeIndex(adjacents[i], vertexToRemove);
        }
        this.removeVertex(vertexToRemove);

        if(!this.checkIfGraphIsConnected()){
            LinkedList<Vertex> removedVertexList = new LinkedList<>();
            removedVertexList.add(vertexToRemove);
            for(int i=0; i<adjacents.length; i++){
                removedVertexList.add(adjacents[i]);
                this.getVertexLinkedList(adjacents[i]).add(adjacentsIndex[i], vertexToRemove);
            }
            this.adjacencyList.add(vertexIndex, removedVertexList);
            return false;
        }
        return true;
    }


    //---------------------------------//
    //      EDGE-RELATED METHODS       //
    //---------------------------------//
    protected boolean addEdge(Vertex firstVertex, Vertex secondVertex){
        if(!this.checkIfEdgeExists(firstVertex, secondVertex)){
            int firstVertexIndex = this.getVertexIndex(firstVertex);
            int secondVertexIndex = this.getVertexIndex(secondVertex);
            this.adjacencyList.get(firstVertexIndex).add(secondVertex);
            this.adjacencyList.get(secondVertexIndex).add(firstVertex);
            return true;
        }
        return false;
    }

    protected void addEdgeWithoutCheck(Vertex firstVertex, Vertex secondVertex){
        int firstVertexIndex = this.getVertexIndex(firstVertex);
        int secondVertexIndex = this.getVertexIndex(secondVertex);
        this.adjacencyList.get(firstVertexIndex).add(secondVertex);
        this.adjacencyList.get(secondVertexIndex).add(firstVertex);
    }

    protected void removeEdgeWithoutCheck(Vertex firstVertex, Vertex secondVertex){
        int firstVertexIndex = this.getVertexIndex(firstVertex);
        int secondVertexIndex = this.getVertexIndex(secondVertex);
        this.adjacencyList.get(firstVertexIndex).remove(secondVertex);
        this.adjacencyList.get(secondVertexIndex).remove(firstVertex);
    }

    protected boolean removeEdgeMaintainingConnection(Vertex firstVertex, Vertex secondVertex){
        int firstVertexEdgePosition = this.getAdjacentRelativeIndex(firstVertex, secondVertex);
        int secondVertexEdgePosition = this.getAdjacentRelativeIndex(secondVertex, firstVertex);
        this.removeEdgeWithoutCheck(firstVertex, secondVertex);
        if(!this.checkIfGraphIsConnected()){
            int firstVertexIndex = this.getVertexIndex(firstVertex);
            int secondVertexIndex = this.getVertexIndex(secondVertex);
            this.adjacencyList.get(firstVertexIndex).add(firstVertexEdgePosition, secondVertex);
            this.adjacencyList.get(secondVertexIndex).add(secondVertexEdgePosition, firstVertex);
            return false;
        }
        return true;
    }

    protected void makeGraphComplete(){
        for(int i=0; i<this.getNumberOfVertexes()-1; i++){
            for(int j=i+1; j<this.getNumberOfVertexes(); j++)
                this.addEdge(this.getVertex(i), this.getVertex(j));
        }
    }


    //-------------------------------------//
    //      ADJACENT-RELATED METHODS       //
    //-------------------------------------//
    protected void reverseAdjacents(Vertex vertex){
        LinkedList<Vertex> vertexList = this.getVertexLinkedList(vertex);
        for(int i=0; i<this.getNumberOfAdjacents(vertex)-1; i++)
            vertexList.add(1+i, vertexList.removeLast());
    }

    protected void switchFirstAndLastAdjacents(Vertex vertex){
        LinkedList<Vertex> vertexList = this.getVertexLinkedList(vertex);
        Vertex firstAdjacent = vertexList.get(1);
        Vertex lastAdjacent = vertexList.getLast();
        vertexList.set(1, lastAdjacent);
        vertexList.set(this.getNumberOfAdjacents(vertex), firstAdjacent);
    }


    //-------------------------------------//
    //          COPY GRAPH METHODS         //
    //-------------------------------------//
    protected Graph copyGraph(){
        Graph newGraph = new Graph();
        newGraph.adjacencyList = new ArrayList<>(this.adjacencyList);
        //newGraph.adjacencyList = (ArrayList<LinkedList<Vertex>>)this.adjacencyList.clone();
        return newGraph;
    }

    protected void vertexesFusion(Vertex firstVertex, Vertex secondVertex, int position){
        this.getAdjacencyList().add(position, new LinkedList<>());
        this.getAdjacencyList().get(position).add(new Vertex());
        Vertex newVertex = this.getVertex(position);
        this.removeEdgeWithoutCheck(firstVertex, secondVertex);
        int firstVertexNumberOfAdjacents = this.getNumberOfAdjacents(firstVertex);
        int secondVertexNumberOfAdjacents = this.getNumberOfAdjacents(secondVertex);

        for(int i = 1; i <= firstVertexNumberOfAdjacents; i++)
            this.addEdgeWithoutCheck(newVertex, this.getAdjacent(firstVertex, i));
        for(int i = 1; i <= secondVertexNumberOfAdjacents; i++)
            this.addEdgeWithoutCheck(newVertex, this.getAdjacent(secondVertex, i));
        
        this.removeVertex(firstVertex);
        this.removeVertex(secondVertex);
    }


    //-----------------------------------------------//
    //          BREADTH-FIRST SEARCH METHODS         //
    //-----------------------------------------------//
    protected Stack<Vertex> breadthFirstSearchForVertexes(Vertex startVertex){
        LinkedList<Vertex> queue = new LinkedList<>();
        queue.add(startVertex);
        Stack<Vertex> foundVertexes = new Stack<>();
        foundVertexes.add(startVertex);
        while(!queue.isEmpty()){
            Vertex currentVertex = queue.remove();
            Vertex[] adjacents = new Vertex[this.getVertexLinkedList(currentVertex).size()];
            adjacents = this.getVertexLinkedList(currentVertex).toArray(adjacents);
            for(int i=1; i<adjacents.length; i++){
                Vertex currentAdjacentVertex = adjacents[i];
                if(!foundVertexes.contains(currentAdjacentVertex)){
                    foundVertexes.add(currentAdjacentVertex);
                    queue.add(currentAdjacentVertex);
                }
            }
        }
        return foundVertexes;
    }

    protected double breadthFirstSearchForEccentricity(Vertex startVertex){
        LinkedList<Vertex> queue = new LinkedList<>();
        queue.add(startVertex);
        Stack<Vertex> foundVertexes = new Stack<>();
        foundVertexes.add(startVertex);
        int vertexEccentricity = 0;
        while(!queue.isEmpty()){
            Vertex currentVertex = queue.remove();
            Vertex[] adjacents = new Vertex[this.getVertexLinkedList(currentVertex).size()];
            adjacents = this.getVertexLinkedList(currentVertex).toArray(adjacents);
            for(int i=1; i<adjacents.length; i++){
                Vertex currentAdjacentVertex = adjacents[i];
                if(!foundVertexes.contains(currentAdjacentVertex)){
                    foundVertexes.add(currentAdjacentVertex);
                    queue.add(currentAdjacentVertex);
                    vertexEccentricity++;
                }
            }
        }
        return vertexEccentricity;
    }
}
import java.util.ArrayList;


public class KTree extends Graph{
    private ArrayList<Graph> cliques = new ArrayList<>();
    private int kValue;


    protected KTree(int k){
        super(k+1);
        this.makeGraphComplete();
        this.createNewCliques(this, null);
        this.kValue = k;
    }


    //----------------------------------//
    //           GET METHODS            //
    //----------------------------------//
    public static int getMaxNumberOfEdges(int k, int n){
		return k*(2*n - k -1)/2;
	}

    protected ArrayList<Graph> getCliques(){
        return this.cliques;
    }

    private Graph getRandomClique(){
        return this.cliques.get(Graph.getRandomGenerator().nextInt(this.cliques.size()));
    }

    public int getKValue(){
        return this.kValue;
    }


    //---------------------------------//
    //       OVERRIDDEN METHODS        //
    //---------------------------------//
    @Override
    protected void addVertex(){
        super.addVertex();
        Graph randomClique = this.getRandomClique();
        this.shiftCliqueToTheStart(randomClique);
        Vertex addedVertex = this.getLastVertex();
        for(int i=0; i<randomClique.getNumberOfVertexes(); i++)
            this.addEdgeWithoutCheck(addedVertex, randomClique.getVertex(i));
        this.createNewCliques(randomClique, addedVertex);
    }

    @Override
    protected void removeVertex(Vertex vertexToRemove){
        for(int j=0; j<this.cliques.size(); j++){
            if(this.cliques.get(j).checkIfContainsVertex(vertexToRemove))
                this.cliques.remove(j);
        }
        super.removeVertex(vertexToRemove);
    }


    //---------------------------------//
    //         CLIQUES METHODS         //
    //---------------------------------//
    private void createNewCliques(Graph clique, Vertex newVertex){
        for(int i=0; i<clique.getNumberOfVertexes(); i++){
            ArrayList<Vertex> newCliqueVertexes = new ArrayList<>();
            for(int j=0; j<clique.getNumberOfVertexes(); j++){
                if(clique.getVertex(j) != clique.getVertex(i))
                    newCliqueVertexes.add(clique.getVertex(j));
            }
            if(newVertex != null)
                newCliqueVertexes.add(newVertex);
            this.addNewCliques(newCliqueVertexes);
        }
    }

    private void addNewCliques(ArrayList<Vertex> vertexes){
        Graph newClique = new Graph(vertexes);
        newClique.makeGraphComplete();
        this.cliques.add(newClique);
    }

    private void shiftCliqueToTheStart(Graph clique){
        this.cliques.remove(clique);
        this.cliques.add(0, clique);
    }


    //-----------------------------------------//
    //          PARTIAL K-TREE METHODS         //
    //-----------------------------------------//
    public Graph getRandomPartialKTree(int vertexesToKeep, int edgesToKeep, boolean connected){
        Graph subgraph = this.copyGraph();
        subgraph.isConnected = connected;
        int subgraphVertexes = subgraph.getNumberOfVertexes();
        boolean calculateEdges = false;
        if(vertexesToKeep == -1 && edgesToKeep == -1){
            vertexesToKeep = Graph.getRandomGenerator().nextInt(1, subgraphVertexes);
            calculateEdges = true;
        }

        if(vertexesToKeep != -1){
            if(edgesToKeep != -1 && subgraph.isConnected)
                deleteVertexesWithGivenEdgesIfConnected(subgraph, vertexesToKeep, this.kValue);
            else if(edgesToKeep == -1 && subgraph.isConnected)
                deleteVertexesIfConnected(subgraph, vertexesToKeep);
            else
                deleteVertexes(subgraph, vertexesToKeep);
        }

        if(edgesToKeep != -1 || calculateEdges){
            ArrayList<Vertex> vertexesWithAdjacents = fillVertexesWithAdjacents(subgraph);
            if(subgraph.isConnected){
                if(calculateEdges)
                    edgesToKeep = Graph.getRandomGenerator().nextInt(subgraph.getNumberOfVertexes() - 1, subgraph.getNumberOfEdges() + 1);
                deleteEdgesIfConnected(subgraph, edgesToKeep, vertexesWithAdjacents);
            }
            else{
                if(calculateEdges)
                    edgesToKeep = Graph.getRandomGenerator().nextInt(subgraph.getNumberOfEdges() + 1);
                deleteEdges(subgraph, edgesToKeep, vertexesWithAdjacents);
            }
        }
        return subgraph;
    }

    private static void deleteVertexesWithGivenEdgesIfConnected(Graph subgraph, int vertexesToKeep, int k){
        int subgraphVertexes = subgraph.getNumberOfVertexes();
        while(subgraphVertexes > vertexesToKeep){
            Vertex randomVertex = subgraph.getRandomVertexWithMaxAdjacents(k);
            if(subgraph.removeVertexMaintainingConnection(randomVertex))
                subgraphVertexes--;
        }
    }

    private static void deleteVertexesIfConnected(Graph subgraph, int vertexesToKeep){
        int subgraphVertexes = subgraph.getNumberOfVertexes();
        while(subgraphVertexes > vertexesToKeep){
            Vertex randomVertex = subgraph.getRandomVertex();
            if(subgraph.removeVertexMaintainingConnection(randomVertex))
                subgraphVertexes--;
        }
    }

    private static void deleteVertexes(Graph subgraph, int vertexesToKeep){
        int subgraphVertexes = subgraph.getNumberOfVertexes();
        while(subgraphVertexes > vertexesToKeep){
            Vertex randomVertex = subgraph.getRandomVertex();
            subgraph.removeVertex(randomVertex);
            subgraphVertexes--;
        }
    }

    private static void deleteEdgesIfConnected(Graph subgraph, int edgesToKeep, ArrayList<Vertex> vertexesWithAdjacents){
        int subgraphEdges = subgraph.getNumberOfEdges();
        while(subgraphEdges > edgesToKeep){
            int randomIndex = Graph.getRandomGenerator().nextInt(vertexesWithAdjacents.size());
            Vertex randomVertex = vertexesWithAdjacents.get(randomIndex);
            Vertex hisRandomAdjacent = subgraph.getRandomAdjacent(randomVertex);
            if(subgraph.removeEdgeMaintainingConnection(randomVertex, hisRandomAdjacent)){
                checkIfVertexesStillHaveEnoughAdjacents(subgraph, vertexesWithAdjacents, randomVertex, hisRandomAdjacent, 2);
                subgraphEdges--;
            }
        }
    }

    private static void deleteEdges(Graph subgraph, int edgesToKeep, ArrayList<Vertex> vertexesWithAdjacents){
        int subgraphEdges = subgraph.getNumberOfEdges();
        while(subgraphEdges > edgesToKeep){
            int randomIndex = Graph.getRandomGenerator().nextInt(vertexesWithAdjacents.size());
            Vertex randomVertex = vertexesWithAdjacents.get(randomIndex);
            Vertex hisRandomAdjacent = subgraph.getRandomAdjacent(randomVertex);
            subgraph.removeEdgeWithoutCheck(randomVertex, hisRandomAdjacent);
            checkIfVertexesStillHaveEnoughAdjacents(subgraph, vertexesWithAdjacents, randomVertex, hisRandomAdjacent, 1);
            subgraphEdges--;
        }
    }

    private static void checkIfVertexesStillHaveEnoughAdjacents(Graph subgraph, ArrayList<Vertex> vertexesWithAdjacents,
                                                                Vertex firstVertex, Vertex secondVertex, int adjacentsNumber){
        if(subgraph.getNumberOfAdjacents(firstVertex) < adjacentsNumber)
            vertexesWithAdjacents.remove(firstVertex);
        if(subgraph.getNumberOfAdjacents(secondVertex) < adjacentsNumber)
            vertexesWithAdjacents.remove(secondVertex);
    }

    private static ArrayList<Vertex> fillVertexesWithAdjacents(Graph subgraph){
        ArrayList<Vertex> vertexesWithAdjacents = new ArrayList<>();
        int subgraphVertexes = subgraph.getNumberOfVertexes();
        if(subgraph.isConnected){
            for(int i=0; i<subgraphVertexes; i++)
                if(subgraph.getNumberOfAdjacents(subgraph.getVertex(i)) >= 2)
                    vertexesWithAdjacents.add(subgraph.getVertex(i));
        } else{
            for(int i=0; i<subgraphVertexes; i++)
                if(subgraph.getNumberOfAdjacents(subgraph.getVertex(i)) >= 1)
                    vertexesWithAdjacents.add(subgraph.getVertex(i));
        }
        return vertexesWithAdjacents;
    }


    //---------------------------------//
    //          STATIC METHODS         //
    //---------------------------------//
    public static KTree createKTree(int k, int numberOfVertexes){
        KTree ktree = new KTree(k);
        for(int i=k+1; i<numberOfVertexes; i++)
            ktree.addVertex();
        return ktree;
    }
}


// tempi, proprietà (?)

//intro: che ho fatto e perché
//cap 1: def grafo, treewidth, famiglie varie (ktree, planar 3-tree, halin)
//cap 2: algoritmi di generazione
//cap 3: implementazione (java, linea di comando, graphml, come i grafi, come l'embedding)
//cap 4: sperimentazione (perché, setting, risultati)
//conclusione: cos'altro si potrebbe fare
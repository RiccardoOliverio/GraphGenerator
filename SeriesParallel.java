public class SeriesParallel extends Graph{
    private static CompositionTree compositionTree = null;
    

    private SeriesParallel(){
        super(2);
        this.addEdgeWithoutCheck(this.getFirstVertex(), this.getLastVertex());
    }
    

    //---------------------------------//
    //           MERGE METHODS         //
    //---------------------------------//
    private static SeriesParallel seriesComposition(SeriesParallel firstGraph, SeriesParallel secondGraph){
        Vertex firstSink = firstGraph.getLastVertex();
        Vertex secondSource = secondGraph.getFirstVertex();
        int firstSinkIndex = firstGraph.getLastIndex();

        firstGraph.getAdjacencyList().addAll(secondGraph.getAdjacencyList());
        firstGraph.vertexesFusion(firstSink, secondSource, firstSinkIndex);
        firstGraph.reverseAdjacents(firstGraph.getVertex(firstSinkIndex));
        return firstGraph;
    }

    private static SeriesParallel parallelComposition(SeriesParallel firstGraph, SeriesParallel secondGraph){
        Vertex firstSource = firstGraph.getFirstVertex();
        Vertex secondSource = secondGraph.getFirstVertex();
        Vertex firstSink = firstGraph.getLastVertex();
        Vertex secondSink = secondGraph.getLastVertex();

        firstGraph.getAdjacencyList().addAll(secondGraph.getAdjacencyList());
        firstGraph.vertexesFusion(firstSource, secondSource, 0);
        firstGraph.vertexesFusion(firstSink, secondSink, firstGraph.getLastIndex());
        return firstGraph;
    }


    //----------------------------------------------//
    //          CREATE GRAPH STATIC METHODS         //
    //----------------------------------------------//
    public static SeriesParallel createSeriesParallelGraph(int vertexesNumber){
        compositionTree = new CompositionTree(vertexesNumber);
        return createSeriesParallelGraphRecursive(compositionTree.getRootIndex());
    }

    private static SeriesParallel createSeriesParallelGraphRecursive(int index){
        if(compositionTree.getNode(index).isS()){
            SeriesParallel leftSP = createSeriesParallelGraphRecursive(compositionTree.getLeftChildIndex(index));
            SeriesParallel rightSP = createSeriesParallelGraphRecursive(compositionTree.getRightChildIndex(index));
            return seriesComposition(leftSP, rightSP);
        }
        else if(compositionTree.getNode(index).isP()){
            SeriesParallel leftSP = createSeriesParallelGraphRecursive(compositionTree.getLeftChildIndex(index));
            SeriesParallel rightSP = createSeriesParallelGraphRecursive(compositionTree.getRightChildIndex(index));
            return parallelComposition(leftSP, rightSP);
        }
        else
            return new SeriesParallel();
    }
}


/*
 * serie:
 *      unisci lista di adiacenza
 *      fondi ultimo vertice della prima lista e primo vertice della seconda lista
 *          crea un nuovo vertice, posizionalo prima dell'ultimo vertice della prima lista
 *          copia gli archi di quest'ultimo nel nuovo vertice
 *          copia gli archi del primo vertice della seconda lista nel nuovo vertice
 *          elimina i due vertici vecchi
 */


 /*
        ricorsione seconda:
            se P + 2 figli Q:
                elimina figli, P diventa Q
            se Q:
                aggiungi nodo/indice ad una lista
                return true
            se S:
                aggiungi nodo/indice ad una lista
                return false
            se P:
                return ricorsione seconda sui figli (||)

        */
        
        /*
        ricorsione:
            se S:
                ricorsione entrambi figli
            se P + 2 figli Q:
                elimina figli, P diventa Q
            se P + 1 figlio S:
                ricorsione entrambi figli
            se P + 2P || P + P + Q:
                ricorsione seconda su entrambi figli per trovare due Q (Q && Q)
                se Q && Q:
                    rimuovile tutte
                    ricorsione sui fratelli delle Q se non sono P
                    ricorsione sugli ultimi nodi visitati che sono S
        */
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class FileCreation {
    private static FileCreation instance = null;
    private String directoryName = null;


    private FileCreation(){
        // do-nothing
    }


    public void setDirectoryName(String name){
        this.directoryName = name;
        new File(directoryName).mkdir();
    }

    public void fromGraphToFile(Graph graph, String type, int index){
        try{
            String filename = getFileName(type, index, false);
            File graphFile = new File(filename);
			graphFile.createNewFile();
            FileWriter fileWriter = new FileWriter(graphFile);

            this.writeFileStartingLines(fileWriter);
            this.writeAllNodes(fileWriter, graph);
            this.writeAllEdges(fileWriter, graph);
            this.writeFileEndingLines(fileWriter);

            fileWriter.close();
        } catch(IOException ioe){
            System.out.println("Something went wrong during file(s) creation.");
            System.exit(-1);
        }
    }

    public void writeGraphProperties(String type, ArrayList<Double> degreeDistribution, double clusteringCoefficient, double diameter, long time, int index){
		try{
			String filename = getFileName(type, index, true);
			File results = new File(filename);
			results.createNewFile();
			FileWriter fileWriter = new FileWriter(filename);
		
            fileWriter.write("Creation time: " + (double)time/1000000 + "(ms)" + "\n");
			fileWriter.write("Degree distribution: " + "\n");
			for(int i=0; i<degreeDistribution.size(); i++)
				fileWriter.write("\t Degree " + i + ": " + degreeDistribution.get(i) + "\n");
            fileWriter.write("Graph diameter: " + diameter + "\n");

            fileWriter.write("Graph clustering coefficient: " + clusteringCoefficient + "\n");

			fileWriter.close();
		} catch(IOException ioe){
			System.out.println("Error during testing.");
			System.exit(-1);
		}
	}

    public void writeGraphProperties(String type, double clusteringCoefficient, int index){
		try{
			String filename = getFileName(type, index, true);
			File results = new File(filename);
			results.createNewFile();
			FileWriter fileWriter = new FileWriter(filename);

            fileWriter.write("Diameter: " + clusteringCoefficient + "\n");

			fileWriter.close();
		} catch(IOException ioe){
			System.out.println("Error during testing.");
			System.exit(-1);
		}
	}

    public String getFileName(String type, int index, boolean isProperties){
        String filename = "", accessory = "", fileFormat = ".graphml";
        if(directoryName != null)
            filename = directoryName + "\\";
        if(isProperties){
            accessory = "_results";
            fileFormat = ".txt";
        }
        
        int vertexes = ParametersReader.getInstance().getVertexes();
		if(type.equals("ktree")){
			int k = ParametersReader.getInstance().getKValue();
			return filename + k + "tree" + vertexes + accessory + " " + index + fileFormat;
		}
        if(type.equals("partial")){
            int k = ParametersReader.getInstance().getKValue();
            if(ParametersReader.getInstance().getGraphType().equals("p3tree"))
                return filename + "partial-planar" + k + "tree" + vertexes + accessory + " " + index + fileFormat;
			return filename + "partial-" + k + "tree" + vertexes + accessory + " " + index + fileFormat;
        }
        if(type.equals("p3tree"))
			return filename + "planar3tree" + vertexes + accessory + " " + index + fileFormat;

		return filename + type + accessory + vertexes + " " + index + fileFormat;
	}

    
    //---------------------------------//
    //       WRITE NODES METHODS       //
    //---------------------------------//
    private void writeAllNodes(FileWriter fileWriter, Graph graph) throws IOException{
        for(int i=0; i<graph.getAdjacencyList().size(); i++){
            fileWriter.write("\t\t<node id=\"n"+graph.getAdjacencyList().get(i).getFirst().getId()+"\">\n");
            fileWriter.write("\t\t</node>\n");
        }
    }


    //---------------------------------//
    //       WRITE EDGES METHODS       //
    //---------------------------------//
    private void writeAllEdges(FileWriter fileWriter, Graph graph) throws IOException{
        for(int i=0; i<graph.getAdjacencyList().size(); i++){
            for(int j=1; j<graph.getAdjacencyList().get(i).size(); j++){
                if(!this.checkIfEdgeHasAlreadyBeenWritten(graph, i, j)){
                    int sourceId = graph.getAdjacencyList().get(i).getFirst().getId();
                    int targetId = graph.getAdjacencyList().get(i).get(j).getId();
                    fileWriter.write("\t\t<edge source=\"n"+sourceId+"\" target=\"n"+targetId+"\">\r\n");
                    this.writeEdgeProperties(fileWriter);
                    fileWriter.write("\t\t</edge>\r\n");
                }
            }
        }
    }

    private boolean checkIfEdgeHasAlreadyBeenWritten(Graph graph, int i, int j){
        for(int k=i-1; k>=0; k--)
            if(graph.getAdjacencyList().get(k).getFirst().equals(graph.getAdjacencyList().get(i).get(j)))
                return true;
        return false;
    }

    private void writeEdgeProperties(FileWriter fileWriter) throws IOException{
        fileWriter.write("\t\t\t<data key=\"d1\">\r\n" + //
                        "\t\t\t\t<y:PolyLineEdge>\r\n" + //
                        "\t\t\t\t\t<y:Arrows source=\"none\" target=\"none\"/>\r\n" + //
                        "\t\t\t\t</y:PolyLineEdge>\r\n" + //
                        "\t\t\t</data>\r\n");
    }


    //---------------------------------------------//
    //          START AND END LINES METHODS        //
    //---------------------------------------------//
    private void writeFileStartingLines(FileWriter fileWriter) throws IOException{
        fileWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + //
                        "<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\"  \r\n" + //
                        "\t\t xmlns:y=\"http://www.yworks.com/xml/graphml\">\r\n" + //
                        "\t\t xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + //
                        "\t\t xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns\r\n" + //
                        "\t\t\thttp://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\"\r\n" + //
                        //"\t<key id=\"d0\" for=\"node\" yfiles.type=\"nodegraphics\"/>\r\n" + //
                        "\t<key id=\"d1\" for=\"edge\" yfiles.type=\"edgegraphics\"/>\r\n"+ //
                        "\t<graph id=\"G\" edgedefault=\"undirected\">\r\n");
    }

    private void writeFileEndingLines(FileWriter fileWriter) throws IOException{
        fileWriter.write("\t</graph>\r\n</graphml>\r\n");
    }


    //---------------------------------//
    //       GET INSTANCE METHOD       //
    //---------------------------------//
    public static FileCreation getInstance(){
        if(instance == null)
            instance = new FileCreation();
        return instance;
    }
}

public class Main{
    public static void main(String[] args){
		ParametersReader.createInstance(args).readParameters();
		createAndWriteGraph();
	}


	private static void createAndWriteGraph(){
		String graphType = ParametersReader.getInstance().getGraphType();
		if(ParametersReader.getInstance().getIsSuite())
			createSuiteOfGraph(graphType);
		else
			createSingleGraph(graphType);
	}


	//----------------------------------------------//
    //        SINGLE GRAPH CREATION METHODS         //
    //----------------------------------------------//
	private static void createSingleGraph(String type){
		int n = ParametersReader.getInstance().getVertexes();
		int instances = ParametersReader.getInstance().getRepeatedInstances();
		switch(type){
			case "halin":
				for(int i=0; i<instances; i++)
					createHalinGraph(n, i);
				break;
			case "p3tree":
				for(int i=0; i<instances; i++)
					createP3TreeGraph(n, i);
				break;
			case "ktree":
				for(int i=0; i<instances; i++)
					createKTreeGraph(ParametersReader.getInstance().getKValue(), n, i);
				break;
			case "series-parallel":
				for(int i=0; i<instances; i++)
					createSPGraph(n, i);
				break;
			case "outerplanar":
				for(int i=0; i<instances; i++)
					createOuterGraph(n, i);
				break;
			default:
				break;
		}
	}
	
	private static void createKTreeGraph(int k, int n, int index){
		KTree graphToGenerate = KTree.createKTree(k, n);
		FileCreation.getInstance().fromGraphToFile(graphToGenerate, "ktree", index);
		if(ParametersReader.getInstance().checkIfPartialHasToBeCreated())
			createPartialKTree(graphToGenerate, index);
	}

	private static void createP3TreeGraph(int n, int index){
		PlanarThreeTree graphToGenerate = PlanarThreeTree.createPlanarThreeTree(n);
		FileCreation.getInstance().fromGraphToFile(graphToGenerate, "p3tree", index);
		if(ParametersReader.getInstance().checkIfPartialHasToBeCreated())
			createPartialKTree(graphToGenerate, index);
	}

	private static void createPartialKTree(KTree originalKTree, int index){
		boolean isConnected = ParametersReader.getInstance().isConnected();
		int vertexesToKeep = ParametersReader.getInstance().getVertexesToKeep();
		int edgesToKeep = ParametersReader.getInstance().getEdgesToKeep();
		
		Graph partialKTree = null;
		partialKTree = originalKTree.getRandomPartialKTree(vertexesToKeep, edgesToKeep, isConnected);
		FileCreation.getInstance().fromGraphToFile(partialKTree, "partial", index);
	}

	private static void createOuterGraph(int n, int index){
		boolean isBiconnected = ParametersReader.getInstance().isBiconnected();
		Outerplanar graphToGenerate;
		if(ParametersReader.getInstance().checkIfPartialDeletesEdges())
			graphToGenerate = Outerplanar.createOuterplanarGraph(n, ParametersReader.getInstance().getEdgesToKeep(), isBiconnected);
		else
			graphToGenerate = Outerplanar.createOuterplanarGraph(n, -1, isBiconnected);
		FileCreation.getInstance().fromGraphToFile(graphToGenerate, "outerplanar", index);
	}

	private static void createHalinGraph(int n, int index){
		Halin graphToGenerate;
		if(ParametersReader.getInstance().checkIfPartialDeletesEdges())
			graphToGenerate = Halin.createHalinGraph(n, ParametersReader.getInstance().getEdgesToKeep());
		else
			graphToGenerate = Halin.createHalinGraph(n, -1);
		FileCreation.getInstance().fromGraphToFile(graphToGenerate, "halin", index);
	}

	private static void createSPGraph(int n, int index){
		SeriesParallel graphToGenerate = SeriesParallel.createSeriesParallelGraph(n);
		FileCreation.getInstance().fromGraphToFile(graphToGenerate, "series-parallel", index);
	}


	//-------------------------------------------------//
    //         SUITE OF GRAPHS CREATION METHOD         //
    //-------------------------------------------------//
	private static void createSuiteOfGraph(String type){
		int startVertexes = ParametersReader.getInstance().getVertexes();
		int finalVertexes = ParametersReader.getInstance().getFinalVertexes();
		int step = ParametersReader.getInstance().getStep();
		switch(type){
			case "halin":
				for(int i=startVertexes; i<finalVertexes; i+=step)
					createHalinGraph(i, 0);
				break;
			case "p3tree":
				for(int i=startVertexes; i<finalVertexes; i+=step)
					createP3TreeGraph(i, 0);
				break;
			case "ktree":
				for(int i=startVertexes; i<finalVertexes; i+=step)
					createKTreeGraph(ParametersReader.getInstance().getKValue(), i, 0);
				break;
			case "series-parallel":
				for(int i=startVertexes; i<finalVertexes; i+=step)
					createSPGraph(i, 0);
				break;
			case "outer":
				for(int i=startVertexes; i<finalVertexes; i+=step)
					createOuterGraph(i, 0);
				break;
			default:
				break;
		}
	}
}


/*
 * private static ArrayList<Double> degreeDistributionSum = new ArrayList<>(), partialDegreeDistributionSum = new ArrayList<>(), cc = new ArrayList<>(), partialcc = new ArrayList<>();
	private static long timeSum = 0, partialTimeSum = 0;
	private static double sumClusteringCoefficient = 0, partialSumClusteringCoefficient = 0, sumDiameter = 0, partialSumDiameter = 0;

	meanDegreeDistributions(degreeDistributionSum, instances);
	sumClusteringCoefficient = sumClusteringCoefficient / instances;
	timeSum = timeSum / instances;
	System.out.println("wrote");
	sumDiameter = sumDiameter / instances;
	FileCreation.getInstance().writeGraphProperties(type, degreeDistributionSum, 0, 0, timeSum, instances+1);

	if(ParametersReader.getInstance().checkIfPartialHasToBeCreated()){
		meanDegreeDistributions(partialDegreeDistributionSum, instances);
		//partialSumClusteringCoefficient = partialSumClusteringCoefficient / instances;
		//partialcc.add(partialSumClusteringCoefficient);
		partialTimeSum = partialTimeSum / instances;
		//partialSumDiameter = partialSumDiameter / instances;
		FileCreation.getInstance().writeGraphProperties("partial", partialDegreeDistributionSum, 0, 0, partialTimeSum, instances + 1);
	}

	timeSum += time;
	ArrayList<Double> degrees = graphToGenerate.getDegreeDistribution();
	degreeDistributionSum = sumDegreeDistributions(degrees, degreeDistributionSum);
	double clusteringCoefficient = graphToGenerate.getGraphClusteringCoefficient();
	sumClusteringCoefficient += clusteringCoefficient;
	FileCreation.getInstance().writeGraphProperties("outerplanar", degrees, clusteringCoefficient, time, index);
	double diameter = graphToGenerate.getGraphDiameter();
	sumDiameter += diameter;
	FileCreation.getInstance().writeGraphProperties("outerplanar", clusteringCoefficient, index);

	private static ArrayList<Double> sumDegreeDistributions(ArrayList<Double> degrees1, ArrayList<Double> degrees2){
		int largerSize = Math.max(degrees1.size(), degrees2.size());
		int smallerSize = Math.min(degrees1.size(), degrees2.size());
		ArrayList<Double> newDegreeDistribution = new ArrayList<>(), largerList = null;
		if(degrees1.size() > degrees2.size())
			largerList = degrees1;
		else if(degrees2.size() > degrees1.size())
			largerList = degrees2;
		
		for(int i=0; i<smallerSize; i++)
			newDegreeDistribution.add(degrees1.get(i) + degrees2.get(i));
		if(largerList != null)
			for(int i=smallerSize; i<largerSize; i++)
				newDegreeDistribution.add(largerList.get(i));

		return newDegreeDistribution;
	}

	private static void meanDegreeDistributions(ArrayList<Double> degrees, int number){
		for(int i=0; i<degrees.size(); i++)
			degrees.set(i, degrees.get(i) / number);
	}
 */
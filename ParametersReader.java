public class ParametersReader{
    private static ParametersReader instance = null;
    private String[] mainParameters;
	private int mainIndex = 0;

	public boolean isSuite = false, isKTree = false, isP3Tree = false, isOuter = false, isHalin = false, isSP = false;
	public boolean createPartial = false, deleteVertexes = false, deleteEdges = false, isConnected = false, isBiconnected = false;
	private boolean isRepeated = false;

	public int k = 3, vertexes, edges, partialVertexes = -1, partialEdges = -1, repeat = 1;
	private int finalVertexes, step;


    private ParametersReader(String[] args){
        this.mainParameters = args;
    }


	//-------------------------------//
    //          GET METHODS          //
    //-------------------------------//
	public boolean getIsSuite(){
		return isSuite;
	}

	public String getGraphType(){
		if(isKTree)
			return "ktree";
		if(isP3Tree)
			return "p3tree";
		if(isOuter)
			return "outerplanar";
		if(isHalin)
			return "halin";
		if(isSP)
			return "series-parallel";
		return null;
	}

	public int getKValue(){
		return k;
	}

	public int getVertexes(){
		return vertexes;
	}

	public int getVertexesToKeep(){
		return partialVertexes;
	}

	public int getEdgesToKeep(){
		if(createPartial)
			return partialEdges;
		return edges;
	}

	public int getFinalVertexes(){
		return finalVertexes;
	}

	public int getStep(){
		return step;
	}

	public int getRepeatedInstances(){
		return repeat;
	}

	public boolean checkIfPartialHasToBeCreated(){
		return createPartial;
	}

	public boolean checkIfPartialDeletesVertexes(){
		return deleteVertexes;
	}

	public boolean checkIfPartialDeletesEdges(){
		return deleteEdges;
	}

	public boolean isConnected(){
		return isConnected;
	}

	public boolean isBiconnected(){
		return isBiconnected;
	}

	public boolean getIsRepeated(){
		return isRepeated;
	}


	//-------------------------------------------//
    //          READ PARAMETERS METHODS          //
    //-------------------------------------------//
	public void readParameters(){
		this.checkGraphType();
		this.readGraphParameters();
		this.checkIfParametersAreCorrect();
	}

	private void checkGraphType(){
		switch(mainParameters[mainIndex++]){
			case "-ktree":
				isKTree = true;
				break;
			case "-p3tree":
				isP3Tree = true;
				break;
			case "-outer":
				isOuter = true;
				break;
			case "-halin":
				isHalin = true;
				break;
			case "-sp":
				isSP = true;
				break;
			case "-suite":
				isSuite = true;
				this.checkGraphType();
				break;
			default:
				closeApplication(getErrorMessage("unknown"));
				break;
		}
	}

	private void readGraphParameters(){
		while(mainIndex < mainParameters.length){
			switch(mainParameters[mainIndex]){
				case "-k":
					k = Integer.parseInt(mainParameters[++mainIndex]);
					break;
				case "-n":
					vertexes = Integer.parseInt(mainParameters[++mainIndex]);
					break;
				case "-e":
					deleteEdges = true;
					edges = Integer.parseInt(mainParameters[++mainIndex]);
					break;
				case "-pkt":
					createPartial = true;
					break;
				case "-pn":
					createPartial = true;
					deleteVertexes = true;
					partialVertexes = Integer.parseInt(mainParameters[++mainIndex]);
					break;
				case "-pe":
					createPartial = true;
					deleteEdges = true;
					partialEdges = Integer.parseInt(mainParameters[++mainIndex]);
					break;
				case "-c":
					isConnected = true;
					break;
				case "-bc":
					isBiconnected = true;
					break;
				case "-repeat": // era dopo dir all'inizio
					isRepeated = true;
					repeat = Integer.parseInt(mainParameters[++mainIndex]);
					break;
				case "-s":
					Graph.setGeneratorSeed(Long.parseLong(mainParameters[++mainIndex]));
					break;
				case "-dir":
					FileCreation.getInstance().setDirectoryName(mainParameters[++mainIndex]);
					break;
				default:
					closeApplication(getErrorMessage("unknown"));
					break;
			}
			mainIndex++;
		}
	}

	private void checkIfParametersAreCorrect(){
		if(isKTree)
			checkParameter("k");
		if(isP3Tree || isKTree){
			checkKTreesParameters();
		} else if(isOuter){
			checkParameter("n");
			if(deleteEdges)
				checkParameter("e");
		} else if(isHalin){
			checkParameter("n");
			if(deleteEdges)
				checkParameter("e");
		} else{
			checkParameter("n");
		}
		if(isRepeated)
			checkParameter("repeat");
	}

	private void checkKTreesParameters(){
		checkParameter("n");
		if(deleteVertexes)
			checkParameter("pn");
		if(deleteEdges)
			checkParameter("pe");
	}

	private void checkParameter(String parameterType){
		if(!getCondition(parameterType))
			closeApplication(getErrorMessage(parameterType));
	}

	private void closeApplication(String message){
		System.out.println(message);
		System.exit(-1);
	}


	//------------------------------------//
    //         CONDITIONS METHODS         //
    //------------------------------------//
	private boolean getCondition(String type){
		switch(type){
			case "k":
				return isPositiveNaturalNumber(k);
			case "n":
				if(isKTree || isP3Tree)
					return isGreaterThan(vertexes, k);
				if(isOuter)
					return isGreaterThan(vertexes, 2);
				if(isHalin)
					return isGreaterThan(vertexes, 3);
				if(isSP)
					return isGreaterThan(vertexes, 1);
			case "e":
				if(isOuter)
					return isNaturalNumber(edges) && isOuterplanarEdgeValueCorrect();
				if(isHalin)
					return isNaturalNumber(edges) && isHalinEdgeValueCorrect();
			case "pn":
				return isNaturalNumber(partialVertexes) && isLessThan(partialVertexes, vertexes);
			case "pe":
				return isNaturalNumber(partialEdges) && isKTreeEdgeValueCorrect();
			case "repeat":
				return isPositiveNaturalNumber(repeat);
			default:
				return false;
		}
	}

	private boolean isNaturalNumber(int value){
		return value >= 0;
	}

	private boolean isPositiveNaturalNumber(int value){
		return value > 0;
	}

	private boolean isGreaterThan(int value, int number){
		return value > number;
	}

	private boolean isLessThan(int value, int number){
		return value < number;
	}

	private boolean isKTreeEdgeValueCorrect(){
		if(deleteVertexes && isConnected)
			return partialEdges >= partialVertexes - 1 && KTree.getMaxNumberOfEdges(k, partialVertexes) >= partialEdges;
		else if(deleteVertexes)
			return KTree.getMaxNumberOfEdges(k, partialVertexes) >= partialEdges;
		else if(isConnected)
			return partialEdges >= vertexes - 1 && KTree.getMaxNumberOfEdges(k, vertexes) >= partialEdges;
		return KTree.getMaxNumberOfEdges(k, vertexes) >= partialEdges;
	}

	private boolean isOuterplanarEdgeValueCorrect(){
		if(isBiconnected)
			return edges >= vertexes && Outerplanar.getMaxNumberOfEdges(vertexes) >= edges;
		return edges >= vertexes - 1 && Outerplanar.getMaxNumberOfEdges(vertexes) >= edges;
	}

	private boolean isHalinEdgeValueCorrect(){
		if(vertexes % 2 == 0)
			return edges >= 3*vertexes/2 && edges <= 2*(vertexes - 1);
		else
			return edges >= (3*vertexes + 1)/2 && edges <= 2*(vertexes - 1);
	}


	//---------------------------------------//
    //         ERROR MESSAGES METHOD         //
    //---------------------------------------//
	private String getErrorMessage(String parameter){
		switch(parameter){
			case "k":
				parameter = theValueOfParameterMustBe(parameter) + ".";
				break;
			case "n":
				if(isP3Tree || isHalin)
					parameter = theValueOfParameterMustBe(parameter) + greaterOrEqualThan("4") + ".";
				else if(isKTree)
					parameter = theValueOfParameterMustBe(parameter) + greaterOrEqualThan("the value of k")+ ".";
				else if(isOuter)
					parameter = theValueOfParameterMustBe(parameter) + greaterOrEqualThan("3") + ".";
				else if(isSP)
					parameter = theValueOfParameterMustBe(parameter) + greaterOrEqualThan("2") + ".";
				break;
			case "e":
				if(isOuter)
					parameter = theValueOfParameterMustBe(parameter) + greaterOrEqualThan(Integer.toString(vertexes - 1)) + " and" +
								lessOrEqualThan(Integer.toString(Outerplanar.getMaxNumberOfEdges(vertexes))) + ".";
				else if(isHalin)
					parameter = theValueOfParameterMustBe(parameter) +
								greaterOrEqualThan(Integer.toString(Halin.getMaxNumberOfEdges(vertexes))) + " and" + 
								lessOrEqualThan(Integer.toString(2*(vertexes - 1))) + ".";
				break;
			case "pn":
				parameter = theValueOfParameterMustBe(parameter) + lessOrEqualThan("the value of n-1") + ".";
				break;
			case "pe":
				if(isConnected && deleteVertexes)
					parameter = theValueOfParameterMustBe(parameter) + greaterOrEqualThan("pn-1") +
								lessOrEqualThan(Integer.toString(KTree.getMaxNumberOfEdges(k, partialVertexes) - 1)) + ".";
				else if(deleteVertexes)
					parameter = theValueOfParameterMustBe(parameter) +
								lessOrEqualThan(Integer.toString(KTree.getMaxNumberOfEdges(k, partialVertexes) - 1)) + ".";
				else if(isConnected)
					parameter = theValueOfParameterMustBe(parameter) + greaterOrEqualThan("n-1") +
								lessOrEqualThan(Integer.toString(KTree.getMaxNumberOfEdges(k, vertexes) - 1)) + ".";
				else
					parameter = theValueOfParameterMustBe(parameter) +
								lessOrEqualThan(Integer.toString(KTree.getMaxNumberOfEdges(k, vertexes) - 1)) + ".";
				break;
			case "repeat":
				parameter = theValueOfParameterMustBe(parameter) + ".";
				break;
			default:
				parameter = "Unknown command.";
				break;
		}
		return parameter;
	}

	private String theValueOfParameterMustBe(String parameter){
		return "The value of " + parameter + " must be a natural number";
	}

	private String greaterOrEqualThan(String value){
		return " greather or equal than " + value;
	}

	private String lessOrEqualThan(String value){
		return " less or equal than " + value;
	}


    //----------------------------------//
    //       GET INSTANCE METHODS       //
    //----------------------------------//
    public static ParametersReader createInstance(String[] args){
        instance = new ParametersReader(args);
        return instance;
    }

	public static ParametersReader getInstance(){
		return instance;
	}
}
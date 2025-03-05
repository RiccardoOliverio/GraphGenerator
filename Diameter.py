import os
import networkx as nx;


# CALCULATE DIAMETER
def getMeanDiameter(graphType, strGraphVertexes):
    i = 1
    sumDiameter = 0
    while i < 8 :
        s = str(i)
        stringpath = "tested_graphs/" + graphType +  " connected/" + graphType + strGraphVertexes + "000 " + s + ".graphml"
        path = os.path.normpath(stringpath)
        G = nx.read_graphml(path)
        sumDiameter += nx.diameter(G)
        i += 1
    return (sumDiameter/10)

def writeMeanDiameter(graphType, strGraphVertexes):
    open(graphType + strGraphVertexes + "000.txt", "a")
    file = open(graphType + strGraphVertexes + "000.txt", "a")
    file.write("Diametro " + strGraphVertexes + ": "+str(getMeanDiameter(graphType, strGraphVertexes)) +"\n")
    file.close()

def writeMeanDiameterForAllVertexes(graphType, start, end):
    graphVertexes = start
    while graphVertexes < end :
        strGraphVertexes = str(graphVertexes)
        getMeanDiameter(graphType, strGraphVertexes)
        writeMeanDiameter(graphType, strGraphVertexes)
        graphVertexes += 1


# CALCULATE CLUSTERING COEFFICIENT
def getMeanCC(graphType, strGraphVertexes):
    i = 1
    sumCC = 0
    while i < 8 :
        s = str(i)
        stringpath = "tested_graphs/" + graphType +  " connected/" + graphType + strGraphVertexes + "000 " + s + ".graphml"
        path = os.path.normpath(stringpath)
        G = nx.read_graphml(path)
        sumCC += nx.average_clustering(G)
        i += 1
    return (sumCC/10)

def writeMeanCC(graphType, strGraphVertexes):
    open(graphType + strGraphVertexes + "000.txt", "a")
    file = open(graphType + strGraphVertexes + "000.txt", "a")
    file.write("CC " + strGraphVertexes + ": "+str(getMeanCC(graphType, strGraphVertexes)) +"\n")
    file.close()

def writeMeanCCForAllVertexes(graphType, start, end):
    graphVertexes = start
    while graphVertexes < end :
        strGraphVertexes = str(graphVertexes)
        getMeanCC(graphType, strGraphVertexes)
        writeMeanCC(graphType, strGraphVertexes)
        graphVertexes += 1


# CALCULATE DIAMETER AND CLUSTERING COEFFICIENT
def getMeanDiameterAndCC(graphType, strGraphVertexes):
    i = 1
    sumDiameter = 0
    sumCC = 0
    while i < 8 :
        s = str(i)
        stringpath = "tested_graphs/" + graphType +  " connected/" + graphType + strGraphVertexes + "000 " + s + ".graphml"
        path = os.path.normpath(stringpath)
        G = nx.read_graphml(path)
        sumDiameter += nx.diameter(G)
        sumCC += nx.average_clustering(G)
        i += 1
    return ((sumCC/10),(sumDiameter/10))

def writeMeanDiameterAndCC(graphType, strGraphVertexes):
    open(graphType + strGraphVertexes + "000.txt", "a")
    file = open(graphType + strGraphVertexes + "000.txt", "a")
    file.write("Diametro " + strGraphVertexes + ": " + str(getMeanDiameter(graphType, strGraphVertexes)) +"\n")
    file.write("CC " + strGraphVertexes + ": " + str(getMeanCC(graphType, strGraphVertexes)) +"\n")
    file.close()

def writeMeanDiameterAndCCForAllVertexes(graphType, start, end):
    graphVertexes = start
    while graphVertexes < end :
        strGraphVertexes = str(graphVertexes)
        getMeanDiameterAndCC(graphType, strGraphVertexes)
        writeMeanDiameterAndCC(graphType, strGraphVertexes)
        graphVertexes += 1


#writeMeanDiameterAndCCForAllVertexes("partial-5tree", 1, 5)
writeMeanDiameterAndCCForAllVertexes("partial-3tree", 7, 8)

#writeMeanDiameterForAllVertexes("3tree", 1, 5)
#writeMeanDiameterAndCCForAllVertexes("series-parallel", 1, 5)
#writeMeanDiameterAndCCForAllVertexes("outerplanar", 1, 5)
#writeMeanDiameterForAllVertexes("5tree", 1, 5)
#writeMeanDiameterForAllVertexes("10tree", 1, 5)
#writeMeanDiameterForAllVertexes("halin", 1, 5)
#writeMeanDiameterForAllVertexes("planar3tree", 1, 5)

#writeMeanDiameterAndCCForAllVertexes("series-parallel", 5, 8)
#writeMeanDiameterAndCCForAllVertexes("outerplanar", 5, 8)
#writeMeanDiameterForAllVertexes("3tree", 5, 8)
#writeMeanDiameterForAllVertexes("5tree", 5, 8)
#writeMeanDiameterForAllVertexes("10tree", 5, 8)
#writeMeanDiameterForAllVertexes("halin", 5, 8)
#writeMeanDiameterForAllVertexes("planar3tree", 5, 8)
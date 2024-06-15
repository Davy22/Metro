import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import net.datastructures.Edge;
import net.datastructures.Vertex;

public class ParisMetro {
    private Graph subway;

    ParisMetro(String fileName){
        subway = readMetro(fileName);
    };

    //read file reader 
    public Graph readMetro(String fileName){
        // Assuming the metro is an directed graph
        subway = new Graph(true); 
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            boolean isStationData = true;
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                if (line.equals("$")) {
                    isStationData = false;
                    continue;
                }
        
                if (isStationData) {
                    String[] stationInfo = line.split("\\s", 2);
                    Integer stationValue = Integer.parseInt(stationInfo[0]);
                    // Create vertices with station values and insert into the graph
                    subway.insertVertex(stationValue);
                    // Optionally store station name somewhere, if needed
                } else {
                    String[] edgeInfo = line.split("\\s");
                    Integer uValue = Integer.parseInt(edgeInfo[0]);
                    Integer vValue = Integer.parseInt(edgeInfo[1]);
                    Integer weight = Integer.parseInt(edgeInfo[2]);
                    if(uValue != null &&  vValue!= null) {
                        subway.insertEdge(subway.getVertex(uValue), subway.getVertex(vValue), weight);
                    }else{
                        System.out.println("Vertices not found for edge: " + line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return subway;
    }

    public void printStations(){
        subway.printVertices();
    }
    public void printLines(){
        subway.printEdges();
    }

    public static void main(String[] args){
        ParisMetro sub = new ParisMetro("metro.txt");
        sub.printStations();
        sub.printLines();
        
    }
}

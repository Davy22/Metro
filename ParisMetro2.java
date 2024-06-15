import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

// Existing class and methods...

public class ParisMetro2 {
    Graph<String, Integer> subwayGraph; // Graph representation of the subway network

    // ... (Constructor and other existing methods)

    // Method to identify stations on the same line as the given station number (N1)
    public void identifyStationsOnSameLine(int stationNumber) {
        Vertex<String> givenStation = null;

        // Find the vertex corresponding to the given station number
        for (Vertex<String> vertex : subwayGraph.vertices()) {
            if (vertex.getElement().equals(Integer.toString(stationNumber))) {
                givenStation = vertex;
                break;
            }
        }

        if (givenStation != null) {
            // Traverse the graph and collect stations on the same line
            List<String> stationsOnSameLine = new ArrayList<>();
            for (Edge<Integer> edge : subwayGraph.outgoingEdges(givenStation)) {
                Vertex<String> destination = subwayGraph.opposite(givenStation, edge);
                stationsOnSameLine.add(destination.getElement());
            }

            // Print the stations on the same line as the given station number
            System.out.println("Line: " + String.join(" ", stationsOnSameLine));
        } else {
            System.out.println("Station not found in the subway network.");
        }
    }

    // Main method and other existing code...
}

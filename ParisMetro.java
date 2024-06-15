import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.*;
import net.datastructures.Graph;
import net.datastructures.GraphAlgorithms;
import net.datastructures.Vertex;
import net.datastructures.Edge;
import net.datastructures.AdjacencyMapGraph;
import net.datastructures.Map;

public class ParisMetro{
    Graph<String, Integer> subway;

    public ParisMetro(String fileName) throws Exception, IOException {
        subway = new AdjacencyMapGraph<>(true);
        readMetro(fileName);
    };

    public void readMetro(String fileName) throws IOException{
		BufferedReader file = new BufferedReader(new FileReader("P2SampleTestCases.txt"));
		Hashtable<String, Vertex<String>> vertices = new Hashtable<>(); // Store vertices

        String line;
        while ((line = file.readLine()) != null) { // Read each line in the file
            StringTokenizer st = new StringTokenizer(line); // Tokenize the line
            if (st.countTokens() != 3) // Ensure there are three tokens (source, dest, weight)
                throw new IOException("Incorrect input file at line " + line);
            String source = st.nextToken(); // Extract source vertex
            String dest = st.nextToken(); // Extract destination vertex
            int weight = Integer.parseInt(st.nextToken()); // Extract edge weight

            // Check if source vertex exists, if not add to the graph
            Vertex<String> sv = vertices.get(source);
            if (sv == null) {
                sv = subway.insertVertex(source);
                vertices.put(source, sv);
            }

            // Check if destination vertex exists, if not add to the graph
            Vertex<String> dv = vertices.get(dest);
            if (dv == null) {
                dv = subway.insertVertex(dest);
                vertices.put(dest, dv);
            }
            // Add an edge between source and destination with the given weight
            subway.insertEdge(sv, dv, weight);
        }
    }

    public static void main(String[] args){
       
    }
}
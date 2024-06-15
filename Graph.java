import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.*;

public class Graph {
    private ArrayList<Vertex> vertices = new ArrayList<>();
    private ArrayList<Edge> edges = new ArrayList<>();
    private boolean directed;

    static final int WALKING_TIME = 90;

    public Graph(boolean directed){
        this.directed = directed;
    }
    public ArrayList<Edge> getEdges(){return edges;}
    public ArrayList<Vertex> getVertices(){return vertices;}

    public Vertex insertVertex(Integer value){
        Vertex vertice = new Vertex(value, directed);
        vertices.add(vertice);
        vertice.setIndexofstation(vertices.size()-1);
        return vertice;
    }

    public List<Integer> getStationsOnSameLine(int stationValue) {
        Vertex startVertex = getVertex(stationValue);
        if (startVertex == null) {
            throw new IllegalArgumentException("Invalid station number.");
        }

        List<Integer> stationsOnSameLine = new ArrayList<>();
        Map<Vertex, Edge> outgoingEdges = startVertex.getOutgoing();

        // Perform BFS starting from the prompted station
        Queue<Vertex> queue = new LinkedList<>();
        Set<Vertex> visited = new HashSet<>();

        queue.offer(startVertex);
        visited.add(startVertex);

        while (!queue.isEmpty()) {
            Vertex currentVertex = queue.poll();
            stationsOnSameLine.add(currentVertex.getStationValue());

            Map<Vertex, Edge> neighbors = currentVertex.getOutgoing();
            for (Vertex neighbor : neighbors.keySet()) {
                Edge edge = neighbors.get(neighbor);
                if (edge.getWeight() != -1 && !visited.contains(neighbor)) {
                    queue.offer(neighbor);
                    visited.add(neighbor);
                }
            }
        }

        return stationsOnSameLine;
    }


    public List<Integer> findShortestPath(Integer sourceStationValue, Integer destinationStationValue) {
        Vertex sourceVertex = getVertex(sourceStationValue);
        Vertex destinationVertex = getVertex(destinationStationValue);

        if (sourceVertex == null || destinationVertex == null) {
            throw new IllegalArgumentException("Invalid station number.");
        }

        Map<Vertex, Integer> distances = new HashMap<>();
        Map<Vertex, Vertex> previousVertices = new HashMap<>();
        PriorityQueue<Vertex> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        // Initialize distances with infinity for all vertices except the source vertex
        for (Vertex vertex : vertices) {
            if (vertex.equals(sourceVertex)) {
                distances.put(vertex, 0);
            } else {
                distances.put(vertex, Integer.MAX_VALUE);
            }
            queue.add(vertex);
        }

        while (!queue.isEmpty()) {
            Vertex currentVertex = queue.poll();

            if (currentVertex.equals(destinationVertex)) {
                // Reached the destination vertex, construct and return the shortest path
                List<Integer> shortestPath = constructShortestPath(previousVertices, destinationVertex);
                int totalWeight = distances.get(destinationVertex);
                System.out.println("Time in Seconds: " + totalWeight);
                return shortestPath;
            }

            for (Map.Entry<Vertex, Edge> entry : currentVertex.getOutgoing().entrySet()) {
                Vertex neighbor = entry.getKey();
                Edge edge = entry.getValue();

                int edgeWeight = (edge.getWeight() == -1) ? 90 : edge.getWeight();
                int newDistance = distances.get(currentVertex) + edgeWeight;
                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    previousVertices.put(neighbor, currentVertex);

                    // Update the priority queue with the new distance
                    queue.remove(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        // No path found
        return new ArrayList<>();
    }

    private List<Integer> constructShortestPath(Map<Vertex, Vertex> previousVertices, Vertex destinationVertex) {
        List<Integer> shortestPath = new LinkedList<>();
        Vertex currentVertex = destinationVertex;

        while (currentVertex != null) {
            shortestPath.add(0, currentVertex.getStationValue());
            currentVertex = previousVertices.get(currentVertex);
        }

        return shortestPath;
    }

    public List<Integer> findShortestPathWithLineDown(Integer sourceStationValue, Integer destinationStationValue, Integer downStationValue) {
        Vertex sourceVertex = getVertex(sourceStationValue);
        Vertex destinationVertex = getVertex(destinationStationValue);
        Vertex downVertex = getVertex(downStationValue);

        if (sourceVertex == null || destinationVertex == null || downVertex == null) {
            throw new IllegalArgumentException("Invalid station number.");
        }

        // Get all stations on the same line as the down station
        List<Integer> stationsOnSameLine = getStationsOnSameLine(downStationValue);

        // Remove the stations on the same line from the graph
        for (Vertex vertex : vertices) {
            if (stationsOnSameLine.contains(vertex.getStationValue())) {
                vertex.getIncoming().clear();
                vertex.getOutgoing().clear();
            }
        }

        // Find the shortest path using Dijkstra's algorithm
        List<Integer> shortestPath = findShortestPath(sourceStationValue, destinationStationValue);

        // Add the stations on the same line back to the graph
        for (Edge edge : edges) {
            Vertex u = edge.getEndpoints()[0];
            Vertex v = edge.getEndpoints()[1];
            if (stationsOnSameLine.contains(u.getStationValue()) && stationsOnSameLine.contains(v.getStationValue())) {
                u.getOutgoing().put(v, edge);
                v.getIncoming().put(u, edge);
            }
        }

        return shortestPath;
    }




    public Edge getEdge(Vertex u, Vertex v){
        return u.getOutgoing().get(v);
    }
    public Vertex getVertex(Integer stationValue) {
        for (Vertex vertex : vertices) {
            if (vertex.getStationValue().equals(stationValue)) {
                return vertex;
            }
        }
        return null; // Vertex not found
    }
    public Edge insertEdge(Vertex u, Vertex v, Integer weight) {
        Edge existingEdge = getEdge(u, v);
        if (existingEdge != null) {
            throw new IllegalArgumentException("Edge from u to v already exists");
        } else {
            Edge edge = new Edge(u, v, weight);
            edges.add(edge);
            edge.setPosition(edges.size() - 1);
            u.getOutgoing().put(v, edge);
            v.getIncoming().put(u, edge);
            return edge;
        }
    }
    
    public Vertex opposites(Vertex v, Edge e){
        Vertex[] endpoints = e.getEndpoints();
        if (endpoints[0] == v)
            return endpoints[1];
        else if (endpoints[1] == v)
            return endpoints[0];
        else
            throw new IllegalArgumentException("v is not incident to this edge");
    }

    private class Vertex {
        private Integer stationValue;
        private Integer indexofstation;
        private Map<Vertex, Edge> outgoing;
        private Map<Vertex, Edge> incoming;

        public Vertex(Integer stationValue, boolean directed) {
            this.stationValue = stationValue;
            outgoing = new HashMap<>();
            if (directed) {
                incoming = new HashMap<>();
            } else {
                incoming = outgoing;
            }
        }
        public Integer getStationValue() {
            return stationValue;
        }
        public Integer getIndexofstation() {
            return indexofstation;
        }
        public Map<Vertex, Edge> getOutgoing() {
            return outgoing;
        }
        public Map<Vertex, Edge> getIncoming() {
            return incoming;
        }
        public void setIndexofstation(Integer indexofstation) {
            this.indexofstation = indexofstation;
        }
        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Vertex{");
            stringBuilder.append("stationValue=").append(stationValue);
            stringBuilder.append(", indexofstation=").append(indexofstation);
            stringBuilder.append(", outgoing=").append(outgoing);
            stringBuilder.append(", incoming=").append(incoming);
            stringBuilder.append('}');
            return stringBuilder.toString();
        }
    }

    public class Edge {
        private Integer weight;
        private Integer indexofEdge;
        private Vertex[] endpoints;

        public Edge(Vertex u, Vertex v, Integer weight) {
            this.weight = weight;
            endpoints = new Vertex[]{u, v};
        }
        public Integer getWeight() {
            return weight;
        }
        public Vertex[] getEndpoints() {
            return endpoints;
        }
        public void setPosition(Integer indexofEdge) {
            this.indexofEdge = indexofEdge;
        }
        public Integer getPosition() {
            return indexofEdge;
        }
        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Edge{");
            stringBuilder.append("weight=").append(weight);
            stringBuilder.append(", indexofEdge=").append(indexofEdge);
            stringBuilder.append(", endpoints=[");
            for (Vertex v : endpoints) {
                stringBuilder.append(v.getStationValue()).append(", ");
            }
            stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length()); // Remove the extra comma and space
            stringBuilder.append("]}");
            return stringBuilder.toString();
        }
    }

}

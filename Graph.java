import java.lang.reflect.Array;
import java.util.*;

public class Graph {
    private ArrayList<Vertex> vertices = new ArrayList<>();
    private ArrayList<Edge> edges = new ArrayList<>();
    private boolean directed;

    static final int WALKING_TIME = 90;
    //constructor
    public Graph(boolean directed){
        this.directed = directed;
    }
    public ArrayList<Edge> getEdges(){return edges;}
    public ArrayList<Vertex> getVertices(){return vertices;}
    //vertex method
    public Vertex insertVertex(Integer value){
        Vertex vertice = new Vertex(value, directed);
        vertices.add(vertice);
        vertice.setIndexofstation(vertices.size()-1);
        return vertice;
    }
    //edge method
    public Edge getEdge(Vertex u, Vertex v){
        return u.getOutgoing().get(v);
    }
    public Vertex getVertex(Integer stationValue){
        return vertices.get(stationValue);
    }
    public Edge insertEdge(Vertex u, Vertex v, Integer weight){
        if(getEdge(u,v) == null){
            Edge edge = new Edge(u, v, weight);
            edges.add(edge);
            edge.setPosition(edges.size()-1);
            u.getOutgoing().put(v, edge);
            v.getIncoming().put(u, edge);
            return edge;
        } else
        throw new IllegalArgumentException("Edge from u to v exists");
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
    //printing vertices 
    public void printVertices(){
        for(Vertex v: vertices){
            System.out.println(v);
        }
    }
    //print edges
    public void printEdges(){
        for(Edge e : edges){
            System.out.println(e);
        }
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

    public List<Integer> findShortestPath(Integer startStation, Integer destinationStation) {
        Vertex sourceVertex = getVertex(startStation);
        Vertex destinationVertex = getVertex(destinationStation);

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
                return constructShortestPath(previousVertices, destinationVertex);
            }

            for (Map.Entry<Vertex, Edge> entry: currentVertex.getOutgoing().entrySet()) {
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
    //
    private List<Integer> constructShortestPath(Map<Vertex, Vertex> prevVertices, Vertex destVertex) {
        List<Integer> shortestPath = new LinkedList<>();
        Vertex currentVertex = destVertex;

        while(currentVertex != null){
            shortestPath.add(0, currentVertex.getStationValue());
            currentVertex = prevVertices.get(currentVertex);
        }
        return shortestPath;
    }

    public List<Integer> findShortestPathWithoutFunctionalLine(Integer sourceStationValue, Integer destinationStationValue, Integer nonFunctionalStationValue){
        List<Integer> stationsOnSameLine = getStationsOnSameLine(nonFunctionalStationValue);
        // If the non-functional station is not on any line, or if the source or destination is the non-functional station,
        // return an empty path
        if(stationsOnSameLine.isEmpty()||sourceStationValue.equals(nonFunctionalStationValue)||destinationStationValue.equals(nonFunctionalStationValue)){
            return new ArrayList<>();
        }
        List<Integer> shortestPath = new ArrayList<>();
        // Find the shortest path between source and destination excluding the non-functional station and stations on the same line
        List<Integer> pathToFirstStation = findShortestPath(sourceStationValue, stationsOnSameLine.get(0));
        shortestPath.addAll(pathToFirstStation);

        for(int i = 0; i < stationsOnSameLine.size() - 1; i++) {
            Integer startStation = stationsOnSameLine.get(i);
            Integer endStation = stationsOnSameLine.get(i + 1);
            // Skip the non-functional station and stations on the same line
            if(startStation.equals(nonFunctionalStationValue)||endStation.equals(nonFunctionalStationValue)) {
                continue;
            }
            List<Integer> path = findShortestPath(startStation, endStation);
            shortestPath.addAll(path.subList(1, path.size() - 1));
        }
        List<Integer> pathFromLastStation = findShortestPath(stationsOnSameLine.get(stationsOnSameLine.size() - 1), destinationStationValue);
        shortestPath.addAll(pathFromLastStation.subList(1, pathFromLastStation.size()));
        return shortestPath;
    }

    public List<Integer> findShortestPathWithLineDown(Integer sourceStationValue, Integer destinationStationValue, Integer downStationValue) {
        Vertex sourceVertex = getVertex(sourceStationValue);
        Vertex destinationVertex = getVertex(destinationStationValue);
        Vertex downVertex = getVertex(downStationValue);

        if (sourceVertex == null || destinationVertex == null || downVertex == null) {
            throw new IllegalArgumentException("Invalid station number.");
        }

        // Remove the down station from the graph
        for (Edge edge : downVertex.getIncoming().values()) {
            Vertex u = opposites(downVertex, edge);
            u.getOutgoing().remove(downVertex);
        }
        for (Edge edge : downVertex.getOutgoing().values()) {
            Vertex v = opposites(downVertex, edge);
            v.getIncoming().remove(downVertex);
        }

        // Find the shortest path using Dijkstra's algorithm
        List<Integer> shortestPath = findShortestPath(sourceStationValue, destinationStationValue);

        // Add the down station back to the graph
        for (Edge edge : downVertex.getIncoming().values()) {
            Vertex u = opposites(downVertex, edge);
            u.getOutgoing().put(downVertex, edge);
        }
        for (Edge edge : downVertex.getOutgoing().values()) {
            Vertex v = opposites(downVertex, edge);
            v.getIncoming().put(downVertex, edge);
        }

        return shortestPath;
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
        public Map<Vertex, Edge> getIncoming(){
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
            stringBuilder.append(", outgoing={");
            for (Map.Entry<Vertex, Edge> entry : outgoing.entrySet()) {
                stringBuilder.append("(")
                            .append(entry.getKey().getStationValue())
                            .append(", ")
                            .append(entry.getValue().getWeight())
                            .append("), ");
            }
            if (!outgoing.isEmpty()) {
                stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length()); // Remove the extra comma and space
            }
            stringBuilder.append("}, incoming={");
            for (Map.Entry<Vertex, Edge> entry : incoming.entrySet()) {
                stringBuilder.append("(")
                            .append(entry.getKey().getStationValue())
                            .append(", ")
                            .append(entry.getValue().getWeight())
                            .append("), ");
            }
            if (!incoming.isEmpty()) {
                stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length()); // Remove the extra comma and space
            }
            stringBuilder.append("}}");
            return stringBuilder.toString();
        }

    }

    private class Edge {
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
            stringBuilder.append(", endpoints=(");
            stringBuilder.append(endpoints[0].getStationValue()).append(", ");
            stringBuilder.append(endpoints[1].getStationValue()).append(")}");
            return stringBuilder.toString();
        }

    }

}

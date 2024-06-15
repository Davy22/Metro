import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class ParisMetro {
    private Graph subway;

    ParisMetro(String fileName) {
        subway = readMetro(fileName);
    }

    public void printStationsOnSameLine(int stationValue) {
        List<Integer> stationsOnSameLine = subway.getStationsOnSameLine(stationValue);

        System.out.println("Stations on the same line as station " + stationValue + ":");
        for (int station : stationsOnSameLine) {
            System.out.print(station + " ");
        }
        System.out.println();
    }

    public List<Integer> findShortestPath(int sourceStationValue, int destinationStationValue) {
        return subway.findShortestPath(sourceStationValue, destinationStationValue);
    }

    public List<Integer> findShortestPathWithLineDown(
            int sourceStationValue, int destinationStationValue, int nonFunctionalStationValue) {
        return subway.findShortestPathWithLineDown(sourceStationValue, destinationStationValue,
                nonFunctionalStationValue);
    }

    public Graph readMetro(String fileName) {
        subway = new Graph(true); // Assuming the metro is an undirected graph
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
                    String stationName = stationInfo[1].trim();

                    // Create vertices with station values and insert into the graph
                    subway.insertVertex(stationValue);
                    // Optionally store station name somewhere, if needed
                } else {
                    String[] edgeInfo = line.split("\\s");
                    Integer uValue = Integer.parseInt(edgeInfo[0]);
                    Integer vValue = Integer.parseInt(edgeInfo[1]);
                    Integer weight = Integer.parseInt(edgeInfo[2]);

                    // Insert an edge between vertices if they exist
                    if (uValue != null && vValue != null) {
                        subway.insertEdge(subway.getVertex(uValue), subway.getVertex(vValue), weight);
                    } else {
                        System.out.println("Vertices not found for edge: " + line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return subway;
    }

    public static void main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            System.out.println("Invalid number of arguments. Usage: ParisMetro N1 N2 N3");
            return;
        }

        int station1 = Integer.parseInt(args[0]);

        ParisMetro metro = new ParisMetro("metro.txt");

        if (args.length == 1) {
            System.out.println("Input: N0 = " + station1);
            metro.printStationsOnSameLine(station1);
        } else if (args.length == 2) {
            int station2 = Integer.parseInt(args[1]);
            System.out.println("Input: N0 = " + station1 + ", N1 = " + station2);
            List<Integer> shortestPath = metro.findShortestPath(station1, station2);
            if (shortestPath.isEmpty()) {
                System.out.println("No path found between stations " + station1 + " and " + station2);
            } else {
                System.out.println("Shortest path between stations " + station1 + " and " + station2 + ":");
                for (int station : shortestPath) {
                    System.out.print(station + " ");
                }
                System.out.println();
            }
        } else if (args.length == 3) {
            int station2 = Integer.parseInt(args[1]);
            int station3 = Integer.parseInt(args[2]);
            System.out.println("Input: N0 = " + station1 + ", N2 = " + station2 + ", N3 = " + station3);
            List<Integer> shortestPathWithoutFunctionalLine = metro.findShortestPathWithLineDown(
                    station1, station2, station3);
            if (shortestPathWithoutFunctionalLine.isEmpty()) {
                System.out.println("No path found between stations " + station1 + " and " + station2 +
                        " without passing through station " + station3);
            } else {
                System.out.println("Shortest path between stations " + station1 + " and " + station2 +
                        " given the line with station " + station3 + " is down:");
                for (int station : shortestPathWithoutFunctionalLine) {
                    System.out.print(station + " ");
                }
                System.out.println();
            }
        }
    }
}
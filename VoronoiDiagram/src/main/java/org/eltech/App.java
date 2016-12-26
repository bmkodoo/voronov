package org.eltech;

import one.util.streamex.StreamEx;
import org.eltech.structures.Edge;
import org.eltech.structures.Point2D;
import org.eltech.algorithm.VoronoiDiagram;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class App {

    public static List<Point2D> parsePoints(String inputPath) throws IOException {
        try (FileInputStream input = new FileInputStream(inputPath)) {
            BufferedReader inputStream =
                    new BufferedReader(
                            new InputStreamReader(
                                    input,
                                    "UTF-8"));
            return StreamEx.of(inputStream.lines())
                    .map(l -> l.split(","))
                    .map(split -> new Point2D(Double.valueOf(split[0]), Double.valueOf(split[1])))
                    .collect(Collectors.toList());
        }
    }

    public static void saveIntersectionsToDisk(String outputPath, List<Edge> edges) throws IOException {
        try (FileOutputStream output = new FileOutputStream(outputPath)) {
            BufferedWriter inputStream = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"));

            String outputText = edges.stream()
                    .map(edge -> edge.getP1().getX() +
                            "," + edge.getP1().getY() +
                            "," + edge.getP2().getX() +
                            "," + edge.getP2().getY())
                    .collect(Collectors.joining("\n"));

            inputStream.write(outputText);
            inputStream.flush();
        }
    }

    public static void main(String[] args) throws IOException {
        List<Point2D> points = parsePoints("/home/kodoo/points.txt");
        System.out.println(VoronoiDiagram.Companion.createDiagram(points));
//        saveIntersectionsToDisk(args[1], edges);

    }
}

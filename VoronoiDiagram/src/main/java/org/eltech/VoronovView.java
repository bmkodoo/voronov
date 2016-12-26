package org.eltech;


import org.eltech.structures.Edge;
import org.eltech.structures.Point2D;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VoronovView extends JPanel {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    private List<Point2D> points;
    private List<Edge> edges;

    public VoronovView(List<Point2D> points, List<Edge> edges) {
        this.points = points;
        this.edges = edges;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(WIDTH, HEIGHT);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.black);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(Color.red);
        points.forEach((point) -> g.fillOval((int) point.getX() - 2, (int) point.getY() - 2, 4, 4));

        g.setColor(Color.orange);
        edges.forEach(edge -> {
            Point2D a = edge.getP1();
            Point2D b = edge.getP2();
            if (Math.abs(a.getY() - b.getY()) > 300)
                return;
            g.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY());
        });
    }
}

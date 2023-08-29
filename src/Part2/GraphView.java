package Part2;

import javafx.collections.ListChangeListener;
import javafx.collections.SetChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class GraphView extends Pane {
    private final Canvas canvas;
    private final GraphicsContext gc;

    /**
     * No argument constructor. Initializes GraphModel listeners and InteractionModel listeners.
     */
    public GraphView() {
        canvas = new Canvas();
        gc = canvas.getGraphicsContext2D();
        getChildren().add(canvas);
        initListeners();
    }

    @Override
    public void layoutChildren() {
        canvas.setWidth(this.getWidth());
        canvas.setHeight(this.getHeight());
        draw(gc);
    }

    public void draw(GraphicsContext gc) {
        Point2D p1;
        Point2D p2;
        double angle1;
        double angle2;
        double px;

        //clear the screen and initialize fonts and sizing.
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        px = pointToPixel(10);
        gc.setFont(new Font("Arial", px));
        gc.setLineWidth(1);

        //Draw existing vertices:
        for (Vertex v : Main.model.getVertexListProperty()) {
            gc.setFill(v.getColor());
            gc.fillOval(v.getCords().getX(), v.getCords().getY(), v.getRadius() * 2, v.getRadius() * 2);
            gc.strokeText(Integer.toString(v.getVertexNum()), v.getCenter().getX() - px/2, v.getCenter().getY() + px/2);
        }

        //Shift click is active:
        if (Main.interact.isLineSet()) {
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(3);

            //draw outline around selected vertex:
            if (Main.interact.getSelectedVertex() != null) {
                gc.strokeOval(Main.interact.getSelectedVertex().getCords().getX(),
                              Main.interact.getSelectedVertex().getCords().getY(),
                             Main.interact.getSelectedVertex().getRadius() * 2,
                             Main.interact.getSelectedVertex().getRadius() * 2);
            }
            //find point on circumference of vertex:
            angle1 = getAngle(Main.interact.getLineStart(), Main.interact.getLineEnd());
            p1 = getCircPoint(Main.interact.getDefaultRadius(), angle1, Main.interact.getSelectedVertex().getCenter());

            //Do not draw line unless the end point falls outside the selected vertex:
            if (!(Main.interact.getSelectedVertex().containsPoint(Main.interact.getLineEnd()))) {
                gc.strokeLine(p1.getX(), p1.getY(),
                        Main.interact.getLineEnd().getX(), Main.interact.getLineEnd().getY());
            }
        }

        //Draw existing edges:
        for (Edge e : Main.model.getEdgeSetProperty()) {
            gc.setLineWidth(1);

            //get points on circumference:
            angle1 = getAngle(e.getVertex1().getCenter(), e.getVertex2().getCenter());
            angle2 = getAngle(e.getVertex2().getCenter(), e.getVertex1().getCenter());
            p1 = getCircPoint(e.getVertex1().getRadius(), angle1, e.getVertex1().getCenter());
            p2 = getCircPoint(e.getVertex2().getRadius(), angle2, e.getVertex2().getCenter());

            //draw the connecting edge:
            gc.strokeLine(p1.getX(), p1.getY(),
                          p2.getX(), p2.getY());
        }
    }

    /**
     * Initializes listeners for the view.  If the appropriate items in the GraphModel or InteractionModel change
     * the view will automatically be redrawn.
     */
    final void initListeners() {
        Main.interact.getLineStartProperty().addListener((ov, oldVal, newVal) -> draw(gc));
        Main.interact.getLineEndProperty().addListener((ov, oldVal, newVal) -> draw(gc));
        Main.interact.getLineSetProperty().addListener((ov, oldVal, newVal) -> draw(gc));

        //vertexPropertyList change listener. Listens for internal vertex object property changes.
        Main.model.getVertexListProperty().addListener(new ListChangeListener<Vertex>() {
            @Override
            public void onChanged(Change<? extends Vertex> c) {
                while (c.next()) {
                    if (c.wasPermutated()) {
                        for (int i = c.getFrom(); i < c.getTo(); ++i) {
                            draw(gc);
                        }
                    } else if (c.wasUpdated()) {
                        for (int i = c.getFrom(); i < c.getTo(); ++i) {
                            draw(gc);
                        }
                    } else {
                        for (Vertex removedItem : c.getRemoved()) {
                            draw(gc);
                        }
                        for (Vertex addedItem : c.getAddedSubList()) {
                            draw(gc);
                        }
                    }
                }
            }
        });

        Main.model.getEdgeSetProperty().addListener(new SetChangeListener<Edge>() {
            @Override
            public void onChanged(Change<? extends Edge> c) {
                draw(gc);
            }
        });
    }

    /*The following methods are used directly by this class, but may be better placed in
    a separate class containing static methods for generic geometric manipulations.  Left here
    in order to keep the code close to its usage.
    */

    /**
     * Given two points returns the angle from the horizontal.
     * @param p1 the first point.
     * @param p2 the second point
     * @return the angle from the horizontal.
     */
    public static double getAngle(Point2D p1, Point2D p2) {
        return (Math.atan2(p2.getY() - p1.getY(), p2.getX() - p1.getX()));

    }

    /**
     * Calculates a point on the circumference of the circle in the direction of a line.
     * @param radius double representing the radius of the circle
     * @param angle double representing the angle from the horizontal.
     * @param p Point2D representing the center point of the circle.
     * @return Point2D representing the point on circumference of the circle in the direction of the line.
     */
    public static Point2D getCircPoint(double radius, double angle, Point2D p) {
        double x;
        double y;
        x = p.getX() + radius * Math.cos(angle);
        y = p.getY() + radius * Math.sin(angle);
        return new Point2D(x, y);
    }

    /**
     * Calculates the distance between two points.
     * @param p1 Point2D representing the start point
     * @param p2 Point2D representing the end point.
     * @return double representing the distance between two points.
     */
    public static double pointDistance(Point2D p1, Point2D p2) {
        double d1 ,d2;
        d1 = p1.getX() - p2.getX();
        d2 = p1.getY() - p2.getY();
        return Math.sqrt(Math.pow(d1, 2) + Math.pow(d2,2));
    }

    /**
     * Converts a font pt sizing to a pixel px.
     * @param pt  a double representing a font pt value.
     * @return double pixel px value.
     */
    private double pointToPixel(double pt) {
        return pt * (96f / 72f);
    }
}









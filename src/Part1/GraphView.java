package Part1;

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

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        px = pointToPixel(10);
        gc.setFont(new Font("Arial", px));

        gc.setLineWidth(1);
        //Draw vertices:
        for (Vertex v : Main.model.getVertexListProperty()) {
            gc.setFill(v.getColor());
            gc.fillOval(v.getCords().getX(), v.getCords().getY(), v.getRadius() * 2, v.getRadius() * 2);
            gc.strokeText(Integer.toString(v.getVertexNum()), v.getCenter().getX() - px/2, v.getCenter().getY() + px/2);
        }

        //Shift click:
        if (Main.model.isLineSet()) {
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(3);

            //draw outline around selected vertex:
            if (Main.model.getSelectedVertex() != null) {
                gc.strokeOval(Main.model.getSelectedVertex().getCords().getX(),
                        Main.model.getSelectedVertex().getCords().getY(),
                        Main.model.getSelectedVertex().getRadius() * 2,
                        Main.model.getSelectedVertex().getRadius() * 2);
            }

            //find point on circumference of vertex:
            angle1 = getAngle(Main.model.getLineStart(), Main.model.getLineEnd());
            p1 = getCircPoint(Main.model.getDefaultRadius(), angle1, Main.model.getSelectedVertex().getCenter());

            //Do not draw unless the line end point falls outside the selected vertex:
            if (!(Main.model.getSelectedVertex().containsPoint(Main.model.getLineEnd()))) {
                gc.strokeLine(p1.getX(), p1.getY(),
                             Main.model.getLineEnd().getX(), Main.model.getLineEnd().getY());
            }
        }

        for (Edge e : Main.model.getEdgeSetProperty()) {
            gc.setLineWidth(1);
            angle1 = getAngle(e.getVertex1().getCenter(), e.getVertex2().getCenter());
            angle2 = getAngle(e.getVertex2().getCenter(), e.getVertex1().getCenter());
            p1 = getCircPoint(e.getVertex1().getRadius(), angle1, e.getVertex1().getCenter());
            p2 = getCircPoint(e.getVertex2().getRadius(), angle2, e.getVertex2().getCenter());

            //draw connecting edges:
            gc.strokeLine(p1.getX(), p1.getY(),
                          p2.getX(), p2.getY());
        }
    }

    /**
     * Initializes listeners for the view.  If the appropriate items in the GraphModel change
     * the view will automatically be redrawn.
     */
    final void initListeners() {
        Main.model.getLineStartProperty().addListener((ov, oldVal, newVal) -> draw(gc));
        Main.model.getLineEndProperty().addListener((ov, oldVal, newVal) -> draw(gc));
        Main.model.getLineSetProperty().addListener((ov, oldVal, newVal) -> draw(gc));

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

        //set of edges (set thus no duplicates allowed, i.e. {1,2} == {2,1})
        Main.model.getEdgeSetProperty().addListener(new SetChangeListener<Edge>() {
            @Override
            public void onChanged(Change<? extends Edge> c) {
                draw(gc);
            }
        });
    }

    /**
     * Given two points returns the angle from the horizontal.
     * @param p1 the first point.
     * @param p2 the second point
     * @return the angle from the horizontal.
     */
    private double getAngle(Point2D p1, Point2D p2) {
        return (Math.atan2(p2.getY() - p1.getY(), p2.getX() - p1.getX()));

    }

    /*The following methods are used directly by this class, but may be better placed in
    a separate class containing static methods for generic geometric manipulations.  Left here
    in order to keep the code close to its usage.
    */

    /**
     * Calculates a point on the circumference of the circle in the direction of a line.
     * @param radius double representing the radius of the circle
     * @param angle double representing the angle from the horizontal.
     * @param p Point2D representing the center point of the circle.
     * @return Point2D representing the point on circumference of the circle in the direction of the line.
     */
    private Point2D getCircPoint(double radius, double angle, Point2D p) {
        double x;
        double y;
        x = p.getX() + radius * Math.cos(angle);
        y = p.getY() + radius * Math.sin(angle);
        return new Point2D(x, y);
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









package Part2;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.paint.Color;


public class InteractionModel {
    protected enum ProgramState {creation, removal} //the permissible states of operation for the application.
    private ProgramState programState; //holds the current ProgramState
    private Color buttonColor;
    private Vertex selectedVertex;
    private int defaultRadius = 30; //the default radius for all vertices.

    private final SimpleBooleanProperty lineSet = new SimpleBooleanProperty();  //True if we are attempting to create a new edge, false otherwise.
    private final SimpleObjectProperty<Point2D> lineStartProperty = new SimpleObjectProperty<>(); //Line start coordinate for potential Edge creation
    private final SimpleObjectProperty<Point2D> lineEndProperty = new SimpleObjectProperty<>(); //Line end coordinate for potential Edge creation

    public void setState(ProgramState state) {
        this.programState= state;
      }

    public ProgramState getState() {
        return programState;
    }

    public void setButtonColor(Color c) {
        buttonColor = c;
    }

    public Color getButtonColor() {
        return buttonColor;
    }

    /**
    * Checks to see if we are currently drawing a line.
    * @return true if line is being drawn, false otherwise.
    */
    public boolean isLineSet() {
        return lineSet.get();
    }

    /**
     * Sets line drawing state to true or false
     * @param b true or false
     */
    public void setLine(boolean b) {
        lineSet.set(b);
    }

    public SimpleBooleanProperty getLineSetProperty() {
        return lineSet;
    }

    /**
     * Sets the start point of a line.  A potential Edge.
     * @param p Point2D representing the start of the line coordinates.
     */
    public void setLineStart(Point2D p) {
        lineStartProperty.set(p);
    }

    /**
     * Sets the end point of the line being drawn. A potential Edge.
     * @param p Point2D representing the end of the line coordinates.
     */
    public void setLineEnd(Point2D p) {
        lineEndProperty.set(p);
    }

    public Point2D getLineStart() {
        return lineStartProperty.get();
    }

    public Point2D getLineEnd() {
        return lineEndProperty.get();
    }

    public SimpleObjectProperty<Point2D> getLineStartProperty() {
        return lineStartProperty;
    }

    public SimpleObjectProperty<Point2D> getLineEndProperty() {
        return lineEndProperty;
    }

    public void setSelectedVertex(Vertex v) {
        if (Main.model.getVertexListProperty().contains(v)) {
            selectedVertex = Main.model.getVertexListProperty().get(Main.model.getVertexListProperty().indexOf(v));
        }
    }

    public Vertex getSelectedVertex() {
        return selectedVertex;
    }

    public void updateSelectedVertex(Point2D p) {
        if (selectedVertex != null)
            selectedVertex.setCords(p);
    }

    public void changeCursor(Cursor cursor) {
        Main.view.setCursor(cursor);
    }

    public int getDefaultRadius() {
        return defaultRadius;
    }

    public void setDefaultRadius(int r) {
        defaultRadius = r;
    }

    /**
     * Checks to see if a point contains vertex -OR- when using the same point to create a new vertex,
     * will the new vertex intersect an existing vertex.
     * @param p Point2D of the point
     * @return true if the point intersects an existing vertex OR WILL intersect an existing vertex
     * if a new vertex is created.
     */
    public boolean intersectVertex(Point2D p) {
        Vertex tempV = new Vertex(Main.model.getNumVerts(),defaultRadius, p);
        for (Vertex v: Main.model.getVertexListProperty()) {
            if (v.containsPoint(p)) {
                System.out.println("New vertex will intersect with existing vertex");
                return true;
            }else if (vertexTouchExternal(v,tempV)) {
                System.out.println("New vertex will intersect with existing vertex");
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to if Vertex a will intersect with Vertex b
     * @param a Vertex to be checked
     * @param b Vertex to check
     * @return true if vertex a intersects with b, false otherwise.
     */
    private boolean vertexTouchExternal(Vertex a, Vertex b) {
        double r1, r2;
        double d1 ,d2;
        double dist;
        r1 = a.getRadius();
        r2 = b.getRadius();
        d1 = a.getCenter().getX() - b.getCenter().getX();
        d2 = a.getCenter().getY() - b.getCenter().getY();
        dist =  Math.sqrt(Math.pow(d1, 2) + Math.pow(d2,2));
        if (r1 - r2 < dist && r1 + r2 > dist) {
            return true; //intersection
        }else if ((int) dist == (int) (r1 + r2)) {
            return true;
        }else
            return false;
    }
}




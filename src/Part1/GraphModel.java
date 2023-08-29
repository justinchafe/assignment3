package Part1;

import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.geometry.Point2D;
import javafx.util.Callback;

import java.util.HashSet;

public class GraphModel {
    private final SimpleListProperty<Vertex> vertexListProperty; //List of Vertices
    private final SimpleSetProperty<Edge> edgeSetProperty; //Set of Edges between Vertices.
    private final SimpleObjectProperty<Point2D> lineStartProperty = new SimpleObjectProperty<>(); //Line start coordinate for Edge creation
    private final SimpleObjectProperty<Point2D> lineEndProperty = new SimpleObjectProperty<>(); //Line end coordinate for Edge creation
    private final SimpleBooleanProperty lineSet = new SimpleBooleanProperty();  //True if we are attempting to create a new edge, false otherwise.

    private int defaultRadius = 30; //the default radius for all vertices.
    private int numVerts; //the number of vertices in the graph.
    private Vertex selectedVertex; //the currently selected vertex.

    public GraphModel() {
        ObservableList<Vertex> observableList = (ObservableList<Vertex>) FXCollections.observableArrayList(
                new Callback<Vertex, Observable[]>() {
                    @Override
                    public Observable[] call(Vertex param) {
                        return new Observable[]{
                                param.vertexCordsProperty(),
                                param.vertexColorProperty()

                        };
                    }
                }
        );
        vertexListProperty = new SimpleListProperty<>(observableList); //wrap our listProperty in Observable.

        HashSet<Edge> hashSet = new HashSet<>();
        ObservableSet<Edge> edgeSet = (ObservableSet<Edge>) FXCollections.observableSet(hashSet);
        edgeSetProperty = new SimpleSetProperty<>(edgeSet);
        this.numVerts = 0;
    }

   public SimpleObjectProperty<Point2D> getLineStartProperty() {
        return lineStartProperty;
    }

    public SimpleObjectProperty<Point2D> getLineEndProperty() {
        return lineEndProperty;
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

    public boolean isLineSet() {
        return lineSet.get();
    }

    /**
     * Sets line drawing state to true or false. A potential edge is either being drawn or not.
     * @param b true or false
     */
    public void setLine(boolean b) {
        lineSet.set(b);
    }

    public SimpleBooleanProperty getLineSetProperty() {
        return lineSet;
    }

    public int getDefaultRadius() {
        return defaultRadius;
    }

    public SimpleListProperty<Vertex> getVertexListProperty() {
        return vertexListProperty;
    }

    public SimpleSetProperty<Edge> getEdgeSetProperty() {
        return edgeSetProperty;
    }

    /**
     * Adds a vertex to the graph
     * @param p Point2D coordinates of the vertex
     * @param radius int radius of the vertex
     */
    public void addVertex(Point2D p, int radius)
    {
        vertexListProperty.add(new Vertex(numVerts++, radius,p));
    }

    /**
     * Adds an Edge connecting two vertices in the graph. The graphs edge set does not allow duplicate objects.
     * For instance, if edge {v1,v2} exists in the graph then {v2,v1} will not be added (where v = vertex)
     * Vertices are equal if their coordinates and radii are equal.
     * @param v1 - a vertex the edge is connected to.
     * @param v2 - a vertex  the edge is connected to such that v2 != v1.
     */
    public void addEdge(Vertex v1, Vertex v2) {
       edgeSetProperty.add(new Edge(v1,v2));
    }

    /**
     * Checks to see if a point contains vertex -OR- when using the same point to create a new vertex,
     * will the new vertex intersect an existing vertex.
     * @param p Point2D of the point
     * @return true if the point intersects an existing vertex OR WILL intersect an existing vertex
     * if a new vertex is created.
     */
    public boolean intersectVertex(Point2D p) {
        Vertex tempV = new Vertex(numVerts,defaultRadius, p);
        for (Vertex v: vertexListProperty) {
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

    public static double pointDistance(Point2D p1, Point2D p2) {
        double d1 ,d2;
        d1 = p1.getX() - p2.getX();
        d2 = p1.getY() - p2.getY();
        return Math.sqrt(Math.pow(d1, 2) + Math.pow(d2,2));
    }

    public void setSelectedVertex(Vertex v) {
        if (vertexListProperty.contains(v)) {
            selectedVertex = vertexListProperty.get(vertexListProperty.indexOf(v));
        }
    }

    public Vertex getSelectedVertex() {
        return selectedVertex;
    }

    public void updateSelectedVertex(Point2D p) {
        if (selectedVertex != null)
            selectedVertex.setCords(p);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("****Vertices****\n");
        for (Vertex v : vertexListProperty) {
            sb.append(v.toString());
            sb.append("\n");
        }
        sb.append("****Edges****\n");
        for (Edge e : edgeSetProperty) {
            sb.append(e.toString());
            sb.append("\n");
        }
        return sb.toString();
    }


}


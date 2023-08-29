package Part2;

import javafx.beans.Observable;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.util.HashSet;
import java.util.Iterator;

public class GraphModel {
    private final SimpleListProperty<Vertex> vertexListProperty; //List of Vertices.
    private final SimpleSetProperty<Edge> edgeSetProperty; //Set of Edges between Vertices.
    private int numVerts; //number of vertices in the graph.

    public GraphModel() {
        ObservableList<Vertex> observableList = (ObservableList<Vertex>) FXCollections.observableArrayList(
                new Callback<Vertex, Observable[]>() {
                    @Override
                    public Observable[] call(Vertex param) {
                        return new Observable[]{
                                param.vertexCordsProperty(),
                                param.vertexColorProperty(),
                                param.vertexNumProperty()

                        };
                    }
                }
        );
        vertexListProperty = new SimpleListProperty<>(  observableList); //wrap our listProperty in Observable.

        HashSet<Edge> hashSet = new HashSet<>();
        ObservableSet<Edge> edgeSet = (ObservableSet<Edge>) FXCollections.observableSet(hashSet);
        edgeSetProperty = new SimpleSetProperty<>(edgeSet);
        this.numVerts = 0;
    }

    /**
     *Gets the number of Vertices in the graph.
     * @return int containing number of vertices.
     */
    public int getNumVerts() {
        return numVerts;
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
     * Adds a vertex to the graph
     * @param p Point2D coordinates of the vertex
     * @param radius int radius of the vertex
     * @param c Color representing the color of the vertex.
     */
    public void addVertex(Point2D p, int radius, Color c)
    {
        vertexListProperty.add(new Vertex(numVerts++, radius,p,c));
    }

    /**
     * Removes vertex from the graph and all it's associated edges from the graphs edge set.
     * @param v - The vertex to be removed from the Graph. All edges associated with this vertex are removed automatically.
     */
    public void removeVertex(Vertex v) {
        int i = 0;
        Iterator<Edge> iterator = edgeSetProperty.iterator();
        while (iterator.hasNext()) {
            Edge e = iterator.next();
            if (e.contains(v))
                iterator.remove();
        }
        vertexListProperty.remove(v);

        for (Vertex vert: vertexListProperty) {
            vert.setVertexNum(i++);
        }
       numVerts=i;
    }

    /**
     * Adds an Edge connecting two vertices in the graph. Sets do not allow duplicate objects.
     * For instance, if edge {1,2} exists in the graph then {2,1} will not be added.
     * Vertices are equal if their coordinates and radii are equal.
     * @param v1 - a vertex the edge is connected to.
     * @param v2 - a vertex  the edge is connected to such that v2 != v1.
     */
    public void addEdge(Vertex v1, Vertex v2) {
        edgeSetProperty.add(new Edge(v1,v2));
    }


     @Override
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


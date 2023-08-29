package Part2;

import javafx.geometry.Point2D;
import java.util.Objects;

public class Edge {
    private Vertex v1; //a vertex that the edge connects with.
    private Vertex v2; //a vertex the edge is connected with such that v2 != v1

    public Edge(Vertex v1, Vertex v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    /**
     * Sets the vertices connecting an edge.
     * @param v1 a vertex connecting this edge.
     * @param v2 another vertex connecting this edge.
     */
    public void setEdge(Vertex v1, Vertex v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    /**
     * Checks to see if this edge has a connection to the provided vertex.
     * @param v Vertex a vertex
     * @return true if the edge connects to the provided vertex, false otherwise.
     */
    public boolean contains(Vertex v) {
        if (v1.equals(v) || v2.equals(v)) {
            return true;
        }
        return false;
    }

    public Vertex getVertex1() {
        return v1;
    }

    public Vertex getVertex2() {
        return v2;
    }

    public void setVertex1(Vertex v) {
        v1 = v;
    }

    public void setVertex2(Vertex v) {v2 = v;}

    /**
     * gets the Point2D coordinates of the "first" vertex connecting this edge.
     * @return Point2D of the Vertex.
     */
    public Point2D getV1Cords() {
        return v1.getCords();
    }
    /**
     * gets the Point2D coordinates of the "second" vertex connecting this edge.
     * @return Point2D of the Vertex.
     */
    public Point2D getV2Cords() {
        return v2.getCords();
    }

    /**
     * Checks Edge for equality. Vertex order is unimportant.
     * For instance, the edge represented by {Vertex1, Vertex2} is equivalent to {Vertex2,Vertex1}.
     * @param o the Object to check for equality with another Edge object.
     * @return true if the object is equal to this Edge, false otherwise.
     */
    @Override
    public boolean equals(Object o) {

        // null check
        if (o == null) {
            return false;
        }

        // this instance check
        if (this == o) {
            return true;
        }

        // instanceof Check and actual value check
        if ((o instanceof Edge) && (((Edge) o).getVertex1().equals(this.v1)) && (((Edge) o).getVertex2().equals(this.v2))) {
            return true;
        } else if ((o instanceof Edge) && (((Edge) o).getVertex1().equals(this.v2)) && (((Edge) o).getVertex2().equals(this.v1))) {
            return true;
        }else
            return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(v1,v2);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(v1.toString());
        sb.append(", ");
        sb.append(v2.toString());
        sb.append(")");
        return sb.toString();
    }
}
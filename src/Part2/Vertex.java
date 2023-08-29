package Part2;


import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.Objects;

public class Vertex {
    private int radius;
    private final SimpleObjectProperty<Point2D> vertexCords = new SimpleObjectProperty<>(); //upper right x/y coordinates.
    private final SimpleObjectProperty<Color> vertexColor = new SimpleObjectProperty<>(); //Vertex Color
    private final SimpleIntegerProperty vertexNum = new SimpleIntegerProperty(); //the vertex number

    /**
     * Constructor for a Vertex
     * @param vNum int which sets the vertex number.
     * @param radius the radius of this vertex.
     * @param p Point2D representing the upper left x,y coordinates of this vertex.
     */
    public Vertex(int vNum, int radius, Point2D p) {
        setVertexNum(vNum);
        this.radius = radius;
        setCords(p);
        setColor(Color.LIGHTBLUE); //a default color.
    }

    /**
     * Constructor for a Vertex
     * @param vNum int which sets the vertex number.
     * @param radius the radius of this vertex.
     * @param p Point2D representing the upper left x,y coordinates of this vertex.
     * @param color Color representing the Color of the vertex.
     */
    public Vertex(int vNum, int radius, Point2D p, Color color) {
        setVertexNum(vNum);
        this.radius = radius;
        setCords(p);
        setColor(color);
    }

    public void setVertexNum(int vNum) {
        vertexNum.set(vNum);
    }

    public int getVertexNum() {
        return vertexNum.get();
    }

    public SimpleIntegerProperty vertexNumProperty() {
        return vertexNum;
    }

    public void setCords(Point2D p) {
        vertexCords.set(p);
    }

    public Point2D getCords() {
        return vertexCords.get();
    }

    public SimpleObjectProperty<Point2D> vertexCordsProperty() {
        return vertexCords;
    }

    public void setColor(Color c) {
        vertexColor.set(c);
    }

    public Color getColor() {
        return vertexColor.get();
    }

    public SimpleObjectProperty<Color> vertexColorProperty() {
        return vertexColor;
    }

    public int getRadius() {
        return radius;
    }

    /**
     * Gets the center Point2D coordinates of this vertex object.
     * @return Point2D center coordinates of the vertex.
     */
    public Point2D getCenter() {
        return new Point2D(getCords().getX()+radius, getCords().getY()+radius);
    }

    public double getCenterX() {
        return getCords().getX()+radius;
    }

    public double getCenterY() {
        return getCords().getY()+radius;
    }

    /**
     * Checks if the provided Point lies within this vertex.
     * @param p Point2D representing the point to check.
     * @return true if p lies within the vertex.  False otherwise.
     */
    public boolean containsPoint(Point2D p) {
        Circle circle = new Circle();
        circle.setCenterX(getCenter().getX());
        circle.setCenterY(getCenter().getY());
        circle.setRadius(radius);
        return circle.contains(p);
    }

    /**
     * Checks Vertex for equality. Two vertices are equal if their radii and coordinates are equal.
     *
     * @param o the Object to check for equality with another Vertex object.
     * @return true if the object is equal to this Vertex, false otherwise.
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
        if ( ((o instanceof Vertex)) && this.radius == ((Vertex) o).getRadius() && this.containsPoint( ((Vertex) o).getCords()) ) {
            return true;
        }else if ( (o instanceof Vertex) && this.radius == ((Vertex) o).getRadius() && this.getCords().equals(((Vertex) o).getCords()) ) {
            return true;
        }else
            return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertexNum, radius, vertexCords,vertexColor);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vertex #: ");
        sb.append(getVertexNum());
        sb.append(", ");
        sb.append(getCords().toString());
        return sb.toString();
    }

}
package Part1;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class GraphViewController {
    private enum State{INIT,SEL,SHIFTCLICK} //INIT = unselected, SEL = vertex selected, SHFITCLICK = vertex was shift clicked.
    private State state; //the current state for our event handler.
    private Point2D initP; //the initial end point of a lint attached to the center of a selected vertex.
    private Point2D endP;//the end point when dragging a line.

    public GraphViewController() {
        state = State.INIT;
        initP = new Point2D(0, 0);
        endP = new Point2D(0,0);

        //Note: Although it is the desired functionality, it is too brittle.
        //Use a state pattern in the future:
        Main.view.addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {
                switch (state) {
                    case SHIFTCLICK:
                        if (e.getEventType() == MouseEvent.MOUSE_CLICKED) {
                            Main.model.getSelectedVertex().setColor(Color.LIGHTBLUE);
                            state = State.INIT;
                            endP = new Point2D(e.getX(), e.getY());
                            finalizeEdge(e);
                            //reset the state and edge start coordinate.
                            initP = new Point2D(0, 0);
                            endP = new Point2D(0, 0);
                            e.consume();

                        } else if (e.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                            endP = new Point2D(e.getX(), e.getY());
                            Main.model.setLineStart(initP);
                            Main.model.setLineEnd(endP);
                            e.consume();
                        }
                        break;

                    case SEL:
                        if (e.getEventType() == MouseEvent.MOUSE_CLICKED && !isVertexSelected(e)) {
                            state = State.INIT;
                            Main.model.getSelectedVertex().setColor(Color.LIGHTBLUE);
                            e.consume();

                        } else if (e.getEventType() == MouseEvent.MOUSE_CLICKED && !compareSelectedVertex(e)) {
                            state = State.INIT;
                            Main.model.getSelectedVertex().setColor(Color.LIGHTBLUE);
                            e.consume();

                        } else if (e.getEventType() == MouseEvent.MOUSE_CLICKED) {
                            Main.model.getSelectedVertex().setColor(Color.ORANGE);
                            e.consume();

                        } else if (e.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                            Main.model.updateSelectedVertex(new Point2D(e.getX() - Main.model.getDefaultRadius(), e.getY() - Main.model.getDefaultRadius()));
                            Main.model.getSelectedVertex().setColor(Color.ORANGE);

                        } else if (e.getEventType() == MouseEvent.MOUSE_RELEASED) {
                            Main.model.getSelectedVertex().setColor(Color.LIGHTBLUE);
                            state = State.INIT;
                            e.consume();
                        }
                        break;


                    case INIT:
                        if (e.getEventType() == MouseEvent.MOUSE_CLICKED && e.isShiftDown() && isVertexSelected(e)) {
                            state = State.SHIFTCLICK;
                            setSelectedVertex(e);
                            initEdge(e);
                            if (Main.model.getSelectedVertex() != null)
                                Main.model.getSelectedVertex().setColor(Color.LIGHTBLUE);
                            e.consume();

                        } else if (e.getEventType() == MouseEvent.MOUSE_PRESSED && isVertexSelected(e) && !e.isShiftDown()) {
                            state = State.SEL;
                            setSelectedVertex(e);
                            if (Main.model.getSelectedVertex() != null)
                                Main.model.getSelectedVertex().setColor(Color.ORANGE);

                            e.consume();

                        } else if (e.getEventType() == MouseEvent.MOUSE_PRESSED && !e.isShiftDown()) {
                            if (!Main.model.intersectVertex(new Point2D(e.getX() - Main.model.getDefaultRadius(), e.getY() - Main.model.getDefaultRadius()))) {
                                Main.model.addVertex(new Point2D(e.getX() - Main.model.getDefaultRadius(),
                                        e.getY() - Main.model.getDefaultRadius()), Main.model.getDefaultRadius()); //CLICK IN CENTER, offset coordinates for top
                            }
                            e.consume();
                        }
                        break;
                }
            }

            /**
             * Checks mouse coordinates against graph to see if a vertex is selected.
             * @param e MouseEvent from which Point2D coordinates are extracted.
             * @return boolean true if a vertex is selected, false otherwise.
             */
            private boolean isVertexSelected(MouseEvent e) {
                Point2D P1 = new Point2D(e.getX(), e.getY());

                for (Vertex v : Main.model.getVertexListProperty()) {
                    if (v.containsPoint(P1)) {
                        return true;
                    }
                }
                return false;
            }

            /**
             * Checks the currently selected vertex to see if Point2D coordinates lie within it.
             * @param e MouseEvent from which Point2D coordinates are extracted.
             * @return boolean true if Point2D coordinates lie within the selected vertex, false otherwise.
             */
            private boolean compareSelectedVertex(MouseEvent e) {
                return Main.model.getSelectedVertex().containsPoint(new Point2D(e.getX(),e.getY()));
            }

            /**
             * Checks mouse coordinates against graph vertices, if a match is found sets the vertex as the selected vertex.
             * @param e MouseEvent from which the Point2D coordinates are extracted.
             */
            private void setSelectedVertex(MouseEvent e) {
                Point2D P1 = new Point2D(e.getX(), e.getY());
                for (Vertex v : Main.model.getVertexListProperty()) {
                    if (v.containsPoint(P1)) {
                        Main.model.setSelectedVertex(v);
                    }
                }
            }

            /**
             * Initializes a potential edge and anchors it to the selected vertex.
             * @param e MouseEvent from which the Point2D coordinates are extracted.
             */
            private void initEdge(MouseEvent e) {
                Point2D P1 = new Point2D(e.getX(), e.getY());
                for (Vertex v : Main.model.getVertexListProperty()) {
                    if (v.containsPoint(P1)) {
                        initP = v.getCenter(); //attaches to the center of the vertex
                        Main.model.setLineStart(initP);
                        Main.model.setLineEnd(initP);
                        Main.model.setLine(true);
                        break;
                    }
                }
            }

            /**
             * Attempts to attach an edge from the selected vertex to another vertex.
             * @param e MouseEvent from which the Point2D coordinates are extracted.
             */
            private void finalizeEdge(MouseEvent e) {
                Vertex v1, v2;
                v1 = null;
                v2 = null;
                Point2D P2 = new Point2D(e.getX(), e.getY());
                if (!(P2.equals(initP))) {
                    for (Vertex v : Main.model.getVertexListProperty()) {
                        if (v.containsPoint(initP))
                            v1 = v;
                        if (v.containsPoint(P2))
                            v2 = v;
                    }
                }
                if (v1 != null && v2 != null && !(v1.equals(v2)))
                    Main.model.addEdge(v1, v2);

                Main.model.setLine(false);
            }

        });

    }
}
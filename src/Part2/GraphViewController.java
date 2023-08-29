package Part2;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;


public class GraphViewController {

    private enum State{INIT,SEL,SHIFTCLICK}  //INIT = unselected, SEL = vertex selected, SHFITCLICK = vertex was shift clicked
    private State state; //the current state for our event handler.
    private Point2D initP; //the initial point of a center of a selected vertex.
    private Point2D endP; //the end point when dragging a line.
    private Color origColor; //need to reset after drag (turns orange).

    public GraphViewController() {
        state = State.INIT;
        initP = new Point2D(0, 0);
        endP = new Point2D(0,0);

        //Note: Although it is the desired functionality, it is too brittle.
        //Use a state pattern in the future:
        Main.view.addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {
            public void handle(MouseEvent e) {
                //Creation Mode:
                if (Main.interact.getState() == InteractionModel.ProgramState.creation) {
                    switch (state) {
                        case SHIFTCLICK:
                            if (e.getEventType() == MouseEvent.MOUSE_CLICKED) {
                                state = State.INIT;
                                endP = new Point2D(e.getX(), e.getY());
                                finalizeEdge(e);
                                //reset the state and edge start coordinate.
                                initP = new Point2D(0, 0);
                                endP = new Point2D(0, 0);
                                e.consume();

                            } else if (e.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                                endP = new Point2D(e.getX(), e.getY());
                                Main.interact.setLineStart(initP);
                                Main.interact.setLineEnd(endP);
                                e.consume();
                            }
                            break;

                        case SEL:
                            if (e.getEventType() == MouseEvent.MOUSE_CLICKED && !isVertexSelected(e)) {
                                state = State.INIT;
                                Main.interact.getSelectedVertex().setColor(origColor);
                                e.consume();

                            } else if (e.getEventType() == MouseEvent.MOUSE_CLICKED && !compareSelectedVertex(e)) {
                                state = State.INIT;
                                Main.interact.getSelectedVertex().setColor(origColor);
                                e.consume();

                            } else if (e.getEventType() == MouseEvent.MOUSE_CLICKED) {
                                Main.interact.getSelectedVertex().setColor(Color.ORANGE);
                                e.consume();

                            } else if (e.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                                Main.interact.updateSelectedVertex(new Point2D(e.getX() - Main.interact.getDefaultRadius(), e.getY() - Main.interact.getDefaultRadius()));
                                Main.interact.getSelectedVertex().setColor(Color.ORANGE);

                            } else if (e.getEventType() == MouseEvent.MOUSE_RELEASED) {
                                Main.interact.getSelectedVertex().setColor(origColor);
                                state = State.INIT;
                                e.consume();
                            }
                            break;


                        case INIT:
                            if (e.getEventType() == MouseEvent.MOUSE_CLICKED && e.isShiftDown() && isVertexSelected(e)) {
                                state = State.SHIFTCLICK;
                                setSelectedVertex(e);
                                initEdge(e);
                                if (Main.interact.getSelectedVertex() != null)
                                    e.consume();

                            } else if (e.getEventType() == MouseEvent.MOUSE_PRESSED && isVertexSelected(e) && !e.isShiftDown()) {
                                state = State.SEL;
                                setSelectedVertex(e);
                                if (Main.interact.getSelectedVertex() != null)
                                    origColor = Main.interact.getSelectedVertex().getColor();
                                    Main.interact.getSelectedVertex().setColor(Color.ORANGE);

                                e.consume();

                            } else if (e.getEventType() == MouseEvent.MOUSE_PRESSED && !e.isShiftDown()) {
                                if (!Main.interact.intersectVertex(new Point2D(e.getX() - Main.interact.getDefaultRadius(), e.getY() - Main.interact.getDefaultRadius()))) {
                                    Main.model.addVertex(new Point2D(e.getX() - Main.interact.getDefaultRadius(),
                                            e.getY() - Main.interact.getDefaultRadius()), Main.interact.getDefaultRadius(),
                                             Main.interact.getButtonColor()); //CLICK IN CENTER, offset coordinates for top
                                    origColor = Main.interact.getButtonColor();
                                }
                                e.consume();
                            }
                            break;
                    }

                 //Removal Mode:
                } else if (Main.interact.getState() == InteractionModel.ProgramState.removal) {

                    if (e.getEventType() == MouseEvent.MOUSE_MOVED && isVertexSelected(e)) {
                        Main.interact.changeCursor(Cursor.CROSSHAIR);
                    }else if (e.getEventType() == MouseEvent.MOUSE_MOVED && !isVertexSelected(e)) {
                        Main.interact.changeCursor(Cursor.DEFAULT);
                    }

                    if (e.getEventType() == MouseEvent.MOUSE_PRESSED && isVertexSelected(e)) {
                        setSelectedVertex(e);
                        removeSelectedVertex(e);
                        Main.interact.changeCursor(Cursor.DEFAULT);
                    }
                }
            }

            /**
             * Removes the selected vertex from the graph
             * @param e MouseEvent - unused.
             */
            private void removeSelectedVertex(MouseEvent e) {
                Main.interact.setLine(false);
                if (Main.interact.getSelectedVertex() != null) {
                    Main.model.removeVertex(Main.interact.getSelectedVertex());
                    Main.interact.setLine(false);
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
                return Main.interact.getSelectedVertex().containsPoint(new Point2D(e.getX(),e.getY()));
            }

            /**
             * Checks mouse coordinates against graph vertices, if a match is found sets the vertex as the selected vertex.
             * @param e MouseEvent from which the Point2D coordinates are extracted.
             */
            private void setSelectedVertex(MouseEvent e) {
                Point2D P1 = new Point2D(e.getX(), e.getY());
                for (Vertex v : Main.model.getVertexListProperty()) {
                    if (v.containsPoint(P1)) {
                        Main.interact.setSelectedVertex(v);
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
                        Main.interact.setLineStart(initP);
                        Main.interact.setLineEnd(initP);
                        Main.interact.setLine(true);
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
                        if (v.containsPoint(initP) )
                            v1 = v;
                        if (v.containsPoint(P2))
                            v2 = v;
                    }
                }
                if (v1 != null && v2 != null && !(v1.equals(v2)))
                    Main.model.addEdge(v1, v2);

                Main.interact.setLine(false);
            }

        });

    }


}

package Part2;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;

public class ToolBarController {

    @FXML private ToggleButton greenButton;
    @FXML private ToggleButton redButton;
    @FXML private ToggleButton blueButton;
    @FXML private ToggleButton creation;
    @FXML private ToggleButton removal;

    public ToolBarController() {
        Main.interact.setState(InteractionModel.ProgramState.creation);
    }

    public void removal() {
        keepSelected(removal);
        Main.interact.setState(InteractionModel.ProgramState.removal);
    }

    public void creation() {
        keepSelected(creation);
        Main.interact.setState(InteractionModel.ProgramState.creation);
        Main.interact.changeCursor(Cursor.DEFAULT);
    }

    public void setGreenButton() {
        setColor(greenButton);
    }

    public void setBlueButton() {
        setColor(blueButton);
    }

    public void setRedButton() {
        setColor(redButton);
    }

    /**
     * Sets the color according to the currently depressed Toggle Button.  Color is extracted
     * from the buttons style property in the FXML file.
     * @param button the depressed ToggleButton in the colorGroup.
     */
    private void setColor(ToggleButton button) {
        keepSelected(button);
        //extract hex color from -fx-base:# leaving [A-Za-z0-9]{6};
        String str = button.getStyle().toString();
        str = str.substring(str.indexOf('#') + 1, str.lastIndexOf(';'));
        Main.interact.setButtonColor(Color.web(str));
    }

    /**
     * Creates RadioButton style behavior.
     * @param button the button to keep depressed.
     */
    private void keepSelected(ToggleButton button) {
        if (!button.isSelected())
            button.setSelected(true);
    }

}

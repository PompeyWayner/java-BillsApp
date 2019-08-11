package bills;

import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * class NewMonthController - controller for newMonthDialog.fxml UI.
 *
 * @author Wayne Sandford
 * @version 06-08-2019
 */
public class NewMonthController {

    @FXML
    private ChoiceBox choiceId;

    public void initialize() {
//        ObservableList<String> options = FXCollections.observableArrayList("January","February", "March", "April",
//                "May", "June", "July", "August", "September", "October", "November", "December");
//        choiceId.setValue("January"); // Default month.
//        choiceId.setItems(options); // Adds all values in choiceBox.
    }

    public String getNewMonth() {

        String month;

        month = String.valueOf(choiceId.getValue());
        return month;
    }
}

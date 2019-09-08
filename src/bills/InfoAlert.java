package bills;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class InfoAlert {

    /**
     * Display Alert Box for Information.
     * @param title a string containing title of Alert Box.
     * @param contentText a string containing contentText of Alert Box.
     */
    public static void displayInfoAlert(String title, String contentText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    /**
     * Display Alert Box for Information.
     * @param title a string containing title of Alert Box.
     */
    public static void displayInfoAlert(String title) {
        displayInfoAlert(title, "");
    }

    /**
     * Display Alert Box for Confirmation.
     * @param title a string containing title of Alert Box.
     * @param contentText a string containing contentText of Alert Box.
     * @return
     */
    public static boolean displayConfirmAlert(String title, String contentText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION); // Confirmation alert.
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(contentText);

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            return true;
        }
        return false;
    }
}

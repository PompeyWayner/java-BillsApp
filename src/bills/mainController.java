package bills;

import bills.datamodel.Bill;
import bills.datamodel.BillData;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Controller for MainWindow.fxml
 *
 * @author Wayne Sandford
 * @version 08-08-19 02
 */
public class mainController {

    @FXML
    private AnchorPane mainPanel;
    @FXML
    private TableView<Bill> billsTable;
    @FXML
    private TableColumn<Bill, Double> amountColumn;
    @FXML
    private TableColumn<Bill, LocalDate> dateColumn;
    @FXML
    private Label nameLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label amountLabel;
    @FXML
    private Label fromAccountLabel;
    @FXML
    private Label startDateLabel;
    @FXML
    private Label changedDateLabel;
    @FXML
    private Label previousAmountLabel;
    @FXML
    private Label notesLabel;
    @FXML
    private Label totalLabel;
    @FXML
    private Label monthLabel;

    private BillData data; // class BillData variable

    /**
     * Initialise variables, set up bill list.
     */
    public void initialize() {

        showPersonDetails(null); // Clear person details.
        data = new BillData(); // Create new BillData instance.
        data.loadBills(); // Load the bills from file.
        data.setBills(data.getBillsMap().get(data.getCurrentMonth())); // Load List with default month to show

        System.out.println("mainController.itialise() - Displaying current state of map loaded from file in");

        billsTable.setItems(data.getBills()); // Set up TableView to list bills

        // Formatting of date for table column
        dateColumn.setCellFactory(tc -> new TableCell<>() {

            @Override
            protected void updateItem(LocalDate aDate, boolean empty) {
                super.updateItem(aDate, empty);
                if (empty) {
                    setText(null);
                } else {

                    setText(String.format(DateUtil.format(aDate)));
                }
            }
        });

        // Listen for selection changes and show the person details when changed.
        // With billsTable.getSelectionModel... we get the selectedItemProperty
        // of the bill table and add a listener to it.
        // Whenever the user selects a bill in the table, our lambda expression is executed.
        // We take the newly selected person and pass it to the showPersonDetails(...) method.
        billsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showPersonDetails(newValue));

        // Formatting for decimal amount value in table column.
        amountColumn.setCellFactory(tc -> new TableCell<Bill, Double>() {

            @Override
            protected void updateItem(Double anAmount, boolean empty) {
                super.updateItem(anAmount, empty);
                if (empty) {
                    setText(null);
                } else {
                    //setText(decimalFormat.format(anAmount));
                    setText(String.format("%.2f", anAmount.doubleValue()));
                }
            }
        });

        // Update current total of bill amounts and display in appropriate label
       // totalLabel.setText(Double.toString(data.calculateTotal())); // Set up current bills total.

        // Display current month that bills are for.
        monthLabel.setText(data.getCurrentMonth());
    }

    /**
     * Fills all text fields to show details about the bill.
     * If the specified bill is null, all text fields are cleared.
     *
     * @param bill the person or null.
     */
    private void showPersonDetails(Bill bill) {
        if (bill != null) {
            // Fill the labels with info from the bill object.
            nameLabel.setText(bill.getName());
            dateLabel.setText(DateUtil.format(bill.getDateOfPayment()));
            amountLabel.setText(Double.toString(bill.getAmount()));
            fromAccountLabel.setText(bill.getBankAccount());
            startDateLabel.setText(DateUtil.format(bill.getDateStarted()));
            changedDateLabel.setText(DateUtil.format(bill.getDateChanged()));
            previousAmountLabel.setText(Double.toString(bill.getPreviousAmount()));
            notesLabel.setText(bill.getNotes());

        } else {
            // Person is null, remove all the text.
            nameLabel.setText("");
            dateLabel.setText("");
            amountLabel.setText("");
            fromAccountLabel.setText("");
            startDateLabel.setText("");
            changedDateLabel.setText("");
            previousAmountLabel.setText("");
            notesLabel.setText("");
        }
    }

    /**
     * Event handler for add menu item.
     */
    @FXML
    public void showAddBillDialog() {
        Dialog<ButtonType> dialog = new Dialog<>(); // Create new dialog box instance.
        dialog.initOwner(mainPanel.getScene().getWindow());
        dialog.setTitle("Add New Bill");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("dialogDetails.fxml"));
        // Load dialog box.
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());

        } catch(IOException e) {
            System.out.println("Could not load the dialog");
            e.printStackTrace();
            return;
        }

        // Add buttons.
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait(); // Dialog box in modal mode.
        // If content is present and button OK is pressed.
        if(result.isPresent() && result.get() == ButtonType.OK) {
            // Get access to the controller associated with the bill dialog fxml file.
            BillController billController = fxmlLoader.getController(); // Menu dialog controller retrieve.
            Bill newBill = billController.getNewBill(); // Retrieve new bill object from dialog controller.

            // Test to see if newBill is valid - add new Bill object to current Month.
            if(newBill != null) {
                for(String eachMonth : data.getBillsMap().keySet()) {
                    System.out.println("mainController.showAddBillDialog()" + eachMonth);
                    if (newBill.getMonth().equals(eachMonth)) {
                        data.getBillsMap().get(eachMonth).add(newBill);
                        System.out.println("mainController.showAddBillDialog" + newBill.toString());
                    }
                }
                data.saveBills(); // Write updated list to file.

                // Update current total of bill amounts and display in appropriate label
                //totalLabel.setText(Double.toString(data.calculateTotal())); // Set up current bills total.

            } else { // Invalid object - null returned
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Invalid Name or Due date for Bill");
                alert.setHeaderText(null);
                alert.setContentText("Please try adding Bill details again");
                alert.showAndWait();
                return;
            }
        }
    }

    /**
     * Event handler for edit a bill currently displayed in the table.
     */
    @FXML
    public void showEditBillDialog() {

        // Will retreive the highlighed row/Bill object from the tableview.
        Bill selectedBill = billsTable.getSelectionModel().getSelectedItem();
        if (selectedBill == null) { // No bill object chosen, display error message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Bill Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select the Bill you want to edit");
            alert.showAndWait();
            return;
        }

        // Valid bill selected.
        Dialog<ButtonType> dialog = new Dialog<>(); // Create new dialog box instance.
        dialog.initOwner(mainPanel.getScene().getWindow()); // Assign it to be part of the main panel.
        dialog.setTitle("Edit Bill");
        FXMLLoader fxmlLoader = new FXMLLoader();
        // Assign fxml loader - same one as adding a contact.
        fxmlLoader.setLocation(getClass().getResource("dialogDetails.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Could not load the dialog");
            e.printStackTrace();
            return;
        }

        // Display buttons
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        //
        BillController billController = fxmlLoader.getController();
        // Populate the dialog box with the bill data of chosen object so it can be edited.
        billController.editBill(selectedBill);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if(billController.updateBill(selectedBill)) { // Validated updated bill
                {
                    data.saveBills();
                    showPersonDetails(selectedBill);
                    totalLabel.setText(Double.toString(data.calculateTotal())); // Update bills total.
                }
            } else { // Invalid updated bill
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Invalid Name or Due Date for edited Bill");
                alert.setHeaderText(null);
                alert.setContentText("Please try re-editing Bill details again");
                alert.showAndWait();
                return;
            }
        }
    }

    /**
     * Event handler for deleting a Bill.
     */
    @FXML
    public void deleteBill() {
        Bill selectedBill = billsTable.getSelectionModel().getSelectedItem();
        if(selectedBill == null) { // No contacted selected
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Bill Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select the Bill you want to delete.");
            alert.showAndWait();
            return;
        }

        // Suitable record to delete.
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION); // Confirmation alert.
        alert.setTitle("Delete Bill");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure yo want to delete selected bill: " +
                selectedBill.getName()); // Final check if bill object to be deleted.

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) { // Delete
            data.deleteBill(selectedBill); // Delete bill object from list.
            data.saveBills(); // Write updated list to file
            totalLabel.setText(Double.toString(data.calculateTotal())); // Update bills total.
        }
    }

    /**
     * Event handler for about menu option.
     */
    @FXML
    public void aboutHandler() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION); // Information alert.
        alert.setTitle("About This Application");
        alert.setHeaderText(null);
        alert.setContentText("Written by Wayne Sandford\n2019");
        Optional<ButtonType> result = alert.showAndWait();
        data.displayMap(); // Temporary for debugging
    }

    /**
     * Event handler foe exit menu option
     */
    @FXML
    public void handleExit() {
        Platform.exit();
    }

    @FXML
    public void addNewMonthDialog() {
        Dialog<ButtonType> dialog = new Dialog<>(); // Create new dialog box instance.
        dialog.initOwner(mainPanel.getScene().getWindow());
        dialog.setTitle("Add New Month");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("newMonthDialog.fxml"));
        // Load dialog box.
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());

        } catch(IOException e) {
            System.out.println("Could not load the dialog");
            e.printStackTrace();
            return;
        }

        // Add buttons.
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait(); // Dialog box in modal mode.
        // If content is present and button OK is pressed.
        if(result.isPresent() && result.get() == ButtonType.OK) {
            // Get access to the controller associated with the bill dialog fxml file.
            NewMonthController newMonthController = fxmlLoader.getController(); // New month dialog controller retrieve.
//          // *** Test to see if month does not already exist for this year ***
            data.setCurrentMonth(newMonthController.getNewMonth());
            // Display current month that bills are for.
            monthLabel.setText(data.getCurrentMonth());

            // Test entered month is first month added to map.
            if(data.getBillsMap().isEmpty()) {
                System.out.println("mainController.addNewMonthDialog() map is currently empty - first month added");
                data.getBillsMap().put(data.getCurrentMonth(), FXCollections.observableArrayList());
            } else {
                if (data.getBillsMap().containsKey(data.getCurrentMonth())) { // Month already exists.
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("The month entered already exists for this year!!");
                    alert.setHeaderText(null);
                    alert.setContentText("Please try adding another month again.");
                    alert.showAndWait();
                    return;
                } else {
                    System.out.println("mainController.addNewMonthDialog() new month added to currently populated map");
                    data.getBillsMap().put(data.getCurrentMonth(), FXCollections.observableArrayList());
                }
            }

            showPersonDetails(null); // Clear persons details
            // ** load bills for that month in tableview ***
            data.setBills(data.getBillsMap().get(data.getCurrentMonth()));
            billsTable.setItems(data.getBills());
        }
    }

    @FXML
    public void changeMonthDialog() {
        Dialog<ButtonType> dialog = new Dialog<>(); // Create new dialog box instance.
        dialog.initOwner(mainPanel.getScene().getWindow());
        dialog.setTitle("Change Existing Month");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("newMonthDialog.fxml"));
        // Load dialog box.
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());

        } catch(IOException e) {
            System.out.println("Could not load the dialog");
            e.printStackTrace();
            return;
        }

        // Add buttons.
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait(); // Dialog box in modal mode.
        // If content is present and button OK is pressed.
        if(result.isPresent() && result.get() == ButtonType.OK) {
            // Get access to the controller associated with the bill dialog fxml file.
            NewMonthController newMonthController = fxmlLoader.getController(); // New month dialog controller retrieve.
//          // *** Test to see if month does not already exist for this year ***
            data.setCurrentMonth(newMonthController.getNewMonth());
            // Test to see if entered month is already exists
            if (data.getBillsMap().containsKey(data.getCurrentMonth())) { // Month already exists - change to that month.
                showPersonDetails(null);
                // ** load bills for that month in tableview ***
                data.setBills(data.getBillsMap().get(data.getCurrentMonth()));
                billsTable.setItems(data.getBills());
            } else { // Month not in map - alert.
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("The month entered does not exist for this year!!");
                alert.setHeaderText(null);
                alert.setContentText("Please try changing month again.");
                alert.showAndWait();
                return;
            }

            // Display current month that bills are for.
            monthLabel.setText(data.getCurrentMonth());
        }
    }
}

package bills;

import bills.datamodel.Bill;
import bills.datamodel.BillData;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.*;

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
    @FXML
    private ComboBox<String> monthChoice;
    @FXML
    private ComboBox<String> addMonthChoice;
    @FXML
    private ComboBox<String> deleteMonthChoice;

    private BillData data; // class BillData variable.

    private final String[] monthsOfYear = {"January", "February", "March", "April",
            "May", "June", "July", "August", "September", "October", "November", "December"};

    private ObservableList<String> options = FXCollections.observableArrayList(); // Change month list for ComboBox.
    private ObservableList<String> addMonthOptions = FXCollections.observableArrayList(); // Add month list for ComboBox.
    private ObservableList<String> deleteMonthOptions = FXCollections.observableArrayList(); // Delete another month list for ComboBox.

    /**
     * Initialise variables, set up bill list.
     */
    public void initialize() {

        showPersonDetails(null); // Clear person details.
        data = new BillData(); // Create new BillData instance.
        data.loadBills("billsFile.xml"); // Load default file with bills from file.
        data.setBills(data.getBillsMap().get(data.getCurrentMonth())); // Load List with default month to show
        System.out.println("mainController.initialise()");
        billsTable.setItems(data.getBills()); // Set up TableView to list bills of default month.

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
        billsTable.getSelectionModel().selectFirst();

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
        if(!data.getBillsMap().isEmpty()) {
            totalLabel.setText(String.format("%.2f", data.calculateTotal())); // Update bill total.
        }
        monthLabel.setText(data.getCurrentMonth()); // Update month label.

        // Set up ComboBox data.
        updateAddComboBox();
        updateChangeAnotherMonthComboBox();
        updateDeleteAnotherMonthComboBox();
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
            amountLabel.setText(String.format("%.2f",bill.getAmount()));
            fromAccountLabel.setText(bill.getBankAccount());
            startDateLabel.setText(DateUtil.format(bill.getDateStarted()));
            changedDateLabel.setText(DateUtil.format(bill.getDateChanged()));
            //previousAmountLabel.setText(Double.toString(bill.getPreviousAmount()));
            previousAmountLabel.setText(String.format("%.2f",bill.getPreviousAmount()));
            notesLabel.setText(bill.getNotes());
            totalLabel.setText(String.format("%.2f", data.calculateTotal())); // Update bill total.
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
    public boolean showAddBillDialog() {
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
            return false;
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
                    if (newBill.getMonth().equals(eachMonth)) {
                        data.getBillsMap().get(eachMonth).add(newBill);
                    }
                }
                data.saveBills(); // Write updated list to file.
                // Update current total of bill amounts and display in appropriate label
                totalLabel.setText(String.format("%.2f", data.calculateTotal()));
                return true;

            } else { // Invalid object - null returned
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Invalid Name or Due date for Bill");
                alert.setHeaderText(null);
                alert.setContentText("Please try adding Bill details again");
                alert.showAndWait();
            }
        }
        return false;
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
                    totalLabel.setText(String.format("%.2f", data.calculateTotal()));
                }
            } else { // Invalid updated bill
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Invalid Name or Due Date for edited Bill");
                alert.setHeaderText(null);
                alert.setContentText("Please try re-editing Bill details again");
                alert.showAndWait();
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
            totalLabel.setText(String.format("%.2f", data.calculateTotal()));
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
        data.displayMap(); // Temporary for debugging.
        System.out.println(data.getMonthsSet().toString());
    }

    /**
     * Event handler foe exit menu option
     */
    @FXML
    public void handleExit() {
        Platform.exit();
    }

    /**
     * Event handler for adding a new month.
     */
    @FXML
    public void addNewMonthDialog() {
        System.out.println("mainController.addNewMonthDialog - just entered!!!");
        String month = addMonthChoice.getValue(); // Retrieve chosen month.
        data.getBillsMap().put(month, FXCollections.observableArrayList()); // Add month to Map
        String tempMonth = data.getCurrentMonth(); // Store last month before updating to new current month;
        data.setCurrentMonth(month); // Set currentMonth to entered month

        // New month added therefore a valid Bill object needs to be added.
        if(this.showAddBillDialog()) {
            data.getMonthsSet().add(month); // Add new month to monthsSet.
            showPersonDetails(null); // Clear persons details
            data.setBills(data.getBillsMap().get(month)); // Load bill list with correct month bills.
            billsTable.setItems(data.getBills()); // Set up TableView to list bills
            billsTable.getSelectionModel().selectFirst();
            monthLabel.setText(month); // Update month label.
            totalLabel.setText(String.format("%.2f", data.calculateTotal())); // Update bill total.
            addMonthChoice.getItems().remove(data.getCurrentMonth()); // Update add month ComboBox on menu.
            updateChangeAnotherMonthComboBox();
            updateDeleteAnotherMonthComboBox();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(" Invalid first Bill entry.");
            alert.setHeaderText(null);
            alert.setContentText("The month " + month + " could not be added. Please try adding new month again.");
            alert.showAndWait();
            data.getBillsMap().remove(month);
            data.setCurrentMonth(tempMonth);
        }
    }

    /**
     * Event handler for changing to another month.
     */
    @FXML
    public void changeMonthDialog() {
        System.out.println("changeMonthDialog - just entered!!!");
        String month = monthChoice.getValue(); // Retrieve chosen month.

        // Test to see if entered month is already exists
        if (data.getBillsMap().containsKey(month)) { // Month already exists - change to that month.
            showPersonDetails(null);  // Clear persons details.
            data.setBills(data.getBillsMap().get(month)); // Load bill list with correct month bills.
            billsTable.setItems(data.getBills()); // Display bills in TableView.
            billsTable.getSelectionModel().selectFirst();
            data.setCurrentMonth(month); // Set month to currentMonth.
            monthLabel.setText(month); // Update month label.
            totalLabel.setText(String.format("%.2f", data.calculateTotal())); // Update bill total.
        }
    }

    /**
     * Event handler for deleting current month.
     */
    @FXML
    public void deleteCurrentMonth() {

        if (data.getCurrentMonth() != null) {
            // Suitable record to delete.
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION); // Confirmation alert.
            alert.setTitle("Delete Current Month");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure yo want to delete the current month " + data.getCurrentMonth() +
                    " and all associated bills with this month?"); // Final check if month to be deleted.

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) { // Delete
                data.getBillsMap().remove(data.getCurrentMonth()); // Remove map entry
                data.getMonthsSet().remove(data.getCurrentMonth()); // Remove current month from monthsSet.
                showPersonDetails(null); // Clear person details.
                billsTable.setItems(null); // Clear TableView
                monthLabel.setText(null); // Clear month label.
                data.saveBills(); // Write updated list to file
                totalLabel.setText("0.00"); // Set up current bills total.
                // Update ComboBoxes.
                monthChoice.getItems().remove(data.getCurrentMonth());
                deleteMonthChoice.getItems().remove(data.getCurrentMonth());
                updateAddComboBox();
                data.setCurrentMonth(null);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("There is no current month");
            alert.setHeaderText(null);
            alert.setContentText("Choose <Delete Another Month> option to remove another Month");
            alert.showAndWait();
            return;
        }
    }

    /**
     * Event handler for deleting another month.
     */
    @FXML
    public void deleteAnotherMonth() {
        System.out.println("mainController.deleteAnotherMonth - just entered!!!");
        String month = deleteMonthChoice.getValue(); // Retrieve chosen month.

        // Confirm that month to be deleted.
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION); // Confirmation alert.
        alert.setTitle("Delete Another Month");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure yo want to delete the month " + month +
                " and all associated bills with this month?"); // Final check if month to be deleted.
        Optional<ButtonType> answer = alert.showAndWait();
        if (answer.isPresent() && answer.get() == ButtonType.OK) { // Delete OK confirmed.
            System.out.println("Alert Delete confirmed");
            data.getBillsMap().remove(month); // Remove map entry.
            data.getMonthsSet().remove(month); // Remove selected month from monthsSet.
            billsTable.setItems(null); // Clear TableView
            showPersonDetails(null);  // Clear persons details.
            totalLabel.setText("0.00"); // Set up current bills total.
            monthLabel.setText(null); // Update month label.
            monthChoice.getItems().remove((month)); // Update change month ComboBox on menu;
            updateAddComboBox();
        }
    }

    /**
     * Helper method.
     * Sorts the current used months into correct calendar months order.
     * This has been created due to the fact the current used months in stored in a set.
     * @return sortedMonthList a List of String containing the current used months.
     */
    private List<String> sortedMonths(Set<String> monthsSet) {
        List<String> sortedMonthsList = new ArrayList<>();

        for(String eachMonth : this.monthsOfYear) {
            for (String eachCurrentMonth : monthsSet) {
                if(eachCurrentMonth.equals(eachMonth)) {
                    sortedMonthsList.add(eachCurrentMonth);
                    break;
                }
            }
        }
        return sortedMonthsList;
    }

    /**
     * Update the months ComboBox that can be added in correct order.
     */
    private void updateAddComboBox() {
        addMonthChoice.getItems().clear();
        addMonthOptions.clear();
        for(String eachMonth : this.monthsOfYear) { // Not in current months list.
            if(!data.getMonthsSet().contains(eachMonth)) {
                addMonthOptions.add(eachMonth);
            }

            options.addAll(this.sortedMonths(data.getMonthsSet()));

        }
        addMonthChoice.getItems().addAll(addMonthOptions); // Add month to ComboBox.
        if(!addMonthOptions.isEmpty()) {
            addMonthChoice.setValue(addMonthOptions.get(0)); // Display first month in list as a default.
        }
    }

    /**
     * Update the delete another month ComboBox.
     */
    private void updateDeleteAnotherMonthComboBox() {
        deleteMonthChoice.getItems().clear();
        deleteMonthOptions.clear();
        deleteMonthOptions.addAll(this.sortedMonths(data.getMonthsSet())); // Delete current used months from set to the to list.
        deleteMonthChoice.getItems().addAll(deleteMonthOptions); // Add month to ComboBox.
        if(!deleteMonthOptions.isEmpty()) {
            deleteMonthChoice.setValue(deleteMonthOptions.get(0)); // Display first month in list as a default.
        }
    }

    /**
     * Update the change to another month ComboBox.
     */
    private void updateChangeAnotherMonthComboBox() {
        monthChoice.getItems().clear();
        options.clear();
        options.addAll(this.sortedMonths(data.getMonthsSet()));
        monthChoice.getItems().addAll(options); // Add current months to change month ComboBox.
        if(!options.isEmpty()) {
            monthChoice.setValue(options.get(0)); // Display first month in list as a default.
        }
    }

    /**
     * Event handler that handles selecting a file to open.
     */
    @FXML
    public void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select location of file to open");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML files", "*.XML")
        );
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            System.out.println("File selected: " + selectedFile.getName()); // *** delete when tested ***
            // Load data from selected file and display
            data.loadBills(selectedFile.getAbsolutePath());
            data.setBills(data.getBillsMap().get(data.getCurrentMonth())); // Load List with default month to show
            billsTable.setItems(data.getBills()); // Set up TableView to list bills of default month.
            totalLabel.setText(String.format("%.2f", data.calculateTotal())); // Update bill total.
            monthLabel.setText(data.getCurrentMonth()); // Update month label.
            updateAddComboBox();
            updateChangeAnotherMonthComboBox();
            updateDeleteAnotherMonthComboBox();
        }
        else {
            System.out.println("File selection cancelled.");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("File selection Cancelled");
            alert.setHeaderText(null);
            alert.setContentText("No File Opened");
            alert.showAndWait();

        }
    }

    /**
     * Event handler for creating a new file.
     */
    @FXML
    public void newFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose location To Save File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML files", "*.XML")
        );
        File selectedFile = null;
        while(selectedFile== null){
            selectedFile = fileChooser.showSaveDialog(null);
        }

        File file = new File(selectedFile.getAbsolutePath());

        System.out.println("New File name  is " + selectedFile.getAbsolutePath()); // *** remove after testing ***
        PrintWriter outFile = null;
        try {
            outFile = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        outFile.close();

        // Clear all bills data - if any.
        data.getBillsMap().clear();
        data.getMonthsSet().clear();
        billsTable.setItems(null); // Clear TableView

        showPersonDetails(null);  // Clear persons details.
        totalLabel.setText("0.00"); // Set up current bills total.
        monthLabel.setText(null); // Update month label.
        updateAddComboBox();
        updateChangeAnotherMonthComboBox();
        updateDeleteAnotherMonthComboBox();
    }
}

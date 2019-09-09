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
import java.io.IOException;
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
    private Label nameLabel, dateLabel, amountLabel, fromAccountLabel, startDateLabel, changedDateLabel;
    @FXML
    private Label previousAmountLabel, notesLabel, totalLabel, monthLabel, yearLabel;
    @FXML
    private ComboBox<String> monthChoice, addMonthChoice, deleteMonthChoice, newYearChoice;
    @FXML
    private Menu billMenu;

    private BillData data; // class BillData variable.
    private final String[] monthsOfYear = {"January", "February", "March", "April",
            "May", "June", "July", "August", "September", "October", "November", "December"};
    private final String[] years = {"2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018",
                                    "2019", "2020", "2021", "2022", "2023", "2024", "2025"};

    private ObservableList<String> options = FXCollections.observableArrayList(); // Change month list for ComboBox.
    private ObservableList<String> addMonthOptions = FXCollections.observableArrayList(); // Add month list for ComboBox.
    private ObservableList<String> deleteMonthOptions = FXCollections.observableArrayList(); // Delete another month list for ComboBox.
    private ObservableList<String> newYearOptions = FXCollections.observableArrayList(); // New Year File available Years.

    /**
     * Initialise variables, set up bill list.
     */
    public void initialize() {

        showPersonDetails(null); // Clear person details.
        data = new BillData(); // Create new BillData instance.

        // Load in default file.
        data.setDefaultFileName(TextFileOperation.readDefaultFile()); // Retrieve default filename.
        data.setCurrentYearFileName(data.getDefaultFileName());
        if(data.getCurrentYearFileName() != null) { // File exists and something has been returned.
            data.loadBills(data.getDefaultFileName()); // Load default file with bills from file.
        } else { // No default file, create one.
            File defaultTextFile = new File("C:\\Users\\wsand\\IdeaProjects\\BillsApp\\default.txt");
            TextFileOperation.writeDefaultFile(defaultTextFile.getName(), data.getDefaultFileName());
            //data.loadBills(data.getDefaultFileName()); // Load default file with bills from file.
            data.saveBills(data.getDefaultFileName()); // Save empty file
        }

        data.setBills(data.getBillsMap().get(data.getCurrentMonth())); // Load List with default month to show.
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
        yearLabel.setText(data.getCurrentYear()); // Update year label.

        // Set up ComboBox data.
        updateAddComboBox();
        updateChangeAnotherMonthComboBox();
        updateDeleteAnotherMonthComboBox();
        updateNewYearCombo();


        if(data.getBillsMap().isEmpty()) { // Disable Bill Menu if empty file.
            billMenu.setDisable(true);
        }
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
//                for(String eachMonth : data.getBillsMap().keySet()) {
////                    if (newBill.getMonth().equals(eachMonth)) {
////                        data.getBillsMap().get(eachMonth).add(newBill);
////                    }
////                }
                data.getBillsMap().get(data.getCurrentMonth()).add(newBill);
                data.saveBills(data.getDefaultFileName()); // Write updated list to file.
                if(data.getBillsMap().size() > 1) {
                    totalLabel.setText(String.format("%.2f", data.calculateTotal())); // Update bill month total.
                }
                return true;

            } else { // Invalid object - null returned
                InfoAlert.displayInfoAlert("Invalid Name or Due date for Bill", "Please try adding Bill details again");
            }
        }
        return false;
    }

    /**
     * Event handler for edit a bill currently displayed in the table.
     */
    @FXML
    public void showEditBillDialog() {

        // Retreive selected bill from TableView.
        Bill selectedBill = billsTable.getSelectionModel().getSelectedItem();
        if (selectedBill == null) { // No bill object chosen, display error message.
            InfoAlert.displayInfoAlert("No Bill Selected", "Please select the Bill you want to edit");
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

        BillController billController = fxmlLoader.getController();
        // Populate the dialog box with the bill data of chosen object so it can be edited.
        billController.editBill(selectedBill);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if(billController.updateBill(selectedBill)) { // Validated updated bill
                {
                    data.saveBills(data.getDefaultFileName());
                    showPersonDetails(selectedBill);
                    totalLabel.setText(String.format("%.2f", data.calculateTotal()));
                }
            } else { // Invalid updated bill
                InfoAlert.displayInfoAlert("Invalid Name or Due Date for edited Bill", "Please try re-editing Bill details again");
            }
        }
    }

    /**
     * Event handler for deleting a Bill.
     */
    @FXML
    public void deleteBill() {
        Bill selectedBill = billsTable.getSelectionModel().getSelectedItem();
        if(selectedBill == null) { // No Bill selected.
            InfoAlert.displayInfoAlert("No Bill Selected", "Please select the Bill you want to delete");
        }

        // Confirm that Bill should be removed.
        if(InfoAlert.displayConfirmAlert("Delete Bill",
                "Are you sure you want to delete selected bill> " + selectedBill.getName() + "?")) {
            data.deleteBill(selectedBill); // Delete bill object from list.
            data.saveBills(data.getDefaultFileName()); // Write updated list to file
            totalLabel.setText(String.format("%.2f", data.calculateTotal()));
        }
    }

    /**
      * Event handler for about menu option.
     */
    @FXML
    public void aboutHandler() {
        InfoAlert.displayInfoAlert("About This Application", "Written by Wayne Sandford\n2019");
        data.displayMap(); // Temporary for debugging.
        System.out.println(data.getMonthsSet().toString()); // Temporary for debugging.
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
        billMenu.setDisable(false);
        String month = addMonthChoice.getValue(); // Retrieve chosen month.
        data.getBillsMap().put(month, FXCollections.observableArrayList()); // Add month to Map
        String tempMonth = data.getCurrentMonth(); // Store last month before updating to new current month;
        data.setCurrentMonth(month); // Set currentMonth to entered month
        billMenu.setDisable(false);

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
            InfoAlert.displayInfoAlert("Invalid first Bill entry.",
                    "The month " + month + " could not be added. Please try adding new month again.");
            data.getBillsMap().remove(month);
            data.setCurrentMonth(tempMonth);
        }
    }

    /**
     * Event handler for changing to another month.
     */
    @FXML
    public void changeMonthDialog() {
        billMenu.setDisable(false);
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
            if(InfoAlert.displayConfirmAlert("Delete Current Month",
                    "Are you sure yo want to delete the current month " + data.getCurrentMonth() +
                    " and all associated bills with this month?" )) {
                data.getBillsMap().remove(data.getCurrentMonth()); // Remove map entry
                data.getMonthsSet().remove(data.getCurrentMonth()); // Remove current month from monthsSet.
//                showPersonDetails(null); // Clear person details.
//                billsTable.setItems(null); // Clear TableView
//                monthLabel.setText(null); // Clear month label.
//                totalLabel.setText("0.00"); // Set up current bills total.
                clearDataFromMainWindow();
                data.saveBills(data.getDefaultFileName()); // Write updated list to file
                // Update ComboBoxes.
                monthChoice.getItems().remove(data.getCurrentMonth());
                deleteMonthChoice.getItems().remove(data.getCurrentMonth());
                data.getBills().clear(); // Remove all Bill entries from list.
                updateAddComboBox();
                updateDeleteAnotherMonthComboBox();
                data.setCurrentMonth(null);
                if(data.getBillsMap().isEmpty()) {
                    billMenu.setDisable(true);
                }
                for(Bill bill : data.getBills()) {
                    System.out.println("Current bills in list" + bill.toString());
                }
                if(data.getBills().isEmpty()) {
                    billMenu.setDisable(true);
                }
            }
        } else {
            InfoAlert.displayInfoAlert("There is no current month.",
                    "Choose <Delete Another Month> option to remove another Month.");
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
        if(InfoAlert.displayConfirmAlert("Delete Another Month",
                "Are you sure yo want to delete the month " + month +
               " and all associated bills with this month?")) {
            System.out.println("Alert Delete confirmed");
            data.getBillsMap().remove(month); // Remove map entry.
            data.getMonthsSet().remove(month); // Remove selected month from monthsSet.
            data.saveBills(data.getDefaultFileName()); // Write updated list to file
            monthChoice.getItems().remove((month)); // Update change month ComboBox on menu;
            updateAddComboBox();
            updateDeleteAnotherMonthComboBox();
            if(data.getBillsMap().isEmpty()) {
                billMenu.setDisable(true);
            }
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
        billMenu.setDisable(false);
        if (selectedFile != null) {
            // Load data from selected file and display
            data.loadBills(selectedFile.getAbsolutePath());
            data.setBills(data.getBillsMap().get(data.getCurrentMonth())); // Load List with default month to show
            billsTable.setItems(data.getBills()); // Set up TableView to list bills of default month.
            if(!data.getBillsMap().isEmpty()) {
                totalLabel.setText(String.format("%.2f", data.calculateTotal())); // Update bill total.
            }
            //totalLabel.setText(String.format("%.2f", data.calculateTotal())); // Update bill total.
            monthLabel.setText(data.getCurrentMonth()); // Update month label.
            yearLabel.setText(data.getCurrentYear()); // Update year label.
            data.setCurrentYearFileName(selectedFile.getAbsolutePath());
            updateAddComboBox();
            updateChangeAnotherMonthComboBox();
            updateDeleteAnotherMonthComboBox();
            // Prompt to make new open file as default file.
            if(isNewDefault(selectedFile)) {
                data.setDefaultFileName(selectedFile.getAbsolutePath()); // Set default filename.
                TextFileOperation.writeDefaultFile("C:\\Users\\wsand\\IdeaProjects\\BillsApp\\default.txt", data.getDefaultFileName());
            }
        }
        else {
            InfoAlert.displayInfoAlert("File selection Cancelled.", "No File Opened.");
        }
    }

    /**
     * Event handler for creating a new file.
     */
    @FXML
    public void newFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose location and filename for New File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML files", "*.XML")
        );
        File selectedFile = null;
        while(selectedFile== null){
            selectedFile = fileChooser.showSaveDialog(null);
        }
        data.setCurrentYearFileName(selectedFile.getAbsolutePath());
        //File file = new File(selectedFile.getAbsolutePath());
        // Prompt to make new open file as default file.
        if(isNewDefault(selectedFile)) {
            data.setDefaultFileName(selectedFile.getAbsolutePath()); // Set default filename.
            TextFileOperation.writeDefaultFile("C:\\Users\\wsand\\IdeaProjects\\BillsApp\\default.txt", data.getDefaultFileName());
        }
        // Clear all bills data - if any.
        data.getBillsMap().clear();
        data.getMonthsSet().clear();
        clearDataFromMainWindow();
        yearLabel.setText(data.getCurrentYear()); //
        data.saveBills(data.getCurrentYearFileName()); // Write updated list to file
        updateAddComboBox();
        updateChangeAnotherMonthComboBox();
        updateDeleteAnotherMonthComboBox();
        if(data.getBillsMap().isEmpty()) {
            billMenu.setDisable(true);
            InfoAlert.displayInfoAlert("File Empty", "There are no months. Please add a month.");
        }
    }

    /**
     * Event handler that displays the current filename as the default for when starting program.
     */
    @FXML
    public void currentDefaultFile() {
        InfoAlert.displayInfoAlert("Default File Path and Name.", "The default file is " + data.getDefaultFileName());
    }

    /**
     * Event handler that changes the default filename.
     */
    @FXML
    public void changeDefaultFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose File to be the default on startup");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML files", "*.XML")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            // Prompt to file as default file.
            if(isNewDefault(selectedFile)) {
                data.setDefaultFileName(selectedFile.getAbsolutePath()); // Set default filename.
                TextFileOperation.writeDefaultFile("C:\\Users\\wsand\\IdeaProjects\\BillsApp\\default.txt", data.getDefaultFileName());
            }
        }
        else {
            InfoAlert.displayInfoAlert("Default File not changed.");

        }
    }

    /**
     * Helper method to determine if file is to be made the default.
     * @param file a file object.
     * @return true if file to be made default otherwise false.
     */
    private boolean isNewDefault(File file) {
        // Prompt to make new open file as default file.
        return InfoAlert.displayConfirmAlert("Make as Default file",
                "Do you want this file " + file.getName() + " as the default file on start up?");
    }

    /**
     * Event handler that saves the current file.
     */
    public void saveFile() {
        data.saveBills(data.getDefaultFileName());
        InfoAlert.displayInfoAlert("Save File", data.getDefaultFileName() + " has been saved.");
    }

    public void saveAsFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File as...");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML files", "*.XML")
        );
        File selectedFile = null;
        while(selectedFile== null){
            selectedFile = fileChooser.showSaveDialog(null);
        }

        File savedAsfile = new File(selectedFile.getAbsolutePath());

        if (savedAsfile != null) {

            data.saveBills(savedAsfile.getAbsolutePath());
            // Prompt to file as default file.
            if(isNewDefault(savedAsfile)) {
                data.setDefaultFileName(savedAsfile.getAbsolutePath()); // Set default filename.
                TextFileOperation.writeDefaultFile("C:\\Users\\wsand\\IdeaProjects\\BillsApp\\default.txt", data.getDefaultFileName());
            }
        }
        else {
            InfoAlert.displayInfoAlert("Default File not changed.");
        }
    }

    private void clearDataFromMainWindow() {
        showPersonDetails(null); // Clear person details.
        billsTable.setItems(null); // Clear TableView
        monthLabel.setText(null); // Clear month label.
        totalLabel.setText("0.00"); // Set up current bills total.
        yearLabel.setText(null); // Clear year label.
    }

    public void currentYearFile() {
        InfoAlert.displayInfoAlert("Current Year File Open", data.getCurrentYearFileName());
    }

    public void updateNewYearCombo() {
        newYearChoice.getItems().clear();
        newYearOptions.clear();
        newYearOptions.addAll(this.years);
        newYearChoice.getItems().addAll(newYearOptions);

    }

    @FXML
    public void chooseNewYear () {
        String yearChosen = newYearChoice.getValue(); // Retrieve chosen month.
        data.setCurrentYear(yearChosen);
        newFileChooser();
    }
}

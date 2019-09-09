package bills;

import bills.datamodel.Bill;
import bills.datamodel.BillData;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;

/**
 * class BillController - controller for dialogDetails.fxml UI.
 *
 * @author Wayne Sandford
 * @version 07-07-19
 */
public class BillController {

    // Instance variables
    @FXML
    private TextField nameField;
    @FXML
    private DatePicker dateField;
    @FXML
    private TextField amountField;
    @FXML
    private ComboBox<String> bankAccount; // Value injected by FXMLLoader
    @FXML
    private TextArea notesField;


    private Bill currentBill = new Bill(); // Will hold details from dialog box entry
    private double previousAmount;

    public void initialize() {
        dateField.valueProperty().bindBidirectional(currentBill.dateOfPaymentProperty());
    }

    /**
     * Creates a new Bill object from the information entered from the add dialog box.
     * If new Bill details fails validation then a null bill object is returned otherwise
     * valid bill object returned.
     * @return newBill a Bill object.
     */
    public Bill getNewBill() {

        String name;
        LocalDate dueDate;
        double amount;
        String chosenAccount;
        String notes;

        // Retrieve information from dialog text fields and validate each field.
        if (isValidStringField(nameField.getText())) {
            name = nameField.getText();
        } else { // Invalid bill object - needs bill name
            return null;
        }

        if(dateField.getValue() == null) {
            return null;
        } else {
            dueDate = dateField.getValue();
        }

        if (isValidStringField(notesField.getText())) {
            notes = notesField.getText();
        } else {
            notes = " ";
        }

        if (isValidAmountField(amountField.getText())) {
            amount = Double.parseDouble(amountField.getText());
        } else {
            amount = 0.00;
        }

        if ("Wayne's Account".equals(bankAccount.getValue())) {
            chosenAccount = "Wayne";
        } else if ("Nicki's Account".equals(bankAccount.getValue())) {
            chosenAccount = "Nicki";
        } else {
            chosenAccount = "None";
        }

        // Create new Bill object from entered ino
        LocalDate todaysDate = LocalDate.now();
        return new Bill(name, dueDate, amount, chosenAccount, notes, todaysDate, todaysDate, 0.00,
                BillData.currentMonth, BillData.currentYear);
    }

    /**
     * Sets the dialog fields with the Contact arguments filed values.
     * The bill passed to the dialog box fields will currently have valid
     * fields for all of the bill object.
     *
     * @param bill object
     */
    public void editBill(Bill bill) {
        nameField.setText(bill.getName());
        dateField.setValue(bill.getDateOfPayment());
        //amountField.setText(Double.toString(bill.getAmount()));
        amountField.setText(String.format("%.2f",bill.getAmount()));
        bankAccount.setValue(bill.getBankAccount());
        notesField.setText(bill.getNotes());
        previousAmount = bill.getAmount(); // Current amount stored in case changed
    }

    /**
     * Updates the Bill objects fields with the current values of the dialog box values.
     * Validates each updated field.
     *
     * @param bill a Bill object to use for updating.
     */
    public boolean updateBill(Bill bill) {

        // Retrieve information from dialog text fields and validate each field.
        if (isValidStringField(nameField.getText())) {
            bill.setName(nameField.getText());
        } else {
            return false; // Invalid updated bill name.
        }

        bill.setDateOfPayment(dateField.getValue());

        if (isValidStringField(notesField.getText())) {
            bill.setNotes(notesField.getText());
        } else {
            bill.setNotes(" ");
        }

        if (isValidAmountField(amountField.getText())) {
            bill.setAmount(Double.parseDouble(amountField.getText()));
        } else {
            bill.setAmount(0.0);
        }
        bill.setPreviousAmount(previousAmount);

        String chosenAccount;
        if ("Wayne's Account".equals(bankAccount.getValue())) {
            chosenAccount = "Wayne";
        } else if ("Nicki's Account".equals(bankAccount.getValue())) {
            chosenAccount = "Nicki";
        } else {
            chosenAccount = "None";
        }
        bill.setBankAccount(chosenAccount);

        LocalDate todaysDate = LocalDate.now();
        bill.setDateChanged(todaysDate);
        bill.setPreviousAmount(this.previousAmount);

        return true; // Valid updated bill.
    }

    /**
     * Tests to see if the textfield has valid input.
     *
     * @param aField a String to be tested.
     * @return true is String is not null or empty, otherwise false.
     */
    private boolean isValidStringField(String aField) {

        // Test for null or empty String.
        return aField != null && !aField.equals("");
    }

    /**
     * Tests to see if amount field is a valid double.
     *
     * @param anAmount a String to be tested for valid double value.
     * @return true if a valid positive double otherwise false.
     */
    private boolean isValidAmountField(String anAmount) {

        // Test to see if an actual number
        try {
            double a = Double.parseDouble(anAmount);
            if(a > 0.0) { // Test for positive amount
                return true;
            }
        } catch (NumberFormatException nfe) {
            return false;
        }
        return false;
    }
}

package bills.datamodel;

import javafx.beans.property.*;
import java.time.LocalDate;

/**
 * class Bill holds the details of a household bill.
 *
 * @author Wayne Sandford
 * @version 03-07-19 02
 */
public class Bill {

    // Instance variables - SimpleStringProperty types used for binding requirements in JavaFX
    private SimpleStringProperty name = new SimpleStringProperty("");
    private ObjectProperty<LocalDate> dateOfPayment = new SimpleObjectProperty<>();
    private SimpleDoubleProperty amount = new SimpleDoubleProperty(0.0);
    private SimpleStringProperty bankAccount = new SimpleStringProperty();
    private SimpleStringProperty notes = new SimpleStringProperty("");
    private final ObjectProperty<LocalDate> dateStarted = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> dateChanged = new SimpleObjectProperty<>();
    private SimpleDoubleProperty previousAmount = new SimpleDoubleProperty();

    // Constructors.
    public Bill() {
    }

    public Bill(String name, LocalDate dateOfPayment, double amount, String account, String notes,
                LocalDate dStarted, LocalDate dChanged, double pAmount) {
        this.name.set(name);
        this.dateOfPayment.set(dateOfPayment);
        this.amount.set(amount);
        this.bankAccount.set(account);
        this.notes.set(notes);
        this.dateStarted.set(dStarted);
        this.dateChanged.set(dChanged);
        this.previousAmount.set(pAmount);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public double getAmount() {
        return amount.get();
    }

    public SimpleDoubleProperty amountProperty() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount.set(amount);
    }

    public LocalDate getDateOfPayment() {
        return dateOfPayment.get();
    }

    public ObjectProperty<LocalDate> dateOfPaymentProperty() {
        return dateOfPayment;
    }

    public void setDateOfPayment(LocalDate dateOfPayment) {
        this.dateOfPayment.set(dateOfPayment);
    }

    public String getNotes() {
        return notes.get();
    }

    public SimpleStringProperty notesProperty() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes.set(notes);
    }

    public String getBankAccount() {
        return bankAccount.get();
    }

    public SimpleStringProperty bankAccountProperty() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount.set(bankAccount);
    }

    public LocalDate getDateStarted() {
        return dateStarted.get();
    }

    public ObjectProperty<LocalDate> dateStartedProperty() {
        return dateStarted;
    }

    public void setDateStarted(LocalDate dateStarted) {
        this.dateStarted.set(dateStarted);
    }

    public LocalDate getDateChanged() {
        return dateChanged.get();
    }

    public ObjectProperty<LocalDate> dateChangedProperty() {
        return dateChanged;
    }

    public void setDateChanged(LocalDate dateChanged) {
        this.dateChanged.set(dateChanged);
    }

    public double getPreviousAmount() {
        return previousAmount.get();
    }

    public SimpleDoubleProperty previousAmountProperty() {
        return previousAmount;
    }

    public void setPreviousAmount(double previousAmount) {
        this.previousAmount.set(previousAmount);
    }

    @Override
    public String toString() {
        return "Bill{" +
                "name=" + name +
                ", dateOfPayment=" + dateOfPayment +
                ", amount=" + amount +
                ", bankAccount=" + bankAccount +
                ", notes=" + notes +
                ", dateStarted=" + dateStarted +
                ", dateChanged=" + dateChanged +
                ", previousAmount=" + previousAmount +
                '}';
    }
}


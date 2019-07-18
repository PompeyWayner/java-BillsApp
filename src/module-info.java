module BillsApp {
    requires javafx.fxml;
    requires javafx.controls;
    requires java.xml;
    requires java.base;
    opens bills;
    opens bills.datamodel;
}
package bills.datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.xml.stream.*;
import javax.xml.stream.events.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/** class BillData holds a Map. The key is the month and the value
 * is the the list of Bill objects. There are also methods to
 * performs Read/Write operations to file in XML format.
 *
 * @author Wayne Sandford
 * @version 08-08-2019 02
 */
public class BillData {

    private static final String BILLS_FILE = "billsFile.xml";
    private static final String BILL = "bill";
    private static final String NAME = "name";
    private static final String DATE_DUE = "date_due";
    private static final String AMOUNT = "amount";
    private static final String BANK_ACCOUNT = "bank_account";
    private static final String DATE_STARTED = "date_started";
    private static final String DATE_CHANGED = "date_changed";
    private static final String PREVIOUS_AMOUNT = "previous_amount";
    private static final String NOTES = "notes";
    private static final String MONTH = "month";

    private ObservableList<Bill> bills; // Holds Bill objects
    private Map<String, ObservableList<Bill>> billsMap; // Holds each month of Bills.
    public static String currentMonth; // Holds current month of bills being displayed.

    // Constructor
    public BillData() {
        bills = FXCollections.observableArrayList();
        billsMap = new HashMap<>();
    }

    public ObservableList<Bill> getBills() {
        return bills;
    }

    public void setBills(ObservableList<Bill> bills) {
        this.bills = bills;
    }

    public String getCurrentMonth() {
        return currentMonth;
    }

    public void setCurrentMonth(String currentMonth) {
        this.currentMonth = currentMonth;
    }

    public Map<String, ObservableList<Bill>> getBillsMap() {
        return billsMap;
    }

    public void setBillsMap(Map<String, ObservableList<Bill>> billsMap) {
        this.billsMap = billsMap;
    }

    /**
     * Add a new Bill.
     * @param bill a Bill object to add to List of Bills.
     */
    public void addBill(Bill bill) {
        bills.add(bill);
    }

    /**
     * Add a new month to the map and set up a empty Bill list.
     * @param aMonth
     */
    public void addNewMonth(String aMonth) {

        billsMap.put(aMonth, bills);
    }

    /**
     * Remove a bill.
     * @param bill a Bill object to remove from List of Bills.
     */
    public void deleteBill(Bill bill) {
        bills.remove(bill);
    }

    /**
     * Add current Bill object to the correct month.
     * @param aBill a Bill object to be placed in correct month.
     */
    public void addToCorrectMonth(Bill aBill) {
        System.out.println("start of BillData.addToCorrectMonth" + aBill.toString());
        // Test for empty Map - and add first key entry if necessary.
        if(billsMap.isEmpty()) {
            billsMap.put(aBill.getMonth(), FXCollections.observableArrayList());
            billsMap.get(aBill.getMonth()).add(aBill);
            this.setCurrentMonth(aBill.getMonth());
            System.out.println("BillData.addToCurrentMonth empty map so current month added");
        } else { // Bill object month already in map
            if(billsMap.containsKey(aBill.getMonth())) {
                billsMap.get(aBill.getMonth()).add(aBill);
                System.out.println("BillData.addToCurrentMonth - month already exists bill added");
            } else { // Month does not exist in currently populated map.
                billsMap.put(aBill.getMonth(), FXCollections.observableArrayList());
                billsMap.get(aBill.getMonth()).add(aBill);
                System.out.println("BillData.addToCurrentMonth - new month added - bill added");
            }
        }
    }

    /**
     * Test method that display contents of map.
     */
    public void displayMap() {
        for(String eachMonth : billsMap.keySet()) {
            System.out.println("Month : " + eachMonth);
            for(Bill eachBill : this.billsMap.get(eachMonth)) {
                System.out.println(eachBill.toString());
            }
        }
    }

    /**
     * Load bills from XML file into bills list.
     */
    public void loadBills() {
        try {
            // First, create a new XMLInputFactory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            // Setup a new eventReader
            InputStream in = new FileInputStream(BILLS_FILE);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
            // read the XML document
            Bill bill = null;

            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    // If we have a bill item, we create a new bill
                    if (startElement.getName().getLocalPart().equals(BILL)) {
                        bill = new Bill();
                        continue;
                    }

                    if (event.isStartElement()) {
                        if (event.asStartElement().getName().getLocalPart()
                                .equals(NAME)) {
                            event = eventReader.nextEvent();
                            bill.setName(event.asCharacters().getData());
                            continue;
                        }
                    }

                    if (event.asStartElement().getName().getLocalPart()
                            .equals(DATE_DUE)) {
                        event = eventReader.nextEvent();

                        // Convert text to LocalDate object.
                        String date = (event.asCharacters().getData());
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                        bill.setDateOfPayment(LocalDate.parse(date, formatter));
                        continue;
                    }

                    if (event.asStartElement().getName().getLocalPart()
                            .equals(AMOUNT)) {
                        event = eventReader.nextEvent();
                        bill.setAmount(Double.parseDouble(event.asCharacters().getData()));
                        continue;
                    }

                    if (event.asStartElement().getName().getLocalPart()
                            .equals(NOTES)) {
                        event = eventReader.nextEvent();
                        bill.setNotes(event.asCharacters().getData());
                        continue;
                    }

                    if (event.asStartElement().getName().getLocalPart()
                            .equals(BANK_ACCOUNT)) {
                        event = eventReader.nextEvent();
                        bill.setBankAccount(event.asCharacters().getData());
                        continue;
                    }

                    if (event.asStartElement().getName().getLocalPart()
                            .equals(DATE_STARTED)) {
                        event = eventReader.nextEvent();

                        // Convert text to LocalDate object.
                        String date = (event.asCharacters().getData());
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                        bill.setDateStarted(LocalDate.parse(date, formatter));
                        continue;
                    }

                    if (event.asStartElement().getName().getLocalPart()
                            .equals(DATE_CHANGED)) {
                        event = eventReader.nextEvent();

                        // Convert text to LocalDate object.
                        String date = (event.asCharacters().getData());
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                        bill.setDateChanged(LocalDate.parse(date, formatter));
                        continue;
                    }

                    if (event.asStartElement().getName().getLocalPart()
                            .equals(PREVIOUS_AMOUNT)) {
                        event = eventReader.nextEvent();
                        bill.setPreviousAmount(Double.parseDouble(event.asCharacters().getData()));
                        continue;
                    }

                    if (event.isStartElement()) {
                        if (event.asStartElement().getName().getLocalPart()
                                .equals(MONTH)) {
                            event = eventReader.nextEvent();
                            bill.setMonth(event.asCharacters().getData());
                            continue;
                        }
                    }

                }

                // If we reach the end of a contact element, we add it to the list.

                if (event.isEndElement()) {
                    EndElement endElement = event.asEndElement();
                    if (endElement.getName().getLocalPart().equals(BILL)) {
                        addToCorrectMonth(bill);
                    }
                }
            }
        }
        catch (FileNotFoundException e) {
            //e.printStackTrace();
        }
        catch (XMLStreamException e) {
            e.printStackTrace();
        }

        // Initialise first month of map as the default to show on start up
        //this.bills = billsMap.get(this.getCurrentMonth()); ** Remove on next cleanup
    }

    /**
     * Save bills to XML file.
     */
    public void saveBills() {

        try {
            // create an XMLOutputFactory
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            // create XMLEventWriter
            XMLEventWriter eventWriter = outputFactory
                    .createXMLEventWriter(new FileOutputStream(BILLS_FILE));
            // create an EventFactory
            XMLEventFactory eventFactory = XMLEventFactory.newInstance();
            XMLEvent end = eventFactory.createDTD("\n");
            // create and write Start Tag
            StartDocument startDocument = eventFactory.createStartDocument();
            eventWriter.add(startDocument);
            eventWriter.add(end);

            StartElement contactsStartElement = eventFactory.createStartElement("",
                    "", "bills");
            eventWriter.add(contactsStartElement);
            eventWriter.add(end);

            // Save each Bill object from each Month.
            for(String eachMonth : billsMap.keySet()) {
                System.out.println(eachMonth);
                for(Bill bill : billsMap.get(eachMonth)) {
                    saveBill(eventWriter, eventFactory, bill);
                }
            }

            eventWriter.add(eventFactory.createEndElement("", "", "bills"));
            eventWriter.add(end);
            eventWriter.add(eventFactory.createEndDocument());
            eventWriter.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("Problem with Bills file: " + e.getMessage());
            e.printStackTrace();
        }
        catch (XMLStreamException e) {
            System.out.println("Problem writing bill: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Write one bill to XML file
     * @param eventWriter a
     * @param eventFactory a
     * @param bill a Bill object that will be written to file.
     * @throws FileNotFoundException file not found.
     * @throws XMLStreamException unexpected process.
     */
    private void saveBill(XMLEventWriter eventWriter, XMLEventFactory eventFactory, Bill bill)
            throws FileNotFoundException, XMLStreamException {

        XMLEvent end = eventFactory.createDTD("\n");

        // create contact open tag
        StartElement configStartElement = eventFactory.createStartElement("",
                "", BILL);
        eventWriter.add(configStartElement);
        eventWriter.add(end);
        // Write the different nodes
        createNode(eventWriter, NAME, bill.getName());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedString = bill.getDateOfPayment().format(formatter);
        createNode(eventWriter, DATE_DUE, formattedString);
        createNode(eventWriter, AMOUNT, Double.toString(bill.getAmount()));
        createNode(eventWriter, BANK_ACCOUNT, bill.getBankAccount());
        formattedString = bill.getDateStarted().format(formatter);
        createNode(eventWriter, DATE_STARTED, formattedString);
        formattedString = bill.getDateChanged().format(formatter);
        createNode(eventWriter, DATE_CHANGED, formattedString);
        createNode(eventWriter, PREVIOUS_AMOUNT, Double.toString(bill.getPreviousAmount()));
        createNode(eventWriter, NOTES, bill.getNotes());
        createNode(eventWriter, MONTH, bill.getMonth());

        eventWriter.add(eventFactory.createEndElement("", "", BILL));
        eventWriter.add(end);
    }

    private void createNode(XMLEventWriter eventWriter, String name,
                            String value) throws XMLStreamException {

        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent end = eventFactory.createDTD("\n");
        XMLEvent tab = eventFactory.createDTD("\t");
        // create Start node
        StartElement sElement = eventFactory.createStartElement("", "", name);
        eventWriter.add(tab);
        eventWriter.add(sElement);
        // create Content
        Characters characters = eventFactory.createCharacters(value);
        eventWriter.add(characters);
        // create End node
        EndElement eElement = eventFactory.createEndElement("", "", name);
        eventWriter.add(eElement);
        eventWriter.add(end);
    }

    /**
     * Calculate total of all the bills.
     */
    public double calculateTotal () {

        double total = 0.0;

        for(Bill aBill : this.bills){
            total += aBill.getAmount();
        }
        return total;
    }

}

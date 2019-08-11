package bills.datamodel;

import java.util.ArrayList;
import java.util.List;

/**
 * class Months - holds a List of current Months that have
 * been created by the user for string Bill objects in.
 *
 * @author Wayne Sandford
 * @version 08-08-2019 01
 */
public class Months {

    private List<String> createdMonths;

    // Constructor
    public Months() {
        this.createdMonths = new ArrayList<>();
    }

    public List<String> getCreatedMonths() {
        return createdMonths;
    }

    public void setCreatedMonths(List<String> createdMonths) {
        this.createdMonths = createdMonths;
    }

    public void addMonth(String aMonth) {
        this.createdMonths.add(aMonth);
    }

    @Override
    public String toString() {
        return "Months{" +
                "createdMonths=" + createdMonths +
                '}';
    }
}

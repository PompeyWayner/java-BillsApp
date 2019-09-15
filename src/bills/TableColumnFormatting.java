package bills;

import bills.DateUtil;
import bills.DateUtil;
import bills.datamodel.Bill;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

import java.time.LocalDate;

public class TableColumnFormatting {

    public static void formatDate(TableColumn<Bill, LocalDate> tableColumn) {
        tableColumn.setCellFactory(tc -> new TableCell<>() {

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
    }

    public static void formatAmount(TableColumn<Bill, Double> tableColumn) {
        tableColumn.setCellFactory(tc -> new TableCell<Bill, Double>() {

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
    }
}

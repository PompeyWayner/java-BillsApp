# java-BillsApp
An application that allows monthly budgeting to be performed.

This application is written using the JavaFX framework and using the IntelliJ IDE.

Git Commit - 11-08-2019
- Updated the menu system to allow the month to be changed, added and deleted.
- Now using a Map to store the Bills for a particular month.
- Gave mainWindow and Dialog Boxes some CSS styling.
- Bug fixed when Bill amount has been edited and previous amount did not update.
- Cleaning up of code.

Git Commit - 31-08-2019
- Improved the ComboBoxes for adding, changing and deleting months so they are always in correct month order.
- Widened label for bill total so long amounts are displayed in full.

Git Commit - 01-09-2019
- New File and Open File options coded. Still maybe bugs.
- Added default menu item to be implemented.

Git Commit - 08-09-2019
- Save, SaveAs and Default File Current and Change Default File menu options implemented.
- Default filename is stored in "default.txt" - on first ever start up or deletion of default file, a new default file
  is created and called defaultFileName.xml .
- Fixed bugs for New and Open File.
- Refactored code so that the Alert notifications call a separate class.

Git Commit - 09-09-2019
- Added the Year option, so that the year of the file is displayed and the year is added to all bill records.
- New Year Menu Option allows for new year to be specified.
- Removed change year option from Date menu as this is address in the Year File menu.
- Current Year/Default Year bug fix.

Git Commit - 09-09-2019 2nd
- Updated UI created and existing components linked.
- Number Of UI nodes to be implemented in future versions
- MainWindow CSS updated.
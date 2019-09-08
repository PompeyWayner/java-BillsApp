package bills;

import java.io.*;

public class TextFileOperation {

    public static String readDefaultFile() {

        File file = new File("C:\\Users\\wsand\\IdeaProjects\\BillsApp\\default.txt");
        String line = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));

            line=br.readLine();
            System.out.println("Default filename is " + line);

            br.close();
        } catch (FileNotFoundException e) {
            // File not found
            e.printStackTrace();
        } catch (IOException e) {
            // Error when reading the file
            e.printStackTrace();
        }
        System.out.println("Read in filename from defaulttextfile which is " + line);
        return line;
    }

    public static void writeDefaultFile(String defaultFile, String newDefaultName) {
        try {
            System.out.println("writeDefaultFile()");
            File fout = new File(defaultFile);
            System.out.println(fout.getAbsolutePath());
            FileOutputStream fos = new FileOutputStream(fout);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.write(newDefaultName);
            System.out.println(newDefaultName);
            bw.newLine();
            bw.close();
        } catch (FileNotFoundException e){
            // File was not found
            e.printStackTrace();
        } catch (IOException e) {
            // Problem when writing to the file
            e.printStackTrace();
        }
    }
}

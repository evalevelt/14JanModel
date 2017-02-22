package components;
import Jama.Matrix;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.io.FileWriter;




/**
 * Created by eva on 19/02/2017.
 */
public class CSVdealer {

    int N_INSTITUTIONS;
    String separator = ",";
    String newline = "\n";



    public CSVdealer(){
    }

    public Matrix readFile(String fileName,     int columns, int N_INSTITUTIONS) {

        // This will reference one line at a time
        String Line = null;


        Matrix Data= new Matrix(N_INSTITUTIONS,columns);


        try {

            List data = new ArrayList<String[]>();
            // FileReader reads text files in the default encoding.
            FileReader fileReader =
                    new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            while((Line = bufferedReader.readLine()) != null) {
                String[] tokens = Line.split(separator);
                if (tokens.length>0){
                    data.add(tokens);

                }
            }

            int i = 1;
            int j = 0;

            for (i=1; i < data.size(); i++) {
                String[] row = (String[]) (data.get(i));
                for (j = 0; j < columns; j++) {
                    Data.set(i-1, j, Double.parseDouble(row[j]));

                }
            }

            // Always close files.
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'");
        } catch (IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'");
            // Or we could just do this:
            // ex.printStackTrace();

        }


        return Data;

    }

    public void writeFile1(String fileName, double [][] finalequity, Matrix shocks) {


        FileWriter fileWriter = null;

        try{
            fileWriter = new FileWriter(fileName);
            fileWriter.append("Shock");
            fileWriter.append(separator);
            fileWriter.append("Equities");
            fileWriter.append(newline);
            for(int i=0; i<shocks.getRowDimension(); i++){
                fileWriter.append(String.valueOf(shocks.get(i,0)));
                fileWriter.append(separator);
                for(int j=0; j<finalequity[i].length; j++){
                fileWriter.append(String.valueOf(finalequity[i][j]));
                fileWriter.append(separator);}
                fileWriter.append(newline);

            }






        }catch(Exception e){
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        }finally{
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();


            }

            }

    }

    public void writeFile2(String fileName, Matrix shocks, Matrix allinfo) {


        FileWriter fileWriter = null;

        try{
            fileWriter = new FileWriter(fileName);
            fileWriter.append("Shock");
            fileWriter.append(separator);
            fileWriter.append("Final Equity");
            fileWriter.append(separator);
            fileWriter.append("Steps Taken");
            fileWriter.append(separator);
            fileWriter.append("Number of Defaulted Banks");
            fileWriter.append(separator);
            fileWriter.append("Number of Defaulted Hedgefunds");
            fileWriter.append(separator);
            fileWriter.append("FinalPrice");
            fileWriter.append(newline);
            for(int i=0; i<shocks.getRowDimension(); i++){
                fileWriter.append(String.valueOf(shocks.get(i,0)));
                fileWriter.append(separator);
                for(int j=0; j<5; j++){
                    fileWriter.append(String.valueOf(allinfo.get(i,j)));
                    fileWriter.append(separator);}
                fileWriter.append(newline);

            }






        }catch(Exception e){
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        }finally{
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();


            }

        }

    }
}



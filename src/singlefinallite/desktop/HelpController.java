package singlefinallite.desktop;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.net.URL;

import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.TextArea;

/**
 * The Controller for the Help page.
 * @author Kyle
 */
public class HelpController
   implements Initializable
{
   /**
    * A text area to display the contents of the help file.
    */
   @FXML
   private TextArea helpDisplay;

   /**
    * Initialize - Initializes the window and fills in the data.
    *
    * @param url unused variable parameter.
    * @param rb unused variable parameter.
    */
   @Override
   public void initialize(URL url, ResourceBundle rb)
   {
       System.out.println("Opening file " + getClass().getResource("HELPME.txt").toString());
      String helpText = readFile("src/singlefinallite/desktop/HELPME.txt");
      helpDisplay.setEditable(false);
      helpDisplay.setText(helpText);
   }

   /**
    * Reads information from a file and returns the contents as a String.
    *
    * @param filename is the name of the file to be read.
    *
    * @return the file contents in the form of a String.
    */
   public String readFile(String filename)
   {
      String content = null;
      File file = new File(filename); //for ex foo.txt

      try
      {
         FileReader reader = new FileReader(file);
         char[] chars = new char[(int) file.length()];
         reader.read(chars);
         content = new String(chars);
         reader.close();
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }

      return content;
   }
}
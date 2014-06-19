package singlefinallite.desktop;

import singlefinallite.service.*;
import singlefinallite.system.*;
import static singlefinallite.system.ResourceGetter.*;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Serves as the driver for the algorithms associated with scoring.
 *
 * @author Chris Vergaray
 */
public class SingleFinalLite
   extends Application
{
   /**
    * Start() - called at runtime first. Initializes the main window and sets
    *           up the controller for the window.
    *
    * @param stage The window that will hold the main GUI interface.
    *
    * @throws Exception Throws an exception if unable to load the fxml file.
    */
   @Override
   public void start(Stage stage)
      throws Exception
   {
      // revert to /resources/properties/MainGUI.fxml
      Parent root =
         FXMLLoader.load(new URL(getResource("MainGUI.fxml")));
      mStage = stage;

      Scene scene = new Scene(root);
      stage.setTitle("Single Final Lite");
      stage.setScene(scene);
      stage.show();
   }

   /**
    * Singleton Reference.
    */
   private final static SingleFinalLite mInstance = new SingleFinalLite();

   /**
    * Data Object
    */
   private Data mData;

   /**
    * Variable representing the current javaFX stage. May be useful for
    * creating alerts and dialog boxes.
    */
   private Stage mStage;

   /**
    * The rules will be applied based on the functions implemented in this object.
    * The implementRules function must be implemented in this function, from there
    * the rules may be implemented as desired. So, I may need to make an interface
    * for this, so that any number of rules could be created.
    */
   private Ruler mRuler = new Ruler();

   /**
    * The member printer object, which is not needed, but may be used eventually
    * in later versions of the program.
    */
   private Printer mPrinter = new Printer("");

   /**
    * Provides access to the member ruler object
    */
   public void implementRules()
   {
      mRuler.implementRules(mData);
   }

   /**
    * @param args the command line arguments
    */
   public static void main(String[] args)
   {
      launch(args);
   }

   /**
    * A test function. Is not needed for the final product and should be
    * deleted eventually
    *
    * @param coupleScores
    * @param testDescription
    * @param couples
    * @param judges
    * @return data
    */
   public Data test(int[][] coupleScores, String testDescription, int couples,
      int judges)
   {
      /*mInstance = new */ buildSingleFinalLite(coupleScores, testDescription, couples, judges);

      return mRuler.implementRules(mData);

      //mInstance.display();
   }

   /**
    * Makes sure that there is a Data object. There will be no values in
    * the data object, the object itself will be created.
    */
   public SingleFinalLite()
   {
      mData = new Data();
   }

   /**
    * Non-Default Constructor.
    * Used for testing purposes
    *
    * @author Chris Vergaray, Adam Harris
    */
   public void buildSingleFinalLite(int[][] coupleScores,
      String testDescription, int couples, int judges)
   {
      //The following is for testing purposes only
      //It will be deleted later
      mData.setHeat(testDescription, "Level", "Style", judges, couples, "A");

      mData.createCouples(couples);

      mData.createNM(couples);

      for (int i = 0; i < couples; i++)
      {
         mData.setNM(i, false);
      }

      for (int i = 0; i < couples; i++)
      {
         mData.setCouple(i, coupleScores[i][0]);

         for (int j = 0; j < judges; j++)
         {
            mData.getCouple(i).setScores(j, coupleScores[i][j + 1]);
         }
      }
   }

   /**
    * getInstance() - returns a reference to the instance of SingleFinalLite.
    *
    * @return Returns the instance of SingleFinalLite.
    */
   public static SingleFinalLite getInstance()
   {
      return mInstance;
   }

   /**
    * A getter for the Data Object.
    * Allows other classes to access and use the data.
    * @return Returns the data object.
    */
   public Data getData()
   {
      return mData;
   }

   /**
    * This will append the current heat information to a specified text file.
    * If the name of the text file has not been specified previously,
    * it will be saved to a default file.
    *
    * @author Adam Harris, Chris Vergaray
    * @return A string which reports the success or failure status.
    */
   public String save(File fileName)
   {
      //If the filename has not been set, we want to initialize it
      String status = "";
      FileWriter fw;

      //We will try to create a file to write to
      File f = fileName;

      try
      {
         //This is called with true so that we are appending to
         //the end of the file instead of the beginning
         fw = new FileWriter(fileName, true);

         //If the file exists, we just want to append to it.
         if (f.length() != 0)
         {
            //First the page feed character must be written so that
            //each section is on it's own page
            fw.write((char) 12);
         }

         //Write the table to the file
         fw.write(new DisplayStringBuilder().buildTable(mData));

         //Close the file, we are done with it.
         fw.close();
         status = "Save Successful.";
      }
      catch (Exception e)
      {
         status = "Save was Unsuccessful. Please set Save File.";
         System.out.print("Adam, guess what!? We Failed!! " + e.getMessage());
      }

      return status;
   }

   /**
    * Provided to allow running without a GUI. Builds the same table string
    * as used in the main portion of the program, and prints it to the console
    */
   public void display()
   {
      System.out.print(new DisplayStringBuilder().buildTable(mData));
   }

   /**
    * A getter for the current stage. This may be needed for JavaFX functions
    * in other parts of the program
    *
    * @return Stage
    */
   public Stage getStage()
   {
      return mStage;
   }
}


package singlefinallite.service;

import java.util.ArrayList;

import javafx.scene.control.TextField;

/**
 * Utility class that parses the text boxes to make sure they all contain 
 * correct data
 * 
 * @author Kyle, Adam Harris
 */
public class TextBoxParser
{
   /**
    * A list of the results as entered by the user
    */
   private static ArrayList result = new ArrayList();

   /**
    * A list of the textFields
    */
   private static ArrayList<TextField> parsed = new ArrayList();

   /**
    * Mutator function to clear the results array
    */
   public static void clearResult()
   {
      result.clear();
   }

   /**
    * This will make sure that no judges have entered in the same numbers duplicate
    * times, and that every judge entered in the same couple numbers. Any "illegal"
    * entries will be highlighted in red.
    *
    * @param boxes ArrayList<textfield> Boxes contains the judges scores
    * @param judges int that holds the number of judges in this heat
    * @param couples int that holds the number of couples in this heat
    * @return Boolean isValid represents whether the values entered into the string
    *         are valid or not.
    */
   public static Boolean validateCoupleNumbers(ArrayList<TextField> boxes,
      int judges, int couples)
   {
      int temp;
      int i;
      Boolean isInColumn = true;
      Boolean isValid = true;

      //Check for duplicates
      for (int k = 0; k < judges; k++)
      {
         for (i = 0; i < couples; i++)
         {
            temp = Integer.parseInt(boxes.get(i + (k * couples)).getText());

            for (int j = i + 1 + (k * couples); j < ((k + 1) * couples); j++)
            {
               if (Integer.parseInt(boxes.get(j).getText()) == temp)
               {
                  boxes.get(j).setStyle("-fx-background-color:red");
                  isValid = false;
               }
            }
         }
      }

      //Check if scores entered match the first "array"
      for (i = 0; i < (judges * couples); i++)
      {
         temp = Integer.parseInt(boxes.get(i).getText());

         if (isInColumn == false)
         {
            boxes.get(i - 1).setStyle("-fx-background-color:red");
            isValid = false;
         }

         isInColumn = false;

         for (int j = 0; j < couples; j++)
         {
            if (Integer.parseInt(boxes.get(j).getText()) == temp)
            {
               isInColumn = true;
            }
         }
      }

      //Make sure we catch that last one
      if (isInColumn == false)
      {
         boxes.get(i - 1).setStyle("-fx-background-color:red");
         isValid = false;
      }

      parsed = boxes;

      return isValid;
   }

   /**
    * Makes sure that whatever has been entered into the text boxes is 
    * actually a number
    *
    * @param boxes the list of the boxes to be checked
    * @param judges The number of judges in this heat
    * @param couples The nubmer of couples in this heat
    *
    * @return True if there was an error, false otherwise
    */
   public static Boolean checkNumbers(ArrayList<TextField> boxes, int judges,
      int couples)
   {
      Boolean errorFlag = false;

      for (int x = 0; x < (judges * couples); x++)
      {
         int test = 0;
         boxes.get(x).setStyle("-fx-background-color:white");

         try
         {
            test = Integer.parseInt(boxes.get(x).getText());
            //System.out.println(test);
         }
         catch (NumberFormatException ex)
         {
            boxes.get(x).setStyle("-fx-background-color:red");
            errorFlag = true;
         }
      }

      parsed = boxes;

      return errorFlag;
   }

   /**
    * Checks to make sure that the data provided in the text boxes are valid
    * couple numbers. Also populated the parsed results array with the valid 
    * numbers
    *
    * @param boxes the list of the boxes to be checked
    * @param judges The number of judges in this heat
    * @param couples The nubmer of couples in this heat 
    *
    * @return True if there was an error, false otherwise
    */
   public static Boolean checkBounds(ArrayList<TextField> boxes, int judges,
      int couples)
   {
      Boolean errorFlag = false;

      for (int x = 0; x < (judges * couples); x++)
      {
         int test = 0;

         try
         {
            test = Integer.parseInt(boxes.get(x).getText());
         }
         catch (NumberFormatException ex)
         {
            boxes.get(x).setStyle("-fx-background-color:red");
         }

         if ((99 < test) && (test < 1000))
         {
            result.add(test);
         }
         else
         {
            boxes.get(x).setStyle("-fx-background-color:red");
            errorFlag = true;
         }
      }

      parsed = boxes;

      return errorFlag;
   }

   /**
    * Provides access to the results array
    *
    * @return ArrayList The results array
    */
   public static ArrayList getResult()
   {
      return result;
   }

   /**
    * Provides access to the parsed boxes ArrayList
    *
    * @return ArrayList the parsed boxes arrayList
    */
   public static ArrayList<TextField> getParsedBoxes()
   {
      return parsed;
   }
}

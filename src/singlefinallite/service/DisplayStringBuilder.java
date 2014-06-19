package singlefinallite.service;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Builds string for using throughout the program. Given data is formatted for
 * in a table. The table is stored in a single string including newline characters.
 * 
 * @author Chris Vergaray
 */
public class DisplayStringBuilder
{
   /**
    * Data Object
    */
   Data mData;

   /**
    * Builds a string to be displayed as the header of the calculated results.
    * This is all data drawn directly from the heat object
    *
    * @return string representing the header.
    */
   private String buildHeader()
   {
      return "" + mData.getHeat().getAge() + "\n" + mData.getHeat().getLevel() +
      "\n" + mData.getHeat().getStyle() + "\n\n" + "Number of Judges: " +
      mData.getHeat().getNumJudges() + "\n" + "Number of Couples: " +
      mData.getHeat().getNumCouples() + "\n" + "Majority: " +
      mData.getMajority() + "\n" + "A/B: " + mData.getHeat().getAB() + "\n\n";
   }

   /**
    * Builds a string displaying the calculated placements for the couples.
    *
    * @param pCouples representing the array of placed Couple objects.
    *
    * @return String representing placement table of Couples.
    */
   private String buildPlacments(Couple[] pCouples)
   {
      //The couples will be displayed in numerical order by couple number
      Arrays.sort(mData.getCouples(), new RankComparator());

      String output = "Ranking:\n";

      for (Couple current : pCouples)
      {
         output += ("" + current.getResult() + ": Couple " +
         current.getCoupleNum() + "\n");
      }

      return output;
   }

   /**
    * Builds a single line in the table for couple data.
    *
    * @param pCouple The couple to be displayed
    *
    * @return the formatted string
    */
   private String buildCouple(Couple pCouple)
   {
      //The couple number should always be three digits, but this will 
      //guarantee it
      String output = String.format("Couple %3d: ", pCouple.getCoupleNum());

      //The first columns will show the scores the judges gave the couple
      for (int i = 0; i < pCouple.mScores.length; i++)
      {
         output += (pCouple.getScores(i) + " ");
      }

      //Separated by a colon
      output += " : |";

      /*
       *In order to track before and after displaying a nonZero number
       *two flags are needed: if the current number is nonZero and if
       *the couple has been placed by the time that column displays.
       */
      boolean isNonZero = false;
      boolean placed = false;

      for (int i = 0; i < pCouple.mPlaces.length; i++)
      {
         //If the previous number was non zero, but the current one is zero
         //The couple has been placed by now.
         if (isNonZero && (pCouple.getPlaces(i) == 0))
         {
            placed = true;
         }

         //isNonZero may be updated now to prepare for the next column
         isNonZero = (pCouple.getPlaces(i) > 0);

         //If in calculating, there was an equal majority with another couple
         //the sum of the scores up to that column must be included
         if (pCouple.mEqualMajority[i])
         {
            output += (String.format("%2d", pCouple.getPlaces(i)) +
            String.format("(%2d)|", pCouple.sumScores(i + 1)));
         }
         else //Otherwise, the display is normal
         {
            //If the place has a number, display a number
            if (pCouple.getPlaces(i) != 0)
            {
               output += String.format("%6d|", pCouple.getPlaces(i));
            } //Display a single dash if zero before placement
            else if (! placed)
            {
               output += "     -|";
            } //Display a solid line if zero after placement
            else
            {
               output += "――――――|";
            }

            //This is the old obfuscated (but one line) version of this line
            //output += ((pCouple.getPlaces(i) != 0) ? String.format("%6d|", 
            //pCouple.getPlaces(i))
            //           : (!placed ? "     -|" : "------|"));
         }
      }

      //Finally, display the final result
      output += (" : " + pCouple.getResult() + "\n");

      return output;
   }

   /**
    * Builds the output into a single string, complete with newline characters
    * that are used to create a table. This is suitable for displaying to the
    * screen, printing to a file, or printing a physical printer.
    * 
    * @param pData the data object that will be formatted
    * 
    * @return String representing fully calculated table.
    */
   public String buildTable(Data pData)
   {
      mData = pData;

      //The first few lines are the header from the Heat object
      String output = buildHeader();

      //Between the header and the main table, we need to include a ranking 
      //of the couples
      output += buildPlacments(mData.getCouples());

      //The first part of the table must be indented 12 spaces
      output += "\n            ";

      //The judges are separated by letter
      for (int i = 0; i < mData.getHeat().getNumJudges(); i++)
      {
         output += ((char) ('A' + i) + " ");
      }

      //The first column is the sum of the scores of 1
      output += "   |   1  |";

      //Every subsequent column headers increment by 1
      for (int i = 2; i < (mData.getHeat().getNumCouples() + 1); i++)
         output += ("  1-" + i + " |");

      //The first part of the table must be indented 12 spaces
      output += "\n               ";

      //As well as spacers for the judge columns
      for (int i = 0; i < mData.getHeat().getNumJudges(); i++)
      {
         output += "  ";
      }

      output += "―";

      //Plus a row to divide the header from the rest of the table.
      for (int i = 0; i < mData.getHeat().getNumCouples(); i++)
         output += ("―――――――");

      //Now the table starts
      output += "\n";

      //The couples will be displayed in numerical order by couple number
      Arrays.sort(mData.getCouples(), new LexigcographicComparator());

      //Each couple knows how to display its own scores.
      for (Couple current : mData.getCouples())
      {
         output += buildCouple(current);
      }

      //Print out the spacers for the first portion
      output += "            ";

      //As well as spacers for the judge columns
      for (int i = 0; i < mData.getHeat().getNumJudges(); i++)
      {
         output += "  ";
      }

      //The first NM column just has the left part of the column border
      output += "   |";

      //Then print the NMs
      for (int i = 0; i < mData.getHeat().getNumCouples(); i++)
      {
         output += (mData.getNM(i) ? "    NM|" : "      |");
      }

      //Followed by two spacing lines.                        
      output += "\n\n";

      return output;
   }

   /**
    * An implementation of the Comparator interface. Used to sort the couples
    * in order based on their couple ID number
    */
   class LexigcographicComparator
      implements Comparator<Couple>
   {
      /**
       * Compares the couple numbers of two couples to place them in
       * lexicographic order. 
       * 
       * @param a The first couple to be compared
       * @param b The second couple to be compared
       *
       * @return an integer indicating which couple comes first
       */
      @Override
      public int compare(Couple a, Couple b)
      {
         return a.getCoupleNum() - b.getCoupleNum();
      }
   }

   /**
    * An implementation of the Comparator interface. Used to sort the couples
    * in order based on their couple ID number
    */
   class RankComparator
      implements Comparator<Couple>
   {
      /**
       * Compares the rank of two couples to determine which had the
       * better (smaller) final placement. A negative number indicates
       * that the first couple is better than the other, positive numbers
       * indicate that second couple is placed better. Zero means they
       * are the same.
       * 
       * @param a The first couple to be compared
       * @param b The second couple to be compared
       *
       * @return an integer indicating which couple is greater 
       */
      @Override
      public int compare(Couple a, Couple b)
      {
         return a.getResult() - b.getResult();
      }
   }
}
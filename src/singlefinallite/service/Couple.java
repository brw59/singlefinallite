package singlefinallite.service;

/**
 * Couple Class
 * Contains the information relative to the Couples, including scoring
 * information and their own display function.
 *
 * @author Adam Harris, Chris Vergaray
 */
public class Couple
{
   /**
    * Contains the list of scores given to the couple.
    */
   protected int[] mScores;

   /**
    * Contains the list of places to be calculated.
    */
   protected int[] mPlaces;

   /**
    * Contains the result that will be assigned to the couple.
    */
   protected int mResult;

   /**
    * The number identifying the couple.
    */
   protected int mCoupleNum;

   /**
    * Indicates whether a place column had the same
    * majority as another couple.
    */
   protected boolean[] mEqualMajority;

   /**
    * Holds a reference to the Heat.
    */
   protected Heat mHeat;

   /**
    * Non-Default Constructor
    * Will create a Couple and instantiate the couple's ID.
     * @param pCoupleNum A number representing the couple's number ID
     * @param pHeat The Current Heat.
    */
   public Couple(int pCoupleNum, Heat pHeat)
   {
      if ((pCoupleNum <= 999) && (pCoupleNum >= 0))
      {
         mCoupleNum = pCoupleNum;
      }
      else
      {
         System.out.println("ERROR: CoupleNum Outside of Range!");
      }

      mHeat = pHeat;
      mScores = new int[mHeat.getNumJudges()];
      mPlaces = new int[mHeat.getNumCouples()];
      mEqualMajority = new boolean[mHeat.getNumCouples()];
      mResult = -1;
   }

   //-------------------------------------------------
   //-----------------ACCESORS------------------------
   //-------------------------------------------------

   /**
    * Get the score in a certain index.
    *
    * @param index of the mScores array.
    * @return score value
    */
   public int getScores(int index)
   {
      assert (index <= mScores.length);

      return mScores[index];
   }

   /**
    * Get the place value in a certain index of mPlaces.
    *
    * @param index of the mPlaces array.
    *
    * @return place valueString
    */
   public int getPlaces(int index)
   {
      assert (index <= mPlaces.length);

      return mPlaces[index];
   }

   /**
    * Gets the result value.
    *
    * @return the result value.
    */
   public int getResult()
   {
      return mResult;
   }

   /**
    * Gets the number for the Couple.
    *
    * @return the Couple number.
    */
   public int getCoupleNum()
   {
      return mCoupleNum;
   }

   //-------------------------------------------------
   //-----------------MUTATORS------------------------
   //-------------------------------------------------

   /**
    * Assigns a score value to a certain index.
    *
    * @param index to be set.
    * @param pScore is the value to be assigned.
    */
   public void setScores(int index, int pScore)
   {
      assert ((index >= 0) && (index <= mScores.length));
      assert (pScore >= 0);
      assert (pScore <= mHeat.getNumCouples());

      mScores[index] = pScore;
   }

   /**
    * Sets a place value to a certain index.
    *
    * @param index to be set.
    * @param pPlace is the value that will be assigned to the index.
    */
   public void setPlaces(int index, int pPlace)
   {
      assert ((index >= 0) && (index <= mPlaces.length));
      assert (pPlace >= 0);
      assert (pPlace <= mHeat.getNumJudges());

      mPlaces[index] = pPlace;
   }

   /**
    * Sets the calculated result value.
    *
    * @param pResult is the value to be set.
    * @return Returns if the result is set.
    */
   public boolean setResult(int pResult)
   {
      assert (pResult >= 0);
      assert (pResult <= mHeat.getNumCouples());

      mResult = ((mResult > 0) ? mResult : pResult);

      return (mResult == pResult);
   }

   /**
    * Sets the flag for the given index to true, indicating that this couple
    * had the same place sum as another couple in the indicated place column.
    *
    * @param pIndex
    */
   public void setEqualMajority(int pIndex)
   {
      assert (pIndex >= 0);
      assert (pIndex <= mHeat.getNumCouples());

      mEqualMajority[pIndex] = true;
   }

   /**
    * Utility function that calculates the sum of the scores less than or equal
    * to the given score.
    *
    * @param pMaxScore
    * @return sum of the scores
    */
   public int sumScores(int pMaxScore)
   {
      int sum = 0;

      for (int i : mScores)
      {
         sum += ((i <= pMaxScore) ? i : 0);
      }

      return sum;
   }

   /**
    * Deprecated string building method. This functionality has been replaced by
    * the DisplayStringBuilder.
    *
    * @return A formatted string
    */
   public String buildString()
   {
      //The couple number should always be three digits, but this will guarantee it
      String output = String.format("Couple %3d: ", mCoupleNum);

      //The first columns will show the scores the judges gave the couple
      for (int i = 0; i < mScores.length; i++)
      {
         output += (mScores[i] + " ");
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

      for (int i = 0; i < mPlaces.length; i++)
      {
         //If the previous number was non zero, but the current one is zero
         //The couple has been placed by now.
         if (isNonZero && (mPlaces[i] == 0))
         {
            placed = true;
         }

         //isNonZero may be updated now to prepare for the next column
         isNonZero = (mPlaces[i] > 0);

         //If in calculating, there was an equal majority with another couple
         //the sum of the scores up to that column must be included
         if (mEqualMajority[i])
         {
            output += (String.format("%2d", mPlaces[i]) +
            String.format("(%2d)|", sumScores(i + 1)));
         }
         else //Otherwise, the display is normal
         {
            //If the place has a number, display a number
            if (mPlaces[i] != 0)
            {
               output += String.format("%6d|", mPlaces[i]);
            }

            //Display a single dash if zero before placement
            else if (! placed)
            {
               output += "     -|";
            }

            //Display a solid line if zero after placement
            else
            {
               output += "------|";
            }

            //This is the old obfuscated (but one line) version of this line
            //output += ((mPlaces[i] != 0) ? String.format("%6d|", mPlaces[i])
            //           : (!placed ? "     -|" : "------|"));
         }
      }

      //Finally, display the final result
      output += (" : " + mResult + "\n");

      return output;
   }

   /**
    * Deprecated display function
    * Will display the scores, places, and results of the couple in that order,
    * to the console.
    */
   public void display()
   {
      System.out.print(buildString());
   }
}

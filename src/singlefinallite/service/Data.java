package singlefinallite.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;
import java.util.SortedMap;
import java.util.TreeMap;

import javafx.stage.Stage;

/**
 * Data Class
 *
 * @author Adam Harris, Chris Vergaray
 */
public class Data
{
   /**
    * integer representing the minimum number of scores
    * needed to have a majority
    */
   private static int mMajority;

   /**
    * An array of couples.
    * Essentially one dimension in our multidimensional array
    */
   private Couple[] mCouples;

   /**
    * The (Glorified Struct) that holds the data associated with the current
    * heat.
    */
   private static Heat mHeat;

   /**
    * A boolean array representing whether or not that column had no majority
    */
   private Boolean[] mNm;

   //-------------------------------------------------
   //-----------------ACCESORS------------------------
   //-------------------------------------------------

   /**
    * Get the number required for majority.
    *
    * @return integer representing minimum number needed for majority
    */
   public int getMajority()
   {
      return mMajority;
   }

   /**
    * Gets the array of couples.
    *
    * @return array of couples in this dance
    */
   public Couple[] getCouples()
   {
      return mCouples;
   }

   /**
    * Returns the couple object from the couples array.
    * If the provided index is invalid, it is rounded to the
    * nearest valid index.
    *
    * @param pIndex an int of a specific couple
    * @return Couple object located at the given index
    */
   public Couple getCouple(int pIndex)
   {
      pIndex = (pIndex > mCouples.length) ? mCouples.length : pIndex;
      pIndex = (pIndex < 0) ? 0 : pIndex;

      return mCouples[pIndex];
   }

   /**
    * Get the heat object.
    *
    * @return mHeat member heat object
    */
   public Heat getHeat()
   {
      return mHeat;
   }

   /**
    * Gets whether the specified column has no majority.
    * Invalid numbers are rounded to nearest valid index
    *
    * @param pIndex the column to be tested
    * @return true if no majority, false otherwise
    */
   public Boolean getNM(int pIndex)
   {
      pIndex = (pIndex > mNm.length) ? mNm.length : pIndex;
      pIndex = (pIndex < 0) ? 0 : pIndex;

      return mNm[pIndex];
   }

   //-------------------------------------------------
   //-----------------MUTATORS------------------------
   //-------------------------------------------------

   /**
    * This will create an array of Couple objects as large as is designated.
    *
    * @param pNum represents how many slots in the array will be allocated.
    */
   public void createCouples(int pNum)
   {
      mCouples = new Couple[pNum];
   }

   /**
    * Creates a new couple object and assigns it to a index in the array.
    *
    * @param pIndex determines which spot in the array the new couple will
    *        occupy.
    * @param pCoupleNumber determines the number of the couple.
    */
   public void setCouple(int pIndex, int pCoupleNumber)
   {
      mCouples[pIndex] = new Couple(pCoupleNumber, mHeat);
   }

   /**
    * Creates an array of boolean values of the designated size.
    *
    * @param pNum determines the size of the array.
    */
   public void createNM(int pNum)
   {
      mNm = new Boolean[pNum];

      for (int i = 0; i < pNum; i++)
      {
         mNm[i] = false;
      }
   }

   /**
    * Setter for the heat member variable. Also recalculates the majority based
    * on the heat that was just set. The majority is recalculated whether the
    * heat was reset or not, just as a precaution in case the majority was set
    * to the wrong value somewhere else.
    * Creates a heat object using the given values and stores it.
    *
    * @param pAge The age group
    * @param pLevel The skill level of this current dance group 
    * @param pStyle The style of dance they will be dancing
    * @param pNumJudges The number of judges in this heat
    * @param pNumCouples The number of couples in this heat
    * @param pAB Whether this is an A or B round
    */
   public void setHeat(String pAge, String pLevel, String pStyle,
      int pNumJudges, int pNumCouples, String pAB)
   {
      //Provided we have actually recieved a valid heat, the member is assigned
      mHeat = new Heat(pAge, pLevel, pStyle, pNumJudges, pNumCouples, pAB);
      createNM(pNumCouples);
      //The majority must be recalculated based on the current number of judges.
      mMajority = ((mHeat.getNumJudges() + 1) / 2);
   }

   /**
    * Setter for the heat member variable. Also recalculates the majority based
    * on the heat that was just set. The majority is recalculated whether the
    * heat was reset or not, just as a precaution in case the majority was set
    * to the wrong value somewhere else.
    * Stores the heat obejct provided.
    *
    * @param pHeat represents the heat to be copied.
    */
   public void setHeat(Heat pHeat)
   {
      //Provided we have actually recieved a valid heat, the member is assigned
      mHeat = pHeat;
      createNM(pHeat.getNumCouples());
      //The majority must be recalculated based on the current number of judges.
      mMajority = ((mHeat.getNumJudges() + 1) / 2);
   }

   /**
    * Set No Majority. This will set the value of the NM array to the specified
    * boolean value at the specified index.
    *
    * @param pIndex int index of the array.
    * @param isNM Boolean value representing if there was a majority or not
    */
   public void setNM(int pIndex, Boolean isNM)
   {
      mNm[pIndex] = isNM;
   }

   /**
    * When retrieved from the GUI, the couple's ranks will be indicated by
    * their order in a list. The length of the list will be equal to the
    * number of judges multiplied by the number of couples because each
    * couple is listed by each judge once.
    * The Heat must be set before this function can be called.
    * 
    * @param pCouples an ArrayList<Integer> representing the list of Couple
    *  objects to be loaded
    */
   public void loadCouples(ArrayList<Integer> pCouples)
   {
      //We will use a tree map to store our couples
      SortedMap<Integer, Couple> map = new TreeMap<Integer, Couple>();

      //An iterator will allow traversal of the provided list of couples
      ListIterator<Integer> listIt = pCouples.listIterator();

      //The first set of couple numbers will be used to indicate
      //the couples that are in this specific heat.
      for (int score = 1; score < (mHeat.getNumCouples() + 1); score++)
      {
         //The current integer is the couple number
         Integer current = listIt.next();

         //A couple can be created with that couple ID number and current score
         Couple tempCouple = new Couple(current.intValue(), mHeat);
         tempCouple.setScores(0, score);
         map.put(current, tempCouple);
      }

      //The rest of the couple numbers are processed similarly,
      //except within a loop for each judge.
      //Also, we don't need to create the couples every time.
      for (int judge = 1; judge < mHeat.getNumJudges(); judge++)
      {
         for (int score = 1; score < (mHeat.getNumCouples() + 1); score++)
         {
            Integer current = listIt.next();
            map.get(current).setScores(judge, score);
         }
      }

      //The couples we created will be stored in the member array
      mCouples = new Couple[mHeat.getNumCouples()];

      int i = 0;

      for (Couple currentCouple : map.values())
      {
         mCouples[i++] = currentCouple;
      }
   }
}
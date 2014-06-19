package singlefinallite.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Ruler()- holds the rules and their implementation.
 *
 * @author Chris Vergaray
 */
public class Ruler
{
   /**
    * The current score that is being assigned. We retain this here so that all
    * methods have access to it
    */
   private int mCurrentScore;

   /**
    * A handle for the data object received so that it can be modified.
    */
   private Data mData;

   /**
    * The interface that must be implemented in order for the rules to be applied.
    * Takes the data object, mutates it, and returns it when finished. This way, 
    * the function can be applied in testing functions as well as for the main program
    * 
    * @param pData The data object that will be mutated
    * @return mData The data object, having been modified by the rules.
    */
   public Data implementRules(Data pData)
   {
      mData = pData;

      //Reset the current score to be placed to 1
      mCurrentScore = 1;

      for (int col = 0; col < mData.getHeat().getNumCouples(); col++)
      {
         //We must tally the scores for each column
         tallyColumn(col, mData.getCouples());

         //And then apply the rules to that collumn
         switch (rule5(col, mData.getCouples()))
         {
            //If none of the couples had a majority, we move to the next column
            case 0:
               rule8(col);

               break;

            //If one couple had a majority, it was already placed   
            case 1:
               break;

            //Otherwise, we move to rule 6
            default:
               rule6(col, mData.getCouples());

               break;
         }
      }

      return mData;
   }

   /**
    * Adds up a specific Column to tally the scores. Only scores for couples
    * that have not been calculated are tallied
    *
    * @param pColumnNumber The current column to be placed. This is zero based.
    * @param pCouples represnts the couples currently being placed.
    */
   private void tallyColumn(int pColumnNumber, Couple[] pCouples)
   {
      for (Couple current : pCouples)
      {
         //If the result has not been set, it will be negative
         if (current.mResult < 0)
         {
            //We want to keep track of how many scores of a given level have
            //been recieved
            int scoreCount = 0;

            //Tally up the nubmer of scores that are equal to the 
            //pColumnNumber plus one 
            for (int i = 0; i < current.mScores.length; i++)
            {
               scoreCount += ((current.mScores[i] == (pColumnNumber + 1)) ? 1 : 0);
            }

            //The current tally is equal to the number of scores
            //recieved of that level
            current.mPlaces[pColumnNumber] = scoreCount;

            //The tally also includes the sum of better placements
            current.mPlaces[pColumnNumber] += ((pColumnNumber > 0)
            ? current.mPlaces[pColumnNumber - 1] : 0);
         }
      }
   }

   /**
    * Implements rule 5. A couple is placed when only they have a majority of
    * scores of a given placement or higher. If multiple couples have a
    * majority, this rule does not apply. When this rule applies, the couple is
    * placed automatically.
    *
    * @param pColumnNumber The current column to be placed. This is zero based.
    * @param pCouples represnts the couples currently being placed.
    * @return the number of couples with a majority
    */
   private int rule5(int pColumnNumber, Couple[] pCouples)
   {
      //To keep track of how many couples have a majority
      int countWithMajority = 0;

      //We keep a handle on the couple that actually has the majority
      Couple coupleWithMajority = null;

      for (Couple current : pCouples)
      {
         //If the result has not been set, it will be negative
         if (current.mResult < 0)
         {
            //If the current couple has a majority,
            //we want to keep track of them
            if (current.mPlaces[pColumnNumber] >= mData.getMajority())
            {
               countWithMajority++;
               coupleWithMajority = current;
            }
         }
      }

      //If only one couple had a majority, then by rule 5,
      //they get the current placement
      if ((countWithMajority == 1) && (coupleWithMajority != null))
      {
         coupleWithMajority.setResult(mCurrentScore);
         mCurrentScore++;
      }

      //We report how many had a majority from this round.
      return countWithMajority;
   }

   /**
    * Applies rule 6. Breaks ties whenever multiple couples have a majority in
    * the same column. This is done by creating a list of couples that have a
    * majority and then sorting that list by the number with which they had a
    * majority. A smaller number is better, so we can assign them their
    * placements as long as they are not tied with the same score.
    *
    * @param pColumnNumber represents the current column under scrutiny.
    * @param pCouples represents the Couple objects being placed.
    */
   private void rule6(int pColumnNumber, Couple[] pCouples)
   {
      //We find all the couples with a majority
      ArrayList<Couple> couplesWithMajority = new ArrayList<Couple>();

      //By checking each coule
      for (Couple current : pCouples)
      {
         //If they have a majority, they are of interest
         if (current.getPlaces(pColumnNumber) >= mData.getMajority())
         {
            couplesWithMajority.add(current);
         }
      }

      //We sort these couples by the magnitude of their majority
      Collections.sort(couplesWithMajority, new PlaceComparator(pColumnNumber));

      //Then we need to check for duplicates      
      for (int i = 0; i < (couplesWithMajority.size() - 1); i++)
      {
         //If a given couple has the same majority as the next couple
         if (couplesWithMajority.get(i).getPlaces(pColumnNumber) == couplesWithMajority.get(i +
                   1).getPlaces(pColumnNumber))
         {
            //Store the magnitude of the majority so it can be used to compare
            int magnitude = couplesWithMajority.get(i).getPlaces(pColumnNumber);

            //Find all the duplicates with this majority
            ArrayList<Couple> duplicates = new ArrayList<Couple>();

            for (int j = i; j < couplesWithMajority.size(); j++)
            {
               if (couplesWithMajority.get(j).getPlaces(pColumnNumber) == magnitude)
               {
                  duplicates.add(couplesWithMajority.get(j));
               }
               else
               {
                  break;
               }
            }

            //Apply rule 7 to them
            rule7(pColumnNumber, duplicates);

         }
         else
         {
            //If they are the only ones with that majority, they get
            //the better score
            if (couplesWithMajority.get(i).setResult(mCurrentScore))
            {
               mCurrentScore++;
            }
         }

         //Then we try and set the last couple. If they have already
         //been placed then we don't worry about them nor
         //incrementing the score.
         if (couplesWithMajority.get(couplesWithMajority.size() - 1)
                                    .setResult(mCurrentScore))
         {
            mCurrentScore++;
         }
      }
   }

   /**
    * Rule 7 says that if multiple couples have a majority, and the majorities
    * are equal, the tie will be broken by summing the scores that add up to
    * that majority and whoever has the greater sum recieves the better
    * placement. If they both have the same sum, we cascade to the next collumn
    * and begin applying rules 5, 6, and 7 on just those couples until they are
    * placed.
    * 
    * @param pColumnNumber the current column under scutiny.
    * @param pCouples represents the couples being placed.
    */
   private void rule7(int pColumnNumber, List<Couple> pCouples)
   {
      //We need to indicate that these couples had an equal majority with some
      //other couple, for display purposes.
      for (Couple current : pCouples)
      {
         current.setEqualMajority(pColumnNumber);
      }

      //We will keep track of couples with the same majority with an ArrayList
      ArrayList<Couple> duplicates = new ArrayList<Couple>();

      //The couples passed into the function are sorted by the sum of their 
      //placements
      Collections.sort(pCouples, new PlaceTallyComparator(pColumnNumber + 1));

      //Then we go through and check each one
      for (int i = 0; i < (pCouples.size() - 1); i++)
      {
         //If the sums of their scores are equal
         if (pCouples.get(i).sumScores(pColumnNumber + 1) == pCouples.get(i +
                   1).sumScores(pColumnNumber + 1))
         {
            //Store the sum of their placements to test with
            int magnitude = pCouples.get(i).sumScores(pColumnNumber + 1);

            //Find all the duplicates with this sum
            for (int j = i; j < pCouples.size(); j++)
            {
               if (pCouples.get(j).sumScores(pColumnNumber + 1) == magnitude)
               {
                  duplicates.add(pCouples.get(j));
               }
               else
               {
                  break;
               }
            }

            //If This is the last collumn, we don't want to dive deeper
            //It really was an absoulte tie
            if (! absoluteTie(pColumnNumber + 1, duplicates))
            {
               //Tally the scores for only those duplicates in the next column
               tallyColumn(pColumnNumber + 1,
                  duplicates.toArray(new Couple[duplicates.size()]));

               //Apply rule 7 to them
               switch (rule5(pColumnNumber + 1,
                  duplicates.toArray(new Couple[duplicates.size()])))
               {
                  case 0:
                     break;

                  case 1:
                     break;

                  default:
                     rule6(pColumnNumber + 1,
                        duplicates.toArray(new Couple[duplicates.size()]));

                     break;
               }
            }
         }
         else
         {
            //Otherwise, they are the only ones that had that score, 
            //so we try and set their position
            if (pCouples.get(i).setResult(mCurrentScore))
            {
               mCurrentScore++;
            }
         }
      }

      //Just in case the last couple didn't get set, it gets set now.
      if (pCouples.get(pCouples.size() - 1).setResult(mCurrentScore))
      {
         mCurrentScore++;
      }
   }

   /**
    * Rule 8 states that if no couples had a majority in a given column,
    * then that column has no majority and the next column is placed 
    * under scrutiny
    * 
    * @param col represented the current column under scrutiny.
    */
   private void rule8(int col)
   {
      mData.setNM(col, true);
   }

   /**
    * An alternate version of rule 8 used when rule 7 fails. In this case
    * there is a true tie. We check to see if there are no more columns
    * and if there are not, the couples that made it this far are placed.
    * 
    * @param col represents the currnt column under scrutiny.
    * @param pCouples represents the Couples being placed.
    * @return True if the couples had an absoulte tie, false otherwise
    */
   private Boolean absoluteTie(int col, ArrayList<Couple> pCouples)
   {
      if (col == (mData.getHeat().getNumCouples()))
      {
         int countCouples = 0;

         for (Couple current : pCouples)
         {
            if (current.setResult(mCurrentScore))
            {
               countCouples++;
            }
         }

         mCurrentScore += countCouples;

         return true;
      }

      return false;
   }

   /**
    * An implementation of the Comparator interface. Used in rule 6 when
    * multiple couples have a majority by first determining which are tied and
    * then sorting them by their placements.
    * When used in a sort, couples are placed in ascending order by their placement
    */
   class PlaceComparator
      implements Comparator<Couple>
   {
      /**
       * The index of the places column that the couples are being compared for
       */
      private int mIndex;

      /**
       * Creates a new PlaceComparator object.
       *
       * @param pIndex The column under scrutiny
       */
      PlaceComparator(int pIndex)
      {
         mIndex = pIndex;
      }

      /**
       * Compares the placements of two couples to determine which had the 
       * better (smaller) score. A negative number indicates that the first 
       * couple is better than the other, positive numbers indicate that 
       * second couple is placed better. Zero means they are the same.
       *
       * @param a The first couple to be compared
       * @param b The second couple to be compared
       *
       * @return an integer indicating which couple is greater
       */
      @Override
      public int compare(Couple a, Couple b)
      {
         return b.getPlaces(mIndex) - a.getPlaces(mIndex);
      }
   }

   /**
    * An implementation of the Comparator interface. Used to sort the
    * couples in  order based on the sum of the scores they have
    * received up to this point.
    * When used in a sort, couples are placed in descending order by 
    * the sum of their scores
    */
   class PlaceTallyComparator
      implements Comparator<Couple>
   {
      /**
       * The max score used in the summations
       */
      private int mIndex;

      /**
       * Creates a new PlaceTallyComparator object.
       *
       * @param pIndex The max score used in the summations
       */
      PlaceTallyComparator(int pIndex)
      {
         mIndex = pIndex;
      }

      /**
       * Compares the placements of two couples to determine which had the
       * better (larger) sum of scores. A negative number indicates that 
       * the first couple is better than the other, positive numbers 
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
         return a.sumScores(mIndex) - b.sumScores(mIndex);
      }
   }
}
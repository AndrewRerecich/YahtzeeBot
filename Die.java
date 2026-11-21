import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>
 * Class representing a die used in the game of Yahtzee.
 * The number of faces is generalized but must be at least 1.
 * This class handles the rolling of the die and keeps track of its current face
 * value.
 * </p>
 *
 * @author Stephen Adams
 * @version 202535.003
 */
public class Die {

   private final int faces; // the number of faces on the die
   private int currentFace; // the current value of the die

   /**
    * Constructs a Die with the specified number of faces.
    * 
    * @param faces the number of faces on the die, must be greater than or equal to
    *              1
    * @throws IllegalArgumentException if faces is less than 1
    */
   public Die(int faces) {
      if (faces < 1) {
         throw new IllegalArgumentException("Number of faces must be at least 1.");
      }
      this.faces = faces;
      currentFace = roll();
   }

   /**
    * Constructs a standard six-sided die (D6).
    */
   public Die() {
      this(6);
   }

   /**
    * Returns the current face value of the die.
    * 
    * @return the current face value of the die
    */
   public int getCurrentFace() {
      return currentFace;
   }

   /**
    * Rolls the die and updates the current face value to a random number between 1
    * and the number of faces.
    * 
    * @return the new face value after the roll
    */
   public int roll() {
      currentFace = ThreadLocalRandom.current().nextInt(1, faces + 1);
      return currentFace;
   }

   /**
    * Shows the current face values of all dice as a comma-separated string.
    *
    * @return a string representing the face values of the dice
    */
   public static String showDice(Die[] dice) {
      StringBuilder sb = new StringBuilder();

      for (Die d : dice)
         sb.append(d.getCurrentFace()).append(",");

      // Remove the final comma if the StringBuilder has content
      if (sb.length() > 0)
         sb.setLength(sb.length() - 1);

      return sb.toString();
   }

   /**
    * Returns the current sum of all dice.
    *
    * @return integer sum of the die faces
    */
   public static int sumDice(Die[] dice) {
      int sum = 0;
      for (Die d : dice) sum += d.getCurrentFace();
      return sum;
   }

   // Evaluates if the Yahtzee patterns are found in the Die[] and returns a Map<Scorecard.Boxes, Boolean>
   public static Map<Scorecard.Boxes, Boolean> has(Die[] dice) {
      // Sort the dice once before checking with the private method.
      // note that many of the private method checks rely on this sorting
      int[] roll = new int[5];
      for ( int i=0; i < 5; i++ )
         roll[i] = dice[i].getCurrentFace();
      Arrays.sort( roll );

      Map<Scorecard.Boxes, Boolean> boxes = new EnumMap<Scorecard.Boxes, Boolean>(Scorecard.Boxes.class);

      // for each thing in the enum check if the\\\ roll has it
      for ( Scorecard.Boxes b : Scorecard.Boxes.values() ) {
         boxes.put( b, has( roll, b ) );
      }

      return boxes;
   }

   public static Map<Scorecard.Boxes, Boolean> has (int[] dice ) {
      int[] roll = dice.clone();
      Arrays.sort( roll );

      Map<Scorecard.Boxes, Boolean> boxes = new EnumMap<Scorecard.Boxes, Boolean>(Scorecard.Boxes.class);

      // for each thing in the enum check if the\\\ roll has it
      for ( Scorecard.Boxes b : Scorecard.Boxes.values() ) {
         boxes.put( b, has( roll, b ) );
      }

      return boxes;
   }

   /**
    * @param theRoll an array containing the values of 5 dice, sorted numerically
    * @param b the box you wish to test against
    * @return true if the dice score > 0 in the box you provided
    */
   private static boolean has( int[] theRoll, Scorecard.Boxes b ) {
      // Patterns assume 5 dice.
      boolean found = false;

      int[] roll = theRoll.clone();
      Arrays.sort(roll);

      switch ( b ) {
         case U1: found = contains( 1, roll);break;
         case U2: found = contains( 2, roll);break;
         case U3: found = contains( 3, roll);break;
         case U4: found = contains( 4, roll);break;
         case U5: found = contains( 5, roll);break;
         case U6: found = contains( 6, roll);break;
         case TK:
            found = (roll[0]==roll[1] && roll[0]==roll[2]) ||
                    (roll[1]==roll[2] && roll[1]==roll[3]) ||
                    (roll[2]==roll[3] && roll[2]==roll[4]);
            break;
         case FK:
            found = ( roll[0]==roll[3] || roll[1]==roll[4] );
            break;
         case FH:
            found = ( roll[0] == roll[1] && roll[1] != roll[2] &&
                    roll[2] == roll[3] && roll[3] == roll[4] ) ||
                    ( roll[0] == roll[1] && roll[1] == roll[2] &&
                            roll[2] != roll[3] && roll[3] == roll[4] );
            break;
         case SS:
            // 1-2-3-4-* 2-3-4-5-*, *-2-3-4-5, *-3-4-5-6
            // Careful, the * could appear anywhere and throw our indexing right out of whack
            boolean pair = false;
            int inARow = 1;
            for (int i=0; i < roll.length-1; i++) {
               if ( roll[i]+1 == roll[i+1] )
                  inARow++;
               else if ( roll[i] == roll[i+1] && !pair )
                  pair = true; // a pair is allowed, once
               else if ( roll[i] == roll[i+1] && pair )
                  break; // has 3 of a kind or better
               else
                  inARow = 1;  // if we have any break in the sequence we have to reset

               if ( inARow == 4 ) break;
            }
            found = inARow == 4;
            break;
         case LS:
            found = ( roll[0] == 1 && roll[1] == 2 && roll[2] == 3 && roll[3] == 4 && roll[4] == 5 ) ||
                    ( roll[0] == 2 && roll[1] == 3 && roll[2] == 4 && roll[3] == 5 && roll[4] == 6 );
            break;
         case Y:
            found = true;
            for ( int d : roll )
               found &= ( d == roll[0] );
            break;
         case C:
            // all yahtzee dice configurations can be scored in chance
            found = true;
            break;
      }

      return found;
   }

   private static boolean contains( int val, int[] roll ) {
      boolean found = false;
      for ( int d : roll )
         if ( d == val ) {
            found = true;
            break;
         }
      return found;
   }

   public static class PatternMatch {
      public final boolean present;
      public final boolean[] keep;

      public PatternMatch(boolean present, boolean[] keep) {
         this.present = present;
         this.keep = keep;
      }
   }

   public static Map<Scorecard.Boxes, PatternMatch> hasWithKeep(int[] dice) {
      int[] roll = dice.clone();
      Arrays.sort(roll);

      Map<Scorecard.Boxes, PatternMatch> results =
              new EnumMap<>(Scorecard.Boxes.class);

      for (Scorecard.Boxes b : Scorecard.Boxes.values()) {
         boolean found = has(roll, b); // your existing detection logic
         boolean[] keepMask = new boolean[dice.length];

         if (found) {
            switch (b) {
               case U1: case U2: case U3: case U4: case U5: case U6:
                  selectUpper(dice, keepMask, b.ordinal() + 1);
                  break;
               case TK:
                  selectThreeKind(dice, keepMask);
                  break;
               case FK:
                  selectFourKind(dice, keepMask);
                  break;
               case FH:
                  selectFullHouse(dice, keepMask);
                  break;
               case SS:
                  selectSmallStraight(dice, keepMask);
                  break;
               case LS: // large straight
               case Y:  // yahtzee
               case C:  // chance
                  Arrays.fill(keepMask, true);
                  break;
               case YB: // yahtzee bonus never rolled directly
                  Arrays.fill(keepMask, false);
                  break;
            }
         }
         results.put(b, new PatternMatch(found, keepMask));
      }
      return results;
   }

   private static void selectUpper(int[] dice, boolean[] keep, int face) {
      for (int i = 0; i < dice.length; i++) {
         if (dice[i] == face) keep[i] = true;
      }
   }

   private static void selectThreeKind(int[] dice, boolean[] keep) {
      int[] count = new int[7];
      for (int d : dice) count[d]++;
      int tripleFace = -1;
      for (int v = 1; v <= 6; v++) if (count[v] >= 3) tripleFace = v;
      if (tripleFace != -1) {
         for (int i = 0; i < dice.length; i++)
            if (dice[i] == tripleFace) keep[i] = true;
      }
   }

   private static void selectFourKind(int[] dice, boolean[] keep) {
      int[] count = new int[7];
      for (int d : dice) count[d]++;
      int quadFace = -1;
      for (int v = 1; v <= 6; v++) if (count[v] >= 4) quadFace = v;
      if (quadFace != -1) {
         for (int i = 0; i < dice.length; i++)
            if (dice[i] == quadFace) keep[i] = true;
      }
   }

   private static void selectFullHouse(int[] dice, boolean[] keep) {
      int[] count = new int[7];
      for (int d : dice) count[d]++;
      int triple = -1, pair = -1;
      for (int v = 1; v <= 6; v++) {
         if (count[v] == 3) triple = v;
         if (count[v] == 2) pair = v;
      }
      if (triple != -1 && pair != -1) {
         for (int i = 0; i < dice.length; i++) {
            if (dice[i] == triple || dice[i] == pair) keep[i] = true;
         }
      }
   }

   private static void selectSmallStraight(int[] dice, boolean[] keep) {
      int[][] straights = { {1,2,3,4}, {2,3,4,5}, {3,4,5,6} };

      for (int[] straight : straights) {
         boolean allPresent = true;
         for (int v : straight) {
            boolean found = false;
            for (int d : dice) if (d == v) { found = true; break; }
            if (!found) { allPresent = false; break; }
         }
         if (allPresent) {
            boolean[] used = new boolean[straight.length];
            for (int i = 0; i < dice.length; i++) {
               for (int j = 0; j < straight.length; j++) {
                  if (!used[j] && dice[i] == straight[j]) {
                     keep[i] = true;
                     used[j] = true;
                     break;
                  }
               }
            }
            break; // only one straight needed
         }
      }
   }




}
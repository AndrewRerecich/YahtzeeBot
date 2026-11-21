// StAuth10222: I Andrew Rerecich, 000845049 certify that this material is my original work. No other person's work has been used without due acknowledgement. I have not made my work available to anyone else.

/*
 * Starter code for the COMP 10185 Yahtzee Strategy Assignment
 * @author Andrew Rerecich
 * @version 202535.003
 *
 *
 */

import java.util.Arrays;
import java.util.Map;

public class YahtzeeStrategy implements Cloneable {
    // Before performing large numbers of sims, set this to false.
    // Printing to the screen is relatively slow and will cause your game to under perform.
    final boolean _STRAT_DEBUG_ = false;
    //final boolean _STRAT_DEBUG_ = true;

    public void debugWrite(String str) {
        if (_STRAT_DEBUG_)
            System.out.println(str);
    }

    private boolean is_open(Scorecard score, Scorecard.Boxes box) {
        return !score.isBoxFilled(box) && !score.isBoxScratched(box);
    }

    public boolean[] first_roll_strategy(int[] dice, Scorecard scorecard) {
        boolean[] keep = new boolean[5];

        debugWrite("First roll: " + Arrays.toString(dice));

        Map<Scorecard.Boxes, Die.PatternMatch> thisRollHas = Die.hasWithKeep(dice);
        int dice_sum = dice[0] + dice[1] + dice[2] + dice[3] + dice[4];

        // **** DO NOT SORT THE DICE ARRAY - the order is significant!! ****
        //
        // The main routine does NOT reorder the Die[] so you will go out of sync and keep the wrong dice.
        // Make a temp array if you need to for detection. See the logic in Die.hasWithKeep
        int[] tempRoll = dice.clone();
        Arrays.sort(tempRoll);

        // Always keep a yahtzee
        if (thisRollHas.get(Scorecard.Boxes.Y).present)
            return thisRollHas.get(Scorecard.Boxes.Y).keep;


        if (thisRollHas.get(Scorecard.Boxes.FK).present) {
            if (is_open(scorecard, Scorecard.Boxes.FK)) {
                return thisRollHas.get(Scorecard.Boxes.FK).keep;
            } else {
                if ((tempRoll[2] == 6 && (is_open(scorecard, Scorecard.Boxes.U6) || is_open(scorecard, Scorecard.Boxes.TK) || is_open(scorecard, Scorecard.Boxes.C))) ||
                        (tempRoll[2] == 5 && (is_open(scorecard, Scorecard.Boxes.U5) || is_open(scorecard, Scorecard.Boxes.TK) || is_open(scorecard, Scorecard.Boxes.C))) ||
                        (tempRoll[2] == 4 && (is_open(scorecard, Scorecard.Boxes.U4) || is_open(scorecard, Scorecard.Boxes.TK) || is_open(scorecard, Scorecard.Boxes.C))) ||
                        (tempRoll[2] == 3 && is_open(scorecard, Scorecard.Boxes.U3)) ||
                        (tempRoll[2] == 2 && is_open(scorecard, Scorecard.Boxes.U2)) ||
                        (tempRoll[2] == 1 && is_open(scorecard, Scorecard.Boxes.U1)))
                    return thisRollHas.get(Scorecard.Boxes.FK).keep;
                else if ((tempRoll[2] == 6 && !is_open(scorecard, Scorecard.Boxes.U6) && !is_open(scorecard, Scorecard.Boxes.TK))) {
                    if (is_open(scorecard, Scorecard.Boxes.FH))
                        return thisRollHas.get(Scorecard.Boxes.TK).keep;
                    else return thisRollHas.get(Scorecard.Boxes.FK).keep;
                } else return thisRollHas.get(Scorecard.Boxes.FK).keep;
            }
        }

        if (thisRollHas.get(Scorecard.Boxes.TK).present) {
            if (thisRollHas.get(Scorecard.Boxes.FH).present && is_open(scorecard, Scorecard.Boxes.FH)) {
                if (dice_sum < 21 || (!is_open(scorecard, Scorecard.Boxes.TK) && !is_open(scorecard, Scorecard.Boxes.FK) && dice_sum < 25)) {
                    return thisRollHas.get(Scorecard.Boxes.FH).keep;
                } else if (dice_sum < 25 && (is_open(scorecard, Scorecard.Boxes.TK) && is_open(scorecard, Scorecard.Boxes.FK))) {
                    return thisRollHas.get(Scorecard.Boxes.TK).keep;
                } else if (dice_sum >= 25 && (tempRoll[2] == 6 && (is_open(scorecard, Scorecard.Boxes.U6) || is_open(scorecard, Scorecard.Boxes.C))) ||
                        (tempRoll[2] == 5 && (is_open(scorecard, Scorecard.Boxes.U5) || is_open(scorecard, Scorecard.Boxes.C)))) {
                    return thisRollHas.get(Scorecard.Boxes.FH).keep;
                } else if (tempRoll[2] == 4 && is_open(scorecard, Scorecard.Boxes.U4)) {
                    return thisRollHas.get(Scorecard.Boxes.U4).keep;
                } else if (tempRoll[2] == 3 && is_open(scorecard, Scorecard.Boxes.U3)) {
                    return thisRollHas.get(Scorecard.Boxes.U3).keep;
                } else if (tempRoll[2] == 2 && is_open(scorecard, Scorecard.Boxes.U2)) {
                    return thisRollHas.get(Scorecard.Boxes.U2).keep;
                } else if (tempRoll[2] == 1 && is_open(scorecard, Scorecard.Boxes.U1)) {
                    return thisRollHas.get(Scorecard.Boxes.U1).keep;
                }
            } else if (!is_open(scorecard, Scorecard.Boxes.FH) || !thisRollHas.get(Scorecard.Boxes.FH).present) {
                if ((is_open(scorecard, Scorecard.Boxes.TK) || is_open(scorecard, Scorecard.Boxes.FK)) && tempRoll[2] >= 4) {
                    return thisRollHas.get(Scorecard.Boxes.TK).keep;
                } else if (tempRoll[2] == 3 && is_open(scorecard, Scorecard.Boxes.U3)) {
                    return thisRollHas.get(Scorecard.Boxes.U3).keep;
                } else if (tempRoll[2] == 2 && is_open(scorecard, Scorecard.Boxes.U2)) {
                    return thisRollHas.get(Scorecard.Boxes.U2).keep;
                } else if (tempRoll[2] == 1 && is_open(scorecard, Scorecard.Boxes.U1)) {
                    return thisRollHas.get(Scorecard.Boxes.U1).keep;
                }
            }
        }

        // Keep a large straight if we haven't previously scored one.
        if (thisRollHas.get(Scorecard.Boxes.LS).present && is_open(scorecard, Scorecard.Boxes.LS))
            return thisRollHas.get(Scorecard.Boxes.LS).keep;

        else if (thisRollHas.get(Scorecard.Boxes.SS).present) {
            if (is_open(scorecard, Scorecard.Boxes.SS)) {
                return thisRollHas.get(Scorecard.Boxes.SS).keep;
            } else if ((!is_open(scorecard, Scorecard.Boxes.SS)) && is_open(scorecard, Scorecard.Boxes.LS)) {
                if (has_a_pair(dice)) {
                    if (tempRoll[0] == tempRoll[1] && ((tempRoll[0] + tempRoll[1] == 8 && is_open(scorecard, Scorecard.Boxes.U4)) || (tempRoll[0] + tempRoll[1] == 10 && is_open(scorecard, Scorecard.Boxes.U5)) || (tempRoll[0] + tempRoll[1] == 12 && (is_open(scorecard, Scorecard.Boxes.U6) || is_open(scorecard, Scorecard.Boxes.C))))) {
                        keep_a_pair(dice);
                    } else if (tempRoll[1] == tempRoll[2] && ((tempRoll[1] + tempRoll[2] == 8 && is_open(scorecard, Scorecard.Boxes.U4)) || (tempRoll[1] + tempRoll[2] == 10 && is_open(scorecard, Scorecard.Boxes.U5)) || (tempRoll[1] + tempRoll[2] == 12 && (is_open(scorecard, Scorecard.Boxes.U6) || is_open(scorecard, Scorecard.Boxes.C))))) {
                        keep_a_pair(dice);
                    } else if (tempRoll[2] == tempRoll[3] && ((tempRoll[2] + tempRoll[3] == 8 && is_open(scorecard, Scorecard.Boxes.U4)) || (tempRoll[2] + tempRoll[3] == 10 && is_open(scorecard, Scorecard.Boxes.U5)) || (tempRoll[2] + tempRoll[3] == 12 && (is_open(scorecard, Scorecard.Boxes.U6) || is_open(scorecard, Scorecard.Boxes.C))))) {
                        keep_a_pair(dice);
                    } else if (tempRoll[3] == tempRoll[4] && ((tempRoll[3] + tempRoll[4] == 8 && is_open(scorecard, Scorecard.Boxes.U4)) || (tempRoll[3] + tempRoll[4] == 10 && is_open(scorecard, Scorecard.Boxes.U5)) || (tempRoll[3] + tempRoll[4] == 12 && (is_open(scorecard, Scorecard.Boxes.U6) || is_open(scorecard, Scorecard.Boxes.C))))) {
                        keep_a_pair(dice);
                    } else {
                        return thisRollHas.get(Scorecard.Boxes.SS).keep;
                    }
                }
            }
        }


        // Here we use a custom detector/selector because it wasn't built in but we
        // are interested in this pattern. Note I wrote the methods to use the
        // unsorted dice array as a simplification.
        if (has_two_pair(dice) && is_open(scorecard, Scorecard.Boxes.FH))
            return keep_two_pair(dice);
        else if (has_two_pair(dice) && !is_open(scorecard, Scorecard.Boxes.FH))
            return keep_a_pair(dice);
        else if (has_a_pair(dice)) {
            if (pair_numbers(dice) > 3 || (pair_numbers(dice) == 2 && is_open(scorecard, Scorecard.Boxes.U2)) || (pair_numbers(dice) == 1 && is_open(scorecard, Scorecard.Boxes.U1)))
                return keep_a_pair(dice);
        } else if (tempRoll[1] == 2 && tempRoll[2] == 3 && tempRoll[3] == 5 && (is_open(scorecard, Scorecard.Boxes.SS)) || is_open(scorecard, Scorecard.Boxes.LS))
            for (int i = 0; i <= 4; i++) {
                if (dice[i] == 2 || dice[i] == 3 || dice[i] == 5 || dice[i] == 6)   {
                    keep[i] = true;
                }
            }


        debugWrite("Keeping: " + Arrays.toString(keep));
        return keep;
    }

    public boolean[] second_roll_strategy(int[] dice, Scorecard scorecard) {
        debugWrite( "Second roll: " + Arrays.toString(dice) );
        return first_roll_strategy(dice, scorecard);
    }

    public Scorecard.Boxes scoring_phase_strategy(int[] dice, Scorecard scorecard) {
        debugWrite("Starting Scoring Phase:");
        debugWrite("Dice: " + Arrays.toString(dice));

        Scorecard.Boxes box = null;
        // returning null will trigger the scratch routine.
        // returning all booleans as true will trigger the scoring routine.

        // Scoring Priority is set by ordering.

        // what patterns match our dice?
        Map<Scorecard.Boxes, Boolean> thisRollHas = Die.has(dice);
        int dice_sum = dice[0] + dice[1] + dice[2] + dice[3] + dice[4];

        // does the roll have a yahtzee? don't test YB directly.
        if (thisRollHas.get(Scorecard.Boxes.Y))
            return Scorecard.Boxes.Y;

        if (thisRollHas.get(Scorecard.Boxes.LS) && is_open(scorecard, Scorecard.Boxes.LS))
            return Scorecard.Boxes.LS;

        if (thisRollHas.get(Scorecard.Boxes.FK) && is_open(scorecard, Scorecard.Boxes.FK)) {
            if (dice_sum >= 21)
                return Scorecard.Boxes.FK;
        }

        if (thisRollHas.get(Scorecard.Boxes.TK) && is_open(scorecard, Scorecard.Boxes.TK)) {
            if (!thisRollHas.get(Scorecard.Boxes.FH) && dice_sum >= 18)
                return Scorecard.Boxes.TK;
        }
        if (thisRollHas.get(Scorecard.Boxes.FH) && is_open(scorecard, Scorecard.Boxes.FH)) return Scorecard.Boxes.FH;
        if (thisRollHas.get(Scorecard.Boxes.SS) && is_open(scorecard, Scorecard.Boxes.SS)) return Scorecard.Boxes.SS;


        if (thisRollHas.get(Scorecard.Boxes.C) && is_open(scorecard, Scorecard.Boxes.C) && dice_sum >= 20 && !thisRollHas.get(Scorecard.Boxes.TK))
            return Scorecard.Boxes.C;

        if (thisRollHas.get(Scorecard.Boxes.U6) && is_open(scorecard, Scorecard.Boxes.U6) && thisRollHas.get(Scorecard.Boxes.TK) && dice[2] == 6)
            return Scorecard.Boxes.U6;
        if (thisRollHas.get(Scorecard.Boxes.U5) && is_open(scorecard, Scorecard.Boxes.U5) && thisRollHas.get(Scorecard.Boxes.TK) && dice[2] == 5)
            return Scorecard.Boxes.U5;
        if (thisRollHas.get(Scorecard.Boxes.U4) && is_open(scorecard, Scorecard.Boxes.U4) && thisRollHas.get(Scorecard.Boxes.TK) && dice[2] == 4)
            return Scorecard.Boxes.U4;
        if (thisRollHas.get(Scorecard.Boxes.U3) && is_open(scorecard, Scorecard.Boxes.U3) && thisRollHas.get(Scorecard.Boxes.TK) && dice[2] == 3)
            return Scorecard.Boxes.U3;
        if (thisRollHas.get(Scorecard.Boxes.U2) && is_open(scorecard, Scorecard.Boxes.U2) && thisRollHas.get(Scorecard.Boxes.TK) && dice[2] == 2)
            return Scorecard.Boxes.U2;
        if (thisRollHas.get(Scorecard.Boxes.U1) && is_open(scorecard, Scorecard.Boxes.U1) && thisRollHas.get(Scorecard.Boxes.TK) && dice[2] == 1)
            return Scorecard.Boxes.U1;
        if (thisRollHas.get(Scorecard.Boxes.U1) && is_open(scorecard, Scorecard.Boxes.U1)) return Scorecard.Boxes.U1;
        if (thisRollHas.get(Scorecard.Boxes.U2) && is_open(scorecard, Scorecard.Boxes.U2)) return Scorecard.Boxes.U2;
        if (thisRollHas.get(Scorecard.Boxes.U3) && is_open(scorecard, Scorecard.Boxes.U3)) return Scorecard.Boxes.U3;
        if (thisRollHas.get(Scorecard.Boxes.U4) && is_open(scorecard, Scorecard.Boxes.U4)) return Scorecard.Boxes.U4;
        if (thisRollHas.get(Scorecard.Boxes.U5) && is_open(scorecard, Scorecard.Boxes.U5)) return Scorecard.Boxes.U5;
        if (thisRollHas.get(Scorecard.Boxes.U6) && is_open(scorecard, Scorecard.Boxes.U6)) return Scorecard.Boxes.U6;

        if (thisRollHas.get(Scorecard.Boxes.C) && is_open(scorecard, Scorecard.Boxes.C)) return Scorecard.Boxes.C;

        return null;
    }

    public Scorecard.Boxes joker_scoring(Scorecard score, Die[] dice) {
        // You may change the order of jokerScoring as you wish.

        Scorecard.Boxes box = null;

        // Joker Rules: Score the total of all 5 dice in the appropriate upper section box (enforced by Scorecard)
        //              If the appropriate upper section box is still open, Scorecard will place it automatically —
        //              this method is only consulted once that box is unavailable.

        //              If that box has already been filled in, score as follows in any
        //              lower section box: 3K, 4K, FH, SS, LS, C as normal rules.

        // These can be re-ordered or additional logic placed here to improve the score.
        if (!score.isBoxFilled(Scorecard.Boxes.TK) && !score.isBoxScratched(Scorecard.Boxes.TK))
            return Scorecard.Boxes.TK;
        if (!score.isBoxFilled(Scorecard.Boxes.FK) && !score.isBoxScratched(Scorecard.Boxes.FK))
            return Scorecard.Boxes.FK;
        if (!score.isBoxFilled(Scorecard.Boxes.FH) && !score.isBoxScratched(Scorecard.Boxes.FH))
            return Scorecard.Boxes.FH;
        if (!score.isBoxFilled(Scorecard.Boxes.SS) && !score.isBoxScratched(Scorecard.Boxes.SS))
            return Scorecard.Boxes.SS;
        if (!score.isBoxFilled(Scorecard.Boxes.LS) && !score.isBoxScratched(Scorecard.Boxes.LS))
            return Scorecard.Boxes.LS;
        if (!score.isBoxFilled(Scorecard.Boxes.C) && !score.isBoxScratched(Scorecard.Boxes.C)) return Scorecard.Boxes.C;

        //              If the appropriate upper section box is filled in, and all lower section
        //              boxes are filled in, you must enter a zero in any open upper section box.
        switch (dice[0].getCurrentFace()) {
            case 1 -> box = Scorecard.Boxes.U1;
            case 2 -> box = Scorecard.Boxes.U2;
            case 3 -> box = Scorecard.Boxes.U3;
            case 4 -> box = Scorecard.Boxes.U4;
            case 5 -> box = Scorecard.Boxes.U5;
            case 6 -> box = Scorecard.Boxes.U6;
        }

        if (!score.isBoxFilled(box) && !score.isBoxScratched(box)) return box;

        return null;
    }

    public Scorecard.Boxes scratch_strategy(Scorecard score) {
        // Initial logic is quite trivial and should be replaced.

        // The loop goes from the top of the scorecard downward looking for a box to scratch.
        for (Scorecard.Boxes box : Scorecard.Boxes.values()) {
            if (!score.isBoxFilled(box) && !score.isBoxScratched(box))
                return box;
        }

        throw new IllegalStateException("No available boxes to scratch found");
    }

    /**
     * Example of writing a custom detector/selector for your own
     * interesting patterns.
     * <p>
     * <p>
     * Detect whether the dice contain two distinct pairs.
     * <p>
     * Examples:
     * [2,2,5,5,6] -> true
     * [3,3,4,4,4] -> true (two pair plus extra)
     * [1,1,1,2,2] -> true (full house has two pairs inside)
     * [6,6,6,6,2] -> false (four of a kind, only one distinct value)
     * [1,2,3,4,5] -> false
     */
    public boolean has_two_pair(int[] dice) {
        int[] counts = new int[7]; // counts[1..6]

        // enumerate how many of each value is present
        for (int d : dice) counts[d]++;

        // if a value has >= 2 of that value then it's a pair
        // yes, we are calling a TK or FK a pair, but with
        // 5 dice total the two pair condition might fail.
        // if you want TK/FK to return false, just change the >= to ==
        int pairs = 0;
        for (int v = 1; v <= 6; v++) {
            if (counts[v] >= 2) pairs++;
        }

        return pairs >= 2;
    }

    public boolean has_a_pair(int[] dice) {
        int[] counts = new int[7]; // counts[1..6]

        // enumerate how many of each value is present
        for (int d : dice) counts[d]++;

        // if a value has >= 2 of that value then it's a pair
        // yes, we are calling a TK or FK a pair, but with
        // 5 dice total the two pair condition might fail.
        // if you want TK/FK to return false, just change the >= to ==
        int pair = 0;
        for (int v = 1; v <= 6; v++) {
            if (counts[v] == 2) pair++;
        }

        return pair == 1;
    }

    public boolean[] keep_two_pair(int[] dice) {
        boolean[] keep = new boolean[dice.length];

        // Count how many times each face appears
        int[] counts = new int[7]; // 1..6
        for (int d : dice) counts[d]++;

        // Identify the two values that form pairs
        int foundPairs = 0;
        int[] pairValues = new int[2];
        for (int v = 1; v <= 6; v++) {
            if (counts[v] >= 2) {
                if (foundPairs < 2) {
                    pairValues[foundPairs++] = v;
                }
            }
        }

        // Not exactly two pairs? return empty mask
        if (foundPairs < 2) return keep;

        // Mark dice belonging to either pair
        for (int i = 0; i < dice.length; i++) {
            if (dice[i] == pairValues[0] || dice[i] == pairValues[1]) {
                keep[i] = true;
            }
        }

        return keep;
    }

    public boolean[] keep_a_pair(int[] dice) {
        boolean[] keep = new boolean[dice.length];

        int[] counts = new int[7]; // 1..6
        for (int d : dice) counts[d]++;

        int foundPair = 0;
        int[] pairValues = new int[1];
        for (int v = 6; v >= 1; v--) {
            if (counts[v] == 2) {
                if (foundPair < 1) {
                    pairValues[foundPair++] = v;
                }
            }
        }

        if (foundPair < 1) return keep;

        for (int i = 0; i < dice.length; i++) {
            if (dice[i] == pairValues[0]) {
                keep[i] = true;
            }
        }

        return keep;
    }

    public int pair_numbers(int[] dice) {

        int[] counts = new int[7]; // 1..6
        for (int d : dice) counts[d]++;

        for (int v = 6; v >= 1; v--) {
            if (counts[v] == 2) {
                return v;
            }
        }

        return -1;

    }

    public boolean has_SSS(int[] dice) {
        boolean[] keep = new boolean[dice.length];

        int[] counts = new int[7]; // 1..6
        for (int d : dice) counts[d]++;

        int[] pairValues = new int[1];
        for (int v = 6; v >= 3; v--) {
            if (counts[v] >= 1 && counts[v - 1] >= 1 && counts[v - 2] >= 1) {
                return true;
            }
        }

        return false;

    }


}
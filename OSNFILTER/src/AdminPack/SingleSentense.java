/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AdminPack;

import java.io.Serializable;
import java.util.Vector;

public class SingleSentense implements Serializable {

    public String originalSentence, stemmedSentence;
    public Vector<String> matchingComments;
    public int score = 0;
    public static String searchName;
    public static Vector<String> inputReviews = new Vector<String>();

    public SingleSentense() {
        originalSentence = stemmedSentence = "";
        matchingComments = new Vector<String>();
        score = 0;
        searchName = "";

    }
}

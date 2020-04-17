/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AdminPack;

/**
 *
 * @author Oorja
 */
import facebook4j.*;
import facebook4j.conf.Configuration;
import facebook4j.conf.ConfigurationBuilder;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

public class GetReviewsFaceBook {

    public static ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();

    public static void fetchReviews() throws FacebookException, IOException {
        configurationBuilder.setDebugEnabled(true);
        configurationBuilder.setOAuthAppId("616225431810457");
        configurationBuilder.setOAuthAppSecret("616225431810457|s4jGja-8S_gj3KxSJmEGm5jhGTk");
        configurationBuilder.setOAuthAccessToken("CAAIwdCn28ZAkBAD9qYqybZANRgtZB6qCJ9gknJbVytOV8LUM0lDURGF0OjeiF7tNXTSSEQEZBdLbFzycMzb1SDPmkvI10r9cdPirOn0Q847PM4boCPW6dnGe8t2lPEFSyLsXT7oEO5VaxL9zqNjCtvywBckl1m7xZCoxU3xIxr4HgIqZCqwkupu2jVeMvzvVPm1WPnQE2RqvpoMQo6cFXX");
        configurationBuilder.setOAuthPermissions("shreehari.ahire@gmail.com, publish_stream, id, ShreeApp, oorja, ot, read_stream , generic");
        configurationBuilder.setUseSSL(true);
        configurationBuilder.setJSONStoreEnabled(true);
        Configuration configuration = configurationBuilder.build();
        FacebookFactory ff = new FacebookFactory(configuration);
        Facebook Facebook = ff.getInstance();
        try {
            // Set search string and get results
            String searchPost = SingleSentense.searchName;
            System.out.println("Name of Bike: " + SingleSentense.searchName);
            File fin = new File("D:\\ProjectData\\8943DB\\FacebookConfigFolder\\File\\" + searchPost + ".txt");
            if (fin.exists()) {
                fin.delete();
            }
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy_hh_mm");
            String fileName = "D:\\ProjectData\\8943DB\\FacebookConfigFolder\\File\\" + searchPost + ".txt";
            String results = getFacebookPostes(Facebook, searchPost);
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(results);
                bw.close();
                System.out.println("Completed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This method is used to get Facebook posts based on the search string set 
    // above
    public static String getFacebookPostes(Facebook Facebook, String searchPost) throws FacebookException {
        //String searchResult = "Item : " + searchPost + "\n";
        String searchResult = "";
        int cnt = 0;
        StringBuffer searchMessage = new StringBuffer();
        ResponseList<Post> results = Facebook.getPosts(searchPost);
        for (Post post : results) {
            //  System.out.println(post.getMessage());
            //  searchMessage.append(post.getMessage() + "\n");
            for (int j = 0; j < post.getComments().size(); j++) {
                String temp = post.getComments().get(j).getMessage();
                String temp1 = post.getComments().get(j).getCreatedTime().toString();
                if ((temp.isEmpty()) || (temp.contains("http")) || (temp.contains("www.")) || (post.getComments().get(j).getCreatedTime().toString().isEmpty())) {
                    continue;
                } else {
                    String parse_String = parse_String(temp);
                    String parse_time = parse_Time(temp1);
                    System.out.println("Input String: " + temp1);
                    System.out.println("Parse String: " + parse_time);
                    if ((parse_String.equals("")) || parse_time.equals("")) {
                        continue;
                    } else {
                        searchMessage.append(post.getComments().get(j).getFrom().getName() + "## ");
                        searchMessage.append(parse_String + "##");
                        searchMessage.append(parse_time + "##");
                    }

                }
            }
        }
        //   String feedString = getFacebookFeed(Facebook, searchPost);
        //searchResult = searchResult + searchMessage.toString();
        searchResult = searchMessage.toString();
        // searchResult = searchResult + feedString;
        return searchResult;
    }
///Function Will PArse Comments

    public static String parse_String(String input) {
        String return_output = "";
        int len = input.length();
        for (int i = 0; i < len; i++) {
            Character ss = input.charAt(i);
            int ascc = (int) ss;
            if (ascc == 32) {
                return_output += " ";
            } else {
                if ((ascc >= 64 && ascc <= 90) || (ascc >= 97 && ascc <= 122)) {
                    return_output += ss;
                }
            }
        }
        return return_output;
    }

    ///Function Will PArse Date and time of Comment
    public static String parse_Time(String input) {
        String return_output = "";
        int len = input.length();
        for (int i = 0; i < len; i++) {
            Character ss = input.charAt(i);
            int ascc = (int) ss;
            if (ascc == 32) {
                return_output += " ";
            } else if (ascc == 58) {
                return_output += ":";
            } else {
                if ((ascc >= 64 && ascc <= 90) || (ascc >= 97 && ascc <= 122) || (ascc >= 48 && ascc <= 57)) {
                    return_output += ss;
                }
            }
        }
        return return_output;
    }

// This method is used to get Facebook feeds based on the search string set
// above
    public static String getFacebookFeed(Facebook Facebook, String searchPost) throws FacebookException {
        String searchResult = "";
        StringBuffer searchMessage = new StringBuffer();
        ResponseList<Post> results = Facebook.getFeed(searchPost);
        for (Post post : results) {
            System.out.println(post.getMessage());
            //   searchMessage.append(post.getFrom().getName() + ", ");
            searchMessage.append(post.getMessage() + ", ");
            searchMessage.append(post.getCreatedTime() + "\n");
        }
        searchResult = searchResult + searchMessage.toString();
        return searchResult;
    } // This method is used to create JSON object from data string

    public static String stringToJson(String data) {
        JsonConfig cfg = new JsonConfig();
        try {
            JSONObject jsonObject = JSONObject.fromObject(data, cfg);
            System.out.println("JSON = " + jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "JSON Created";
    }
}

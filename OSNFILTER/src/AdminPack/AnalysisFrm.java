/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AdminPack;

import MyPack.TestimonialInfo;
import dataPack.Stemmer;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.demo.PieChartDemo1;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.RefineryUtilities;

public class AnalysisFrm extends javax.swing.JFrame {

    MainForm parent;
    Connection con;
    Vector<PnlComment> panelVec;
    Vector<TestimonialInfo> commentVec;
    int selectedComment = -1;
    DefaultComboBoxModel cm;
    int non_spam_cnt = 0, spam_cnt = 0, total_cnt = 0;
    double spam_percentage = 0, non_spam_percentage = 0;
    TestimonialInfo ti;
    Statement stmt;
    public int no_of_dots = 0, no_of_slashes = 0, is_suspicious = 0, is_not_https = 0, no_of_http = 0, no_of_https = 0, no_of_special = 0;
    Vector<TestimonialInfo> allTestimonials;
    Vector<String> allTokens, allUrlsTokens, allSentenceTokens, allNegativeDatabase, spamUrls, whiteListUrl, blackListUrl;

    public AnalysisFrm(MainForm parent) {
        this.parent = parent;
        initComponents();
        Dimension sd = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(sd.width / 2 - this.getWidth() / 2, sd.height / 2 - this.getHeight() / 2);

    }

    //This Funcation Will Download and Store the Downloadded comments in a txt File
    public void Download_Comments() {
        SingleSentense.searchName = "honda";
        // System.out.println("Search name in Call:" + SingleSentense.searchName);
        try {
            GetReviewsFaceBook.fetchReviews();
        } catch (Exception e) {
            System.out.println("Failed To Load Data");
        }
    }

    //This Function Will Read Comments From The Txt File
    public void read_comments() {
        SingleSentense.searchName = "honda";
        String fileName = "D:\\ProjectData\\8943DB\\FacebookConfigFolder\\File\\" + SingleSentense.searchName + ".txt";
        String comment = "";
        String date = "";
        int cnt = 0;
        String temp = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            while (br.ready()) {
                temp += br.readLine();
            }

            if (temp.contains("##")) {
                StringTokenizer st = new StringTokenizer(temp, "##");
                while (st.hasMoreTokens()) {
                    st.nextToken().toString();
                    comment = st.nextToken().toString();
                    String wholeDate = st.nextToken().trim();
                    System.out.println(wholeDate);
                    if (wholeDate.contains(" ")) {
                        StringTokenizer st1 = new StringTokenizer(wholeDate, " ");
                        String day = st1.nextToken();
                        String month = st1.nextToken();
                        String no = st1.nextToken();
                        String time = st1.nextToken();
                        String tt = st1.nextToken();
                        String year = st1.nextToken();
                        SingleSentense.inputReviews.add(comment);
                        update_Database(day, month, no, time, year, comment);
                    }
                }
            }
            fillReviews();
        } catch (Exception e) {
            System.out.println("Error in Reading File " + SingleSentense.searchName);
            e.printStackTrace();
        }
    }
    //This Function Will store Comments in Database

    public void update_Database(String day, String month, String no, String time, String year, String comment) {
        String date = "";
        date += no + "/";
        if (month.equalsIgnoreCase("Jan")) {
            date += "1/";
        } else if (month.equalsIgnoreCase("Feb")) {
            date += "2/";
        } else if (month.equalsIgnoreCase("Mar")) {
            date += "3/";
        } else if (month.equalsIgnoreCase("Apr")) {
            date += "4/";
        } else if (month.equalsIgnoreCase("May")) {
            date += "5/";
        } else if (month.equalsIgnoreCase("Jun")) {
            date += "6/";
        } else if (month.equalsIgnoreCase("Jul")) {
            date += "7/";
        } else if (month.equalsIgnoreCase("Aug")) {
            date += "8/";
        } else if (month.equalsIgnoreCase("Sep")) {
            date += "9/";
        } else if (month.equalsIgnoreCase("Oct")) {
            date += "10/";
        } else if (month.equalsIgnoreCase("Nov")) {
            date += "11/";
        } else if (month.equalsIgnoreCase("Dec")) {
            date += "12/";
        }
        date += year;
        //  System.out.println("Final: " + date);
        parent.initDatabase();
        int tid = getNextID();
        String finalComment = "";
        finalComment = comment.replace("'", "");
        try {
            stmt = parent.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            String ssql = "insert into testimonial values(" + tid + ",'" + finalComment + "','" + date + "','" + time + "')";
            System.out.println("Query: " + ssql);
            stmt.executeUpdate(ssql);
        } catch (Exception e) {
            System.out.println("Error Inserting data : " + e);
            e.printStackTrace();
        }
    }

    //This Funcation REturns The Max Count From Table
    public int getNextID() {
        int max = 1;
        try {
            String ssql;
            ResultSet rs;
            stmt = parent.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ssql = "select MAX(tid) from testimonial";
            System.out.println(ssql);
            rs = stmt.executeQuery(ssql);
            rs.first();
            int tid = rs.getInt(1);
            max = tid + 1;
            //   System.out.println("MAX:" + max);
        } catch (Exception e) {
            System.out.println("Error Geting Next ID : " + e);

        }
        return max;
    }

    //This Function Will Seperate the comments from the Database and Store In vector
    void read_Comments_Database() {
        parent.initDatabase();
        SingleSentense.inputReviews.clear();
        try {
            allTestimonials = new Vector<TestimonialInfo>();
            stmt = parent.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            String ssql = "select * from testimonial";
            System.out.println(ssql);
            ResultSet rs = stmt.executeQuery(ssql);
            while (rs.next()) {
                ti = new TestimonialInfo();
                ti.tid = rs.getInt("tid");
                ti.mdate = rs.getString("mdate");
                SingleSentense.inputReviews.add(rs.getString("text"));
                ti.time = rs.getString("mtime");
                allTestimonials.addElement(ti);
            }
            fillReviews();
            //  System.out.println("Query Executed");
        } catch (Exception e) {
            System.out.println("Error Fetching data : " + e);
        }
    }

    //This Function will Fill the separated Reviews in the List
    public void fillReviews() {
        commentVec = new Vector<TestimonialInfo>();
        panelVec = new Vector<PnlComment>();
        int maxComments = SingleSentense.inputReviews.size();
        jPanelHolder.removeAll();
        jScrollPane1.getViewport().removeAll();
        if (maxComments > 8) {
            jPanelHolder.setLayout(new GridLayout(maxComments, 1));
        } else {
            jPanelHolder.setLayout(new GridLayout(8, 1));
        }
        for (int i = 0; i < maxComments; i++) {
            TestimonialInfo ti = new TestimonialInfo();
            ti.to = SingleSentense.searchName;
            ti.text = SingleSentense.inputReviews.elementAt(i).toString();
            commentVec.addElement(ti);
            PnlComment p = new PnlComment(ti);
            panelVec.addElement(p);

            jPanelHolder.add(p);
        }
        jScrollPane1.getViewport().add(jPanelHolder);
    }

    //To Read Negative Comments From Databse
    public void readNegComments() {
        allNegativeDatabase = new Vector<String>();

        try {
            Statement st = MainForm.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = st.executeQuery("select * from negativeComments");
            while (rs.next()) {
                allNegativeDatabase.add(rs.getString("stemmedComment"));
            }
        } catch (Exception e) {
            System.out.println("Erro in reading positive table");
            e.printStackTrace();
        }
    }

    public boolean check_urlPresent(String url) {
        int cnt = 0;
        //Check URL
        if (url.contains("http://")) {
            cnt++;
        }
        if (url.contains("https://")) {
            cnt++;
        }
        if (url.contains(".com")) {
            cnt++;
        }
        if (url.contains("www")) {
            cnt++;
        }
        if (cnt >= 1) {
            return true;
        } else {
            return false;
        }
    }

    public boolean check_Bad_Contents() {
        readNegComments();
        boolean status = false;
        Vector<Integer> negativeVector = new Vector<Integer>();
        Vector<Integer> sentVector = new Vector<Integer>();
        for (int i = 0; i < allNegativeDatabase.size(); i++) {
            negativeVector.add(1);
        }

        for (int i = 0; i < allNegativeDatabase.size(); i++) {
            sentVector.add(0);
        }
     //   System.out.println("Size: " + allSentenceTokens.size());
        for (int i = 0; i < allNegativeDatabase.size(); i++) {
            for (int j = 0; j < allSentenceTokens.size(); j++) {
                if (allNegativeDatabase.get(i).equalsIgnoreCase(allSentenceTokens.get(j))) {
                    sentVector.set(i, 1);

                }
            }
        }

        //Apply Cosine Similarity
        //Formula x*y/(x)1/2 *(y)1/2
        double SumCosine = 0;
        double temp = 0;
        for (int i = 0; i < sentVector.size(); i++) {
            if (((Math.sqrt(negativeVector.get(i))) * (Math.sqrt(sentVector.get(i)))) == 0) {
                SumCosine += 0;
            } else {
                temp = (Double) ((negativeVector.get(i) * sentVector.get(i)) / (Math.sqrt(negativeVector.get(i)) * (Math.sqrt(sentVector.get(i)))));
                SumCosine += temp;
            }
        }

        //Apply Jaccard 
        //Formula x*y/(x)+(y)-(x*y)
        double SumJacard = 0;
        double temp1 = 0;
        for (int i = 0; i < sentVector.size(); i++) {
            temp1 = ((negativeVector.get(i)) + (sentVector.get(i))) - ((negativeVector.get(i)) * (sentVector.get(i)));
            if (temp1 == 0) {
                SumJacard += 0;
            } else {
                temp = (Double) ((negativeVector.get(i) * sentVector.get(i)) / (temp1));
                SumJacard += temp;
            }
        }
        double avg = (SumCosine + SumJacard) / 2;
        if (avg > 0) {
            status = true;
        }
        return status;
    }

    public void read_white_List_URL() {
        whiteListUrl = new Vector<String>();
        try {
            String ssql = "select * from urlContents";
            System.out.println("Query :" + ssql);
            stmt = parent.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery(ssql);
            while (rs.next()) {
                whiteListUrl.add(rs.getString("url"));
            }

        } catch (Exception e) {
            System.out.println("EEROR DISPLAYING DATA" + e);
        }
    }

    public void read_black_List_URL() {
        blackListUrl = new Vector<String>();
        try {
            String ssql = "select * from BlackListurl";
            System.out.println("Query :" + ssql);
            stmt = parent.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery(ssql);
            while (rs.next()) {
                blackListUrl.add(rs.getString("url"));
            }

        } catch (Exception e) {
            System.out.println("EEROR DISPLAYING DATA" + e);
        }
    }

    public boolean check_Valid_URL() {
        boolean status = false, checkSpamURl = true;
        boolean foundBlackListUrl = false;
        spamUrls = new Vector<String>();
        read_white_List_URL();
        read_black_List_URL();
        System.out.println("Url Token Size: " + allUrlsTokens.size());
        for (int j = 0; j < allUrlsTokens.size(); j++) {
            no_of_dots = 0;
            no_of_slashes = 0;
            is_not_https = 0;
            no_of_http = 0;
            no_of_https = 0;
            checkSpamURl = true;
            status = false;
            foundBlackListUrl = false;
            String url = allUrlsTokens.get(j);
            System.out.println("Found URL: " + url);
            for (int i = 0; i < whiteListUrl.size(); i++) {
                if (whiteListUrl.get(i).contains(url)) {
                //    System.out.println("Found White List$$$$$$$$$$$$$");
                    checkSpamURl = false;
                    break;
                }
            }
            //First Check For Black Lsit URL
            if (checkSpamURl) {
               // System.out.println("In White Not Found");
                for (int i = 0; i < blackListUrl.size(); i++) {
                    if (blackListUrl.get(i).contains(url)) {
                   //     System.out.println("Found Black List$$$$$$$$$$$$$");
                        foundBlackListUrl = true;
                        break;
                    }
                }
            } else {
                continue;
            }

            if (!foundBlackListUrl) {
                status = true;
             //   System.out.println("Status 1:  " + status);
            } else {
                //  if (checkSpamURl) {
                if (!url.toLowerCase().startsWith("https://")) {
                    is_not_https = 1;
                    no_of_http++;
                    char[] arr = url.toCharArray();
                    for (int i = 0; i < arr.length; i++) {
                        if (arr[i] == '.') {
                            no_of_dots++;
                        } else if (arr[i] == '/') {
                            no_of_slashes++;
                        } else {
                            if (!(((arr[i] >= 'a') && (arr[i] <= 'z')) || ((arr[i] >= 'A') && (arr[i] <= 'Z')) || ((arr[i] >= '0') && (arr[i] <= '9')))) {
                                is_suspicious++;
                            }
                        }
                    }
               //     System.out.println("Number Of Dots: " + no_of_dots);
               //     System.out.println("Number Of Slashes: " + no_of_slashes);
               //     System.out.println("Suspicious Count: " + is_suspicious);
               //     System.out.println("Nuber Of Https: " + no_of_https);
               //     System.out.println("Nuber Of Http: " + no_of_http);
                    int finalCount = no_of_dots + no_of_slashes + no_of_special;
                    if (finalCount > 8 || is_suspicious > 4) {
                        status = true;
                  //      System.out.println("Status 2:  " + status);
                        break;
                    }
                }
            }
        }
      //  System.out.println("Returning Status: 3" + status);
        return status;
    }

    //This Function Will Be called Each Time new IUtem Is Selected From The List
    void apply_processing() {
        spam_cnt = 0;
        non_spam_cnt = 0;
        spam_percentage = 0;
        non_spam_percentage = 0;
        String str = "";
        Stemmer stem = new Stemmer();
        int urlCnt = 0;

        for (int i = 0; i < commentVec.size(); i++) {
            String source = commentVec.elementAt(i).text;

            String toStem = commentVec.elementAt(i).text;
            if (!toStem.endsWith(".") || !toStem.endsWith("?") || !toStem.endsWith("!")) {
                toStem = toStem + ".";
            }
            str = stem.StemmerResult(toStem);
            str = str.substring(0, str.length() - 1);
            allUrlsTokens = new Vector<String>();
            allSentenceTokens = new Vector<String>();
            StringTokenizer st = new StringTokenizer(str, " ");
            while (st.hasMoreTokens()) {
                String temp = st.nextToken();
                if (check_urlPresent(temp)) {
                    allUrlsTokens.add(temp);
                } else {
                    allSentenceTokens.add(temp);
                }
            }
            boolean flag = check_Valid_URL();
            System.out.println("Return Falg: :::::::::::::::::::::::::::::::" + flag);
            if (flag) {

                spam_cnt++;
                panelVec.elementAt(i).setBackground(new Color(255, 145, 145));
            } else {
                boolean BadContentStatus = check_Bad_Contents();
                if (BadContentStatus) {
                    String op = "";
                    spam_cnt++;
                    panelVec.elementAt(i).setBackground(new Color(255, 145, 145));
                } else {
                    non_spam_cnt++;
                    panelVec.elementAt(i).setBackground(new Color(164, 238, 186));
                }
            }
        }

        System.out.println("Spam Cnt: " + spam_cnt);
        System.out.println("Non Spam Cnt: " + non_spam_cnt);
        total_cnt = spam_cnt + non_spam_cnt;
        spam_percentage = (spam_cnt * 100) / total_cnt;
        non_spam_percentage = (non_spam_cnt * 100) / total_cnt;
        System.out.println("Spam Cnt: " + spam_cnt);
        System.out.println("Non Spam Cnt: " + non_spam_cnt);
        System.out.println("Spam Cnt %: " + spam_percentage);
        System.out.println("Non Spam Cnt %: " + non_spam_percentage);
        lblTotalComments.setText("TOTAL COMMENTS :" + total_cnt);
        lblTotalNonSpam.setText("TOTAL NON SPAM COMMENTS :" + non_spam_cnt);
        lbltotalSpam.setText("TOTAL SPAM COMMENTS :" + spam_cnt);
        lblspamPErc.setText("SPAM PERCENTAGE :" + spam_percentage);
        lblnonSpamPerc.setText("NON SPAM PERCENTAGE :" + non_spam_percentage);



        double SpamDegree = (360 * spam_cnt) / (total_cnt);
        double nonSpamDegree = (360 * non_spam_cnt) / (total_cnt);


        new PieChart("Name", SpamDegree, nonSpamDegree).setVisible(true);


    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanelHolder = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        lblTotalComments = new javax.swing.JLabel();
        lblTotalNonSpam = new javax.swing.JLabel();
        lblnonSpamPerc = new javax.swing.JLabel();
        lbltotalSpam = new javax.swing.JLabel();
        lblspamPErc = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(132, 150, 150));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        jPanel1.setAutoscrolls(true);

        new JavaLib.LoadForm();
        jLabel1.setBackground(new java.awt.Color(98, 124, 124));
        jLabel1.setFont(new java.awt.Font("Aparajita", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("SPAM ANALYSIS");
        jLabel1.setOpaque(true);

        jPanel2.setBackground(new java.awt.Color(221, 230, 230));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        new JavaLib.LoadForm();
        jLabel2.setFont(new java.awt.Font("Calibri", 1, 16)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("COMMENT LIST");

        jPanelHolder.setBackground(new java.awt.Color(224, 227, 227));

        javax.swing.GroupLayout jPanelHolderLayout = new javax.swing.GroupLayout(jPanelHolder);
        jPanelHolder.setLayout(jPanelHolderLayout);
        jPanelHolderLayout.setHorizontalGroup(
            jPanelHolderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 281, Short.MAX_VALUE)
        );
        jPanelHolderLayout.setVerticalGroup(
            jPanelHolderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 557, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(jPanelHolder);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        new JavaLib.LoadForm();
        jPanel5.setBackground(new java.awt.Color(216, 221, 221));
        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel5.setFont(new java.awt.Font("Calibri", 1, 16)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("ANALYSER OUTPUT");

        jButton1.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jButton1.setText("FETCH  COMMENTS AND APPLY ANALYSIS");
        jButton1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/osn spam detection/back..png"))); // NOI18N
        jButton3.setBorder(null);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jPanel4.setBackground(new java.awt.Color(0, 0, 0));

        lblTotalComments.setFont(new java.awt.Font("Aparajita", 1, 18)); // NOI18N
        lblTotalComments.setForeground(new java.awt.Color(255, 153, 0));
        lblTotalComments.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotalComments.setText("TOTAL COMMENTS :");
        lblTotalComments.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        lblTotalNonSpam.setFont(new java.awt.Font("Aparajita", 1, 18)); // NOI18N
        lblTotalNonSpam.setForeground(new java.awt.Color(255, 153, 153));
        lblTotalNonSpam.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotalNonSpam.setText("SPAM PERCENTAGE :");
        lblTotalNonSpam.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        lblnonSpamPerc.setFont(new java.awt.Font("Aparajita", 1, 18)); // NOI18N
        lblnonSpamPerc.setForeground(new java.awt.Color(0, 255, 0));
        lblnonSpamPerc.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblnonSpamPerc.setText("NON SPAM PERCENTAGE :");
        lblnonSpamPerc.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        lbltotalSpam.setFont(new java.awt.Font("Aparajita", 1, 18)); // NOI18N
        lbltotalSpam.setForeground(new java.awt.Color(255, 153, 153));
        lbltotalSpam.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbltotalSpam.setText("TOTAL SPAM COMMENTS :");
        lbltotalSpam.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        lblspamPErc.setFont(new java.awt.Font("Aparajita", 1, 18)); // NOI18N
        lblspamPErc.setForeground(new java.awt.Color(0, 255, 51));
        lblspamPErc.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblspamPErc.setText("TOTAL NON SPAM COMMENTS :");
        lblspamPErc.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTotalComments, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblTotalNonSpam, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblnonSpamPerc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbltotalSpam, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblspamPErc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTotalComments, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbltotalSpam, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblspamPErc, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTotalNonSpam, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblnonSpamPerc, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );

        jLabel6.setBackground(new java.awt.Color(0, 0, 0));
        jLabel6.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 204, 0));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("FILTERING OSN COMMENTS FOR SPAM DETECTION");
        jLabel6.setOpaque(true);

        jPanel3.setBackground(new java.awt.Color(221, 230, 230));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 291, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 987, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
        parent.setVisible(true);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        // Download_Comments();
        // read_comments();
        read_Comments_Database();
        apply_processing();
    }//GEN-LAST:event_jButton1ActionPerformed
    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanelHolder;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblTotalComments;
    private javax.swing.JLabel lblTotalNonSpam;
    private javax.swing.JLabel lblnonSpamPerc;
    private javax.swing.JLabel lblspamPErc;
    private javax.swing.JLabel lbltotalSpam;
    // End of variables declaration//GEN-END:variables
}

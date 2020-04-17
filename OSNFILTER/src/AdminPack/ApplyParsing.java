/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AdminPack;

import MyPack.TestimonialInfo;
import dataPack.Stemmer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultComboBoxModel;

public class ApplyParsing extends javax.swing.JFrame implements MouseListener {

    MainForm parent;
    Connection con;
    Vector<PnlComment> panelVec;
    Vector<TestimonialInfo> commentVec;
    int selectedComment = -1;
    DefaultComboBoxModel cm;
    TestimonialInfo ti;
    Statement stmt;
    public int no_of_dots = 0, no_of_slashes = 0, is_suspicious = 0, is_not_https = 0, no_of_http = 0, no_of_https = 0, no_of_special = 0;
    Vector<TestimonialInfo> allTestimonials;
    Vector<String> allTokens, allUrlsTokens, allSentenceTokens, allNegativeDatabase, spamUrls, whiteListUrl, blackListUrl;

    public ApplyParsing(MainForm parent) {
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
            p.addMouseListener(this);
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
            System.out.println("URL FOUND");
            return true;
        } else {
            System.out.println("URL NOT FOUND");
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
        System.out.println("Size: " + allSentenceTokens.size());
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

    public void black_white_List_URL() {
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
        black_white_List_URL();
        System.out.println("Url Token Size: " + allUrlsTokens.size());
        for (int j = 0; j < allUrlsTokens.size(); j++) {
            no_of_dots = 0;
            no_of_slashes = 0;
            is_not_https = 0;
            no_of_http = 0;
            no_of_https = 0;
            String url = allUrlsTokens.get(j);
            for (int i = 0; i < whiteListUrl.size(); i++) {
                if (whiteListUrl.get(i).contains(url)) {
                    checkSpamURl = false;
                    break;
                }
            }
            //First Check For Black Lsit URL
            if (checkSpamURl) {
                for (int i = 0; i < blackListUrl.size(); i++) {
                    if (blackListUrl.get(i).contains(url)) {
                        foundBlackListUrl = true;
                        break;
                    }
                }
            } else {
                continue;
            }

            if (!foundBlackListUrl) {
                status = true;
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
                    System.out.println("Number Of Dots: " + no_of_dots);
                    System.out.println("Number Of Slashes: " + no_of_slashes);
                    System.out.println("Suspicious Count: " + is_suspicious);
                    System.out.println("Nuber Of Https: " + no_of_https);
                    System.out.println("Nuber Of Http: " + no_of_http);
                    int finalCount = no_of_dots + no_of_slashes + no_of_special;
                    if (finalCount > 8 || is_suspicious > 4) {
                        status = true;
                        break;
                    }
                }
            }
        }
        return status;
    }

    //This Function Will Be called Each Time new IUtem Is Selected From The List
    void apply_processing() {
        String str = "";
        Stemmer stem = new Stemmer();
        int urlCnt = 0;
        jTextAreaComment.setText("");;
        jTextAreaStemmed.setText("");
        String source = commentVec.elementAt(selectedComment).text;

        jTextAreaComment.setText(jTextAreaComment.getText() + source + "\r\n\r\n");
        String toStem = commentVec.elementAt(selectedComment).text;
        if (!toStem.endsWith(".") || !toStem.endsWith("?") || !toStem.endsWith("!")) {
            toStem = toStem + ".";
        }
        str = stem.StemmerResult(toStem);
        str = str.substring(0, str.length() - 1);
        jTextAreaStemmed.setText(jTextAreaStemmed.getText() + str + "\r\n\r\n");

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
        for (int i = 0; i < allUrlsTokens.size(); i++) {
            System.out.println("URLm Found: " + allUrlsTokens.get(i));

        }

        boolean flag = check_Valid_URL();
        if (flag) {
            String op = "";
            lblOutput.setForeground(Color.red);
            op += "SPAM URLS found";
            lblOutput.setText(op);
            panelVec.elementAt(selectedComment).setBackground(new Color(255, 145, 145));

        } else {
            boolean BadContentStatus = check_Bad_Contents();
            if (BadContentStatus) {
                String op = "";
                lblOutput.setForeground(Color.red);
                op += "SPAM WORDS FOUND";
                lblOutput.setText(op);
                panelVec.elementAt(selectedComment).setBackground(new Color(255, 145, 145));
            } else {
                lblOutput.setForeground(Color.GREEN);
                lblOutput.setText("NOT SPAM");
                panelVec.elementAt(selectedComment).setBackground(new Color(164, 238, 186));
            }
        }
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
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaComment = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextAreaStemmed = new javax.swing.JTextArea();
        jPanel5 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        lblOutput = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(132, 150, 150));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        jPanel1.setAutoscrolls(true);

        new JavaLib.LoadForm();
        jLabel1.setBackground(new java.awt.Color(98, 124, 124));
        jLabel1.setFont(new java.awt.Font("Aparajita", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("F I L T E R   C O M M E N T S");
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
        jPanel3.setBackground(new java.awt.Color(195, 201, 201));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel3.setFont(new java.awt.Font("Calibri", 1, 16)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("SELECTED COMMENT");

        jTextAreaComment.setBackground(new java.awt.Color(230, 236, 236));
        jTextAreaComment.setColumns(20);
        jTextAreaComment.setLineWrap(true);
        jTextAreaComment.setRows(5);
        jScrollPane2.setViewportView(jTextAreaComment);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel4.setBackground(new java.awt.Color(197, 205, 205));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        new JavaLib.LoadForm();
        jLabel4.setFont(new java.awt.Font("Calibri", 1, 16)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("STEMMED OUTPUT");

        jTextAreaStemmed.setBackground(new java.awt.Color(234, 240, 240));
        jTextAreaStemmed.setColumns(20);
        jTextAreaStemmed.setLineWrap(true);
        jTextAreaStemmed.setRows(5);
        jScrollPane3.setViewportView(jTextAreaStemmed);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
                    .addComponent(jScrollPane3))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                .addContainerGap())
        );

        new JavaLib.LoadForm();
        jPanel5.setBackground(new java.awt.Color(216, 221, 221));
        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel5.setFont(new java.awt.Font("Calibri", 1, 16)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("OUTPUT");

        jButton1.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jButton1.setText("FETCH  COMMENTS(FaceBook)");
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

        lblOutput.setBackground(new java.awt.Color(0, 0, 0));
        lblOutput.setFont(new java.awt.Font("Aparajita", 1, 24)); // NOI18N
        lblOutput.setForeground(new java.awt.Color(0, 204, 51));
        lblOutput.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblOutput.setOpaque(true);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblOutput, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblOutput, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
    }//GEN-LAST:event_jButton1ActionPerformed
    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanelHolder;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea jTextAreaComment;
    private javax.swing.JTextArea jTextAreaStemmed;
    private javax.swing.JLabel lblOutput;
    // End of variables declaration//GEN-END:variables

    @Override
    public void mouseClicked(MouseEvent e) {
        for (int i = 0; i < panelVec.size(); i++) {
            if (e.getSource().equals(panelVec.elementAt(i))) {
                if (selectedComment != -1) {
                    panelVec.elementAt(selectedComment).setSelected(false);
                }
                selectedComment = i;
                panelVec.elementAt(selectedComment).setSelected(true);
                apply_processing();
                break;
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
}

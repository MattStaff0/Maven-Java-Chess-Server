package chess;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.*;
import java.net.*;

import javax.imageio.ImageIO;


public class ChessHomePage extends JFrame {
    private String username;
    private String hashedPassword;
    private boolean newUser;

    // Panels
    private BackgroundPanel backgroundPanel;
    private JPanel playerStatsPanel = new JPanel();
    private JPanel createServersPanel = new JPanel();
    private JPanel leaderBoardPanel = new JPanel();
    private JPanel activePlayersPanel = new JPanel();

    // active players panel
    JLabel countLabel = new JLabel();

    // server panel
    private JLabel servers;
    private JLabel first;
    private JLabel second;
    private JLabel capacityOne;
    private JLabel capacityTwo;
    private JButton joinFirst;
    private JButton joinSecond;

    //  playerStats panel
    private JButton refresh;
    private JLabel user;
    private JLabel wins;
    private JLabel losses;
    private JLabel wlRate;
    private JLabel wRate;


    //  leaderBoardPanel
    private ArrayList<String> top10Users = new ArrayList<>();
    private ArrayList<Integer> top10Wins = new ArrayList<>();
    private ArrayList<Float> top10WinLoss = new ArrayList<>();

    //  connection to alive server
    private Socket clientSocket;
    BufferedReader reader;
    private PrintWriter writer;


    public ChessHomePage(String username, String hashedPassword, boolean newUser) {
        // settings
        this.setTitle("Chess");
        this.setSize(730, 450);
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //  class var initialization 
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.newUser = newUser;

        // background 
        backgroundPanel = new BackgroundPanel("/Users/matt_staff/Desktop/Java Chess Game Maven/chessgame/src/main/resources/mainPage.jpeg");
        this.backgroundPanel.setLayout(new BorderLayout());
        this.add(backgroundPanel, BorderLayout.CENTER);

        // playerstats panel
        this.makePlayerStatsPanel(username);

        // leaderboard panel
        this.makeleaderBoard();

        //  server panels for players to join a chess server
        this.makeServersPanel();

        //  active players panel
        this.makeActivePlayers();

        this.setVisible(true);
        //System.out.println("WORKS");
    }

    private void makeServersPanel(){
        this.createServersPanel.setSize(510, 450);
        this.createServersPanel.setLayout(new GridBagLayout());

        //  a for color allows to be transparent
        this.createServersPanel.setBackground(new Color(0,0,0,75));

        // title for servers 

        this.servers = new JLabel("Available Servers");
        this.servers.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 14));

        //  first server components

        this.first = new JLabel("Server 1");
        this.first.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 14));

        this.capacityOne = new JLabel("Current Players: 0");
        this.capacityOne.setFont(new Font("Serif", Font.BOLD| Font.ITALIC, 12));

        this.joinFirst = new JButton("Join!");
        this.joinFirst.setFont(new Font("Serif", Font.BOLD, 12));
        this.joinFirst.addActionListener(e -> handleServerOne());
    
        
        // second server components

        this.second = new JLabel("Server 2");
        this.second.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 14));


        this.capacityTwo = new JLabel("Current Players: 0");
        this.capacityTwo.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 12));

        this.joinSecond = new JButton("Join!");
        this.joinSecond.setFont(new Font("Serif", Font.BOLD, 12));


        //  adding components
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // adding title 
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        this.createServersPanel.add(this.servers, gbc);

        //  adding server one components

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        this.createServersPanel.add(this.first, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        this.createServersPanel.add(this.capacityOne, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        this.createServersPanel.add(this.joinFirst, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        this.createServersPanel.add(this.second, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        this.createServersPanel.add(this.capacityTwo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        this.createServersPanel.add(this.joinSecond, gbc);

        //  adding create servers panel to the background panel
        this.backgroundPanel.add(this.createServersPanel, BorderLayout.CENTER);

        //  connecting to the server
        int portNum = 12345;
        String hostName = "127.0.0.1";
        try{
        this.clientSocket = new Socket(hostName, portNum);
        this.reader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
        this.writer = new PrintWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));
        ClientThread clientThread = new ClientThread(this, this.countLabel);
        clientThread.start();
        System.out.println("worked");
        } catch (IOException e){
            e.printStackTrace();
        };
    }


    private void handleServerOne() {
        handleChessGui();
    }

    public void handleChessGui(){
        
    }

    public void sendHeartBeat(){
        this.writer.println("still_alive");
        this.writer.flush();
    }

    private void makeActivePlayers(){
        this.countLabel = new JLabel("Active Players: ");
        this.countLabel.setFont(new Font("Serif", Font.ITALIC | Font.BOLD, 14));
        this.activePlayersPanel.add(this.countLabel);
        this.backgroundPanel.add(activePlayersPanel, BorderLayout.NORTH);

    }

    private void makePlayerStatsPanel(String username){

        this.playerStatsPanel.setSize(new Dimension(110, 450));
        this.playerStatsPanel.setLayout(new GridBagLayout());

        //  a for color allows to be transparent
        this.playerStatsPanel.setBackground(new Color(0,0,0,75));

        //  making labels
        this.fetchPlayerStats(username);
        this.user = new JLabel("Stats for: " + username);

        // making button 
        this.refresh = new JButton("Refresh");
        this.refresh.addActionListener(e -> fetchPlayerStats(username));

        // 
        this.user.setFont(new Font("Serif", Font.ITALIC | Font.BOLD, 14));
        this.wins.setFont(new Font("Serif", Font.ITALIC | Font.BOLD, 14));
        this.losses.setFont(new Font("Serif", Font.ITALIC | Font.BOLD, 14));
        this.wRate.setFont(new Font("Serif", Font.ITALIC | Font.BOLD, 14));
        this.wlRate.setFont(new Font("Serif", Font.ITALIC | Font.BOLD, 14));
        this.refresh.setFont(new Font("Serif", Font.ITALIC | Font.BOLD, 14));


        //  adding them to the panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // adding the title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.playerStatsPanel.add(this.user, gbc);

        // adding the wins
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.playerStatsPanel.add(this.wins, gbc);

        // adding the losses
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.playerStatsPanel.add(this.losses, gbc);

        // adding the winRate
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.playerStatsPanel.add(this.wRate, gbc);

        // adding the wlRate
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.playerStatsPanel.add(this.wlRate, gbc);

        this.backgroundPanel.add(this.playerStatsPanel, BorderLayout.WEST);


    }
    private void fetchPlayerStats(String username){
        int wins=-1;
        int loss=-1;
        double wlRatio=0;
        int gamesPlayed = -1;
        double winRate = 0;

        PreparedStatement ps = null;
        String query = "SELECT Wins, Losses FROM WinLoss WHERE Username = ?;";

        try(Connection connect = DBConnect.connect()){
            ps = connect.prepareStatement(query);
            ps.setString(1, username);

            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    wins = resultSet.getInt("Wins");
                    loss = resultSet.getInt("Losses");
                    if (wins != 0 || loss != 0){
                        double wlRatioDecimal = (double) wins / loss;
                        wlRatio = Math.round(wlRatioDecimal * 100.0) / 100.0;
                        
                        gamesPlayed = wins + loss;
                        double winRateDecimal = (double) wins / gamesPlayed;
                        winRate = Math.round(winRateDecimal * 100.0) / 100.0;
                    }
                }
            }
        } catch (SQLException e){}

        //  setting the class vars to them
        this.losses = new JLabel("Losses: " + loss);
        this.wins = new JLabel("Losses: " + wins);
        this.wlRate = new JLabel("Losses: " + wlRatio +'%');
        this.wRate = new JLabel("Losses: " + winRate + '%');

        this.revalidate();
        return;
    }

    private void makeleaderBoard(){
        //  settings
        this.leaderBoardPanel.setLayout(new GridBagLayout());
        this.leaderBoardPanel.setBackground(new Color(0,0,0,75));
        this.leaderBoardPanel.setSize(new Dimension(110, 450));

        // fetching stats 
        fetchLeaderBoardData();

        // loops to add labels to the panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        //  titles for the layout 
        JLabel title = new JLabel("LeaderBoards");
        title.setFont(new Font("Serif", Font.ITALIC | Font.BOLD, 15));

        gbc.gridx = 0;
        gbc.gridy= 0;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        this.leaderBoardPanel.add(title, gbc);

        JLabel usersLabel = new JLabel("Usernames");
        usersLabel.setFont(new Font("Serif", Font.ITALIC | Font.BOLD, 15));
        gbc.gridx = 0;
        gbc.gridy= 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        this.leaderBoardPanel.add(usersLabel, gbc);

        JLabel winsLabel = new JLabel("Wins");
        winsLabel.setFont(new Font("Serif", Font.ITALIC | Font.BOLD, 15));
        gbc.gridx = 1;
        gbc.gridy= 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        this.leaderBoardPanel.add(winsLabel, gbc);

        JLabel wlLabel = new JLabel("Win/Loss");
        wlLabel.setFont(new Font("Serif", Font.ITALIC | Font.BOLD, 15));
        gbc.gridx = 2;
        gbc.gridy= 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        this.leaderBoardPanel.add(wlLabel, gbc);

        for (int i =2; i<this.top10Users.size()+1; i++){
            String user = this.top10Users.get(i-2);
            JLabel top10 = new JLabel(user);
            top10.setFont(new Font("Serif", Font.ITALIC | Font.BOLD, 12));
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.anchor = GridBagConstraints.WEST;
            this.leaderBoardPanel.add(top10, gbc);
        }
        for (int i =2; i<this.top10Wins.size()+1; i++){
            int user = this.top10Wins.get(i-2);
            JLabel top10 = new JLabel(""+user);
            top10.setFont(new Font("Serif", Font.ITALIC | Font.BOLD, 12));
            gbc.gridx = 1;
            gbc.gridy = i;
            gbc.anchor = GridBagConstraints.CENTER;
            this.leaderBoardPanel.add(top10, gbc);
        }
        for (int i =2; i<this.top10WinLoss.size()+1; i++){
            Float user = this.top10WinLoss.get(i-2);
            JLabel top10 = new JLabel(user+ "");
            top10.setFont(new Font("Serif", Font.ITALIC | Font.BOLD, 12));
            gbc.gridx = 2;
            gbc.gridy = i;
            gbc.anchor = GridBagConstraints.EAST;
            this.leaderBoardPanel.add(top10, gbc);
        }

        this.backgroundPanel.add(this.leaderBoardPanel, BorderLayout.EAST);
         
    }
    private void fetchLeaderBoardData(){
        String query = "SELECT Username, Wins, WinLossPercentage FROM WinLoss ORDER BY Wins DESC LIMIT 10";

        try(Connection connect = DBConnect.connect();
            PreparedStatement ps = connect.prepareStatement(query);
            ResultSet rs = ps.executeQuery();){

                while (rs.next()) {
                    // Add the username, wins, and win-loss percentage to the respective ArrayLists
                    this.top10Users.add(rs.getString("Username"));
                    this.top10Wins.add(rs.getInt("Wins"));
                    this.top10WinLoss.add(rs.getFloat("WinLossPercentage"));
                }
        } catch (SQLException e){}

    }
}

class ClientThread extends Thread{
    private ChessHomePage parent;
    private JLabel countJLabel;

    public ClientThread(ChessHomePage parent, JLabel countLabel){
        this.parent = parent;
        this.countJLabel = countLabel;
        this.parent.sendHeartBeat();
    }

    @Override
    public void run(){
        try{
            String message;
            while((message = this.parent.reader.readLine()) != null){
                String finalMessage = message;
                System.out.println(finalMessage);
                this.parent.sendHeartBeat();
                this.parent.countLabel.setText("Active Players: " + finalMessage);
            }
        } catch (IOException e){}

    }
    
}




































class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel(String imagePath) {
        this.setLayout(new BorderLayout());
        try {
            backgroundImage = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

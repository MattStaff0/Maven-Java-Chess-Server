package chess;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.*;

public class ChessBoard extends JFrame implements MouseListener, MouseMotionListener {
    private Point initialClick;
    private JButton draggedButton;
    private Map<String, BufferedImage> pieceImages = new HashMap<>();
    private JButton[][] chessBoardSquares = new JButton[8][8];
    private JPanel board = new JPanel(new GridLayout(8, 8));
    private JPanel main = new JPanel(new BorderLayout());

    // Panels
    JPanel stats = new JPanel(new GridLayout(5, 1));
    JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 71, 1));
    JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 71, 1));
    JPanel westPanel = new JPanel();
    JPanel eastPanel = new JPanel();

    public ChessBoard() {
        this.setSettings();
        this.makeOutside();
        this.loadPieceImages();
        this.makeBoard();

        this.add(this.main);
        this.setVisible(true);
    }

    private void loadPieceImages() {
        String[] pieces = {"black/pawn", "black/rook", "black/knight", "black/bishop", "black/queen", "black/king",
                           "white/pawn", "white/rook", "white/knight", "white/bishop", "white/queen", "white/king"};
        String basePath = "/pieces/";

        for (String piece : pieces) {
            try (InputStream is = getClass().getResourceAsStream(basePath + piece + ".png")) {
                if (is != null) {
                    pieceImages.put(piece, ImageIO.read(is));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void makeBoard() {
        boolean brown = true;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(80, 80));
                b.setOpaque(true);
                b.addMouseListener(this);
                b.addMouseMotionListener(this);

                BufferedImage pieceImage = getPiece(i, j);
                if (pieceImage != null) {
                    ImageIcon icon = new ImageIcon(pieceImage.getScaledInstance(80, 80, Image.SCALE_SMOOTH));
                    b.setIcon(icon);
                }

                // Set background color alternating
                if (brown) {
                    b.setBackground(new Color(92, 64, 51));
                } else {
                    b.setBackground(new Color(196, 164, 132));
                }

                brown = !brown;
                chessBoardSquares[i][j] = b;
                this.board.add(b);
            }
            brown = !brown;
        }

        this.board.setPreferredSize(new Dimension(640, 640));
        this.main.add(this.board, BorderLayout.CENTER);
    }

    private BufferedImage getPiece(int row, int col) {
        if (row == 1) return pieceImages.get("black/pawn");
        if (row == 6) return pieceImages.get("white/pawn");
        if (row == 0) {
            if (col == 0 || col == 7) return pieceImages.get("black/rook");
            if (col == 1 || col == 6) return pieceImages.get("black/knight");
            if (col == 2 || col == 5) return pieceImages.get("black/bishop");
            if (col == 3) return pieceImages.get("black/queen");
            if (col == 4) return pieceImages.get("black/king");
        }
        if (row == 7) {
            if (col == 0 || col == 7) return pieceImages.get("white/rook");
            if (col == 1 || col == 6) return pieceImages.get("white/knight");
            if (col == 2 || col == 5) return pieceImages.get("white/bishop");
            if (col == 3) return pieceImages.get("white/queen");
            if (col == 4) return pieceImages.get("white/king");
        }
        return null;
    }

    public void makeOutside() {
        int sidePanelWidth = 50;
        int topBottomPanelHeight = 50;

        // Set background colors for visual clarity
        northPanel.setBackground(new Color(248, 229, 187));
        southPanel.setBackground(new Color(248, 229, 187));
        westPanel.setBackground(new Color(248, 229, 187));
        eastPanel.setBackground(new Color(248, 229, 187));

        // Set preferred sizes
        northPanel.setPreferredSize(new Dimension(this.getWidth(), topBottomPanelHeight));
        southPanel.setPreferredSize(new Dimension(this.getWidth(), topBottomPanelHeight));
        westPanel.setPreferredSize(new Dimension(sidePanelWidth, this.getHeight() - 2 * topBottomPanelHeight));
        eastPanel.setPreferredSize(new Dimension(sidePanelWidth, this.getHeight() - 2 * topBottomPanelHeight));

        populateNorth();
        populateSouth();
        populateWest();
        populateEast();

        // Add panels to main panel
        main.add(northPanel, BorderLayout.NORTH);
        main.add(southPanel, BorderLayout.SOUTH);
        main.add(westPanel, BorderLayout.WEST);
        main.add(eastPanel, BorderLayout.EAST);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        JButton button = (JButton) e.getSource();
        initialClick = e.getPoint();
        draggedButton = button;
        button.setBorder(BorderFactory.createLineBorder(Color.RED));
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (draggedButton != null) {
            JComponent component = (JComponent) e.getSource();
            Container parent = component.getParent();
            Point parentLocation = parent.getLocationOnScreen();

            int x = parentLocation.x + e.getX() - initialClick.x;
            int y = parentLocation.y + e.getY() - initialClick.y;
            draggedButton.setLocation(x, y);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (draggedButton != null) {
            draggedButton.setBorder(BorderFactory.createEmptyBorder());

            JButton targetButton = (JButton) e.getSource();
            if (targetButton != draggedButton) {
                // Swap icons
                Icon draggedIcon = draggedButton.getIcon();
                Icon targetIcon = targetButton.getIcon();

                draggedButton.setIcon(targetIcon);
                targetButton.setIcon(draggedIcon);
            }

            draggedButton = null;
        }
    }

    // Other MouseListener and MouseMotionListener methods
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mouseMoved(MouseEvent e) {}

    private void populateWest() {
        this.westPanel.setLayout(new BoxLayout(westPanel, BoxLayout.Y_AXIS));
        Dimension minSize = new Dimension(5, 10);
        Dimension prefSize = new Dimension(5, 10);
        Dimension maxSize = new Dimension(Short.MAX_VALUE, 10);
        this.westPanel.add(new Box.Filler(minSize, prefSize, maxSize));

        for (int i = 8; i > 0; i--) {
            JLabel nums = new JLabel(i + "");
            nums.setFont(new Font("Serif", Font.BOLD, 15));
            nums.setForeground(Color.BLACK);
            nums.setHorizontalAlignment(SwingConstants.CENTER);

            // Create a new JPanel for the label
            JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            labelPanel.setBackground(new Color(248, 227, 187));
            labelPanel.add(nums);

            this.westPanel.add(labelPanel);

            minSize = new Dimension(5, 50);
            prefSize = new Dimension(5, 50);
            maxSize = new Dimension(Short.MAX_VALUE, 50);
            this.westPanel.add(new Box.Filler(minSize, prefSize, maxSize));
        }
    }

    private void populateEast() {
        this.eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.Y_AXIS));
        Dimension minSize = new Dimension(5, 10);
        Dimension prefSize = new Dimension(5, 10);
        Dimension maxSize = new Dimension(Short.MAX_VALUE, 10);
        this.eastPanel.add(new Box.Filler(minSize, prefSize, maxSize));

        for (int i = 8; i > 0; i--) {
            JLabel nums = new JLabel(i + "");
            nums.setFont(new Font("Serif", Font.BOLD, 15));
            nums.setForeground(Color.BLACK);
            nums.setHorizontalAlignment(SwingConstants.CENTER);

            // Create a new JPanel for the label
            JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            labelPanel.setBackground(new Color(248, 227, 187));
            labelPanel.add(nums);

            this.eastPanel.add(labelPanel);

            minSize = new Dimension(5, 50);
            prefSize = new Dimension(5, 50);
            maxSize = new Dimension(Short.MAX_VALUE, 50);
            this.eastPanel.add(new Box.Filler(minSize, prefSize, maxSize));
        }
    }

    private void populateSouth() {
        for (int i = 0; i < 8; i++) {
            JLabel letters = new JLabel(convertToLetter(i));
            letters.setFont(new Font("Serif", Font.BOLD, 15));
            letters.setForeground(Color.BLACK);
            letters.setHorizontalAlignment(SwingConstants.CENTER);
            this.southPanel.add(letters);
        }
    }

    private void populateNorth() {
        for (int i = 0; i < 8; i++) {
            JLabel letters = new JLabel(convertToLetter(i));
            letters.setFont(new Font("Serif", Font.BOLD, 15));
            letters.setForeground(Color.BLACK);
            letters.setHorizontalAlignment(SwingConstants.CENTER);
            this.northPanel.add(letters);
        }
    }

    private void setSettings() {
        this.setSize(720, 720);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private String convertToLetter(int col) {
        return String.valueOf((char) ('a' + col));
    }

    public static void main(String[] args) {
        new ChessBoard();
    }
}

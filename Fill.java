import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.*;
import javax.swing.*;

import javax.swing.event.*;

import javax.swing.colorchooser.*;

class Fill implements ActionListener {
  JFrame mainWindow = new JFrame("Fill Algorithms");
  static DrawCanvas canvas;
  JPanel controls = new JPanel(new FlowLayout());

  public static Color fillColor = Color.ORANGE;
  JColorChooser tcc;

  public static int whichFill = 0;
  public static boolean fillMode = false;

  public Fill() {
    // colour chooser:
    tcc = new JColorChooser(Color.ORANGE);
    tcc.getSelectionModel().addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        fillColor = tcc.getColor();
      }
    });
    tcc.setBorder(BorderFactory.createTitledBorder("Choose Text Color"));

    canvas = new DrawCanvas();// size: 561 x 530
    canvas.setBackground(new Color(255, 255, 255));
  }

  public void createAndShowGUI() {
    mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // controls:
    JToggleButton boundaryFill = new JToggleButton("Boundary fill", true);
    JToggleButton floodFill = new JToggleButton("Flood fill");

    boundaryFill.addActionListener(this);

    floodFill.addActionListener(this);

    ButtonGroup whichFill = new ButtonGroup();
    whichFill.add(boundaryFill);
    whichFill.add(floodFill);

    controls.add(boundaryFill);
    controls.add(floodFill);

    JToggleButton fillMode = new JToggleButton("Start filling!");
    fillMode.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Fill.fillMode = !Fill.fillMode;
        if (Fill.fillMode)
          Fill.canvas.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        else
          Fill.canvas.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
      }
    });

    controls.add(fillMode);

    // add to container:
    Container container = mainWindow.getContentPane();
    container.setLayout(new BorderLayout());

    container.add(canvas, BorderLayout.CENTER);
    container.add(controls, BorderLayout.NORTH);

    mainWindow.add(tcc, BorderLayout.EAST);

    mainWindow.setSize(1200, 600);
    mainWindow.setVisible(true);
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand() == "Boundary fill") {
      whichFill = 0;
      canvas.reset();
    } else {
      whichFill = 1;
      canvas.reset();
    }
  }

  public static void main(String[] args) {
    // Schedule a job for the event-dispatching thread:
    // creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        new Fill().createAndShowGUI();
      }
    });
  }
}

class DrawCanvas extends JPanel implements MouseListener {
  static final long serialVersionUID = 654534543;

  int factor = 20;
  Color drawingMatrix[][];
  int fillMatrix[][];
  int currentX;
  int currentY;

  boolean first = true;

  public DrawCanvas() {
    reset();
    addMouseListener(this);
  }

  public void reset() {
    drawingMatrix = null;
    fillMatrix = null;
    drawingMatrix = new Color[500 / factor][500 / factor];
    for (int i = 0; i < drawingMatrix.length; i++) {
      for (int j = 0; j < drawingMatrix[0].length; j++) {
        drawingMatrix[i][j] = new Color(255, 255, 255);
      }
    }
    fillMatrix = new int[500 / factor][500 / factor];
    repaint();
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    // drawing the grid:
    g.setColor(Color.decode("#EDEDED"));
    for (int i = 1; i < 500 - 1; i += factor) {
      for (int j = 1; j < 500 - 1; j += factor) {
        g.drawLine(j, i, j + factor, i);
        g.drawLine(j, i, j, i + factor);
      }
    }

    if (Fill.fillMode) {
      if (Fill.whichFill == 1) {
        System.out.println("Fill");
        floodFill(currentY / factor, currentX / factor, drawingMatrix[currentY / factor][currentX / factor],
            Fill.fillColor);
      } else {
        System.out.println("Boundary");
        boundaryFill(currentY / factor, currentX / factor, Fill.fillColor, Color.BLACK);
      }
      // Fill.fillMode = false;
    }

    if (Fill.whichFill == 1)
      paintFiller(g);
    else
      paintBorders(g);
  }

  public int F(int x) {
    return x * factor;
  }

  public void paintBorders(Graphics g) {
    for (int i = 1; i < drawingMatrix.length - 1; i++) {
      for (int j = 1; j < drawingMatrix[0].length - 1; j++) {
        if (fillMatrix[i][j] == 1 || fillMatrix[i][j] == 2) {
          // draw simple square:
          g.setColor(Color.BLACK);

          g.drawLine(F(j), F(i), F(j) + factor, F(i));// top
          g.drawLine(F(j), F(i) + factor, F(j) + factor, F(i) + factor);// bottom
          g.drawLine(F(j), F(i), F(j), F(i) + factor);// left
          g.drawLine(F(j) + factor, F(i), F(j) + factor, F(i) + factor);// right

          // make adjustments:
          if (isPermissiblePoint(i, j)) {
            g.setColor(Color.decode("#EDEDED"));
            // vertical check:
            if (fillMatrix[i - 1][j] == 1 || fillMatrix[i - 1][j] == 2)
              g.drawLine(F(j) + 1, F(i - 1) + factor, F(j) + factor - 1, F(i - 1) + factor);
            if (fillMatrix[i + 1][j] == 1 || fillMatrix[i + 1][j] == 2)
              g.drawLine(F(j) + 1, F(i + 1), F(j) + factor - 1, F(i + 1));
            // horizontal check:
            if (fillMatrix[i][j - 1] == 1 || fillMatrix[i][j - 1] == 2)
              g.drawLine(F(j - 1) + factor, F(i) + 1, F(j - 1) + factor, F(i) + factor - 1);
            if (fillMatrix[i][j + 1] == 1 || fillMatrix[i][j + 1] == 2)
              g.drawLine(F(j + 1), F(i) + 1, F(j + 1), F(i) + factor - 1);

            g.setColor(Color.BLACK);
          }

          g.setColor(drawingMatrix[i][j]);
          g.fillRect(F(j) + 1, F(i) + 1, factor - 1, factor - 1);
        }
      }
    }
  }

  public void paintFiller(Graphics g) {
    for (int i = 0; i < drawingMatrix.length; i++) {
      for (int j = 0; j < drawingMatrix[0].length; j++) {
        if (fillMatrix[i][j] == 1) {
          g.setColor(drawingMatrix[i][j]);
          g.fillRect(F(j), F(i), factor, factor);
        }
      }
    }
  }

  public void boundaryFill(int x, int y, Color fillColor, Color boundaryColor) {
    // System.out.println("(" + x + ", " + y + ")");
    System.out.println(fillColor);
    if (fillMatrix[x][y] != 0 && fillMatrix[x][y] != 2) {
      fillMatrix[x][y] = 2;
      drawingMatrix[x][y] = fillColor;
      boundaryFill(x - 1, y, fillColor, boundaryColor);
      boundaryFill(x, y - 1, fillColor, boundaryColor);
      boundaryFill(x + 1, y, fillColor, boundaryColor);
      boundaryFill(x, y + 1, fillColor, boundaryColor);
    }
  }

  public void floodFill(int x, int y, Color oldColor, Color newColor) {
    if (fillMatrix[x][y] == 1 && drawingMatrix[x][y].getRGB() == oldColor.getRGB()) {
      drawingMatrix[x][y] = newColor;
      floodFill(x - 1, y, oldColor, newColor);
      floodFill(x, y - 1, oldColor, newColor);
      floodFill(x + 1, y, oldColor, newColor);
      floodFill(x, y + 1, oldColor, newColor);
    }
  }

  public boolean isPermissiblePoint(int x, int y) {
    if (x == 0 || y == 0 || x == drawingMatrix.length || y == drawingMatrix[0].length)
      return false;
    return true;
  }

  @Override
  public void mousePressed(MouseEvent e) {
  }

  @Override
  public void mouseReleased(MouseEvent e) {
  }

  @Override
  public void mouseEntered(MouseEvent e) {
  }

  @Override
  public void mouseExited(MouseEvent e) {
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    int x = e.getX();
    int y = e.getY();

    if (!isPermissiblePoint(x, y))
      return;

    if (!Fill.fillMode) {
      fillMatrix[y / 20][x / 20] = 1;
      if (Fill.whichFill == 1) {
        drawingMatrix[y / 20][x / 20] = Color.decode("#EDEDED");
      }
      repaint();
      displayMatrix();
    } else {
      this.currentX = x;
      this.currentY = y;
      repaint();
      displayMatrix();
    }
  }

  public void displayMatrix() {
    System.out.println("\n\nCurrent status:\ncurrent-color: " + Fill.fillColor + "\ndata-structure-status:");
    for (int i = 0; i < fillMatrix.length; i++) {
      for (int j = 0; j < fillMatrix[0].length; j++) {
        System.out.print(fillMatrix[i][j] + " ");
      }
      System.out.println();
    }
  }
}
import java.io.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.*;
import javax.swing.*;

import javax.swing.event.*;

class Drawing {
  JFrame mainWindow = new JFrame("Drawing Shapes");
  JPanel controls = new DrawingControls();
  JPanel canvas = new DrawingCanvas();

  public void createAndShowGUI() {
    mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    Container container = mainWindow.getContentPane();
    container.setLayout(new BorderLayout());

    container.add(controls, BorderLayout.NORTH);    
    container.add(canvas, BorderLayout.CENTER);

    mainWindow.setSize(1200, 600);
    mainWindow.setVisible(true);
  }

  public static void main(String[] args) {
    // Schedule a job for the event-dispatching thread:
    // creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        new Drawing().createAndShowGUI();
      }
    });
  }
}

class DrawingCanvas extends JPanel implements MouseListener, MouseMotionListener {
  int startX, startY, endX, endY;
  public DrawingCanvas() {
    super();
    setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));    
    addMouseListener(this);
    addMouseMotionListener(this);
  }

  public void paint(Graphics g) {
    g.fillRect(0, 0, this.getWidth(), this.getHeight());
  }

  @Override
  public void mouseEntered(MouseEvent e) {
    
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    System.out.println("clicked: " + e.getX() + ", " + e.getY());
  }

  @Override
  public void mouseExited(MouseEvent e) {
    
  }

  @Override
  public void mousePressed(MouseEvent e) {
    System.out.println("pressed: " + e.getX() + ", " + e.getY());
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    System.out.println("released: " + e.getX() + ", " + e.getY());
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    System.out.println("dragged: " + e.getX() + ", " + e.getY());
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    
  }

}

class DrawingControls extends JPanel implements ActionListener {
  JToggleButton buttons[];
  boolean whichButton[];

  public DrawingControls() {
    super();
    addControls();
    setSize(1200, 50);
    setLayout(new FlowLayout());
  }

  public void addControls() {
    buttons = new JToggleButton[3];
    whichButton = new boolean[3];
    for (int i = 0; i < buttons.length; i++) {
      buttons[i] = new JToggleButton();
    }
    buttons[0] = new JToggleButton("Line", true);
    buttons[1] = new JToggleButton("Circle");
    buttons[2] = new JToggleButton("Ellipse");

    ButtonGroup bg = new ButtonGroup();
    for (int i = 0; i < buttons.length; i++) {
      buttons[i].addActionListener(this);
      bg.add(buttons[i]);
    }

    for (int i = 0; i < buttons.length; i++) {
      add(buttons[i]);
    }
  }

  public void actionPerformed(ActionEvent e) {
    for (int i = 0; i < buttons.length; i++) {
      if (e.getSource() == buttons[i]) {
        whichButton[i] = true;
      } else {
        whichButton[i] = false;
      }
      System.out.println(whichButton[i]);
    }
  }
}

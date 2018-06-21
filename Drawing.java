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
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        new Drawing().createAndShowGUI();
      }
    });
  }
}

class DrawingCanvas extends JPanel implements MouseListener, MouseMotionListener {
  int startX, startY, endX, endY;
  boolean drawingState;
  public DrawingCanvas() {
    super();
    setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));    
    addMouseListener(this);
    addMouseMotionListener(this);
    repaint();
  }
  
  //Bresenham's ellipse drawing algorithm:
  public void ellipse(Graphics g, int xCentre, int yCentre, int a, int b) {
    a = a * a;
    b = b * b;
    double d;
    int x = 0, y = b;
    
    System.out.println("start");
    
    drawPoints(g, x, y, xCentre, yCentre);
    
    d = b + a * (0.25 - Math.sqrt(b));
    while(a * (y - 0.5) > b * (x + 1)) {
      x = x + 1;
      if(d < 0) {
        d += b * (2 * x + 3);
      }
      else {
        d += b * (2 * x + 3) + a * (2 - 2 * y);
        y = y - 1;
      }
      
      drawPoints(g, x, y, xCentre, yCentre);
    }
    
    d = -a * (2 * y - 3);
    while(y > 0) {
      y = y - 1;
      if(d < 0) {
        d += b * (2 * x + 2) + a * (3 - 2 * y);
        x = x + 1;
      } else {
        d += a * (3 - 2 * y);
      }
      drawPoints(g, x, y, xCentre, yCentre);      
    }
  }
  
  //plot the point and it's reflected points:
  public void drawPoints(Graphics g, int x, int y, int xCentre, int yCentre) {
    g.drawLine(x + xCentre, y + yCentre, x + xCentre, y + yCentre);
    g.drawLine(-x + xCentre, y + yCentre, -x + xCentre, y + yCentre);
    g.drawLine(x + xCentre, -y + yCentre, x + xCentre, -y + yCentre);
    g.drawLine(-x + xCentre, -y + yCentre, -x + xCentre, -y + yCentre);
  }
  
  //mid-point circle drawing algorithm:
  public void circle(Graphics g, int xCentre, int yCentre, int r) 
  {
    
    int x = r, y = 0;
    
    g.drawLine(x + xCentre, y + yCentre, x + xCentre, y + yCentre);//first point    
    
    int d = 1 - r;
    while (x > y) {      
      y++;
      
      //inside
      if (d <= 0)
      d = d + 2 * y + 1;
      
      //outside
      else {
        x--;
        d = d + 2 * y - 2 * x + 1;
      }

      if (x < y) break;
      
      g.drawLine(x + xCentre, y + yCentre, x + xCentre, y + yCentre);
      g.drawLine(-x + xCentre, y + yCentre, -x + xCentre, y + yCentre);
      g.drawLine(x + xCentre, -y + yCentre, x + xCentre, -y + yCentre);
      g.drawLine(-x + xCentre, -y + yCentre, -x + xCentre, -y + yCentre);
      
      g.drawLine(y + xCentre, x + yCentre, y + xCentre, x + yCentre);
      g.drawLine(-y + xCentre, x + yCentre, -y + xCentre, x + yCentre);
      g.drawLine(y + xCentre, -x + yCentre, y + xCentre, -x + yCentre);
      g.drawLine(-y + xCentre, -x + yCentre, -y + xCentre, -x + yCentre);
      
    }
  }
  
  //test whether x is positive, negative or 0
  int sign(int x) {
    if(x > 0) return 1;
    if(x < 0) return -1;
    return x;
  }
  
  //Bresenham's Line drawing algorithm:
  void line(Graphics g, int x1, int y1, int x2, int y2)
  {
    int x = x1, y = y1;
    
    int dx = Math.abs(x2 - x1);
    int dy = Math.abs(y2 - y1);
    
    int s1 = sign(x2 - x1);
    int s2 = sign(y2 - y1);
    boolean swap = false;
    
    if(dy > dx) {
      swap = true;
      dx = dx + dy;
      dy = dx - dy;
      dx = dx - dy;
    }
    
    g.drawLine(x1, y1, x1, y1);
    
    int d = 2 * dy - dx;
    
    for(int i=0; i<dx; i++ ) {
      g.drawLine(x, y, x, y);
      while(d >= 0) {
        d = d - 2 * dx;
        if(swap) {
          x += s1;
        } else {
          y += s2;
        }
      }
      
      d = d + 2 * dy;
      if(swap) {
        y += s2;
      } else {
        x += s1;
      }
    }
    
  }   
  
  public void paint(Graphics g) {
    g.setColor(Color.black);
    g.fillRect(0, 0, this.getWidth(), this.getHeight());
    if(drawingState) {//currently dragging the mouse
      int whichButton = DrawingControls.getWhichButton();
      if(whichButton == 0) {//line
        g.setColor(Color.white);
        line(g, startX, startY, endX, endY);
      } else if(whichButton == 1) {//circle
        g.setColor(Color.white);
        circle(g, startX, startY, Math.abs(startX - endX));
      } else if (whichButton == 2) {//ellipse
        g.setColor(Color.white);
        ellipse(g, startX, startY, Math.abs(startX - endX)/2, Math.abs(startY - endY)/2);
      }
    }
  }
  
  @Override
  public void mouseEntered(MouseEvent e) {}
  
  @Override
  public void mouseClicked(MouseEvent e) {}
  
  @Override
  public void mouseExited(MouseEvent e) {}
  
  @Override
  public void mousePressed(MouseEvent e) {
    //start drawing
    startX = e.getX();
    startY = e.getY();
  }
  
  @Override
  public void mouseReleased(MouseEvent e) {
    //end drawing
    drawingState = false;
  }
  
  @Override
  public void mouseDragged(MouseEvent e) {
    //continue drawing
    endX = e.getX();
    endY = e.getY();
    drawingState = true;
    repaint();
  }
  
  @Override
  public void mouseMoved(MouseEvent e) {}
  
}

class DrawingControls extends JPanel implements ActionListener {
  JToggleButton buttons[];
  static boolean whichButton[];
  
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
    
    whichButton[0] = true;
    
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
  
  public static int getWhichButton() {
    for(int i=0; i<whichButton.length; i++) {
      if(whichButton[i]) return i;
    }
    return -1;
  }
}

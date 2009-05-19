/**
 * 
 */
package gnu.escher;

import javax.swing.JButton;
import javax.swing.JFrame;

/**
 * @author Mario Torre <neugens@limasoftware.net>
 */
public class TestButton
{
  public static void main(String[] args)
  {
    //JFrame.setDefaultLookAndFeelDecorated(true);
    JFrame f = new JFrame();
    System.err.println("created new Frame");
    f.setSize(600, 400);
    System.err.println("Set frame size");
    
    JButton button = new JButton("Click Me!");
    System.err.println("created new Button");
    
    button.setSize(50, 20);
    System.err.println("Set button size size");
    
    f.add(button);
    System.err.println("added button");
    
    f.setVisible(true);
    System.err.println("Set visible");
  }
}
/**
 * 
 */
package gnu.escher;

import javax.swing.JFrame;

/**
 * @author Roman Kennke <roman@kennke.org>
 */
public class TestFrame
{
  public static void main(String[] args)
  {
    //JFrame.setDefaultLookAndFeelDecorated(true);
    JFrame f = new JFrame();
    System.err.println("created new Frame");
    f.setSize(600, 400);
    System.err.println("Set size");
    f.setVisible(true);
    System.err.println("Set visible");
  }
}

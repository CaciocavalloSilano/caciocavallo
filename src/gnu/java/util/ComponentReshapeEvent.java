package gnu.java.util;

import java.awt.AWTEvent;
import java.awt.Component;

/**
 * This is used to update the AWT's knowledge about a Window's size when
 * the user changes the window bounds.
 *
 * This event is _not_ posted to the eventqueue, but rather dispatched directly
 * via Window.dispatchEvent(). It is the cleanest way we could find to update
 * the AWT's knowledge of the window size. Small testprograms showed the
 * following:
 * - Component.reshape() and its derivatives are _not_ called. This makes sense
 *   as it could end up in loops,because this calls back into the peers.
 * - Intercepting event dispatching for any events in
 *   EventQueue.dispatchEvent() showed that the size is still updated. So it
 *   is not done via an event dispatched over the eventqueue.
 *
 * Possible other candidates for implementation would have been:
 * - Call a (private) callback method in Window/Component from the native
 *   side.
 * - Call a (private) callback method in Window/Component via reflection.
 *
 * Both is uglier than sending this event directly. Note however that this
 * is impossible to test, as Component.dispatchEvent() is final and can't be
 * intercepted from outside code. But this impossibility to test the issue from
 * outside code also means that this shouldn't raise any compatibility issues.
 */
public class ComponentReshapeEvent
  extends AWTEvent
{

  public int x;
  public int y;
  public int width;
  public int height;

  public ComponentReshapeEvent(Component c, int x, int y, int width, int height)
  {
    super(c, 1999);
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }
}
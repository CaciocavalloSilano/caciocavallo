/* XGraphicsEnvironment.java
   Copyright (C) 2008 Mario Torre and Roman Kennke

This file is part of the Caciocavallo project.

Caciocavallo is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2, or (at your option)
any later version.

Caciocavallo is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with Caciocavallo; see the file COPYING.  If not, write to the
Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301 USA.

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version. */

package gnu.java.awt.peer.x;

import gnu.x11.Display;

import java.awt.GraphicsDevice;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import sun.java2d.SunGraphicsEnvironment;
import sun.java2d.SurfaceManagerFactory;

/**
 * Represents the X environment for AWT.
 *
 * @author Roman Kennke (kennke@aicas.com)
 */
public class XGraphicsEnvironment
  extends SunGraphicsEnvironment
{

  static
  {
    System.err.println("setting the surface manager");
    SurfaceManagerFactory.setInstance(new EscherSurfaceManagerFactory());
    System.err.println("finished setting the surface manager");
    System.setProperty("sun.font.fontmanager", "gnu.java.awt.peer.x.EscherFontManager");
  }

  /**
   * The default graphics device. This is normally the local main X
   * Display, but can be configured to be any X connection.
   */
  private XGraphicsDevice defaultDevice;

  /**
   * All configured devices.
   */
  private XGraphicsDevice[] devices;

  /**
   * Creates a new XGraphicsEnvironment. This loads the configuration if
   * there is one present and initializes the XGraphicsDevices in the
   * environment. If there is no configuration, then there is one
   * default device initialized with the local main X device.
   */
  public XGraphicsEnvironment()
  {
    // Initiliaze the devices.
    Properties props = new Properties();
    File config = new File(System.getProperty("user.home"),
                           ".xawt.properties");

    try
      {
        FileInputStream configIn = new FileInputStream(config);
        props.load(configIn);
        int dev = 1;
        ArrayList<XGraphicsDevice> deviceList = new ArrayList<XGraphicsDevice>();
        while (true)
          {
            String propName = "display." + dev;
            String propValue = props.getProperty(propName);
            if (propValue != null)
              {
                Display.Name displayName = new Display.Name(propValue);
                XGraphicsDevice device = new XGraphicsDevice(displayName);
                if (dev == 1)
                  defaultDevice = device;
                deviceList.add(device);
                dev++;
              }
            else
              {
                if (dev == 1)
                  {
                    defaultDevice = initDefaultDevice();
                    deviceList.add(defaultDevice);
                  }
                break;
              }
          }
        devices = (XGraphicsDevice[]) deviceList.toArray
                                      (new XGraphicsDevice[deviceList.size()]);
      }
    catch (FileNotFoundException ex)
      {
        defaultDevice = initDefaultDevice();
        devices = new XGraphicsDevice[]{ defaultDevice };
      }
    catch (IOException ex)
      {
        defaultDevice = initDefaultDevice();
        devices = new XGraphicsDevice[]{ defaultDevice };
      }
  }

  /**
   * Helper method that initializes the default device in the case when there
   * is no configuration for the default.
   */
  private XGraphicsDevice initDefaultDevice()
  {
    String display = System.getenv("DISPLAY");
    if (display == null)
      display = ":0.0";
    Display.Name displayName = new Display.Name(display);
    return new XGraphicsDevice(displayName);
  }

  @Override
  protected int getNumScreens()
  {
    return devices.length;
  }

  @Override
  protected GraphicsDevice makeScreenDevice(int i)
  {
    return devices[i];
  }

  @Override
  public boolean isDisplayLocal()
  {
    // TODO: Implement properly.
    return true;
  }
}

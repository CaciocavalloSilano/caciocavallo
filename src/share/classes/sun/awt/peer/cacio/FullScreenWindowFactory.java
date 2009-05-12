/*
 * Copyright 2008 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package sun.awt.peer.cacio;

import java.awt.GraphicsConfiguration;
import java.util.HashMap;
import java.util.Map;

public class FullScreenWindowFactory implements PlatformWindowFactory {

    /**
     * This is used to re-source the events coming from the
     * PlatformScreen to the corresponding ManagedWindowContainer.
     */
    private class FullScreenEventSource implements CacioEventSource {
        public EventData getNextEvent() {
            EventData d = eventSource.getNextEvent();
            PlatformScreen source = (PlatformScreen) d.getSource();
            d.setSource(screenMap.get(source));
            return d;
        }
    }

    /**
     *
     */
    private PlatformScreenSelector selector;

    private Map<PlatformScreen,ScreenManagedWindowContainer> screenMap;

    /**
     * The event source that generates the basic events.
     * Note: We create and return a FullScreenEventSource that
     * uses this eventSource as 'backend' and infers all the higher
     * level events.
     */
    private CacioEventSource eventSource;

    /**
     * Constructs a new FullScreenWindowFactory that uses the
     * specified container as container for all toplevel windows.
     *
     * The event source is expected to generate the following event types:
     * <ul>
     * <li>{@code MouseEvent.MOUSE_PRESSED}</li>
     * <li>{@code MouseEvent.MOUSE_RELEASED}</li>
     * <li>{@code MouseEvent.MOUSE_MOVED}</li>
     * <li>{@code KeyEvent.KEY_PRESSED}</li>
     * <li>{@code KeyEvent.KEY_RELEASED}</li>
     * </ul>
     *
     * All the other events (component, window, focus, remaining mouse and key
     * events) are inferred and synthesized by the event source that is
     * created from this factory.
     *
     * @param screen the container to be used for toplevel windows
     * @param s the event source to use
     */
    public FullScreenWindowFactory(PlatformScreen screen,
                                   CacioEventSource s) {

        this(new DefaultScreenSelector(screen), s);
    }

    public FullScreenWindowFactory(PlatformScreenSelector screenSelector,
                                   CacioEventSource s) {

        this.selector = screenSelector;
        this.eventSource = s;
        screenMap = new HashMap<PlatformScreen,ScreenManagedWindowContainer>();
    }


    /**
     * Creates a {@link PlatformWindow} instance.
     *
     * @param cacioComponent the corresponding Cacio component
     * @parent the parent window, or <code>null</code> for top level windows
     *
     * @return the platform window instance
     */
    public final PlatformWindow createPlatformWindow(CacioComponent awtComponent,
                                                     PlatformWindow parent) {
        if (parent == null) {
            throw new IllegalArgumentException("parent cannot be null");
        }

        ManagedWindow p = (ManagedWindow) parent;
        return new ManagedWindow(p, awtComponent);
    }

    @Override
    public final PlatformWindow createPlatformToplevelWindow(CacioComponent comp) {

        GraphicsConfiguration gc = comp.getAWTComponent().getGraphicsConfiguration();
        PlatformScreen screen = selector.getPlatformScreen(gc);
        ScreenManagedWindowContainer smwc = screenMap.get(screen);
        if (smwc == null) {
            smwc = new ScreenManagedWindowContainer(screen);
            screenMap.put(screen, smwc);
        }

        return new ManagedWindow(smwc, comp);
    }

    @Override
    public final CacioEventPump<?> createEventPump() {
        FullScreenEventSource s = new FullScreenEventSource();
        return new FullScreenEventPump(s);
    }

    private static final class DefaultScreenSelector implements
        PlatformScreenSelector {

        PlatformScreen screen = null;

        DefaultScreenSelector(PlatformScreen screen) {

            this.screen = screen;
        }

        @Override
        public PlatformScreen getPlatformScreen(GraphicsConfiguration config) {

            return screen;
        }
    }
}

/*
 * Copyright 2009 Sun Microsystems, Inc.  All Rights Reserved.
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

#include <X11/Xlib.h>
#include "sun_awt_peer_x11_X11PlatformWindow.h"

/*
 * Class:     sun_awt_peer_x11_X11PlatformWindow
 * Method:    nativeInit
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_sun_awt_peer_x11_X11PlatformWindow_nativeInit
  (JNIEnv *env, jobject thiz, jlong dpyPtr, jlong parentPtr,
   int x, int y, int w, int h)
{
    Display *display;
    Window window;
    Window parent;

    display = (Display*) dpyPtr;
    parent = (Window) parentPtr;
    if (parent == 0) {
        parent = DefaultRootWindow(display);
    }
    if (w <= 0) w = 1;
    if (h <= 0) h = 1;
    window = XCreateSimpleWindow(display, parent, x, y, w, h, 0, 0, 0);
}

/*
 * Class:     sun_awt_peer_x11_X11PlatformWindow
 * Method:    nativeSetBounds
 * Signature: (JIIII)V
 */
JNIEXPORT void JNICALL Java_sun_awt_peer_x11_X11PlatformWindow_nativeSetBounds
  (JNIEnv *env, jobject thiz, jlong dpyPtr, jlong nw, jint x, jint y, jint w, jint h)
{
    Display *display;
    Window window;

    display = (Display*) dpyPtr;
    window = (Window) nw;

    if (w <= 0) w = 1;
    if (h <= 0) h = 1;
    XMoveResizeWindow(display, window, x, y, w, h);
}

/*
 * Class:     sun_awt_peer_x11_X11PlatformWindow
 * Method:    nativeSetVisible
 * Signature: (JJZ)V
 */
JNIEXPORT void JNICALL Java_sun_awt_peer_x11_X11PlatformWindow_nativeSetVisible
  (JNIEnv *env, jobject thiz, jlong dpyPtr, jlong nw, jboolean visible)
{
    Display *display;
    Window window;

    display = (Display*) dpyPtr;
    window = (Window) nw;

    if (visible) {
        XMapWindow(display, window);
    } else {
        XUnmapWindow(display, window);
    }
    XFlush(display);
}

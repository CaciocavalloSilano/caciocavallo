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
#include "sun_awt_peer_x11_X11EventData.h"
#include "sun_awt_peer_x11_X11EventPump.h"

static jfieldID typeFID;
static jfieldID windowFID;

/*
 * Class:     sun_awt_peer_x11_X11EventPump
 * Method:    initIDs
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_sun_awt_peer_x11_X11EventPump_initIDs
  (JNIEnv *env, jclass cls)
{
    jclass eventDataCls = (*env)->FindClass(env, "sun/awt/peer/x11/X11EventData");
    if ((*env)->ExceptionCheck(env)) return;
    typeFID = (*env)->GetFieldID(env, eventDataCls, "type", "I");
    if ((*env)->ExceptionCheck(env)) return;
    windowFID = (*env)->GetFieldID(env, eventDataCls, "window", "J");
}

/*
 * Class:     sun_awt_peer_x11_X11EventPump
 * Method:    nativeFetchEvent
 * Signature: (Lsun/awt/peer/x11/X11EventData;)V
 */
JNIEXPORT void JNICALL Java_sun_awt_peer_x11_X11EventPump_nativeFetchEvent
  (JNIEnv *env, jobject thiz, jlong dpyPtr, jobject eventData)
{
    Display *display;
    XEvent event;

    display = (Display*) dpyPtr;

    if (XPending(display) == 0) {
        return;
    }

    XNextEvent(display, &event);
    switch (event.type) {
        case MapNotify:
            (*env)->SetIntField(env, eventData, typeFID, sun_awt_peer_x11_X11EventData_MAP_NOTIFY);
            if ((*env)->ExceptionCheck(env)) return;
            (*env)->SetLongField(env, eventData, windowFID, (jlong) event.xmap.window);
            if ((*env)->ExceptionCheck(env)) return;
            break;
        case Expose:
            (*env)->SetIntField(env, eventData, typeFID, sun_awt_peer_x11_X11EventData_EXPOSE);
            if ((*env)->ExceptionCheck(env)) return;
            (*env)->SetLongField(env, eventData, windowFID, (jlong) event.xexpose.window);
            if ((*env)->ExceptionCheck(env)) return;
            break;
        default:
            fprintf(stderr, "Unhandled X event type: %d\n", event.type);
            break;
    }
}

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
#include "java_awt_event_MouseEvent.h"
#include "sun_awt_peer_x11_X11PlatformScreen.h"

static jmethodID eventDataSetIdMID;
static jmethodID eventDataSetModifiersMID;
static jmethodID eventDataSetXMID;
static jmethodID eventDataSetYMID;

static jclass sunToolkitCls;
static jmethodID sunToolkitLockMID;
static jmethodID sunToolkitUnlockMID;

/*
 * Class:     sun_awt_peer_x11_X11PlatformScreen
 * Method:    initIDs
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_sun_awt_peer_x11_X11PlatformScreen_initIDs
  (JNIEnv *env, jclass cls)
{
    jclass eventDataCls = (*env)->FindClass(env,
                                            "sun/awt/peer/cacio/EventData");
    if ((*env)->ExceptionCheck(env)) return;

    eventDataSetIdMID = (*env)->GetMethodID(env, eventDataCls,
                                            "setId", "(I)V");
    if ((*env)->ExceptionCheck(env)) return;

    eventDataSetModifiersMID = (*env)->GetMethodID(env, eventDataCls,
                                                   "setModifiers", "(I)V");
    if ((*env)->ExceptionCheck(env)) return;

    eventDataSetXMID = (*env)->GetMethodID(env, eventDataCls,
                                           "setX", "(I)V");
    if ((*env)->ExceptionCheck(env)) return;

    eventDataSetYMID = (*env)->GetMethodID(env, eventDataCls,
                                           "setY", "(I)V");
    if ((*env)->ExceptionCheck(env)) return;

    sunToolkitCls = (*env)->FindClass(env, "sun/awt/SunToolkit");
    if ((*env)->ExceptionCheck(env)) return;
    /*
    sunToolkitCls = (*env)->NewGlobalRef(env, sunToolkitCls);
    if ((*env)->ExceptionCheck(env)) return;
     */
    sunToolkitLockMID = (*env)->GetStaticMethodID(env, sunToolkitCls,
                                                  "awtLock", "()V");
    if ((*env)->ExceptionCheck(env)) return;
    sunToolkitUnlockMID = (*env)->GetStaticMethodID(env, sunToolkitCls,
                                                    "awtUnlock", "()V");
    if ((*env)->ExceptionCheck(env)) return;
}

/*
 * Class:     sun_awt_peer_x11_X11PlatformScreen
 * Method:    nativeInitScreen
 * Signature: (II)J
 */
JNIEXPORT jlong JNICALL Java_sun_awt_peer_x11_X11PlatformScreen_nativeInitScreen
  (JNIEnv *env, jobject thiz, jlong dpyPtr, jint w, jint h)
{
    Display *display;
    Window window;

    display = (Display*) dpyPtr;
    (*env)->CallStaticVoidMethod(env, sunToolkitCls, sunToolkitLockMID);
    window = XCreateSimpleWindow(display, DefaultRootWindow(display), 50, 50, w, h, 0, 0, 0);
    XSelectInput(display, window, ButtonPressMask | ButtonReleaseMask
                                  | PointerMotionMask | ExposureMask);
    XMapWindow(display, window);
    XFlush(display);

    (*env)->CallStaticVoidMethod(env, sunToolkitCls, sunToolkitUnlockMID);

    return (jlong) window;
}

static void fetchEvent(JNIEnv *env, Display *display, jobject eventData) {

    XEvent event;

    XNextEvent(display, &event);
    switch (event.type) {
        case ButtonPress: {
            printf("PRESS\n");
            XButtonEvent bp = event.xbutton;
            (*env)->CallVoidMethod(env, eventData, eventDataSetIdMID,
                                   java_awt_event_MouseEvent_MOUSE_PRESSED);
            (*env)->CallVoidMethod(env, eventData, eventDataSetXMID,
                                   (jint) bp.x);
            (*env)->CallVoidMethod(env, eventData, eventDataSetYMID,
                                   (jint) bp.y);
            /* TODO: Map the masks. */
            (*env)->CallVoidMethod(env, eventData, eventDataSetModifiersMID,
                                   java_awt_event_MouseEvent_BUTTON1_DOWN_MASK);
          }
          break;
        case ButtonRelease: {
            printf("RELEASE\n");
            XButtonEvent bp = event.xbutton;
            (*env)->CallVoidMethod(env, eventData, eventDataSetIdMID,
                                   java_awt_event_MouseEvent_MOUSE_RELEASED);
            (*env)->CallVoidMethod(env, eventData, eventDataSetXMID,
                                   (jint) bp.x);
            (*env)->CallVoidMethod(env, eventData, eventDataSetYMID,
                                   (jint) bp.y);
            /* TODO: Map the masks. */
            (*env)->CallVoidMethod(env, eventData, eventDataSetModifiersMID,
                                   0 /*java_awt_event_MouseEvent_BUTTON1_DOWN_MASK*/);
          }
          break;
        case MotionNotify: {
            XButtonEvent bp = event.xbutton;
            (*env)->CallVoidMethod(env, eventData, eventDataSetIdMID,
                                   java_awt_event_MouseEvent_MOUSE_MOVED);
            (*env)->CallVoidMethod(env, eventData, eventDataSetXMID,
                                   (jint) bp.x);
            (*env)->CallVoidMethod(env, eventData, eventDataSetYMID,
                                   (jint) bp.y);
            /* TODO: Map the masks. */
            /*
            (*env)->CallVoidMethod(env, eventData, eventDataSetModifiersMID,
                                   java_awt_event_MouseEvent_BUTTON1_DOWN_MASK);
             **/
          }
          break;
        case Expose:
        default:
          printf("unsupported event type: %d\n" + event.type);
    }
}

/*
 * Class:     sun_awt_peer_x11_X11PlatformScreen
 * Method:    nativeGetEvent
 * Signature: (Lsun/awt/peer/cacio/EventData;)V
 */
JNIEXPORT void JNICALL Java_sun_awt_peer_x11_X11PlatformScreen_nativeGetEvent
  (JNIEnv *env, jobject thiz, jlong dpyPtr, jobject eventData)
{

    Display *display;

    display = (Display*) dpyPtr;

    (*env)->CallStaticVoidMethod(env, sunToolkitCls, sunToolkitLockMID);
    if (XPending(display) > 0) {
        fetchEvent(env, display, eventData);
    }
    (*env)->CallStaticVoidMethod(env, sunToolkitCls, sunToolkitUnlockMID);
}

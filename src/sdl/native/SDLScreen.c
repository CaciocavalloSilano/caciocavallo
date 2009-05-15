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
#include <jni.h>

#include "SDL.h"

#include "java_awt_event_MouseEvent.h"
#include "net_java_openjdk_awt_peer_sdl_SDLScreen.h"

extern void JNU_ThrowByName(JNIEnv *env, const char *name, const char *msg);

static jmethodID eventDataSetIdMID;
static jmethodID eventDataSetModifiersMID;
static jmethodID eventDataSetXMID;
static jmethodID eventDataSetYMID;

static jclass sunToolkitCls;
static jmethodID sunToolkitLockMID;
static jmethodID sunToolkitUnlockMID;

JNIEXPORT void JNICALL Java_net_java_openjdk_awt_peer_sdl_SDLScreen_initIDs
  (JNIEnv *env, jclass cls __attribute__((unused)))
{
    jclass eventDataCls = (*env)->FindClass(env,
                                            "sun/awt/peer/cacio/managed/EventData");
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

JNIEXPORT jlong JNICALL Java_net_java_openjdk_awt_peer_sdl_SDLScreen_nativeInitScreen
  (JNIEnv *env, jobject thiz __attribute__((unused)), jint width, jint height)
{
    SDL_Surface *surface = NULL;

    (*env)->CallStaticVoidMethod(env, sunToolkitCls, sunToolkitLockMID);

    /* passing 0 for bpp takes the current display video depth */
    surface = SDL_SetVideoMode(width, height, 0, SDL_DOUBLEBUF | SDL_HWPALETTE);
    if (surface == NULL) {
        fprintf(stderr, "Unable to set video mode: %s\n", SDL_GetError());
        JNU_ThrowByName(env, "java/lang/InternalError",
                "SDLScreen::nativeInitScreen: cannot create SDL_Surface.");
        return 0L;
    }

    (*env)->CallStaticVoidMethod(env, sunToolkitCls, sunToolkitUnlockMID);

    return (jlong) surface;
}

JNIEXPORT void JNICALL Java_net_java_openjdk_awt_peer_sdl_SDLScreen_nativeGetEvent
  (JNIEnv *env, jobject thiz __attribute__((unused)), jobject eventData)
{
    SDL_Event nextEvent;

    (*env)->CallStaticVoidMethod(env, sunToolkitCls, sunToolkitLockMID);

    SDL_PollEvent(&nextEvent);
    switch (nextEvent.type) {
        
        case SDL_MOUSEMOTION:
        {
            (*env)->CallVoidMethod(env, eventData, eventDataSetIdMID,
                                   java_awt_event_MouseEvent_MOUSE_MOVED);
            (*env)->CallVoidMethod(env, eventData, eventDataSetXMID,
                                   (jint) nextEvent.motion.x);
            (*env)->CallVoidMethod(env, eventData, eventDataSetYMID,
                                   (jint) nextEvent.motion.y);
            break;
        }

        case SDL_MOUSEBUTTONUP:
        {
            (*env)->CallVoidMethod(env, eventData, eventDataSetIdMID,
                                   java_awt_event_MouseEvent_MOUSE_RELEASED);
            (*env)->CallVoidMethod(env, eventData, eventDataSetXMID,
                                   (jint) nextEvent.button.x);
            (*env)->CallVoidMethod(env, eventData, eventDataSetYMID,
                                   (jint) nextEvent.button.y);
            /* TODO: Map the masks. */
            (*env)->CallVoidMethod(env, eventData, eventDataSetModifiersMID,
                                   0 /*java_awt_event_MouseEvent_BUTTON1_DOWN_MASK*/);

            break;
        }
        
        case SDL_MOUSEBUTTONDOWN:
        {
            (*env)->CallVoidMethod(env, eventData, eventDataSetIdMID,
                                   java_awt_event_MouseEvent_MOUSE_PRESSED);
            (*env)->CallVoidMethod(env, eventData, eventDataSetXMID,
                                   (jint) nextEvent.button.x);
            (*env)->CallVoidMethod(env, eventData, eventDataSetYMID,
                                   (jint) nextEvent.button.y);
            /* TODO: Map the masks. */
            (*env)->CallVoidMethod(env, eventData, eventDataSetModifiersMID,
                                   java_awt_event_MouseEvent_BUTTON1_DOWN_MASK);

            break;
        }

        default:
            break;
    }
    
    (*env)->CallStaticVoidMethod(env, sunToolkitCls, sunToolkitUnlockMID);
}

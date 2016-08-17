/*
 * Copyright (c) 2016, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
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
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

#include "net_java_openjdk_cacio_wayland_WaylandEventSource.h"

#include <wayland-util.h>
#include <stdio.h>
#include <linux/input.h>
#include <malloc.h>

#include "java_awt_event_MouseEvent.h"
#include "java_awt_event_KeyEvent.h"
#include "wayland.h"
#include "wayland_events.h"

static jfieldID _eventDataIdFieldID;
static jfieldID _eventDataSourceFieldID;
static jfieldID _eventDataModifiersFieldID;
static jfieldID _eventDataXFieldID;
static jfieldID _eventDataYFieldID;
static jfieldID _eventDataKeyCodeFieldID;
static jfieldID _eventDataKeyCharFieldID;
static jfieldID _eventDataButtonFieldID;
static jfieldID _eventDataClickCountFieldID;


static jclass     _longClass;
static jmethodID  _longCstor;

static int map_modifiers(uint32_t modifiers) {
    int mods = 0;

    if ((modifiers & MOD_SHIFT_MASK) != 0) {
        mods |= java_awt_event_MouseEvent_SHIFT_MASK;
    }

    if ((modifiers & MOD_ALT_MASK) != 0) {
        mods |= java_awt_event_MouseEvent_ALT_MASK;
    }

    if ((modifiers & MOD_CONTROL_MASK) != 0) {
        mods |= java_awt_event_MouseEvent_CTRL_MASK;
    }

    return mods;
}

static int map_mouse_button(uint32_t button) {
    switch (button) {
        case BTN_LEFT:
            return java_awt_event_MouseEvent_BUTTON1_DOWN_MASK;
        case BTN_MIDDLE:
            return java_awt_event_MouseEvent_BUTTON2_DOWN_MASK;
        case BTN_RIGHT:
            return java_awt_event_MouseEvent_BUTTON3_DOWN_MASK;
    }
    return (int)button;
}

void map_mouse_event(JNIEnv* env, Event *event, jobject jevent) {
    jint action = 0;
    int whichButton = map_mouse_button(event->e.m.button);
    switch(event->e.m.action) {
        case MOUSE_ENTER:
            action = java_awt_event_MouseEvent_MOUSE_ENTERED;
            break;
        case MOUSE_LEAVE:
            action = java_awt_event_MouseEvent_MOUSE_EXITED;
            break;
        case MOUSE_MOVE:
            action = java_awt_event_MouseEvent_MOUSE_MOVED;
            break;
         case MOUSE_BUTTON_PRESS:
            action = java_awt_event_MouseEvent_MOUSE_PRESSED;
            break;
         case MOUSE_BUTTON_RELEASE:
            action = java_awt_event_MouseEvent_MOUSE_RELEASED;
            break;
         case MOUSE_CLICK:
            action = java_awt_event_MouseEvent_MOUSE_CLICKED;
            break;
         case MOUSE_DRAG:
            action = java_awt_event_MouseEvent_MOUSE_DRAGGED;
            break;
         case MOUSE_WHEEL:
            action = java_awt_event_MouseEvent_MOUSE_WHEEL;
            break;
    }

    jobject sourceId = (*env)->NewObject(env, _longClass, _longCstor, event->id);
    (*env)->SetObjectField(env, jevent, _eventDataSourceFieldID, sourceId);
    (*env)->SetIntField(env, jevent, _eventDataIdFieldID, action);
    (*env)->SetIntField(env, jevent, _eventDataXFieldID, (jint)event->e.m.x);
    (*env)->SetIntField(env, jevent, _eventDataYFieldID, (jint)event->e.m.y);
    (*env)->SetIntField(env, jevent, _eventDataModifiersFieldID,
        map_modifiers(event->modifiers) | whichButton);
    (*env)->SetIntField(env, jevent, _eventDataButtonFieldID, whichButton);
    (*env)->SetIntField(env, jevent, _eventDataClickCountFieldID, (int)event->e.m.click_count);
}

void map_keyboard_event(JNIEnv* env, Event *event, jobject jevent) {
    jint action = 0;

    switch(event->e.k.action) {
        case KEY_PRESS:
            action = java_awt_event_KeyEvent_KEY_PRESSED;
            break;
        case KEY_TYPE:
            action = java_awt_event_KeyEvent_KEY_TYPED;
            event->e.k.keyCode = java_awt_event_KeyEvent_VK_UNDEFINED;
            break;
        case KEY_RELEASE:
            action = java_awt_event_KeyEvent_KEY_RELEASED;
            break;
        default:
            break;
    }

    jobject sourceId = (*env)->NewObject(env, _longClass, _longCstor, event->id);
    (*env)->SetObjectField(env, jevent, _eventDataSourceFieldID, sourceId);

    (*env)->SetIntField(env, jevent, _eventDataIdFieldID, action);
    (*env)->SetIntField(env, jevent, _eventDataKeyCodeFieldID, (jint)event->e.k.keyCode);
    (*env)->SetCharField(env, jevent, _eventDataKeyCharFieldID, (jchar)event->e.k.keyChar);
    (*env)->SetIntField(env, jevent, _eventDataModifiersFieldID, map_modifiers(event->modifiers));
}


static void handle_display_event(Event* evt) {
    ShmSurface* surface= evt->e.d.surface;
    // A pending flush event, that occurs earlier than most recent flush,
    // should be executed.
    if (timestamp_diff(&evt->e.d.timestamp, &surface->last_update) >= 0) {
        clock_gettime(CLOCK_REALTIME, &surface->last_update);
        wl_surface_attach(surface->surface, surface->buffer, 0, 0);
        wl_surface_commit(surface->surface);
        display_flush();
    }
}

static void handle_surface_event(Event* evt) {
    ShmSurface* surface= evt->e.s.surface;
    switch(evt->e.s.action) {
        case SURFACE_DISPOSE:
            DestroyShmScreenSurface(surface);
            break;
        case SURFACE_UNMAP:
            UnmapShmScreenSurface(surface);
            break;
        case SURFACE_MAP:
            RemapShmScreenSurface(surface, surface->width, surface->height);
            break;
    }
}

// Map native event to awt event
bool map_event(JNIEnv* env, Event *event, jobject jevent) {
    switch(event->kind)  {
        case MOUSE_EVENT:
            map_mouse_event(env, event, jevent);
            return true;
        case KEY_EVENT:
            map_keyboard_event(env, event, jevent);
            return true;
        case DISPLAY_EVENT:
            handle_display_event(event);
            return false;
        case SURFACE_EVENT:
            handle_surface_event(event);
            return false;
    }
    return false;
}

/*
 * Class:     net_java_openjdk_cacio_wayland_WaylandEventSource
 * Method:    initIDs
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_net_java_openjdk_cacio_wayland_WaylandEventSource_initIDs
  (JNIEnv *env, jclass clz) {
    jclass eventDataClass = (*env)->FindClass(env, "sun/awt/peer/cacio/managed/EventData");

    _eventDataIdFieldID = (*env)->GetFieldID(env, eventDataClass, "id", "I");
    _eventDataSourceFieldID = (*env)->GetFieldID(env, eventDataClass, "source", "Ljava/lang/Object;");
    _eventDataModifiersFieldID = (*env)->GetFieldID(env, eventDataClass, "modifiers", "I");
    _eventDataXFieldID = (*env)->GetFieldID(env, eventDataClass, "x", "I");
    _eventDataYFieldID = (*env)->GetFieldID(env, eventDataClass, "y", "I");
    _eventDataButtonFieldID = (*env)->GetFieldID(env, eventDataClass, "button", "I");
    _eventDataKeyCodeFieldID = (*env)->GetFieldID(env, eventDataClass, "keyCode", "I");
    _eventDataKeyCharFieldID = (*env)->GetFieldID(env, eventDataClass, "keyChar", "C");
    _eventDataClickCountFieldID = (*env)->GetFieldID(env, eventDataClass, "clickCount", "I");

    jclass longClz = (*env)->FindClass(env, "java/lang/Long");
    _longClass = (*env)->NewGlobalRef(env, longClz);
    _longCstor = (*env)->GetMethodID(env, _longClass, "<init>", "(J)V");
}


/*
 * Class:     net_java_openjdk_cacio_wayland_WaylandEventSource
 * Method:    nativeGetEvent
 * Signature: (Lsun/awt/peer/cacio/managed/EventData;)V
 */
JNIEXPORT void JNICALL Java_net_java_openjdk_cacio_wayland_WaylandEventSource_nativeGetEvent
  (JNIEnv *env, jobject obj, jobject evt) {
  Event *event;

  if (!is_input_inited()) {
    return;
  }

  // Wayland event loop
  while (true) {
    event = next_event();
    if (event != NULL) {
        bool ret = map_event(env, event, evt);
        free(event);
        if (ret) return;
    } else {
        if (!event_loop()) {
            jclass runtimeExceptionClass = (*env)->FindClass(env, "java/lang/RuntimeException");
            (*env)->ThrowNew(env, runtimeExceptionClass, "Wayland error");
        }
    }
  }
}


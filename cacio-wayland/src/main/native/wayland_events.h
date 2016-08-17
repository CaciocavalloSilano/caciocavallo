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

#ifndef __CACIO_WAYLAND_EVENTS_H
#define __CACIO_WAYLAND_EVENTS_H

#include "wayland.h"
#include "wayland_shm_surface.h"
#include <time.h>

// Define minimal interval to issue a display
// flush to Wayland server
// Interval is in millisecond
#define DISPLAY_UPDATE_INTERVAL 25

#define EVENT_WAIT_TIMEOUT  25

/**
 * Define wayland events
 */
 enum EventKind {
   MOUSE_EVENT,
   KEY_EVENT,
   DISPLAY_EVENT,
   SURFACE_EVENT    // event to manipulate surface
 };

 enum MouseAction {
    MOUSE_ENTER,
    MOUSE_MOVE,
    MOUSE_LEAVE,
    MOUSE_BUTTON_PRESS,
    MOUSE_BUTTON_RELEASE,
    MOUSE_CLICK,
    MOUSE_DRAG,
    MOUSE_WHEEL
 };

 enum KeyAction {
    KEY_PRESS,
    KEY_TYPE,
    KEY_RELEASE
 };

enum SurfaceAction {
    SURFACE_MAP,
    SURFACE_UNMAP,
    SURFACE_DISPOSE
};

struct MouseEvent {
    enum MouseAction action;
    int32_t          x;
    int32_t          y;
    uint32_t         button;
    uint32_t         click_count;
};

struct KeyEvent {
    enum KeyAction action;
    uint32_t       keyCode;
    char           keyChar;
};

struct SurfaceEvent {
    enum SurfaceAction action;
    ShmSurface*        surface;
    int32_t            width;
    int32_t            height;
};

struct DisplayEvent {
    struct shm_surface* surface;
    struct timespec     timestamp;
};

 struct event {
    struct wl_list  link;
    enum EventKind  kind;
    long            id;

    uint32_t        modifiers;

    union {
        struct MouseEvent   m;
        struct KeyEvent     k;
        struct DisplayEvent d;
        struct SurfaceEvent s;
    } e;
 };


typedef struct event Event;

// Return time difference in millisecond
long timestamp_diff(struct timespec* ts1, struct timespec* ts2);

uint32_t find_keycode_by_name(const char* name);

// Get next event
Event* next_event();

// Drain all pending events
void   drain_events();


// Wayland native event loop
bool event_loop();

bool init_event(struct input* input);
void cleanup_event();


void new_mouse_event(enum MouseAction action, long id, int32_t x, int32_t y, uint32_t which, uint32_t click_count);
void new_key_event(enum KeyAction action, long id, uint32_t keyCode, char keyChar);
void new_display_flush_event(ShmSurface* surface);
void new_surface_event(enum SurfaceAction action, ShmSurface* surface, int32_t width, int32_t height);


 #endif // __CACIO_WAYLAND_EVENTS_H

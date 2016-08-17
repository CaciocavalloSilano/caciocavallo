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

#ifndef __CACIO_WAYLAND_H
#define __CACIO_WAYLAND_H

#include <pthread.h>
#include <wayland-client.h>
#include <wayland-client-protocol.h>
#include <wayland-egl.h>
#include <xkbcommon/xkbcommon.h>


#ifndef bool
typedef int bool;
#define true  1
#define false 0
#endif

#ifndef MIN
#define MIN(a, b) ((a) > (b) ? (b) : (a))
#endif

#ifndef MAX
#define MAX(a, b) ((a) > (b) ? (a) : (b))
#endif

#define WL_VERSION 1

#define  MouseClickInterval  300

struct display {
    struct wl_display*          display;
    struct wl_registry*         registry;
    struct wl_compositor*       compositor;
    struct wl_subcompositor*    subcompositor;
    struct wl_shell*            shell;
    struct wl_shm*              shm;
    struct wl_output*           output;

    // Display dimension
    int32_t                     width;
    int32_t                     height;
};


struct shm_surface;

struct input {
    struct wl_display*          display;
    struct wl_seat*             seat;
    struct wl_pointer*          pointer;
    struct wl_keyboard*         keyboard;
    struct wl_touch*            touch;

    // Active Surface
    struct shm_surface*         activeSurface;

    uint32_t                    modifiers;

    // The most recent pointer location
    int                         x;
    int                         y;
    uint32_t                    button;
    uint32_t                    click_count;
    uint32_t                    last_click_time;


    // Keyboard
    struct xkb_context*         xkb_context;
    struct {
        struct xkb_keymap *keymap;
        struct xkb_state *state;
        xkb_mod_mask_t control_mask;
        xkb_mod_mask_t alt_mask;
        xkb_mod_mask_t shift_mask;
    } xkb;

    // key repeat
    int32_t                     repeat_rate_sec;
    int32_t                     repeat_rate_nsec;
    int32_t                     repeat_delay_sec;
    int32_t                     repeat_delay_nsec;

    int                         repeat_timer_fd;
    char                        repeat_char;
    uint32_t                    repeat_keycode;
    uint32_t                    repeat_key;

    int                         epoll_fd;

    // Mutex to protect event list
    pthread_mutex_t             lock;
    // Pending events
    struct wl_list              event_list;
};

#define MOD_SHIFT_MASK          0x01
#define MOD_ALT_MASK            0x02
#define MOD_CONTROL_MASK        0x04



bool wayland_init();
void wayland_cleanup();

struct display* get_display();

bool init_input(struct input* input);
void destroy_input(struct input* input);
bool is_input_inited();

struct input* get_input();


int32_t get_display_width();
int32_t get_display_height();


void display_flush();


#endif //__CACIO_WAYLAND_H

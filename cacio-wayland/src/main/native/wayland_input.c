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

#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/mman.h>
#include <time.h>
#include <xkbcommon/xkbcommon.h>
#include <sys/timerfd.h>

#include "wayland_events.h"


static void keyboard_handle_keymap(void *data, struct wl_keyboard *keyboard,
       uint32_t format, int fd, uint32_t size) {
    struct input* input = (struct input*)data;
    struct xkb_keymap *keymap;
    struct xkb_state *state;
    char *map_str;

    if (data == NULL) {
        close(fd);
        return;
    }

    if (format != WL_KEYBOARD_KEYMAP_FORMAT_XKB_V1) {
        close(fd);
        return;
    }

    map_str = mmap(NULL, size, PROT_READ, MAP_SHARED, fd, 0);
    if (map_str == MAP_FAILED) {
        close(fd);
        return;
    }

    keymap = xkb_keymap_new_from_string(input->xkb_context,
                                        map_str,
                                        XKB_KEYMAP_FORMAT_TEXT_V1,
                                        0);
    munmap(map_str, size);
    close(fd);

    if (!keymap) {
        return;
    }

    state = xkb_state_new(keymap);
    if (!state) {
        xkb_keymap_unref(keymap);
        return;
    }

    xkb_keymap_unref(input->xkb.keymap);
    xkb_state_unref(input->xkb.state);
    input->xkb.keymap = keymap;
    input->xkb.state = state;

    input->xkb.control_mask =
                (xkb_mod_mask_t)(1 << xkb_keymap_mod_get_index(input->xkb.keymap, "Control"));
    input->xkb.alt_mask =
                (xkb_mod_mask_t)(1 << xkb_keymap_mod_get_index(input->xkb.keymap, "Mod1"));
    input->xkb.shift_mask =
                (xkb_mod_mask_t)(1 << xkb_keymap_mod_get_index(input->xkb.keymap, "Shift"));
}

static void keyboard_handle_enter(void *data, struct wl_keyboard *keyboard,
                      uint32_t serial, struct wl_surface *surface,
                      struct wl_array *keys) {
    if (surface != NULL) {
        ShmSurface* activeSurface = (ShmSurface*)wl_surface_get_user_data(surface);
        struct input* input = get_input();
        input->activeSurface = activeSurface;
    }
}

static void stop_keyboard_repeat(struct input* input) {
    struct itimerspec   its;
    its.it_interval.tv_sec = 0;
    its.it_interval.tv_nsec = 0;
    its.it_value.tv_sec = 0;
    its.it_value.tv_nsec = 0;
    timerfd_settime(input->repeat_timer_fd, 0, &its, NULL);
    input->repeat_char = '\0';
    input->repeat_keycode = 0;
}

static void start_keyboard_repeat(struct input* input, char ch, uint32_t keycode) {
    struct itimerspec its;
    input->repeat_char = ch;
    input->repeat_keycode = keycode;

    its.it_interval.tv_sec = input->repeat_rate_sec;
    its.it_interval.tv_nsec = input->repeat_rate_nsec;
    its.it_value.tv_sec = input->repeat_delay_sec;
    its.it_value.tv_nsec = input->repeat_delay_nsec;
    timerfd_settime(input->repeat_timer_fd, 0, &its, NULL);
}

static void keyboard_handle_leave(void *data, struct wl_keyboard *keyboard,
                      uint32_t serial, struct wl_surface *surface) {
    struct input* input = (struct input*)data;
    stop_keyboard_repeat(input);
    input->activeSurface = NULL;
}


static void keyboard_handle_key(void *data, struct wl_keyboard *keyboard,
                    uint32_t serial, uint32_t time, uint32_t key,
                    uint32_t state_w) {
    struct input*              input = (struct input*)data;
    enum wl_keyboard_key_state state = state_w;
    uint32_t                   code;
    int                        num_syms;
    const xkb_keysym_t         *syms;
    xkb_keysym_t               sym;

    code = key + 8;
    if (!input->xkb.state) return;
    ShmSurface* activeSurface = input->activeSurface;
    // No active surface
    if (activeSurface == NULL) return;

    num_syms = xkb_state_key_get_syms(input->xkb.state, code, &syms);
    if (num_syms == 1) {
        sym = syms[0];
    } else {
        return;
    }

    char buffer[128];
    char ch = '\0';

    // The key has valid utf8 character
    if (xkb_keysym_to_utf8(sym, buffer, sizeof(buffer)) > 0) {
        ch = buffer[0];

        if (ch == '\r') {
            ch = '\n';
        }
    }

    uint32_t mapped_keycode = code;
    if (xkb_keysym_get_name(sym, buffer, sizeof(buffer)) > 0) {
        mapped_keycode = find_keycode_by_name(buffer);
        if (mapped_keycode == 0) {
            return;
        }
    } else {
        return;
    }

    if (state == WL_KEYBOARD_KEY_STATE_RELEASED &&
        key == input->repeat_key) {
        stop_keyboard_repeat(input);
        new_key_event(KEY_RELEASE, activeSurface->id, mapped_keycode, ch);
    } else if (state == WL_KEYBOARD_KEY_STATE_PRESSED &&
        xkb_keymap_key_repeats(input->xkb.keymap, code)) {
        input->repeat_key = key;

        new_key_event(KEY_PRESS, activeSurface->id, mapped_keycode, ch);
        if (ch != '\0') {
            new_key_event(KEY_TYPE, activeSurface->id, mapped_keycode, ch);
        }

        start_keyboard_repeat(input, ch, mapped_keycode);
    }

}

static void keyboard_handle_modifiers(void *data, struct wl_keyboard *keyboard,
                          uint32_t serial, uint32_t mods_depressed,
                          uint32_t mods_latched, uint32_t mods_locked,
                          uint32_t group) {
    struct input* input = (struct input*)data;
    xkb_mod_mask_t mask;

    /* If we're not using a keymap, then we don't handle PC-style modifiers */
    if (!input->xkb.keymap) {
        return;
    }

    xkb_state_update_mask(input->xkb.state, mods_depressed, mods_latched,
                              mods_locked, 0, 0, group);
    mask = xkb_state_serialize_mods(input->xkb.state,
                                        XKB_STATE_MODS_DEPRESSED |
                                        XKB_STATE_MODS_LATCHED);
    input->modifiers = 0;
    if (mask & input->xkb.control_mask)
                input->modifiers |= MOD_CONTROL_MASK;
    if (mask & input->xkb.alt_mask)
                input->modifiers |= MOD_ALT_MASK;
    if (mask & input->xkb.shift_mask)
                input->modifiers |= MOD_SHIFT_MASK;
}

static void keyboard_handle_repeat_info(void *data, struct wl_keyboard *keyboard,
                            int32_t rate, int32_t delay) {

    struct input* input = (struct input*)data;

    if (rate == 0) return;

    if (rate == 1) {
        input->repeat_rate_sec = 1;
        input->repeat_rate_nsec = 0;
    } else {
        input->repeat_rate_sec = 0;
        input->repeat_rate_nsec = 1000000000 / rate;
    }

    input->repeat_delay_sec = delay / 1000;
    input->repeat_delay_nsec = (delay % 1000) * 1000 * 1000;
}


static const struct wl_keyboard_listener keyboard_listener = {
        keyboard_handle_keymap,
        keyboard_handle_enter,
        keyboard_handle_leave,
        keyboard_handle_key,
        keyboard_handle_modifiers,
        keyboard_handle_repeat_info
};


static void pointer_handle_enter(void *data, struct wl_pointer *pointer,
      uint32_t serial, struct wl_surface *surface,
      wl_fixed_t sx_w, wl_fixed_t sy_w) {
      if (surface != NULL) {
        int x = wl_fixed_to_int(sx_w);
        int y = wl_fixed_to_int(sy_w);
        ShmSurface* activeSurface = (ShmSurface*)wl_surface_get_user_data(surface);
        struct input* input = get_input();
        input->activeSurface = activeSurface;
        input->x = x;
        input->y = y;
        if (activeSurface != NULL) {
          new_mouse_event(MOUSE_ENTER, activeSurface->id, x, y, 0, 0);
        }
      }
}

static void pointer_handle_leave(void *data, struct wl_pointer *pointer,
      uint32_t serial, struct wl_surface *surface) {
      struct input* input = get_input();
      ShmSurface* activeSurface = input->activeSurface;
      input->activeSurface = NULL;
      input->x = -1;
      input->y = -1;
      if (activeSurface != NULL) {
          new_mouse_event(MOUSE_LEAVE, activeSurface->id, 0, 0, 0, 0);
      }
}

static void pointer_handle_motion(void *data, struct wl_pointer *pointer,
      uint32_t time, wl_fixed_t sx_w, wl_fixed_t sy_w) {
      int x = wl_fixed_to_int(sx_w);
      int y = wl_fixed_to_int(sy_w);
      struct input* input = get_input();
      ShmSurface* activeSurface = input->activeSurface;
      // No active surface
      if (activeSurface == NULL) return;

      input->x = x;
      input->y = y;

      if (input->button == 0) {
        new_mouse_event(MOUSE_MOVE, activeSurface->id, x, y, 0, 0);
      } else {
        new_mouse_event(MOUSE_DRAG, activeSurface->id, x, y, input->button, 0);
      }
}

static void pointer_handle_button(void *data, struct wl_pointer *pointer, uint32_t serial,
    uint32_t time, uint32_t button, uint32_t state_w) {

    enum MouseAction action = (state_w == WL_POINTER_BUTTON_STATE_PRESSED) ?
        MOUSE_BUTTON_PRESS : MOUSE_BUTTON_RELEASE;

    struct input* input = get_input();
    ShmSurface* activeSurface = input->activeSurface;
    // No active surface
    if (activeSurface == NULL) return;

    new_mouse_event(action, activeSurface->id, input->x, input->y, button, 0);
    if (action == MOUSE_BUTTON_RELEASE) {
        input->button = 0;
        uint32_t click_interval = time - input->last_click_time;
        if (click_interval <= MouseClickInterval) {
            input->click_count ++;
        } else {
            input->click_count = 1;
        }
        input->last_click_time = time;
        new_mouse_event(MOUSE_CLICK, activeSurface->id, input->x, input->y, button, input->click_count);
    } else {
        input->button = button;
    }
}

static void pointer_handle_axis(void *data, struct wl_pointer *pointer,
    uint32_t time, uint32_t axis, wl_fixed_t value) {

    struct input* input = get_input();
    ShmSurface* activeSurface = input->activeSurface;
    // No active surface
    if (activeSurface == NULL) return;

    int delta = wl_fixed_to_int(value);
    new_mouse_event(MOUSE_WHEEL, activeSurface->id, input->x, input->y, (delta < 0 ) ? 4 : 0, 0);
}


static const struct wl_pointer_listener pointer_listener = {
        .enter  = pointer_handle_enter,
        .leave  = pointer_handle_leave,
        .motion = pointer_handle_motion,
        .button = pointer_handle_button,
        .axis   = pointer_handle_axis
};


static void seat_handle_capabilities(void *data, struct wl_seat *seat,
      enum wl_seat_capability caps) {

        struct input *input = data;

        if ((caps & WL_SEAT_CAPABILITY_POINTER) && !input->pointer) {
            input->pointer = wl_seat_get_pointer(seat);
            wl_pointer_set_user_data(input->pointer, input);
            wl_pointer_add_listener(input->pointer, &pointer_listener,
                    input);
        } else if (!(caps & WL_SEAT_CAPABILITY_POINTER) && input->pointer) {
            wl_pointer_release(input->pointer);
            input->pointer = NULL;
        }

        if ((caps & WL_SEAT_CAPABILITY_KEYBOARD) && !input->keyboard) {
            input->keyboard = wl_seat_get_keyboard(seat);
            wl_keyboard_set_user_data(input->keyboard, input);
            wl_keyboard_add_listener(input->keyboard, &keyboard_listener,
                input);
        } else if (!(caps & WL_SEAT_CAPABILITY_KEYBOARD) && input->keyboard) {
            wl_keyboard_release(input->keyboard);
            input->keyboard = NULL;
        }
}

static void seat_handle_name(void *data, struct wl_seat *seat,
                 const char *name) {
}

static const struct wl_seat_listener seat_listener = {
        seat_handle_capabilities,
        seat_handle_name
};

bool init_input(struct input* input) {
    if (input->seat == NULL)  return false;

    input->xkb_context = xkb_context_new(0);
    if (input->xkb_context == NULL) {
        return false;
    }

    /*
     * Default key repeat settings
     * Initial delay 250 milliseconds
     */
    input->repeat_delay_sec = 0;
    input->repeat_delay_nsec = 1000 * 1000 * 250;
    /**
     * Repeat every 100 milliseconds
     */
    input->repeat_rate_sec = 0;
    input->repeat_rate_nsec = 1000 * 1000 * 100;

    /**
     * Initial repeat timer
     */
    input->repeat_timer_fd = timerfd_create(CLOCK_MONOTONIC, TFD_CLOEXEC | TFD_NONBLOCK);
    if (input->repeat_timer_fd == -1) {
        return false;
    }

    /**
     * Initial event pump
     */
    if (!init_event(input)) {
        return false;
    }

    /*
     * Initial pending event list and lock
     */
    pthread_mutex_init(&input->lock, NULL);
    wl_list_init(&input->event_list);

    wl_seat_add_listener(input->seat, &seat_listener, input);
    wl_seat_set_user_data(input->seat, input);
    return true;
}


void destroy_input(struct input* input) {
    cleanup_event(input);
    close(input->repeat_timer_fd);

    drain_events();

    if (input->touch != NULL)    wl_touch_release(input->touch);
    if (input->pointer != NULL)  wl_pointer_release(input->pointer);
    if (input->keyboard != NULL) wl_keyboard_release(input->keyboard);
}

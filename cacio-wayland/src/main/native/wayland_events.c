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

#include <malloc.h>
#include "java_awt_event_KeyEvent.h"
#include "wayland_events.h"
#include "wayland.h"
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <sys/epoll.h>


struct name_keyCode {
    const char* name;
    long        code;
};


// Map key name to key code
static struct name_keyCode name_keyCode_map[] = {
    {"Return",      java_awt_event_KeyEvent_VK_ENTER},
    {"KP_Enter",    java_awt_event_KeyEvent_VK_ENTER},
    {"BackSpace",   java_awt_event_KeyEvent_VK_BACK_SPACE},
    {"Tab",         java_awt_event_KeyEvent_VK_TAB},
    {"Cancel",      java_awt_event_KeyEvent_VK_CANCEL},
    {"Clear",       java_awt_event_KeyEvent_VK_CLEAR},
    {"Shift_L",     java_awt_event_KeyEvent_VK_SHIFT},
    {"Shift_R",     java_awt_event_KeyEvent_VK_SHIFT},
    {"Control_L",   java_awt_event_KeyEvent_VK_CONTROL},
    {"Control_R",   java_awt_event_KeyEvent_VK_CONTROL},
    {"Alt_L",       java_awt_event_KeyEvent_VK_ALT},
    {"Alt_R",       java_awt_event_KeyEvent_VK_ALT},
    {"Pause",       java_awt_event_KeyEvent_VK_PAUSE},
    {"Caps_Lock",   java_awt_event_KeyEvent_VK_CAPS_LOCK},
    {"Escape",      java_awt_event_KeyEvent_VK_ESCAPE},
    {"Prior",       java_awt_event_KeyEvent_VK_PAGE_UP},
    {"KP_Prior",    java_awt_event_KeyEvent_VK_PAGE_UP},
    {"Next",        java_awt_event_KeyEvent_VK_PAGE_DOWN},
    {"KP_Next",     java_awt_event_KeyEvent_VK_PAGE_DOWN},
    {"End",         java_awt_event_KeyEvent_VK_END},
    {"KP_End",      java_awt_event_KeyEvent_VK_END},
    {"Home",        java_awt_event_KeyEvent_VK_HOME},
    {"KP_Home",     java_awt_event_KeyEvent_VK_HOME},
    {"Left",        java_awt_event_KeyEvent_VK_LEFT},
    {"KP_Left",     java_awt_event_KeyEvent_VK_KP_LEFT},
    {"Up",          java_awt_event_KeyEvent_VK_UP},
    {"KP_Up",       java_awt_event_KeyEvent_VK_KP_UP},
    {"Right",       java_awt_event_KeyEvent_VK_RIGHT},
    {"KP_Right",    java_awt_event_KeyEvent_VK_KP_RIGHT},
    {"Down",        java_awt_event_KeyEvent_VK_DOWN},
    {"KP_Down",     java_awt_event_KeyEvent_VK_KP_DOWN},
    {"comma",       java_awt_event_KeyEvent_VK_COMMA},
    {"minus",       java_awt_event_KeyEvent_VK_MINUS},
    {"KP_Subtract", java_awt_event_KeyEvent_VK_SUBTRACT},
    {"period",      java_awt_event_KeyEvent_VK_PERIOD},
    {"KP_Decimal",  java_awt_event_KeyEvent_VK_DECIMAL},
    {"slash",       java_awt_event_KeyEvent_VK_SLASH},
    {"space",       java_awt_event_KeyEvent_VK_SPACE},
    {"0",           java_awt_event_KeyEvent_VK_0},
    {"KP_0",        java_awt_event_KeyEvent_VK_NUMPAD0},
    {"1",           java_awt_event_KeyEvent_VK_1},
    {"KP_1",        java_awt_event_KeyEvent_VK_NUMPAD1},
    {"2",           java_awt_event_KeyEvent_VK_2},
    {"KP_2",        java_awt_event_KeyEvent_VK_NUMPAD2},
    {"3",           java_awt_event_KeyEvent_VK_3},
    {"KP_3",        java_awt_event_KeyEvent_VK_NUMPAD3},
    {"4",           java_awt_event_KeyEvent_VK_4},
    {"KP_4",        java_awt_event_KeyEvent_VK_NUMPAD4},
    {"5",           java_awt_event_KeyEvent_VK_5},
    {"KP_5",        java_awt_event_KeyEvent_VK_NUMPAD5},
    {"6",           java_awt_event_KeyEvent_VK_6},
    {"KP_6",        java_awt_event_KeyEvent_VK_NUMPAD6},
    {"7",           java_awt_event_KeyEvent_VK_7},
    {"KP_7",        java_awt_event_KeyEvent_VK_NUMPAD7},
    {"8",           java_awt_event_KeyEvent_VK_8},
    {"KP_8",        java_awt_event_KeyEvent_VK_NUMPAD8},
    {"9",           java_awt_event_KeyEvent_VK_9},
    {"KP_9",        java_awt_event_KeyEvent_VK_NUMPAD9},
    {"semicolon",   java_awt_event_KeyEvent_VK_SEMICOLON},
    {"equal",       java_awt_event_KeyEvent_VK_EQUALS},
    {"a",           java_awt_event_KeyEvent_VK_A},
    {"A",           java_awt_event_KeyEvent_VK_A},
    {"b",           java_awt_event_KeyEvent_VK_B},
    {"B",           java_awt_event_KeyEvent_VK_B},
    {"c",           java_awt_event_KeyEvent_VK_C},
    {"C",           java_awt_event_KeyEvent_VK_C},
    {"d",           java_awt_event_KeyEvent_VK_D},
    {"D",           java_awt_event_KeyEvent_VK_D},
    {"e",           java_awt_event_KeyEvent_VK_E},
    {"E",           java_awt_event_KeyEvent_VK_E},
    {"f",           java_awt_event_KeyEvent_VK_F},
    {"F",           java_awt_event_KeyEvent_VK_F},
    {"g",           java_awt_event_KeyEvent_VK_G},
    {"G",           java_awt_event_KeyEvent_VK_G},
    {"h",           java_awt_event_KeyEvent_VK_H},
    {"H",           java_awt_event_KeyEvent_VK_H},
    {"i",           java_awt_event_KeyEvent_VK_I},
    {"I",           java_awt_event_KeyEvent_VK_I},
    {"j",           java_awt_event_KeyEvent_VK_J},
    {"J",           java_awt_event_KeyEvent_VK_J},
    {"k",           java_awt_event_KeyEvent_VK_K},
    {"K",           java_awt_event_KeyEvent_VK_K},
    {"l",           java_awt_event_KeyEvent_VK_L},
    {"L",           java_awt_event_KeyEvent_VK_L},
    {"m",           java_awt_event_KeyEvent_VK_M},
    {"M",           java_awt_event_KeyEvent_VK_M},
    {"n",           java_awt_event_KeyEvent_VK_N},
    {"N",           java_awt_event_KeyEvent_VK_N},
    {"o",           java_awt_event_KeyEvent_VK_O},
    {"O",           java_awt_event_KeyEvent_VK_O},
    {"p",           java_awt_event_KeyEvent_VK_P},
    {"P",           java_awt_event_KeyEvent_VK_P},
    {"q",           java_awt_event_KeyEvent_VK_Q},
    {"Q",           java_awt_event_KeyEvent_VK_Q},
    {"r",           java_awt_event_KeyEvent_VK_R},
    {"R",           java_awt_event_KeyEvent_VK_R},
    {"s",           java_awt_event_KeyEvent_VK_S},
    {"S",           java_awt_event_KeyEvent_VK_S},
    {"t",           java_awt_event_KeyEvent_VK_T},
    {"T",           java_awt_event_KeyEvent_VK_T},
    {"u",           java_awt_event_KeyEvent_VK_U},
    {"U",           java_awt_event_KeyEvent_VK_U},
    {"v",           java_awt_event_KeyEvent_VK_V},
    {"V",           java_awt_event_KeyEvent_VK_V},
    {"w",           java_awt_event_KeyEvent_VK_W},
    {"W",           java_awt_event_KeyEvent_VK_W},
    {"x",           java_awt_event_KeyEvent_VK_X},
    {"X",           java_awt_event_KeyEvent_VK_X},
    {"y",           java_awt_event_KeyEvent_VK_Y},
    {"Y",           java_awt_event_KeyEvent_VK_Y},
    {"z",           java_awt_event_KeyEvent_VK_Z},
    {"Z",           java_awt_event_KeyEvent_VK_Z},
    {"bracketleft", java_awt_event_KeyEvent_VK_OPEN_BRACKET},
    {"backslash",   java_awt_event_KeyEvent_VK_BACK_SLASH},
    {"bracketright",java_awt_event_KeyEvent_VK_CLOSE_BRACKET},
    {"asterisk",    java_awt_event_KeyEvent_VK_ASTERISK},
    {"KP_Multiply", java_awt_event_KeyEvent_VK_MULTIPLY},
    {"plus",        java_awt_event_KeyEvent_VK_ADD},
    {"KP_Add",      java_awt_event_KeyEvent_VK_ADD},
    {"KP_Divide",   java_awt_event_KeyEvent_VK_DIVIDE},
    {"Delete",      java_awt_event_KeyEvent_VK_DELETE},
    {"KP_Delete",   java_awt_event_KeyEvent_VK_DELETE},
    {"Num_Lock",    java_awt_event_KeyEvent_VK_NUM_LOCK},
    {"Scroll_Lock", java_awt_event_KeyEvent_VK_SCROLL_LOCK},
    {"F1",          java_awt_event_KeyEvent_VK_F1},
    {"F2",          java_awt_event_KeyEvent_VK_F2},
    {"F3",          java_awt_event_KeyEvent_VK_F3},
    {"F4",          java_awt_event_KeyEvent_VK_F4},
    {"F5",          java_awt_event_KeyEvent_VK_F5},
    {"F6",          java_awt_event_KeyEvent_VK_F6},
    {"F7",          java_awt_event_KeyEvent_VK_F7},
    {"F8",          java_awt_event_KeyEvent_VK_F8},
    {"F9",          java_awt_event_KeyEvent_VK_F9},
    {"F10",         java_awt_event_KeyEvent_VK_F10},
    {"F11",         java_awt_event_KeyEvent_VK_F11},
    {"F12",         java_awt_event_KeyEvent_VK_F12},
    {"F13",         java_awt_event_KeyEvent_VK_F13},
    {"F14",         java_awt_event_KeyEvent_VK_F14},
    {"F15",         java_awt_event_KeyEvent_VK_F15},
    {"F16",         java_awt_event_KeyEvent_VK_F16},
    {"F17",         java_awt_event_KeyEvent_VK_F17},
    {"F18",         java_awt_event_KeyEvent_VK_F18},
    {"F19",         java_awt_event_KeyEvent_VK_F19},
    {"F20",         java_awt_event_KeyEvent_VK_F20},
    {"F21",         java_awt_event_KeyEvent_VK_F21},
    {"F22",         java_awt_event_KeyEvent_VK_F22},
    {"F23",         java_awt_event_KeyEvent_VK_F23},
    {"F24",         java_awt_event_KeyEvent_VK_F24},
    {"Insert",      java_awt_event_KeyEvent_VK_INSERT},
    {"Help",        java_awt_event_KeyEvent_VK_HELP},
    {"grave",       java_awt_event_KeyEvent_VK_BACK_QUOTE},
    {"apostrophe",  java_awt_event_KeyEvent_VK_QUOTE},
    {"bar",         java_awt_event_KeyEvent_VK_DEAD_ACUTE},
    {"ampersand",   java_awt_event_KeyEvent_VK_AMPERSAND},
    {"quotedbl",    java_awt_event_KeyEvent_VK_QUOTEDBL},
    {"less",        java_awt_event_KeyEvent_VK_LESS},
    {"greater",     java_awt_event_KeyEvent_VK_GREATER},
    {"braceleft",   java_awt_event_KeyEvent_VK_BRACELEFT},
    {"braceright",  java_awt_event_KeyEvent_VK_BRACERIGHT},
    {"at",          java_awt_event_KeyEvent_VK_AT},
    {"colon",       java_awt_event_KeyEvent_VK_COLON},
    {"asciicircum", java_awt_event_KeyEvent_VK_CIRCUMFLEX},
    {"dollar",      java_awt_event_KeyEvent_VK_DOLLAR},
    {"exclam",      java_awt_event_KeyEvent_VK_EXCLAMATION_MARK},
    {"parenleft",   java_awt_event_KeyEvent_VK_LEFT_PARENTHESIS},
    {"numbersign",  java_awt_event_KeyEvent_VK_NUMBER_SIGN},
    {"parenright",  java_awt_event_KeyEvent_VK_RIGHT_PARENTHESIS},
    {"underscore",  java_awt_event_KeyEvent_VK_UNDERSCORE},
    {"percent",     java_awt_event_KeyEvent_VK_5},
    {"asciitilde",  java_awt_event_KeyEvent_VK_BACK_QUOTE},
    {"question",    java_awt_event_KeyEvent_VK_SLASH},
    {NULL, 0}
};


uint32_t find_keycode_by_name(const char* name) {
    struct name_keyCode* map = name_keyCode_map;
    for (; map->name != NULL; map ++) {
        if (strcmp(map->name, name) == 0) {
            return (uint32_t)map->code;
        }
    }
    return 0;
}


static void append_event(Event* event) {
  struct input* input = get_input();
  event->modifiers = input->modifiers;
  pthread_mutex_lock(&input->lock);
  wl_list_insert(&input->event_list, &event->link);
  pthread_mutex_unlock(&input->lock);
}

Event* next_event() {
  struct input* input = get_input();
  Event* event = NULL;
  pthread_mutex_lock(&input->lock);
  if (!wl_list_empty(&input->event_list)) {
    wl_list_for_each_reverse(event, &input->event_list, link) {
        wl_list_remove(&event->link);
        break;
    }
  }

  pthread_mutex_unlock(&input->lock);

  return event;
}

void new_mouse_event(enum MouseAction action, long id, int32_t x, int32_t y, uint32_t button,
    uint32_t click_count) {
  Event* event = (Event*)malloc(sizeof(Event));
  if (event != NULL) {
    event->kind = MOUSE_EVENT;
    event->id = id;
    event->e.m.action = action;
    event->e.m.button = button;
    event->e.m.x = x;
    event->e.m.y = y;
    event->e.m.click_count = click_count;
    append_event(event);
  }
}

void new_key_event(enum KeyAction action, long id, uint32_t keyCode, char keyChar) {
  Event* event = (Event*)malloc(sizeof(Event));
  if (event != NULL) {
    event->kind = KEY_EVENT;
    event->id = id;
    event->e.k.action = action;
    event->e.k.keyCode = keyCode;
    event->e.k.keyChar = keyChar;
    append_event(event);
  }
}

void new_display_flush_event(ShmSurface* surface) {
  Event* event = (Event*)malloc(sizeof(Event));
  if (event != NULL) {
    event->kind = DISPLAY_EVENT;
    event->e.d.surface = surface;
    clock_gettime(CLOCK_REALTIME, &event->e.d.timestamp);
    append_event(event);
  }
}

void new_surface_event(enum SurfaceAction action, ShmSurface* surface, int32_t width, int32_t height) {
  Event* event = (Event*)malloc(sizeof(Event));
  if (event != NULL) {
    event->kind = SURFACE_EVENT;
    event->e.s.action = action;
    event->e.s.surface = surface;
    event->e.s.width = width;
    event->e.s.height = height;
    append_event(event);
  }
}


long timestamp_diff(struct timespec* ts1, struct timespec* ts2) {
    return (ts1->tv_sec * 1000 + ts1->tv_nsec / 1000000) -
           (ts2->tv_sec * 1000 + ts2->tv_nsec / 1000000);
}


typedef void (*EventHandler)();


static void wayland_event_handler() {
    struct input* input = get_input();
    wl_display_dispatch(input->display);
}

static void repeat_timer_handler() {
    struct input* input = get_input();
    uint64_t  exp;

    read(input->repeat_timer_fd, &exp, sizeof(exp));
    uint32_t keycode = input->repeat_keycode;
    char    ch = input->repeat_char;


   // Nothing to repeat
    if (keycode == 0) {
        return;
    }

    ShmSurface* activeSurface = input->activeSurface;
    if (activeSurface == NULL) return;

    // Generate key repeat
    new_key_event(KEY_PRESS, activeSurface->id, keycode, ch);
    if (ch != '\0') {
        new_key_event(KEY_TYPE, activeSurface->id, keycode, ch);
    }
}

static void watch_fd(struct input* input, int fd, uint32_t events, EventHandler fn) {
    struct epoll_event ep;

    ep.events = events;
    ep.data.ptr = fn;
    epoll_ctl(input->epoll_fd, EPOLL_CTL_ADD, fd, &ep);
}

static void unwatch_fd(struct input* input, int fd) {
    epoll_ctl(input->epoll_fd, EPOLL_CTL_DEL, fd, NULL);
}

bool event_loop() {
    struct epoll_event ep[16];
    int count;
    EventHandler fn;

    struct input* input = get_input();

    count = epoll_wait(input->epoll_fd, ep, sizeof(ep)/sizeof(struct epoll_event), EVENT_WAIT_TIMEOUT);
    for (int index = 0; index < count; index ++) {
        fn = ep[index].data.ptr;
        fn();
    }

    return true;
}


bool init_event(struct input* input) {
    input->epoll_fd = epoll_create(1);
    if (input->epoll_fd == -1) {
        return false;
    }

    // Register Wayland event handler
    watch_fd(input, wl_display_get_fd(input->display), EPOLLIN, wayland_event_handler);
    // Register keyboard repeat timer
    watch_fd(input, input->repeat_timer_fd, EPOLLIN, repeat_timer_handler);

    return true;
}

void cleanup_event(struct input* input) {
    unwatch_fd(input, input->repeat_timer_fd);
    unwatch_fd(input, wl_display_get_fd(input->display));

    close(input->epoll_fd);
}


void drain_events() {
    Event* event;
    while((event = next_event()) != NULL) {
        free(event);
    }
}

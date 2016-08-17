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

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <sys/mman.h>
#include <sys/types.h>
#include <unistd.h>

#include "wayland.h"

static struct display _display;
static struct input   _input;
static bool           _input_inited;



static void display_handle_geometry(void *data,
			struct wl_output *wl_output,
			int32_t x,
			int32_t y,
			int32_t physical_width,
			int32_t physical_height,
			int32_t subpixel,
			const char *make,
			const char *model,
      int32_t transform) {
}

static void display_handle_mode(void *data,
		    struct wl_output *wl_output,
		    uint32_t flags,
		    int32_t width,
		    int32_t height,
		    int32_t refresh) {

  _display.width = width;
  _display.height = height;
}

static const struct wl_output_listener output_listener = {
	display_handle_geometry,
	display_handle_mode
};


static void registry_handler(void *data, struct wl_registry *registry, uint32_t id,
	       const char *interface, uint32_t version) {
    if (strcmp(interface, wl_compositor_interface.name) == 0) {
      _display.compositor = wl_registry_bind(registry,
				      id, &wl_compositor_interface, WL_VERSION);
    } else if (strcmp(interface, wl_shell_interface.name) == 0) {
      _display.shell = wl_registry_bind(registry, id,
                       &wl_shell_interface, WL_VERSION);
    } else if (strcmp(interface, wl_output_interface.name) == 0) {
      _display.output = wl_registry_bind(registry, id, &wl_output_interface, WL_VERSION);
      wl_output_add_listener(_display.output, &output_listener, NULL);
    } else if (strcmp(interface, wl_shm_interface.name) == 0) {
      _display.shm = wl_registry_bind(registry, id, &wl_shm_interface, WL_VERSION);
    } else if (strcmp(interface, wl_subcompositor_interface.name) == 0) {
      _display.subcompositor = wl_registry_bind(registry, id, &wl_subcompositor_interface, WL_VERSION);
    } else if (strcmp(interface, wl_seat_interface.name) == 0) {
      _input.seat = wl_registry_bind(registry, id, &wl_seat_interface, 2);
      _input.display = _display.display;

      if (!init_input(&_input)) {
        fprintf(stderr, "Failed to initialize input\n");
        abort();
      } else {
        _input_inited = true;
      }
    }
}

static void registry_remover(void *data, struct wl_registry *registry, uint32_t id) {
}


static const struct wl_registry_listener registry_listener = {
    registry_handler,
    registry_remover
};


bool wayland_init() {
  _display.display = wl_display_connect(NULL);

  if (_display.display == NULL) {
    return false;
  }

  _display.registry = wl_display_get_registry(_display.display);
  if (_display.registry == NULL) {
    return false;
  }

  wl_registry_add_listener(_display.registry, &registry_listener, NULL);

  wl_display_dispatch(_display.display);
  wl_display_roundtrip(_display.display);


  return true;
}

bool is_input_inited() {
    return _input_inited;
}


void wayland_cleanup() {
 if (_display.compositor != NULL)    wl_compositor_destroy(_display.compositor);
 if (_display.subcompositor != NULL) wl_subcompositor_destroy(_display.subcompositor);
 if (_display.shell != NULL)         wl_shell_destroy(_display.shell);
 if (_display.shm != NULL)           wl_shm_destroy(_display.shm);

 if (_display.output != NULL)        wl_output_destroy(_display.output);

 destroy_input(&_input);

 if (_display.display != NULL)       wl_display_disconnect(_display.display);
}


int32_t get_display_height() {
  return _display.height;
}

int32_t get_display_width() {
  return _display.width;
}


struct display* get_display() { return &_display; }

struct input* get_input() {
    return &_input;
}



void display_flush() {
  wl_display_dispatch_pending(_display.display);
  wl_display_flush(_display.display);
}

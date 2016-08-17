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

#ifndef __CACIO_WAYLAND_SHM_SURFACE_H
#define __CACIO_WAYLAND_SHM_SURFACE_H

#include "wayland.h"
#include <time.h>

struct shm_surface {
    struct wl_surface*          surface;
    struct wl_shell_surface*    shell_surface;
    struct wl_buffer*           buffer;
    struct wl_shm_pool*         pool;

    void*                       content;

    int64_t                     id;
    int32_t                     x;
    int32_t                     y;
    int32_t                     width;
    int32_t                     height;
    int32_t                     pixel_depth;
    uint32_t                    format;

    // Time to track last update
    struct timespec             last_update;
};

typedef struct shm_surface ShmSurface;


ShmSurface* CreateShmScreenSurface(int64_t id, int32_t x, int32_t y, int32_t width, int32_t height,
  int32_t pixel_depth);
void DestroyShmScreenSurface(ShmSurface* surf);

void UnmapShmScreenSurface(ShmSurface* surf);
bool RemapShmScreenSurface(ShmSurface* surface, int32_t width, int32_t height);

#endif // __CACIO_WAYLAND_SHM_SURFACE_H

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

#include "net_java_openjdk_cacio_wayland_WaylandSurface.h"
#include "wayland_shm_surface.h"
#include "wayland_events.h"

/*
 * Class:     net_java_openjdk_cacio_wayland_WaylandSurface
 * Method:    createShmScreenSurface
 * Signature: (JIIIII)J
 */
JNIEXPORT jlong JNICALL Java_net_java_openjdk_cacio_wayland_WaylandSurface_createShmScreenSurface
  (JNIEnv *env, jclass clz, jlong id, jint x, jint y, jint w, jint h, jint depth) {
    return (jlong)CreateShmScreenSurface(id, x, y, w, h, depth);
}


/*
 * Class:     net_java_openjdk_cacio_wayland_WaylandSurface
 * Method:    dispose
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_net_java_openjdk_cacio_wayland_WaylandSurface_dispose
  (JNIEnv *env, jclass clz, jlong surface) {
    new_surface_event(SURFACE_DISPOSE, (ShmSurface*)surface, 0, 0);
}


/*
 * Class:     net_java_openjdk_cacio_wayland_WaylandSurface
 * Method:    unmap
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_net_java_openjdk_cacio_wayland_WaylandSurface_unmap
  (JNIEnv *env, jclass clz, jlong surface) {
    new_surface_event(SURFACE_UNMAP, (ShmSurface*)surface, 0, 0);
}

/*
 * Class:     net_java_openjdk_cacio_wayland_WaylandSurface
 * Method:    remap
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_net_java_openjdk_cacio_wayland_WaylandSurface_remap
  (JNIEnv *env, jclass clz, jlong surface) {
    new_surface_event(SURFACE_MAP, (ShmSurface*)surface, 0, 0);
}

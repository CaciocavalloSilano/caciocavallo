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

#include "net_java_openjdk_cacio_wayland_WaylandShmSurfaceData.h"
#include "SurfaceData.h"
#include "wayland_shm_surface.h"
#include "wayland_events.h"

jclass    _sunToolkitClass;
jmethodID _sunToolkitLockMethodID;
jmethodID _sunToolkitUnlockMethodID;

static jint WLLock(JNIEnv* env, SurfaceDataOps* ops,
                    SurfaceDataRasInfo* rasInfo, jint lockFlags);
static void WLGetRasInfo(JNIEnv* env, SurfaceDataOps* ops,
                          SurfaceDataRasInfo* rasInfo);
static void WLRelease(JNIEnv* env, SurfaceDataOps* ops,
                       SurfaceDataRasInfo* rasInfo);
static void WLUnlock(JNIEnv* env, SurfaceDataOps* ops,
                      SurfaceDataRasInfo* rasInfo);

static void WLDispose(JNIEnv *env, SurfaceDataOps *ops);

/*
 * Class:     net_java_openjdk_cacio_wayland_WaylandSurfaceData
 * Method:    initIds
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_net_java_openjdk_cacio_wayland_WaylandShmSurfaceData_initIds
  (JNIEnv *env, jclass cls) {
    _sunToolkitClass = (*env)->FindClass(env, "sun/awt/SunToolkit");
    _sunToolkitLockMethodID = (*env)->GetStaticMethodID(env, _sunToolkitClass,
                                                  "awtLock", "()V");
    _sunToolkitUnlockMethodID = (*env)->GetStaticMethodID(env, _sunToolkitClass,
                                                  "awtUnlock", "()V");
}


struct WaylandShmSurfaceDataOps {
  SurfaceDataOps sdOps;
  ShmSurface*    surface;
};

/*
 * Class:     net_java_openjdk_cacio_wayland_WaylandShmSurfaceData
 * Method:    initOps
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_net_java_openjdk_cacio_wayland_WaylandShmSurfaceData_initOps
  (JNIEnv *env, jobject obj, jlong surf_ptr) {
  struct WaylandShmSurfaceDataOps* ops = (struct WaylandShmSurfaceDataOps*)
           SurfaceData_InitOps(env, obj, sizeof(struct WaylandShmSurfaceDataOps));
  ShmSurface* surface = (ShmSurface*)surf_ptr;

  ops->sdOps.Lock = &WLLock;
  ops->sdOps.GetRasInfo = &WLGetRasInfo;
  ops->sdOps.Release = &WLRelease;
  ops->sdOps.Unlock = &WLUnlock;
  ops->sdOps.Dispose = &WLDispose;
  ops->surface = surface;
}


static jint WLLock(JNIEnv* env, SurfaceDataOps* ops, SurfaceDataRasInfo* rasInfo, jint lockFlags) {
  struct WaylandShmSurfaceDataOps *wlOps = (struct WaylandShmSurfaceDataOps*) ops;

  (*env)->CallStaticVoidMethod(env, _sunToolkitClass, _sunToolkitLockMethodID);
  ShmSurface* surf = wlOps->surface;

  rasInfo->bounds.x1 = MAX(rasInfo->bounds.x1, 0);
  rasInfo->bounds.x1 = MIN(rasInfo->bounds.x1, surf->width);

  rasInfo->bounds.y1 = MAX(rasInfo->bounds.y1, 0);
  rasInfo->bounds.y1 = MIN(rasInfo->bounds.y1, surf->height);


  rasInfo->bounds.x2 = MIN(rasInfo->bounds.x2, surf->width);
  rasInfo->bounds.y2 = MIN(rasInfo->bounds.y2, surf->height);

  rasInfo->bounds.x1 -= surf->x;
  rasInfo->bounds.x2 -= surf->x;
  rasInfo->bounds.y1 -= surf->y;
  rasInfo->bounds.y2 -= surf->y;


  return SD_SUCCESS;
}

static void WLGetRasInfo(JNIEnv* env __attribute__((unused)), SurfaceDataOps* ops, SurfaceDataRasInfo* rasInfo) {
  struct WaylandShmSurfaceDataOps *wlOps = (struct WaylandShmSurfaceDataOps*) ops;
  ShmSurface* surf = wlOps->surface;

  rasInfo->scanStride = surf->pixel_depth * surf->width;
  rasInfo->rasBase = surf->content;
  rasInfo->pixelStride = surf->pixel_depth;
  rasInfo->pixelBitOffset = 0;
}

static void WLRelease(JNIEnv* env __attribute__((unused)), SurfaceDataOps* ops __attribute__((unused)), SurfaceDataRasInfo* rasInfo __attribute__((unused))) {
  /* nothing to do */
}


static void WLUnlock(JNIEnv* env, SurfaceDataOps* ops, SurfaceDataRasInfo* rasInfo) {
  struct timespec timestamp;

  struct WaylandShmSurfaceDataOps *wlOps = (struct WaylandShmSurfaceDataOps*) ops;
  ShmSurface* surf = wlOps->surface;

  wl_surface_damage(wlOps->surface->surface, rasInfo->bounds.x1, rasInfo->bounds.y1,
    rasInfo->bounds.x2 - rasInfo->bounds.x1, rasInfo->bounds.y2 - rasInfo->bounds.y1);

  // Coalesce display update events, high volumes of update events can stall server,
  // and crash the client
  clock_gettime(CLOCK_REALTIME, &timestamp);
  if (timestamp_diff(&timestamp, &surf->last_update) >= DISPLAY_UPDATE_INTERVAL) {
    wl_surface_attach(surf->surface, surf->buffer, 0, 0);
    wl_surface_commit(surf->surface);
    display_flush();
    clock_gettime(CLOCK_REALTIME, &surf->last_update);
  } else {
      new_display_flush_event(surf);
  }

  (*env)->CallStaticVoidMethod(env, _sunToolkitClass, _sunToolkitUnlockMethodID);
}

static void WLDispose(JNIEnv *env, SurfaceDataOps *ops) {
}

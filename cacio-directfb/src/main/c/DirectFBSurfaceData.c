/*
 * Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
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

#include <directfb.h>

#include "net_java_openjdk_cacio_directfb_DirectFBSurfaceData.h"
#include "SurfaceData.h"

static jclass sunToolkitCls;
static jmethodID sunToolkitLockMID;
static jmethodID sunToolkitUnlockMID;

typedef struct {

  SurfaceDataOps sdOps;
  IDirectFBSurface *surface;
  jint x, y, width, height;
  void* buffer;
  int linePitch;
} DFBSurfaceDataOps;

static jint DFBLock(JNIEnv* env, SurfaceDataOps* ops,
                    SurfaceDataRasInfo* rasInfo, jint lockFlags);
static void DFBGetRasInfo(JNIEnv* env, SurfaceDataOps* ops,
                          SurfaceDataRasInfo* rasInfo);
static void DFBRelease(JNIEnv* env, SurfaceDataOps* ops,
                       SurfaceDataRasInfo* rasInfo);
static void DFBUnlock(JNIEnv* env, SurfaceDataOps* ops,
                      SurfaceDataRasInfo* rasInfo);

JNIEXPORT void JNICALL Java_net_java_openjdk_cacio_directfb_DirectFBSurfaceData_initIds(JNIEnv* env, jclass cls __attribute__((unused))) {
    sunToolkitCls = (*env)->FindClass(env, "sun/awt/SunToolkit");
    if ((*env)->ExceptionCheck(env)) return;

    sunToolkitLockMID = (*env)->GetStaticMethodID(env, sunToolkitCls,
                                                  "awtLock", "()V");
    if ((*env)->ExceptionCheck(env)) return;

    sunToolkitUnlockMID = (*env)->GetStaticMethodID(env, sunToolkitCls,
                                                    "awtUnlock", "()V");
    if ((*env)->ExceptionCheck(env)) return;
}

JNIEXPORT void JNICALL Java_net_java_openjdk_cacio_directfb_DirectFBSurfaceData_initOps(JNIEnv* env, jobject thiz, jlong dfbSurface, jint x, jint y, jint w, jint h) {

  IDirectFBSurface* surface = (IDirectFBSurface*) dfbSurface;

  DFBSurfaceDataOps* ops = (DFBSurfaceDataOps*) SurfaceData_InitOps(env, thiz, sizeof(DFBSurfaceDataOps));
  ops->sdOps.Lock = &DFBLock;
  ops->sdOps.GetRasInfo = &DFBGetRasInfo;
  ops->sdOps.Release = &DFBRelease;
  ops->sdOps.Unlock = &DFBUnlock;
  ops->surface = surface;
  ops->x = x;
  ops->y = y;
  ops->width = w;
  ops->height = h;
}

static jint DFBLock(JNIEnv* env, SurfaceDataOps* ops, SurfaceDataRasInfo* rasInfo, jint lockFlags) {

  DFBSurfaceDataOps *dfbOps = (DFBSurfaceDataOps*) ops;

  (*env)->CallStaticVoidMethod(env, sunToolkitCls, sunToolkitLockMID);

  if (rasInfo->bounds.x1 < 0) {
    rasInfo->bounds.x1 = 0;
  }

  if (rasInfo->bounds.x1 > dfbOps->width) {
    rasInfo->bounds.x1 = dfbOps->width;
  }

  if (rasInfo->bounds.y1 < 0) {
    rasInfo->bounds.y1 = 0;
  }

  if (rasInfo->bounds.y1 > dfbOps->height) {
    rasInfo->bounds.y1 = dfbOps->height;
  }

  if (rasInfo->bounds.x2 > dfbOps->width) {
    rasInfo->bounds.x2 = dfbOps->width;
  }
    
  if (rasInfo->bounds.y2 > dfbOps->height) {
    rasInfo->bounds.y2 = dfbOps->height;
  }

  DFBSurfaceLockFlags flags = DSLF_WRITE;
  void* buffer;
  int pitch;
  dfbOps->surface->Lock(dfbOps->surface, flags, &buffer, &pitch);

  dfbOps->buffer = buffer;
  dfbOps->linePitch = pitch;
  return SD_SUCCESS;
}

static void DFBGetRasInfo(JNIEnv* env __attribute__((unused)), SurfaceDataOps* ops, SurfaceDataRasInfo* rasInfo) {
  DFBSurfaceDataOps *dfbOps = (DFBSurfaceDataOps*) ops;
  rasInfo->rasBase = dfbOps->buffer;
  rasInfo->pixelStride = 4; // ??
  rasInfo->pixelBitOffset = 0;
  rasInfo->scanStride = dfbOps->linePitch;
}

static void DFBRelease(JNIEnv* env __attribute__((unused)), SurfaceDataOps* ops __attribute__((unused)), SurfaceDataRasInfo* rasInfo __attribute__((unused))) {
  /* nothing to do */
}

static void DFBUnlock(JNIEnv* env, SurfaceDataOps* ops, SurfaceDataRasInfo* rasInfo) {

  DFBSurfaceDataOps *dfbOps = (DFBSurfaceDataOps*) ops;
  dfbOps->surface->Unlock(dfbOps->surface);
  const DFBRegion region = { dfbOps->x, dfbOps->y, dfbOps->x + dfbOps->width, dfbOps->y + dfbOps->height };
  dfbOps->surface->Flip(dfbOps->surface, &region, DSFLIP_WAIT);
  dfbOps->buffer = NULL;
  dfbOps->linePitch = 0;

  (*env)->CallStaticVoidMethod(env, sunToolkitCls, sunToolkitUnlockMID);
}

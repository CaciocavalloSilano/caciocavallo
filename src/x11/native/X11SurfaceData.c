/*
 * Copyright 2009 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
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
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

#include <X11/Xlib.h> 
#include "sun_awt_peer_x11_X11SurfaceData.h"

#include "SurfaceData.h"

static jclass sunToolkitCls;
static jmethodID sunToolkitLockMID;
static jmethodID sunToolkitUnlockMID;

typedef struct {

  SurfaceDataOps sdOps;
  Display *display;
  Drawable drawable;
  XImage *img;
  jint x, y, width, height;
} X11SurfaceDataOps;

/*
 * Class:     sun_awt_peer_x11_X11SurfaceData
 * Method:    initIDs
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_sun_awt_peer_x11_X11SurfaceData_initIDs
  (JNIEnv *env, jclass cls)
{
    sunToolkitCls = (*env)->FindClass(env, "sun/awt/SunToolkit");
    if ((*env)->ExceptionCheck(env)) return;
    /*
    sunToolkitCls = (*env)->NewGlobalRef(env, sunToolkitCls);
    if ((*env)->ExceptionCheck(env)) return;
     **/
    sunToolkitLockMID = (*env)->GetStaticMethodID(env, sunToolkitCls,
                                                  "awtLock", "()V");
    if ((*env)->ExceptionCheck(env)) return;
    sunToolkitUnlockMID = (*env)->GetStaticMethodID(env, sunToolkitCls,
                                                    "awtUnlock", "()V");
    if ((*env)->ExceptionCheck(env)) return;
}

static jint X11Lock(JNIEnv* env, SurfaceDataOps* ops, SurfaceDataRasInfo* rasInfo, jint lockFlags) {
  (*env)->CallStaticVoidMethod(env, sunToolkitCls, sunToolkitLockMID);
  return SD_SUCCESS;
}

static void X11GetRasInfo(JNIEnv* env, SurfaceDataOps* ops, SurfaceDataRasInfo* rasInfo) {

  int x, y, w, h;
  X11SurfaceDataOps *xops;
  Display *display;
  Drawable drawable;

  xops = (X11SurfaceDataOps*) ops;
  display = xops->display;
  drawable = xops->drawable;

  x = rasInfo->bounds.x1;
  y = rasInfo->bounds.y1;
  w = rasInfo->bounds.x2 - x;
  h = rasInfo->bounds.y2 - y;

  xops->img = XGetImage(display, drawable,
                        x, y, w, h,
                        -1, ZPixmap);

  if (xops->img) {
    int scan = xops->img->bytes_per_line;
    int mult = 4; /* TODO. */
    xops->x = x;
    xops->y = y;
    xops->width = w;
    xops->height = h;
    rasInfo->rasBase = xops->img->data - x * mult - y * scan;
    rasInfo->pixelStride = mult;
    rasInfo->pixelBitOffset = 0;
    rasInfo->scanStride = scan;
  } else {
    rasInfo->rasBase = NULL;
    rasInfo->pixelStride = 0;
    rasInfo->pixelBitOffset = 0;
    rasInfo->scanStride = 0;
  }
}

static void X11Release(JNIEnv* env, SurfaceDataOps* ops, SurfaceDataRasInfo* rasInfo) {
  
  X11SurfaceDataOps *xops;
  Display *display;
  Drawable drawable;
  GC gc;

  xops = (X11SurfaceDataOps*) ops;
  display = xops->display;
  drawable = xops->drawable;

  gc = XCreateGC(display, drawable, 0, NULL);
  XPutImage(display, drawable, gc, xops->img, 0, 0, xops->x, xops->y, xops->width, xops->height);
  XFreeGC(display, gc);

}

static void X11Unlock(JNIEnv* env, SurfaceDataOps* ops, SurfaceDataRasInfo* rasInfo) {
  (*env)->CallStaticVoidMethod(env, sunToolkitCls, sunToolkitUnlockMID);
}

/*
 * Class:     sun_awt_peer_x11_X11SurfaceData
 * Method:    initOps
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_sun_awt_peer_x11_X11SurfaceData_initOps
  (JNIEnv *env, jobject thiz, jlong d, jlong w)
{
  Display *display;
  Drawable drawable;
  X11SurfaceDataOps *xops;

  display = (Display*) d;
  drawable = (Drawable) w;
  xops = SurfaceData_InitOps(env, thiz, sizeof(X11SurfaceDataOps));

  xops->sdOps.Lock = &X11Lock;
  xops->sdOps.GetRasInfo = &X11GetRasInfo;
  xops->sdOps.Release = &X11Release;
  xops->sdOps.Unlock = &X11Unlock;
  xops->display = display;
  xops->drawable = drawable;
}


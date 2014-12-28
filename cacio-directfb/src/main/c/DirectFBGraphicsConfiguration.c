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

#include "net_java_openjdk_cacio_directfb_DirectFBGraphicsConfiguration.h"

static jfieldID width_field_id;
static jfieldID height_field_id;

jclass tkClass = NULL;
jmethodID awtLockMID = NULL;
jmethodID awtUnlockMID = NULL;

JNIEXPORT void JNICALL Java_net_java_openjdk_cacio_directfb_DirectFBGraphicsConfiguration_initIDs(JNIEnv* env, jclass clazz) {
  jclass rectCls = (*env)->FindClass(env, "net/java/openjdk/cacio/directfb/DirectFBRect");
  width_field_id = (*env)->GetFieldID(env, rectCls, "width", "I");
  height_field_id = (*env)->GetFieldID(env, rectCls, "height", "I");
  tkClass = (*env)->FindClass(env, "sun/awt/SunToolkit");
  awtLockMID = (*env)->GetStaticMethodID(env, tkClass,
                                                  "awtLock", "()V");
  awtUnlockMID = (*env)->GetStaticMethodID(env, tkClass,
                                                "awtUnlock", "()V");
}

JNIEXPORT jlong JNICALL Java_net_java_openjdk_cacio_directfb_DirectFBGraphicsConfiguration_getDirectFBSurface(JNIEnv* env, jobject thiz, jlong dfb) {

  IDirectFB* directFB = (IDirectFB*) dfb;
  DFBSurfaceDescription dsc;

  dsc.flags = DSDESC_CAPS;
  dsc.caps  = DSCAPS_PRIMARY;

  IDirectFBSurface* primary;
  directFB->CreateSurface(directFB, &dsc, &primary);
  DFBSurfacePixelFormat pixelFormat;
  primary->GetPixelFormat(primary, &pixelFormat);
  switch (pixelFormat) {
  case DSPF_RGB24:
    printf("RGB24\n");
    break;
  case DSPF_RGB32:
    printf("RGB32\n");
    break;
  default:
    printf("OTHER\n");
  }
  return (jlong) primary;
}

JNIEXPORT void JNICALL Java_net_java_openjdk_cacio_directfb_DirectFBGraphicsConfiguration_getSurfaceBounds(JNIEnv* env, jobject thiz, jlong srfc, jobject rect) {
  IDirectFBSurface* surface = (IDirectFBSurface*) srfc;
  int w, h;
  surface->GetSize(surface, &w, &h);
  (*env)->SetIntField(env, rect, width_field_id, w);
  (*env)->SetIntField(env, rect, height_field_id, h);
}

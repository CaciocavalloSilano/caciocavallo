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
#include <jni.h>

#include "cacio-web.h"

extern void JNU_ThrowByName(JNIEnv *env, const char *name, const char *msg);

jclass sunToolkitCls;
jmethodID sunToolkitLockMID;
jmethodID sunToolkitUnlockMID;

jclass webSurfaceCls;
jmethodID dirtyRectMID;

static jint WebLock(JNIEnv* env, SurfaceDataOps* ops,
                    SurfaceDataRasInfo* rasInfo, jint lockFlags);
static void WebGetRasInfo(JNIEnv* env, SurfaceDataOps* ops,
                          SurfaceDataRasInfo* rasInfo);
static void WebRelease(JNIEnv* env, SurfaceDataOps* ops,
                       SurfaceDataRasInfo* rasInfo);
static void WebUnlock(JNIEnv* env, SurfaceDataOps* ops,
                      SurfaceDataRasInfo* rasInfo);

JNIEXPORT void JNICALL Java_net_java_openjdk_awt_peer_web_WebSurfaceData_initIDs
  (JNIEnv *env, jclass cls __attribute__((unused)))
{
    sunToolkitCls = (*env)->FindClass(env, "sun/awt/SunToolkit");
    if ((*env)->ExceptionCheck(env)) return;

    sunToolkitLockMID = (*env)->GetStaticMethodID(env, sunToolkitCls,
                                                  "awtLock", "()V");
    if ((*env)->ExceptionCheck(env)) return;

    sunToolkitUnlockMID = (*env)->GetStaticMethodID(env, sunToolkitCls,
                                                    "awtUnlock", "()V");
    if ((*env)->ExceptionCheck(env)) return;
    
    //TODO: Mit globaler Referenz arbeiten, diese in Dispose wieder freigeben.
    webSurfaceCls = (*env)->FindClass(env, "net/java/openjdk/awt/peer/web/WebSurfaceData"); //Classen nicht cachen!
    dirtyRectMID = (*env)->GetMethodID(env, webSurfaceCls,
                                                    "addDirtyRect", "(IIII)V");
}

JNIEXPORT void JNICALL Java_net_java_openjdk_awt_peer_web_WebSurfaceData_initOps
  (JNIEnv *env, jobject thiz, jintArray imgBuffer, jint width, jint height, jint stride)
{
    WebSurfaceDataOps *operations = NULL;

    operations = (WebSurfaceDataOps *)
        SurfaceData_InitOps(env, thiz, sizeof(WebSurfaceDataOps));

    operations->sdOps.Lock = &WebLock;
    operations->sdOps.GetRasInfo = &WebGetRasInfo;
    operations->sdOps.Release = &WebRelease;
    operations->sdOps.Unlock = &WebUnlock;
    operations->imgBuffer = (*env)->NewWeakGlobalRef(env, imgBuffer);
    operations->width = width;
    operations->height = height;
    operations->stride = width*4; //TODO stride und offset setzen!!
}

static void WebRelease(JNIEnv *env, SurfaceDataOps *ops, SurfaceDataRasInfo *rasInfo) {	
    WebSurfaceDataOps *operations = (WebSurfaceDataOps*) ops;
    
    if(operations->imgBuffer && !(*env)->IsSameObject(env, operations->imgBuffer, NULL)) {
     (*env)->ReleasePrimitiveArrayCritical(env, operations->imgBuffer, rasInfo->rasBase, JNI_ABORT);
    }
}

static jint WebLock(JNIEnv* env, SurfaceDataOps* ops,
                    SurfaceDataRasInfo* rasInfo, jint lockFlags)
{	
    WebSurfaceDataOps *operations = NULL;
    int ret = -1;

    operations = (WebSurfaceDataOps*) ops;

    operations->lockFlags = lockFlags;
    (*env)->CallStaticVoidMethod(env, sunToolkitCls, sunToolkitLockMID);

    if (rasInfo->bounds.x1 < 0) {
      rasInfo->bounds.x1 = 0;
    }

    if (rasInfo->bounds.x1 > operations->width) {
        rasInfo->bounds.x1 = operations->width;
    }

    if (rasInfo->bounds.y1 < 0) {
      rasInfo->bounds.y1 = 0;
    }

    if (rasInfo->bounds.y1 > operations->height) {
        rasInfo->bounds.y1 = operations->height;
    }

    if (rasInfo->bounds.x2 > operations->width) {
      rasInfo->bounds.x2 = operations->width;
    }
    
    if (rasInfo->bounds.y2 > operations->height) {
      rasInfo->bounds.y2 = operations->height;
    }
   
    return SD_SUCCESS;
}

static void WebGetRasInfo(JNIEnv* env __attribute__((unused)),
                          SurfaceDataOps* opsPtr,
                          SurfaceDataRasInfo* rasInfo)
{
    WebSurfaceDataOps *ops = NULL;
    ops = (WebSurfaceDataOps*) opsPtr;
    jint *imgPtr = NULL;
	
    if(ops->imgBuffer &&  !(*env)->IsSameObject(env, ops->imgBuffer, NULL)) {
	imgPtr = (jint *) (*env)->GetPrimitiveArrayCritical(env, ops->imgBuffer, NULL);
    }
    
    if (imgPtr != NULL || 1) {
        rasInfo->rasBase = imgPtr;
        rasInfo->pixelStride = 4;
        rasInfo->pixelBitOffset = 0;
        rasInfo->scanStride = ops->stride;
    } else {
        rasInfo->rasBase = NULL;
        rasInfo->pixelStride = 0;
        rasInfo->pixelBitOffset = 0;
        rasInfo->scanStride = 0;
    }
}

static void WebUnlock(JNIEnv* env, SurfaceDataOps* ops, SurfaceDataRasInfo* rasInfo)
{
    WebSurfaceDataOps *operations = NULL;
    int width = 0;
    int height = 0;

    operations = (WebSurfaceDataOps*) ops;

    width = rasInfo->bounds.x2 - rasInfo->bounds.x1;
    height = rasInfo->bounds.y2 - rasInfo->bounds.y1;

    /*
     * FIXME: there is some problem, looks like Java passes us the wrong value
     * of y1 sometimes, causing in an invalid area.
     * This only seems to occurs with the Web backend, so it's probably
     * caused by some mistakes elsewhere, and needs further investigation.
     */
    if (width < 0) {
        width = 0;
    }

    if (height < 0) {
        height = 0;
    }
   
   if(operations->lockFlags > SD_LOCK_READ && !(*env)->IsSameObject(env, ops->sdObject, NULL)) {
    //Dirty Rect tracking
    (*env)->CallVoidMethod(env, ops->sdObject, dirtyRectMID, rasInfo->bounds.x1, rasInfo->bounds.x1 + width, rasInfo->bounds.y1, rasInfo->bounds.y1 + height);
   }
   
    //Release AWT Lock
    (*env)->CallStaticVoidMethod(env, sunToolkitCls, sunToolkitUnlockMID);
}

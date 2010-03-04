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
#include <SDL/SDL_video.h>

#include "SDL.h"
#include "cacio-sdl.h"
#include "net_java_openjdk_awt_peer_sdl_SDLSurfaceData.h"

extern void JNU_ThrowByName(JNIEnv *env, const char *name, const char *msg);

static jclass sunToolkitCls;
static jmethodID sunToolkitLockMID;
static jmethodID sunToolkitUnlockMID;

static jint SDLLock(JNIEnv* env, SurfaceDataOps* ops,
                    SurfaceDataRasInfo* rasInfo, jint lockFlags);
static void SDLGetRasInfo(JNIEnv* env, SurfaceDataOps* ops,
                          SurfaceDataRasInfo* rasInfo);
static void SDLRelease(JNIEnv* env, SurfaceDataOps* ops,
                       SurfaceDataRasInfo* rasInfo);
static void SDLUnlock(JNIEnv* env, SurfaceDataOps* ops,
                      SurfaceDataRasInfo* rasInfo);

JNIEXPORT void JNICALL Java_net_java_openjdk_awt_peer_sdl_SDLSurfaceData_initIDs
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
}

JNIEXPORT void JNICALL Java_net_java_openjdk_awt_peer_sdl_SDLSurfaceData_initOps
  (JNIEnv *env, jobject thiz, jlong nativeSDLSurface, jint width, jint height)
{
    SDL_Surface *surface = NULL;
    SDLSurfaceDataOps *operations = NULL;

    surface = (SDL_Surface *) nativeSDLSurface;

    operations = (SDLSurfaceDataOps *)
        SurfaceData_InitOps(env, thiz, sizeof(SDLSurfaceDataOps));

    operations->sdOps.Lock = &SDLLock;
    operations->sdOps.GetRasInfo = &SDLGetRasInfo;
    operations->sdOps.Release = &SDLRelease;
    operations->sdOps.Unlock = &SDLUnlock;
    operations->surface = surface;
    operations->width = width;
    operations->height = height;
}

static jint SDLLock(JNIEnv* env, SurfaceDataOps* ops,
                    SurfaceDataRasInfo* rasInfo, jint lockFlags)
{
    SDLSurfaceDataOps *operations = NULL;
    int ret = -1;

    operations = (SDLSurfaceDataOps*) ops;

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

/*
    if (rasInfo->bounds.y1 >= 1600) {
        (*env)->CallStaticVoidMethod(env, sunToolkitCls, sunToolkitUnlockMID);
        JNU_ThrowByName(env, "java/lang/InternalError",
                "SDLScreen::SDLLock: fluff!!!!!!!!");
        return ret;
    }
*/
/*
    fprintf(stderr,
     "***** width = %d, height = %d, x1 = %d, x2 = %d, y1 = %d, y2 = %d\n",
            operations->width, operations->height,
            rasInfo->bounds.x1, rasInfo->bounds.x2,
            rasInfo->bounds.y1, rasInfo->bounds.y2);
*/
    if (lockFlags & SD_LOCK_FASTEST &&
        (SDL_MUSTLOCK(operations->surface) != 0)) {
        
        ret = SD_SLOWLOCK;
    } else {
        ret = SD_SUCCESS;
    }

    if (SDL_MUSTLOCK(operations->surface) != 0) {

        if (SDL_LockSurface(operations->surface) < 0) {
            (*env)->CallStaticVoidMethod(env, sunToolkitCls,
                                         sunToolkitUnlockMID);
            JNU_ThrowByName(env, "java/lang/InternalError",
                "SDLSurfaceData::SDLLock: cannot lock SDL_Surface.");
        }
    }

    return ret;
}

static void SDLGetRasInfo(JNIEnv* env __attribute__((unused)),
                          SurfaceDataOps* ops,
                          SurfaceDataRasInfo* rasInfo)
{
    SDLSurfaceDataOps *operations = NULL;
    operations = (SDLSurfaceDataOps*) ops;

    if (operations->surface->pixels) {

        rasInfo->rasBase = operations->surface->pixels;
        rasInfo->pixelStride = operations->surface->format->BytesPerPixel;
        rasInfo->pixelBitOffset = 0;
        rasInfo->scanStride = operations->surface->pitch;

    } else {

        rasInfo->rasBase = NULL;
        rasInfo->pixelStride = 0;
        rasInfo->pixelBitOffset = 0;
        rasInfo->scanStride = 0;
    }
}

static void SDLRelease(JNIEnv* env __attribute__((unused)),
                       SurfaceDataOps* ops __attribute__((unused)),
                       SurfaceDataRasInfo* rasInfo __attribute__((unused)))
{
    /* nothing to do */
}

static void SDLUnlock(JNIEnv* env, SurfaceDataOps* ops, SurfaceDataRasInfo* rasInfo)
{
    SDLSurfaceDataOps *operations = NULL;
    int width = 0;
    int height = 0;

    operations = (SDLSurfaceDataOps*) ops;

    if (SDL_MUSTLOCK(operations->surface) != 0) {
        fprintf(stderr, "SDL_MUSTLOCK::unlocking\n");
        SDL_UnlockSurface(operations->surface);
    }

    width = rasInfo->bounds.x2 - rasInfo->bounds.x1;
    height = rasInfo->bounds.y2 - rasInfo->bounds.y1;

/*
    fprintf(stderr, "!!!!!!!! width = %d, height = %d\n", width, height);
    fprintf(stderr, "rasInfo->bounds.x1 = %d, rasInfo->bounds.x2 = %d, rasInfo->bounds.y1 = %d, rasInfo->bounds.y2 = %d\n",
            rasInfo->bounds.x1,rasInfo->bounds.x2,rasInfo->bounds.y1,rasInfo->bounds.y2);
*/

    /*
     * FIXME: there is some problem, looks like Java passes us the wrong value
     * of y1 sometimes, causing in an invalid area.
     * This only seems to occurs with the SDL backend, so it's probably
     * caused by some mistakes elsewhere, and needs further investigation.
     */
    if (width < 0) {
        width = 0;
    }

    if (height < 0) {
        height = 0;
    }

    SDL_UpdateRect(operations->surface, rasInfo->bounds.x1, rasInfo->bounds.y1,
                   width, height);
    
    (*env)->CallStaticVoidMethod(env, sunToolkitCls, sunToolkitUnlockMID);
}
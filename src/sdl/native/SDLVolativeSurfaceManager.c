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

#include "net_java_openjdk_awt_peer_sdl_SDLVolativeSurfaceManager.h"

JNIEXPORT jlong JNICALL Java_net_java_openjdk_awt_peer_sdl_SDLVolativeSurfaceManager_initSurface
  (JNIEnv *env, jobject thiz __attribute__((unused)), jint width, jint height)
{
    SDL_Surface *surface = NULL;

    /*
     * FIXME: sync these with the ColorModel
     * returned by SDLGraphicsEnvironment
     */
    Uint32 rmask = 0x00FF0000;
    Uint32 gmask = 0x0000FF00;
    Uint32 bmask = 0x000000FF;
    Uint32 amask = 0;
    
    /* TODO: pass the depth we really want for the image. */
    surface = SDL_CreateRGBSurface(SDL_HWSURFACE | SDL_SRCCOLORKEY |
                                   SDL_DOUBLEBUF,
                                   width, height, 32,
                                   rmask, gmask, bmask, amask);
    if(surface == NULL) {
        fprintf(stderr, "CreateRGBSurface failed: %s\n", SDL_GetError());
        JNU_ThrowByName(env, "java/lang/InternalError",
                        "SDLVolativeSurfaceManager::initSurface failed");
    }

    return surface;
}

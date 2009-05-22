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

#include "net_java_openjdk_awt_peer_sdl_SDLBlit.h"

JNIEXPORT void JNICALL Java_net_java_openjdk_awt_peer_sdl_SDLBlit_nativeBlit
  (JNIEnv *env, jobject thiz, jobject src, jobject dest,
   jint sx, jint sy, jint dx, jint dy, jint w, jint h)
{
   SDLSurfaceDataOps *srcOperations = NULL;
   SDLSurfaceDataOps *destOperations = NULL;
   SDL_Rect blitSrcRect;
   SDL_Rect blitDestRect;

   int res = -1;

   srcOperations = (SDLSurfaceDataOps *) SurfaceData_GetOps(env, src);
   destOperations = (SDLSurfaceDataOps *) SurfaceData_GetOps(env, dest);

   blitSrcRect.x = sx;
   blitSrcRect.y = sy;
   blitSrcRect.w = w;
   blitSrcRect.h = h;

   blitDestRect.x = dx;
   blitDestRect.y = dy;
   blitDestRect.w = w;
   blitDestRect.h = h;

   res = SDL_BlitSurface(srcOperations->surface, &blitSrcRect,
                         destOperations->surface, &blitDestRect);
   if (res < 0) {
        fprintf(stderr, "Unable to blit surfaces: %s\n", SDL_GetError());
        JNU_ThrowByName(env, "java/lang/InternalError",
                "SDLBlit::nativeBlit: cannot blit surfaces");
        return;
   }

   SDL_Flip(destOperations->surface);
}
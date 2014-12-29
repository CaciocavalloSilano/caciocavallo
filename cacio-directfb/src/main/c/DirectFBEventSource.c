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

#include "net_java_openjdk_cacio_directfb_DirectFBEventSource.h"
#include "java_awt_event_MouseEvent.h"

static jfieldID eventDataIdFID;
static jfieldID eventDataModifiersFID;
static jfieldID eventDataXFID;
static jfieldID eventDataYFID;

JNIEXPORT void JNICALL Java_net_java_openjdk_cacio_directfb_DirectFBEventSource_initIDs (JNIEnv* env, jclass clazz) {

  jclass eventDataCls = (*env)->FindClass(env, "sun/awt/peer/cacio/managed/EventData");
  eventDataIdFID = (*env)->GetFieldID(env, eventDataCls, "id", "I");
  eventDataModifiersFID = (*env)->GetFieldID(env, eventDataCls, "modifiers", "I");
  eventDataXFID = (*env)->GetFieldID(env, eventDataCls, "x", "I");
  eventDataYFID = (*env)->GetFieldID(env, eventDataCls, "y", "I");

}

JNIEXPORT jlong JNICALL Java_net_java_openjdk_cacio_directfb_DirectFBEventSource_nativeInit(JNIEnv* env, jobject thiz, jlong dfb) {

  IDirectFB* directFB = (IDirectFB*) dfb;
  IDirectFBEventBuffer* eventBuffer = (IDirectFBEventBuffer*) malloc(sizeof(IDirectFBEventBuffer));
  directFB->CreateInputEventBuffer(directFB, DICAPS_ALL, false, &eventBuffer);
  return (jlong) eventBuffer;
}

JNIEXPORT void JNICALL Java_net_java_openjdk_cacio_directfb_DirectFBEventSource_getNextDirectFBEvent(JNIEnv* env, jobject thiz, jobject eventData, jlong ebuf) {

  IDirectFBEventBuffer* eventBuffer = (IDirectFBEventBuffer*) ebuf;
  DFBEvent ev;
  eventBuffer->WaitForEvent(eventBuffer);
  eventBuffer->GetEvent(eventBuffer, &ev);
  if (ev.clazz == DFEC_INPUT) {
    DFBInputEvent in = ev.input;
    switch (in.type) {
    case DIET_AXISMOTION:
      switch(in.axis) {
      case DIAI_X:
        (*env)->SetIntField(env, eventData, eventDataIdFID, java_awt_event_MouseEvent_MOUSE_MOVED);
        (*env)->SetIntField(env, eventData, eventDataXFID, in.axisabs);
        break;
      case DIAI_Y:
        (*env)->SetIntField(env, eventData, eventDataIdFID, java_awt_event_MouseEvent_MOUSE_MOVED);
        (*env)->SetIntField(env, eventData, eventDataYFID, in.axisabs);
        break;
      default:
        printf("unhandled axis motion on axis: %d", in.axis);
        break;
      }
      break;

    case DIET_BUTTONPRESS:
      (*env)->SetIntField(env, eventData, eventDataIdFID, java_awt_event_MouseEvent_MOUSE_PRESSED);
      (*env)->SetIntField(env, eventData, eventDataModifiersFID, java_awt_event_MouseEvent_BUTTON1_DOWN_MASK);
      break;

    case DIET_BUTTONRELEASE:
      (*env)->SetIntField(env, eventData, eventDataIdFID, java_awt_event_MouseEvent_MOUSE_RELEASED);
      (*env)->SetIntField(env, eventData, eventDataModifiersFID, 0);
      break;

    default:
      printf("unhandled input event: %d", in.type);
    }
  }

}

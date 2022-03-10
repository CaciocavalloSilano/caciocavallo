/*
 * Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved.
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
package com.github.caciocavallosilano.cacio.ctc;

import com.github.caciocavallosilano.cacio.peer.managed.FullScreenWindowFactory;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class CTCGraphicsConfiguration extends GraphicsConfiguration {

    private final ColorModel model;
    private final Raster raster;
    private CTCGraphicsDevice device;

    CTCGraphicsConfiguration(CTCGraphicsDevice dev) {
        BufferedImage bufferedImage = new BufferedImage(FullScreenWindowFactory.getScreenDimension().width, FullScreenWindowFactory.getScreenDimension().height, BufferedImage.TYPE_INT_ARGB);
        device = dev;
        model = bufferedImage.getColorModel();
        raster = bufferedImage.getRaster().createCompatibleWritableRaster(1, 1);
    }

    @Override
    public GraphicsDevice getDevice() {
        return device;
    }

    @Override
    public ColorModel getColorModel() {
        return ColorModel.getRGBdefault();
    }

    @Override
    public ColorModel getColorModel(int transparency) {
        return ColorModel.getRGBdefault();
    }

    @Override
    public Rectangle getBounds() {
        Dimension d = FullScreenWindowFactory.getScreenDimension();
        return new Rectangle(0, 0, d.width, d.height);
    }

    @Override
    public BufferedImage createCompatibleImage(int width, int height) {
        WritableRaster wr = raster.createCompatibleWritableRaster(width, height);
        return new BufferedImage(model, wr, model.isAlphaPremultiplied(), null);
    }

    @Override
    public AffineTransform getDefaultTransform() {
        return AffineTransform.getScaleInstance(1, 1);
    }

    @Override
    public AffineTransform getNormalizingTransform() {
        return new AffineTransform();
    }
}

package com.volmit.iris.util.uniques.features;

import com.volmit.iris.util.math.RNG;
import com.volmit.iris.util.noise.CNG;
import com.volmit.iris.util.uniques.UFeature;
import com.volmit.iris.util.uniques.UFeatureMeta;
import com.volmit.iris.util.uniques.UImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

public class UFWarpedCircle implements UFeature {
    @Override
    public void render(UImage image, RNG rng, double t, Consumer<Double> progressor, UFeatureMeta meta) {
        double r = Math.min(image.getWidth(), image.getHeight()) / 2.5D;
        double i, angle, x1, y1;
        CNG xShift = generator("x_warp", rng, 0.6, 1001, meta);
        CNG yShift = generator("y_warp", rng, 0.6, 1002, meta);
        CNG hue = generator("color_hue", rng, rng.d(0.25, 2.5), 1003, meta);
        CNG sat = generator("color_sat",rng, rng.d(0.25, 2.5), 1004, meta);
        CNG bri = generator("color_bri",rng, rng.d(0.25, 2.5), 1005, meta);
        double tcf = rng.d(0.75, 11.25);
        double rcf = rng.d(7.75, 16.25);
        int x = image.getWidth()/2;
        int y = image.getHeight()/2;

        for(int d = 0; d < 256; d++)
        {
            r -= Math.min(image.getWidth(), image.getHeight()) / 300D;

            if(r <= 0)
            {
                return;
            }

            for(i = 0; i < 360; i += 0.1)
            {
                angle = i;
                x1 = r * Math.cos(angle * Math.PI / 180);
                y1 = r * Math.sin(angle * Math.PI / 180);
                image.set((int)Math.round(x + x1 + xShift.fit(-r/2, r/2, x1 + (t+ (d * 8)), -y1 + (t+ (d * 8)))),
                        (int)Math.round(y + y1 + yShift.fit(-r/2, r/2, y1 + (t+ (d * 8)), -x1 + (t+ (d * 8)))),
                        color(hue, sat, bri, x1, y1, (t * tcf) + (d * rcf)));
            }

            progressor.accept(d / 256D);
        }
    }
}

package com.volmit.iris.util.uniques;

import com.volmit.iris.engine.object.NoiseStyle;
import com.volmit.iris.util.function.Function2;
import com.volmit.iris.util.function.NoiseInjector;
import com.volmit.iris.util.interpolation.InterpolationMethod;
import com.volmit.iris.util.math.RNG;
import com.volmit.iris.util.noise.CNG;
import com.volmit.iris.util.stream.ProceduralStream;
import com.volmit.iris.util.stream.interpolation.Interpolated;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.Consumer;

public interface UFeature {
    List<NoiseInjector> injectors = List.of(
            CNG.ADD,
            CNG.DST_MOD,
            CNG.DST_POW,
            CNG.DST_SUBTRACT,
            CNG.MAX,
            CNG.MIN,
            CNG.SRC_MOD,
            CNG.SRC_POW,
            CNG.SRC_SUBTRACT,
            CNG.MULTIPLY
    );

    void render(UImage image, RNG rng, double time, Consumer<Double> progressor, UFeatureMeta meta);

    default Color color(CNG hue, CNG saturation, CNG brightness, double x, double y, double t)
    {
        return Color.getHSBColor((float)hue.fitDouble(0, 1, x + t, y + t),
                (float)saturation.fitDouble(0, 1, x + t, y + t),
                (float)brightness.fitDouble(0, 1, x + t, y + t));
    }

    default InterpolationMethod interpolator(RNG rng)
    {
        return rng.pick(
                UniqueRenderer.renderer.getInterpolators()
        );
    }

    default CNG generator(String key, RNG rng, double scaleMod, long salt, UFeatureMeta meta)
    {
        return generator(key, rng, scaleMod, rng.i(1, 3), rng.i(1, 5), salt, meta);
    }

    default CNG generator(String key, RNG rng, double scaleMod, int fractures, int composites, long salt, UFeatureMeta meta)
    {
        RNG rngg = rng.nextParallelRNG(salt);
        CNG cng = rng.pick(UniqueRenderer.renderer.getStyles()).create(rngg).oct(rng.i(1, 5));
        RNG rngf = rngg.nextParallelRNG(-salt);
        cng.scale(rngf.d(0.33 * scaleMod, 1.66 * scaleMod));

        if(fractures > 0)
        {
            cng.fractureWith(generator(null, rngf.nextParallelRNG(salt + fractures), scaleMod / rng.d(4, 17), fractures-1, composites, salt + fractures + 55, null), scaleMod * rngf.nextDouble(16, 256));
        }

        for(int i = 0; i < composites; i++)
        {
            CNG sub = generator(null, rngf.nextParallelRNG(salt + fractures), scaleMod * rngf.d(0.4, 3.3), fractures / 3,  0, salt + fractures + composites + 78, null);
            sub.setInjector(rng.pick(injectors));
            cng.child(sub);
        }

        if(key != null && meta != null)
        {
            meta.registerGenerator(key, cng);
        }
        return cng;
    }
}

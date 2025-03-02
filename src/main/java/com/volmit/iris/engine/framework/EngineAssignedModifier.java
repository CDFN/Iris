/*
 * Iris is a World Generator for Minecraft Bukkit Servers
 * Copyright (c) 2022 Arcane Arts (Volmit Software)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.volmit.iris.engine.framework;

import com.volmit.iris.Iris;
import com.volmit.iris.util.documentation.BlockCoordinates;
import com.volmit.iris.util.hunk.Hunk;

public abstract class EngineAssignedModifier<T> extends EngineAssignedComponent implements EngineModifier<T> {
    public EngineAssignedModifier(Engine engine, String name) {
        super(engine, name);
    }

    @BlockCoordinates
    public abstract void onModify(int x, int z, Hunk<T> output, boolean multicore);

    @BlockCoordinates
    @Override
    public void modify(int x, int z, Hunk<T> output, boolean multicore) {
        try {
            onModify(x, z, output, multicore);
        } catch(Throwable e) {
            Iris.error("Modifier Failure: " + getName());
            e.printStackTrace();
        }
    }
}

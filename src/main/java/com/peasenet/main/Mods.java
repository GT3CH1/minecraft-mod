/*
 * Copyright (c) 2022. Gavin Pease and contributors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 *  of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 *  following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.peasenet.main;

import com.peasenet.mods.Mod;
import com.peasenet.mods.combat.ModAutoAttack;
import com.peasenet.mods.combat.ModAutoCrit;
import com.peasenet.mods.combat.ModKillAura;
import com.peasenet.mods.esp.ModChestEsp;
import com.peasenet.mods.esp.ModEntityItemEsp;
import com.peasenet.mods.esp.ModEntityPlayerEsp;
import com.peasenet.mods.esp.ModMobEsp;
import com.peasenet.mods.gui.ModGui;
import com.peasenet.mods.gui.ModGuiSettings;
import com.peasenet.mods.misc.ModFpsCounter;
import com.peasenet.mods.misc.ModGuiTextOverlay;
import com.peasenet.mods.movement.*;
import com.peasenet.mods.render.*;
import com.peasenet.mods.tracer.ModChestTracer;
import com.peasenet.mods.tracer.ModEntityItemTracer;
import com.peasenet.mods.tracer.ModEntityPlayerTracer;
import com.peasenet.mods.tracer.ModMobTracer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

/**
 * A placeholder class that initializes all the mods, and contains a few methods for adding/getting to that list.
 */
public class Mods {
    private static final HashMap<String, Mod> modMap = new HashMap<>();

    public Mods() {
        // MOVEMENT
        new ModFly();
        new ModAutoJump();
        new ModClimb();
        new ModNoClip();
        new ModNoFall();
        new ModFastMine();
        new ModFastPlace();
        new ModDolphin();
        // COMBAT
        new ModKillAura();
        new ModAutoCrit();
        new ModAutoAttack();
        // RENDER
        new ModXray();
        new ModAntiHurt();
        new ModFullBright();
        new ModChestEsp();
        new ModChestTracer();
        new ModMobEsp();
        new ModMobTracer();
        new ModEntityItemTracer();
        new ModEntityItemEsp();
        new ModEntityPlayerTracer();
        new ModEntityPlayerEsp();
        new ModAntiPumpkin();
        new ModHealthTag();
        new ModNoRain();
        // GUI
        new ModGui();
        new ModGuiSettings();

        // MISC
        new ModGuiTextOverlay();
        new ModFpsCounter();
        new ModWaypoint();
    }

    /**
     * Gets the list of mods.
     *
     * @return The list of mods.
     */
    public static ArrayList<Mod> getMods() {
        // get all values from the map and add them to the list
        var values = modMap.values();
        // sort the list by name
        var list = new ArrayList<>(values);
        list.sort(Comparator.comparing(Mod::getName));
        return list;
    }

    /**
     * Gets a mod from the given name.
     *
     * @param name - The name of the mod
     * @return The mod with the given name.
     */
    public static Mod getMod(String name) {
        return modMap.get(name);
    }

    /**
     * Adds a mod to the mod hashmap using the mod chat command as the key, and the mod as the value.
     *
     * @param m - The mod to add.
     */
    public static void addMod(Mod m) {
        modMap.put(m.getChatCommand(), m);
    }
}

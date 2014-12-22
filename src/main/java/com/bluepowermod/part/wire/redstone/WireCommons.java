/*
 * This file is part of Blue Power.
 *
 *     Blue Power is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Blue Power is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Blue Power.  If not, see <http://www.gnu.org/licenses/>
 */

package com.bluepowermod.part.wire.redstone;

import java.util.Map.Entry;

import net.minecraftforge.common.util.ForgeDirection;

import com.bluepowermod.api.misc.MinecraftColor;
import com.bluepowermod.api.redstone.IBundledConductor;
import com.bluepowermod.api.redstone.IBundledDevice;
import com.bluepowermod.api.redstone.IRedstoneConductor;
import com.bluepowermod.api.redstone.IRedstoneDevice;

public class WireCommons {

    public static boolean canConnect(IRedstoneConductor a, IRedstoneDevice b) {

        if (b instanceof IRedstoneConductor
                && (((IRedstoneConductor) b).isAnalog() != a.isAnalog() || ((IRedstoneConductor) b).hasLoss() != a.hasLoss()))
            return false;

        MinecraftColor c1 = a.getInsulationColor();
        MinecraftColor c2 = b.getInsulationColor();

        if (c1 == null || c2 == null)
            return false;

        return c1.matches(c2) || c1 == MinecraftColor.NONE || c2 == MinecraftColor.NONE;
    }

    public static boolean canConnect(IBundledConductor a, IBundledDevice b) {

        if (b instanceof IBundledConductor
                && (((IBundledConductor) b).isAnalog() != a.isAnalog() || ((IBundledConductor) b).hasLoss() != a.hasLoss()))
            return false;

        MinecraftColor c1 = a.getBundledColor();
        MinecraftColor c2 = b.getBundledColor();

        if (c1 == null || c2 == null)
            return false;

        return (c1.matches(c2) || c1 == MinecraftColor.NONE || c2 == MinecraftColor.NONE);
    }

    public static void refreshConnections(IRedstoneConductor conductor, IBundledConductor conductor1) {

        for (ForgeDirection d : ForgeDirection.VALID_DIRECTIONS) {
            IRedstoneDevice oldDevice = conductor.getDeviceOnSide(d);
            IBundledDevice oldBundledDevice = conductor1.getBundledDeviceOnSide(d);

            Entry<IBundledDevice, ForgeDirection> bundledDevice = WireHelper.getBundledNeighbor(conductor1, d);
            boolean wasConnected = oldDevice != null || oldBundledDevice != null;

            if (bundledDevice != null && bundledDevice.getKey() != conductor1.getBundledDeviceOnSide(d)) {
                conductor1.onConnect(d, bundledDevice.getKey());
                bundledDevice.getKey().onConnect(bundledDevice.getValue(), conductor1);
            }
            Entry<IRedstoneDevice, ForgeDirection> redstoneDevice = WireHelper.getNeighbor(conductor, d);
            if (redstoneDevice != null && redstoneDevice.getKey() != conductor.getDeviceOnSide(d)) {
                conductor.onConnect(d, redstoneDevice.getKey());
                redstoneDevice.getKey().onConnect(redstoneDevice.getValue(), conductor);
            }
            if (wasConnected && bundledDevice == null && redstoneDevice == null) {
                conductor.onDisconnect(d);
                if (oldDevice != null)
                    for (ForgeDirection s : ForgeDirection.VALID_DIRECTIONS)
                        if (oldDevice.getDeviceOnSide(s) == conductor)
                            oldDevice.onDisconnect(s);
                if (oldBundledDevice != null)
                    for (ForgeDirection s : ForgeDirection.VALID_DIRECTIONS)
                        if (oldBundledDevice.getBundledDeviceOnSide(s) == conductor1)
                            oldBundledDevice.onDisconnect(s);
            }
        }
    }

    public static void refreshConnectionsRedstone(IRedstoneConductor conductor) {

        for (ForgeDirection d : ForgeDirection.VALID_DIRECTIONS) {
            boolean wasConnected = conductor.getDeviceOnSide(d) != null;
            Entry<IRedstoneDevice, ForgeDirection> redstoneDevice = WireHelper.getNeighbor(conductor, d);
            if (redstoneDevice != null && redstoneDevice.getKey() != conductor.getDeviceOnSide(d)) {
                conductor.onConnect(d, redstoneDevice.getKey());
                redstoneDevice.getKey().onConnect(redstoneDevice.getValue(), conductor);
            }
            if (wasConnected && redstoneDevice == null)
                conductor.onDisconnect(d);
        }
    }

    public static void refreshConnectionsBundled(IBundledConductor conductor) {

        for (ForgeDirection d : ForgeDirection.VALID_DIRECTIONS) {
            Entry<IBundledDevice, ForgeDirection> bundledDevice = WireHelper.getBundledNeighbor(conductor, d);
            boolean wasConnected = conductor.getBundledDeviceOnSide(d) != null;
            if (bundledDevice != null && bundledDevice.getKey() != conductor.getBundledDeviceOnSide(d)) {
                conductor.onConnect(d, bundledDevice.getKey());
                bundledDevice.getKey().onConnect(bundledDevice.getValue(), conductor);
            }
            if (wasConnected && bundledDevice == null)
                conductor.onDisconnect(d);
        }
    }

    public static void disconnect(IRedstoneConductor conductor, IBundledConductor conductor1) {

        disconnectRedstone(conductor);
        disconnectBundled(conductor1);
    }

    public static void disconnectRedstone(IRedstoneConductor conductor) {

        for (ForgeDirection d : ForgeDirection.VALID_DIRECTIONS) {
            IRedstoneDevice redstoneDevice = conductor.getDeviceOnSide(d);
            if (redstoneDevice != null) {
                conductor.onDisconnect(d);
                for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                    IRedstoneDevice t = redstoneDevice.getDeviceOnSide(dir);
                    if (t == conductor) {
                        redstoneDevice.onDisconnect(dir);
                        break;
                    }
                }
            }
        }
    }

    public static void disconnectBundled(IBundledConductor conductor) {

        for (ForgeDirection d : ForgeDirection.VALID_DIRECTIONS) {
            IBundledDevice bundledDevice = conductor.getBundledDeviceOnSide(d);
            if (bundledDevice != null) {
                conductor.onDisconnect(d);
                for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                    IBundledDevice t = bundledDevice.getBundledDeviceOnSide(dir);
                    if (t == conductor) {
                        bundledDevice.onDisconnect(dir);
                        break;
                    }
                }
            }
        }
    }

    public static int getColorForPowerLevel(int color, byte power) {

        double mul = (0.3 + (0.7 * ((power & 0xFF) / 255D)));
        return ((int) ((color & 0xFF0000) * mul) & 0xFF0000) + ((int) ((color & 0x00FF00) * mul) & 0x00FF00)
                + ((int) ((color & 0x0000FF) * mul) & 0x0000FF);
    }
}
/*
 * This file is part of Blue Power. Blue Power is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. Blue Power is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along
 * with Blue Power. If not, see <http://www.gnu.org/licenses/>
 */
package com.bluepowermod.block.machine;

import net.minecraft.block.material.Material;

import com.bluepowermod.block.BlockContainerBase;
import com.bluepowermod.init.BPCreativeTabs;
import com.bluepowermod.reference.GuiIDs;
import com.bluepowermod.reference.Refs;
import com.bluepowermod.tile.tier3.TileKinectGenerator;

/**
 * 
 * @author TheFjong
 * 
 */
public class BlockKineticGenerator extends BlockContainerBase {
    
    public BlockKineticGenerator() {
    
        super(Material.iron, TileKinectGenerator.class);
        setCreativeTab(BPCreativeTabs.machines);
        setBlockName(Refs.KINETICGENERATOR_NAME);
        setBlockTextureName(Refs.MODID + ":" + Refs.MACHINE_TEXTURE_LOCATION + Refs.KINETICGENERATOR_NAME + "_front");
    }
    
    @Override
    public GuiIDs getGuiID() {
    
        return GuiIDs.KINETICGENERATOR_ID;
    }
    
}

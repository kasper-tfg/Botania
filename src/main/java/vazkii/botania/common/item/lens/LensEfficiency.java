/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under a
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 License
 * (http://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB)
 * 
 * File Created @ [Jan 24, 2015, 4:34:06 PM (GMT)]
 */
package vazkii.botania.common.item.lens;

import net.minecraft.item.ItemStack;
import vazkii.botania.api.mana.BurstProperties;

public class LensEfficiency extends Lens {

	@Override
	public void apply(ItemStack stack, BurstProperties props) {
		props.manaLossPerTick /= 5F;
		props.ticksBeforeManaLoss *= 1.1F;
	}
	
}

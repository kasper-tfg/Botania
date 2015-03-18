/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * 
 * File Created @ [Mar 17, 2015, 6:36:43 PM (GMT)]
 */
package vazkii.botania.common.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.block.tile.mana.TilePool;
import vazkii.botania.common.block.tile.mana.TilePump;
import vazkii.botania.common.lib.LibMisc;

public class EntityPoolMinecart extends EntityMinecart {
	
	private static final int TRANSFER_RATE = 10000;
	private static final String TAG_MANA = "mana";

    private boolean isBlocked = true;
	
	public EntityPoolMinecart(World p_i1712_1_) {
		super(p_i1712_1_);
	}

	public EntityPoolMinecart(World p_i1715_1_, double p_i1715_2_, double p_i1715_4_, double p_i1715_6_) {
		super(p_i1715_1_, p_i1715_2_, p_i1715_4_, p_i1715_6_);
	}
	
    protected void entityInit() {
        super.entityInit();
        dataWatcher.addObject(16, 0);
    }

	@Override
	public Block func_145817_o() {
		return ModBlocks.pool;
	}

	@Override
	public int getMinecartType() {
		return 0;
	}

	@Override
	public void killMinecart(DamageSource p_94095_1_) {
		super.killMinecart(p_94095_1_);
		this.func_145778_a(Item.getItemFromBlock(ModBlocks.pool), 1, 0.0F);
	}

	@Override
	public int getDefaultDisplayTileOffset() {
		return 8;
	}

	@Override
	public void moveMinecartOnRail(int x, int y, int z, double par4) {
		super.moveMinecartOnRail(x, y, z, par4);
		
		for(ForgeDirection dir : LibMisc.CARDINAL_DIRECTIONS) {
			int xp = x + dir.offsetX;
			int zp = z + dir.offsetZ;
			Block block = worldObj.getBlock(xp, y, zp);
			if(block == ModBlocks.pump) {
				int xp_ = xp + dir.offsetX;
				int zp_ = zp + dir.offsetZ;
				int meta = worldObj.getBlockMetadata(xp, y, zp);
				TileEntity tile = worldObj.getTileEntity(xp_, y, zp_);
				if(tile != null && tile instanceof IManaPool) {
					IManaPool pool = (IManaPool) tile;
					ForgeDirection pumpDir = ForgeDirection.getOrientation(meta);
					boolean did = false;
					
					if(pumpDir == dir) { // Pool -> Cart
						int cartMana = getMana();
						int poolMana = pool.getCurrentMana();
						int transfer = Math.min(TRANSFER_RATE, poolMana);
						int actualTransfer = Math.min(TilePool.MAX_MANA - cartMana, transfer);
						if(actualTransfer > 0) {
							pool.recieveMana(-transfer);
							setMana(cartMana + actualTransfer);
							did = true;
						}
					} else if(pumpDir == dir.getOpposite() && !pool.isFull()) { // Cart -> Pool
						int cartMana = getMana();
						int transfer = Math.min(TRANSFER_RATE, cartMana);
						if(transfer > 0) {
							pool.recieveMana(transfer);
							setMana(cartMana - transfer);
							did = true;							
						}
					}
					
					if(did) {
						worldObj.markBlockForUpdate(xp_, y, zp_);
						TileEntity tile_ = worldObj.getTileEntity(xp, y, zp);
						if(tile_ != null && tile_ instanceof TilePump) {
							TilePump pump = (TilePump) tile_;
							pump.hasCart = true;
							if(!pump.active)
								pump.setActive(true);
						}
					}
				}
			}
		}
	}
	
	@Override
    protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {
        super.writeEntityToNBT(p_70014_1_);
        p_70014_1_.setInteger(TAG_MANA, getMana());
    }

	@Override
    protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
        super.readEntityFromNBT(p_70037_1_);
        setMana(p_70037_1_.getInteger(TAG_MANA));
    }
	
	public int getMana() {
		return dataWatcher.getWatchableObjectInt(16);
	}
	
	public void setMana(int mana) {
		dataWatcher.updateObject(16, mana);
	}
	
}

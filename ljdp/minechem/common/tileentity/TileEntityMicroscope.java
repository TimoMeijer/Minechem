package ljdp.minechem.common.tileentity;

import java.util.ArrayList;

import ljdp.minechem.api.core.EnumMolecule;
import ljdp.minechem.api.recipe.DecomposerRecipe;
import ljdp.minechem.api.recipe.SynthesisRecipe;
import ljdp.minechem.common.MinechemItems;
import ljdp.minechem.common.items.ItemMolecule;
import ljdp.minechem.common.recipe.DecomposerRecipeHandler;
import ljdp.minechem.common.recipe.MinechemRecipes;
import ljdp.minechem.common.recipe.SynthesisRecipeHandler;
import ljdp.minechem.common.utils.MinechemHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public class TileEntityMicroscope extends MinechemTileEntity implements IInventory {
	
	private ItemStack[] microscopeInventory;
	public boolean isShaped = true;
	
	public TileEntityMicroscope() {
		microscopeInventory = new ItemStack[getSizeInventory()];
	}
	
	public void onInspectItemStack(ItemStack itemstack) {
		SynthesisRecipe synthesisRecipe = SynthesisRecipeHandler.instance.getRecipeFromOutput(itemstack);
		DecomposerRecipe decomposerRecipe = DecomposerRecipeHandler.instance.getRecipe(itemstack);
		if(microscopeInventory[1] != null && (synthesisRecipe != null || decomposerRecipe != null))
		{
			MinechemItems.journal.addItemStackToJournal(itemstack, microscopeInventory[1], worldObj);
		}
	}

	@Override
	public int getSizeInventory() {
		return 11;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		ItemStack itemstack = microscopeInventory[slot];
		return itemstack;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		if (slot >= 0 && slot < microscopeInventory.length)
        {
            ItemStack itemstack = microscopeInventory[slot];
            microscopeInventory[slot] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemStack) {
		microscopeInventory[slot] = itemStack;
		if(slot == 0 && itemStack != null && !worldObj.isRemote)
			onInspectItemStack(itemStack);
		if(slot == 1 && itemStack != null && microscopeInventory[0] != null && !worldObj.isRemote)
			onInspectItemStack(microscopeInventory[0]);
	}

	@Override
	public String getInvName() {
		return "container.microscope";
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityPlayer) {
		double dist = entityPlayer.getDistanceSq((double)xCoord + 0.5D, (double)yCoord + 0.5D, (double)zCoord + 0.5D);
		return worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) != this ? false :  dist <= 64.0D;
	}

	@Override
	public void openChest() {
	}

	@Override
	public void closeChest() {
	}
	
	public int getFacing() {
		return worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
	}

	@Override
	void sendUpdatePacket() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);
		ItemStack inpectingStack = microscopeInventory[0];
		if(inpectingStack != null) {
			NBTTagCompound inspectingStackTag = inpectingStack.writeToNBT(new NBTTagCompound());
			nbtTagCompound.setTag("inspectingStack", inspectingStackTag);
		}
		ItemStack journal = microscopeInventory[1];
		if(journal != null) {
			NBTTagCompound journalTag = journal.writeToNBT(new NBTTagCompound());
			nbtTagCompound.setTag("journal", journalTag);
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);
		NBTTagCompound inspectingStackTag = nbtTagCompound.getCompoundTag("inspectingStack");
		NBTTagCompound journalTag = nbtTagCompound.getCompoundTag("journal");
		ItemStack inspectingStack = ItemStack.loadItemStackFromNBT(inspectingStackTag);
		ItemStack journalStack = ItemStack.loadItemStackFromNBT(journalTag);
		microscopeInventory[0] = inspectingStack;
		microscopeInventory[1] = journalStack;
	}
}

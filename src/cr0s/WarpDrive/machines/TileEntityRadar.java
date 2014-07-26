package cr0s.WarpDrive.machines;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraftforge.common.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import cr0s.WarpDrive.*;

public class TileEntityRadar extends WarpEnergyTE implements IPeripheral {
	private String[] methodsArray =
	{
		"scanRay",		// 0
		"scanRadius",		// 1
		"getResultsCount",	// 2
		"getResult",		// 3
		"getEnergyLevel",	// 4
		"pos"			// 5
	};

	private ArrayList<TileEntityReactor> results;

	private int scanRadius = 0;
	private int cooldownTime = 0;

	@Override
	public void updateEntity() {
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			return;
		}
		super.updateEntity();

		try
		{
			if (worldObj.getBlockMetadata(xCoord, yCoord, zCoord) == 2) {
				if (cooldownTime++ > (20 * ((scanRadius / 1000) + 1))) {
					//System.out.println("Scanning...");
					WarpDrive.instance.warpCores.removeDeadCores();
					results = WarpDrive.instance.warpCores.searchWarpCoresInRadius(xCoord, yCoord, zCoord, scanRadius);
					worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 1, 1 + 2);
					cooldownTime = 0;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
	}

	// IPeripheral methods implementation
	@Override
	public String getType()
	{
		return "radar";
	}

	@Override
	public String[] getMethodNames()
	{
		return methodsArray;
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception
	{
		switch (method)
		{
			case 0: // scanRay (toX, toY, toZ)
				return new Object[] { -1 };
				
			case 1: // scanRadius (radius)
				if (arguments.length != 1) {
					return new Boolean[] { false };
				}
				int radius = ((Double)arguments[0]).intValue();
				if (radius <= 0 || radius > 10000)
				{
					scanRadius = 0;
					return new Boolean[] { false };
				}
				if (radius == 0 || !consumeEnergy(radius * radius, false)) {
					results = null;
					return new Boolean[] { false };
				}
				
				// Begin searching
				scanRadius = radius;
				cooldownTime = 0;
				worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 2, 1 + 2);
				return new Boolean[] { true };

			case 2: // getResultsCount
				if (results != null)
					return new Integer[] { results.size() };
				return new Integer[] { 0 };
				
			case 3: // getResult
				if (arguments.length == 1 && (results != null))
				{
					int index = ((Double)arguments[0]).intValue();
					if (index > -1 && index < results.size())
					{
						TileEntityReactor res = results.get(index);
						if (res != null)
						{
							int yAddition = (res.worldObj.provider.dimensionId == WarpDriveConfig.G_SPACE_DIMENSION_ID) ? 256 : (res.worldObj.provider.dimensionId == WarpDriveConfig.G_HYPERSPACE_DIMENSION_ID) ? 512 : 0;
							return new Object[] { (String)res.coreFrequency, (Integer)res.xCoord, (Integer)res.yCoord + yAddition, (Integer)res.zCoord };
						}
					}
				}
				return new Object[] { (String)"FAIL", 0, 0, 0 };
				
			case 4: // getEnergyLevel
				return new Integer[] { getEnergyStored() };
				
			case 5: // Pos
				return new Integer[] { xCoord, yCoord, zCoord };
		}

		return null;
	}

	@Override
	public void attach(IComputerAccess computer) {
		worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 1, 1 + 2);
	}

	@Override
	public void detach(IComputerAccess computer) {
		worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 0, 1 + 2);
	}

	@Override
	public int getMaxEnergyStored() {
		return WarpDriveConfig.WR_MAX_ENERGY_VALUE;
	}

	// IEnergySink methods implementation
	@Override
	public int getMaxSafeInput() {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean equals(IPeripheral other) {
		// TODO Auto-generated method stub
		return false;
	}
}

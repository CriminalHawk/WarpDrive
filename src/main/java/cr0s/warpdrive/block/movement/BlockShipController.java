package cr0s.warpdrive.block.movement;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cr0s.warpdrive.WarpDrive;

public class BlockShipController extends BlockContainer {
	private IIcon[] iconBuffer;
	
	private final int ICON_INACTIVE_SIDE = 0;
	private final int ICON_BOTTOM = 1;
	private final int ICON_TOP = 2;
	private final int ICON_SIDE_ACTIVATED = 3;
	
	public BlockShipController() {
		super(Material.rock);
		setHardness(0.5F);
		setStepSound(Block.soundTypeMetal);
		setCreativeTab(WarpDrive.creativeTabWarpDrive);
		setBlockName("warpdrive.movement.ShipController");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		iconBuffer = new IIcon[10];
		// Solid textures
		iconBuffer[ICON_INACTIVE_SIDE] = par1IconRegister.registerIcon("warpdrive:movement/shipControllerSideInactive");
		iconBuffer[ICON_BOTTOM] = par1IconRegister.registerIcon("warpdrive:movement/shipControllerBottom");
		iconBuffer[ICON_TOP] = par1IconRegister.registerIcon("warpdrive:movement/shipControllerTop");
		// Animated textures
		iconBuffer[ICON_SIDE_ACTIVATED    ] = par1IconRegister.registerIcon("warpdrive:movement/shipControllerSideActive0");
		iconBuffer[ICON_SIDE_ACTIVATED + 1] = par1IconRegister.registerIcon("warpdrive:movement/shipControllerSideActive1");
		iconBuffer[ICON_SIDE_ACTIVATED + 2] = par1IconRegister.registerIcon("warpdrive:movement/shipControllerSideActive2");
		iconBuffer[ICON_SIDE_ACTIVATED + 3] = par1IconRegister.registerIcon("warpdrive:movement/shipControllerSideActive3");
		iconBuffer[ICON_SIDE_ACTIVATED + 4] = par1IconRegister.registerIcon("warpdrive:movement/shipControllerSideActive4");
		iconBuffer[ICON_SIDE_ACTIVATED + 5] = par1IconRegister.registerIcon("warpdrive:movement/shipControllerSideActive5");
		iconBuffer[ICON_SIDE_ACTIVATED + 6] = par1IconRegister.registerIcon("warpdrive:movement/shipControllerSideActive6");
	}
	
	@Override
	public IIcon getIcon(int side, int metadata) {
		if (side == 0) {
			return iconBuffer[ICON_BOTTOM];
		} else if (side == 1) {
			return iconBuffer[ICON_TOP];
		}
		
		if (metadata == 0) { // Inactive state
			return iconBuffer[ICON_INACTIVE_SIDE];
		} else if (metadata > 0) { // Activated, in metadata stored mode number
			if (ICON_SIDE_ACTIVATED + metadata < iconBuffer.length) {
				return iconBuffer[ICON_SIDE_ACTIVATED + metadata];
			}
		}
		
		return null;
	}
	
	@Override
	public TileEntity createNewTileEntity(World var1, int i) {
		return new TileEntityShipController();
	}
	
	@Override
	public int quantityDropped(Random par1Random) {
		return 1;
	}
	
	/**
	 * Returns the items to drop on destruction.
	 */
	@Override
	public Item getItemDropped(int par1, Random par2Random, int par3) {
		return Item.getItemFromBlock(this);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ) {
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			return false;
		}
		
		// Report status
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity != null && tileEntity instanceof TileEntityShipController && (entityPlayer.getHeldItem() == null)) {
			if (entityPlayer.isSneaking()) {
				WarpDrive.addChatMessage(entityPlayer, ((TileEntityShipController)tileEntity).getStatus());
			} else {
				WarpDrive.addChatMessage(entityPlayer, ((TileEntityShipController)tileEntity).attachPlayer(entityPlayer));
			}
			return true;
		}
		
		return false;
	}
}
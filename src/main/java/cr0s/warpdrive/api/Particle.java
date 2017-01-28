package cr0s.warpdrive.api;

import net.minecraft.item.EnumRarity;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Locale;

import static cr0s.warpdrive.config.WarpDriveConfig.clamp;

public class Particle {
	protected final String registryName;
	
	protected int color;
	protected int colorIndex;
	
	protected EnumRarity enumRarity = EnumRarity.COMMON;
	
	public Particle(final String registryName) {
		this.registryName = registryName.toLowerCase(Locale.ENGLISH);
	}
	
	public Particle setRarity(final EnumRarity enumRarity) {
		this.enumRarity = enumRarity;
		return this;
	}
	
	public Particle setColorIndex(final int colorIndex) {
		this.colorIndex = clamp(0, 4, colorIndex);
		return this;
	}
	
	public Particle setColor(final int colorIndex) {
		this.color = clamp(0x000000, 0xFFFFFF, color);
		return this;
	}
	
	public Particle setColor(final int red, final int green, final int blue) {
		this.color = (clamp(0, 255, red) << 16) + (clamp(0, 255,  green) << 8) + clamp(0, 255, blue);
		return this;
	}
	
	public final String getRegistryName()
	{
		return this.registryName;
	}
	
	@SideOnly(Side.CLIENT)
	public String getLocalizedName() {
		String unlocalizedName = getUnlocalizedName();
		return unlocalizedName == null ? "" : new TextComponentTranslation(unlocalizedName + ".name").getFormattedText();
	}
	
	@SideOnly(Side.CLIENT)
	public String getLocalizedTooltip() {
		String unlocalizedName = getUnlocalizedName();
		return unlocalizedName == null ? "" : new TextComponentTranslation(unlocalizedName + ".tooltip").getFormattedText();
	}
	
	public String getUnlocalizedName()
	{
		return "warpdrive.particle." + this.registryName;
	}
	
	/* Default Accessors */
	
	public EnumRarity getRarity()
	{
		return enumRarity;
	}
	
	public int getColorIndex()
	{
		return colorIndex;
	}
	
	public int getColor()
	{
		return color;
	}
}


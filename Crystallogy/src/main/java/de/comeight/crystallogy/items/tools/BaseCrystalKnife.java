package de.comeight.crystallogy.items.tools;

import java.util.List;

import de.comeight.crystallogy.CommonProxy;
import de.comeight.crystallogy.blocks.materials.CustomToolMaterials;
import de.comeight.crystallogy.network.NetworkPacketParticle;
import de.comeight.crystallogy.network.NetworkParticle;
import de.comeight.crystallogy.particles.ParticleB;
import de.comeight.crystallogy.tileEntitys.TileEntityEntityJar;
import de.comeight.crystallogy.util.Log;
import de.comeight.crystallogy.util.ToolTipBuilder;
import de.comeight.crystallogy.util.Utilities;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BaseCrystalKnife extends BaseItemSword{
	//-----------------------------------------------Variabeln:---------------------------------------------
	private float attackDamage;
	
	//-----------------------------------------------Constructor:-------------------------------------------
	public BaseCrystalKnife(String id) {
		super(CustomToolMaterials.CRYSTALLKNIF, id);
		this.canRepair = true;
		
		this.setMaxStackSize(1);
	}
	
	//-----------------------------------------------Set-, Get-Methoden:------------------------------------
	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasEffect(ItemStack stack)
    {
		return hasEntity(stack);
    }
	
	public boolean hasEntity(ItemStack stack){
		NBTTagCompound c = stack.getTagCompound();
		if(c == null||c.getString("name") == null||c.getString("name").equals("-")||c.getString("name").equals("")){
			return false;
		}
        else {
			return true;
		}
	}
	
	protected EntityLivingBase getEntity(ItemStack stack, World worldIn){
		NBTTagCompound c = stack.getTagCompound();
		String id = c.getString("id");
		try {	
			return (EntityLivingBase) worldIn.getEntityByID(Integer.parseInt(id));
		} 
		catch (Exception e) {
			Log.error(e.getMessage());
			return null;
		}
	}
	
	//-----------------------------------------------Sonstige Methoden:-------------------------------------
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		if(playerIn.isSneaking()){
			itemStackIn = removeEntity(itemStackIn, worldIn, playerIn.getPositionVector(), true);
		}
		else{
			if(!hasEntity(itemStackIn)){
				if(!playerIn.capabilities.isCreativeMode){
					playerIn.attackTargetEntityWithCurrentItem(playerIn);
				}
				saveNBT(itemStackIn, playerIn);
			}
		}
		return super.onItemRightClick(itemStackIn, worldIn, playerIn, hand);
	}
	
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(hasEntity(stack)){
			if(worldIn.getTileEntity(pos) instanceof TileEntityEntityJar){
				TileEntityEntityJar tE = (TileEntityEntityJar) worldIn.getTileEntity(pos);
				if(!tE.hasEntity()){
					tE.setEntity(getEntity(stack, worldIn));
					stack = removeEntity(stack, worldIn, playerIn.getPositionVector(), false);
					return EnumActionResult.SUCCESS;
				}
			}
		}
		
		return super.onItemUse(stack, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}

	
	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		if(hasEntity(stack)){
			removeEntity(stack, target.worldObj, attacker.getPositionVector(), true);
		}
		if(!(target instanceof EntityPlayer)){
			stack = saveNBT(stack, target);
			super.hitEntity(stack, target, attacker);
			return true;
		}
		return super.hitEntity(stack, target, attacker);
	}
	
	
	@Override
	public float getDamageVsEntity() {
		return this.attackDamage;
	}
	
	
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		NBTTagCompound nbtTagCompound = stack.getTagCompound();
		if(nbtTagCompound == null){
			stack = saveEmptyNBT(stack);
			tooltip.add(TextFormatting.GOLD + "Name:" + TextFormatting.RESET + " -");
			tooltip.add(TextFormatting.GOLD + "ID:" + TextFormatting.RESET + " -");
		}
		else{
			if(hasEntity(stack)){
				tooltip.add("Has Entity: " + TextFormatting.DARK_GREEN + "Yes");
			}
			else{
				tooltip.add("Has Entity: " + TextFormatting.DARK_RED + "No");
			}
			if(GuiScreen.isShiftKeyDown()){
				tooltip.add("");
				tooltip.add(TextFormatting.GOLD + "Name: " + TextFormatting.RESET + nbtTagCompound.getString("name"));
				tooltip.add(TextFormatting.GOLD + "ID: " + TextFormatting.RESET + nbtTagCompound.getString("id"));
			}
			else{
				ToolTipBuilder.addShiftForMoreDetails(tooltip);
			}
		}
		super.addInformation(stack, playerIn, tooltip, advanced);
	}
	
	
	public ItemStack saveNBT(ItemStack stack, EntityLivingBase entity){
		stack = saveEmptyNBT(stack);
		NBTTagCompound nbtTagCompound = stack.getTagCompound();
		
		nbtTagCompound.setString("name", entity.getName());
		nbtTagCompound.setString("id", String.valueOf(entity.getEntityId()));
		
		stack.setTagCompound(nbtTagCompound);
		return stack;
	}
	
	
	protected ItemStack saveEmptyNBT(ItemStack stack){
		NBTTagCompound nbtTagCompound = stack.getTagCompound();
		if(nbtTagCompound == null){
			nbtTagCompound = new NBTTagCompound();
		}
		
		nbtTagCompound.setString("name", "-");
		nbtTagCompound.setString("id", "-");

		stack.setTagCompound(nbtTagCompound);
		return stack;
	}
	
	
	protected ItemStack getItemStack(){
		return this.saveEmptyNBT(new ItemStack(this));
	}
	
	
	protected ItemStack removeEntity(ItemStack stack, World worldIn, Vec3d pos, boolean release){
		if(hasEntity(stack)){
			stack = saveEmptyNBT(stack);
			
			if(release){
				if(worldIn.isRemote){
					for (int i = 0; i < 5; i++) { //Particel:
						ParticleB gP = new ParticleB(worldIn, pos.xCoord, pos.yCoord, pos.zCoord, 0.0, 0.0, 0.0);
						gP.setParticleMaxAge(120);
						gP.setRBGColorF(Utilities.getRandFloat(0, 100), Utilities.getRandFloat(0, 100), Utilities.getRandFloat(0, 100));
						NetworkParticle nP = new NetworkParticle(gP, gP.name);
						nP.setSize(new Vec3d(1.0, 2.0, 1.0));
						nP.setNumberOfParticle(30);
						NetworkPacketParticle pMtS = new NetworkPacketParticle(nP);
						CommonProxy.NETWORKWRAPPER.sendToServer(pMtS);	
					}
				}
				worldIn.addWeatherEffect(new EntityLightningBolt(worldIn, pos.xCoord, pos.yCoord, pos.zCoord, false));
				worldIn.playSound((EntityPlayer)null, pos.xCoord, pos.yCoord, pos.zCoord, SoundEvents.entity_endermen_stare, SoundCategory.NEUTRAL, 1.0F, 1.0F);
				worldIn.playSound((EntityPlayer)null, pos.xCoord, pos.yCoord, pos.zCoord, SoundEvents.entity_enderdragon_growl, SoundCategory.NEUTRAL, 1.0F, 1.0F);
				worldIn.playSound((EntityPlayer)null, pos.xCoord, pos.yCoord, pos.zCoord, SoundEvents.entity_wither_spawn, SoundCategory.NEUTRAL, 1.0F, 0.6F);
			}
		}
		return stack;
	}

}
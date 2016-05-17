package net.minecraft.entity.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFindEntityNearest;
import net.minecraft.entity.ai.EntityAIFindEntityNearestPlayer;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.loot.LootTableList;

public class EntitySlime extends EntityLiving implements IMob
{
    private static final DataParameter<Integer> SLIME_SIZE = EntityDataManager.<Integer>createKey(EntitySlime.class, DataSerializers.VARINT);
    public float squishAmount;
    public float squishFactor;
    public float prevSquishFactor;
    private boolean wasOnGround;

    public EntitySlime(World worldIn)
    {
        super(worldIn);
        this.moveHelper = new EntitySlime.SlimeMoveHelper(this);
    }

    protected void initEntityAI()
    {
        this.tasks.addTask(1, new EntitySlime.AISlimeFloat(this));
        this.tasks.addTask(2, new EntitySlime.AISlimeAttack(this));
        this.tasks.addTask(3, new EntitySlime.AISlimeFaceRandom(this));
        this.tasks.addTask(5, new EntitySlime.AISlimeHop(this));
        this.targetTasks.addTask(1, new EntityAIFindEntityNearestPlayer(this));
        this.targetTasks.addTask(3, new EntityAIFindEntityNearest(this, EntityIronGolem.class));
    }

    protected void entityInit()
    {
        super.entityInit();
        this.dataWatcher.register(SLIME_SIZE, Integer.valueOf(1));
    }

    protected void setSlimeSize(int size)
    {
        this.dataWatcher.set(SLIME_SIZE, Integer.valueOf(size));
        this.setSize(0.51000005F * (float)size, 0.51000005F * (float)size);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double)(size * size));
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)(0.2F + 0.1F * (float)size));
        this.setHealth(this.getMaxHealth());
        this.experienceValue = size;
    }

    /**
     * Returns the size of the slime.
     */
    public int getSlimeSize()
    {
        return ((Integer)this.dataWatcher.get(SLIME_SIZE)).intValue();
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound tagCompound)
    {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger("Size", this.getSlimeSize() - 1);
        tagCompound.setBoolean("wasOnGround", this.wasOnGround);
    }

    public boolean func_189101_db()
    {
        return this.getSlimeSize() <= 1;
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound tagCompund)
    {
        super.readEntityFromNBT(tagCompund);
        int i = tagCompund.getInteger("Size");

        if (i < 0)
        {
            i = 0;
        }

        this.setSlimeSize(i + 1);
        this.wasOnGround = tagCompund.getBoolean("wasOnGround");
    }

    protected EnumParticleTypes getParticleType()
    {
        return EnumParticleTypes.SLIME;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        if (!this.worldObj.isRemote && this.worldObj.getDifficulty() == EnumDifficulty.PEACEFUL && this.getSlimeSize() > 0)
        {
            this.isDead = true;
        }

        this.squishFactor += (this.squishAmount - this.squishFactor) * 0.5F;
        this.prevSquishFactor = this.squishFactor;
        super.onUpdate();

        if (this.onGround && !this.wasOnGround)
        {
            int i = this.getSlimeSize();
            if (spawnCustomParticles()) { i = 0; } // don't spawn particles if it's handled by the implementation itself
            for (int j = 0; j < i * 8; ++j)
            {
                float f = this.rand.nextFloat() * ((float)Math.PI * 2F);
                float f1 = this.rand.nextFloat() * 0.5F + 0.5F;
                float f2 = MathHelper.sin(f) * (float)i * 0.5F * f1;
                float f3 = MathHelper.cos(f) * (float)i * 0.5F * f1;
                World world = this.worldObj;
                EnumParticleTypes enumparticletypes = this.getParticleType();
                double d0 = this.posX + (double)f2;
                double d1 = this.posZ + (double)f3;
                world.spawnParticle(enumparticletypes, d0, this.getEntityBoundingBox().minY, d1, 0.0D, 0.0D, 0.0D, new int[0]);
            }

            this.playSound(this.func_184709_cY(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) / 0.8F);
            this.squishAmount = -0.5F;
        }
        else if (!this.onGround && this.wasOnGround)
        {
            this.squishAmount = 1.0F;
        }

        this.wasOnGround = this.onGround;
        this.alterSquishAmount();
    }

    protected void alterSquishAmount()
    {
        this.squishAmount *= 0.6F;
    }

    /**
     * Gets the amount of time the slime needs to wait between jumps.
     */
    protected int getJumpDelay()
    {
        return this.rand.nextInt(20) + 10;
    }

    protected EntitySlime createInstance()
    {
        return new EntitySlime(this.worldObj);
    }

    public void notifyDataManagerChange(DataParameter<?> key)
    {
        if (SLIME_SIZE.equals(key))
        {
            int i = this.getSlimeSize();
            this.setSize(0.51000005F * (float)i, 0.51000005F * (float)i);
            this.rotationYaw = this.rotationYawHead;
            this.renderYawOffset = this.rotationYawHead;

            if (this.isInWater() && this.rand.nextInt(20) == 0)
            {
                this.resetHeight();
            }
        }

        super.notifyDataManagerChange(key);
    }

    /**
     * Will get destroyed next tick.
     */
    public void setDead()
    {
        int i = this.getSlimeSize();

        if (!this.worldObj.isRemote && i > 1 && this.getHealth() <= 0.0F)
        {
            int j = 2 + this.rand.nextInt(3);

            for (int k = 0; k < j; ++k)
            {
                float f = ((float)(k % 2) - 0.5F) * (float)i / 4.0F;
                float f1 = ((float)(k / 2) - 0.5F) * (float)i / 4.0F;
                EntitySlime entityslime = this.createInstance();

                if (this.hasCustomName())
                {
                    entityslime.setCustomNameTag(this.getCustomNameTag());
                }

                if (this.isNoDespawnRequired())
                {
                    entityslime.enablePersistence();
                }

                entityslime.setSlimeSize(i / 2);
                entityslime.setLocationAndAngles(this.posX + (double)f, this.posY + 0.5D, this.posZ + (double)f1, this.rand.nextFloat() * 360.0F, 0.0F);
                this.worldObj.spawnEntityInWorld(entityslime);
            }
        }

        super.setDead();
    }

    /**
     * Applies a velocity to each of the entities pushing them away from each other. Args: entity
     */
    public void applyEntityCollision(Entity entityIn)
    {
        super.applyEntityCollision(entityIn);

        if (entityIn instanceof EntityIronGolem && this.canDamagePlayer())
        {
            this.func_175451_e((EntityLivingBase)entityIn);
        }
    }

    /**
     * Called by a player entity when they collide with an entity
     */
    public void onCollideWithPlayer(EntityPlayer entityIn)
    {
        if (this.canDamagePlayer())
        {
            this.func_175451_e(entityIn);
        }
    }

    protected void func_175451_e(EntityLivingBase p_175451_1_)
    {
        int i = this.getSlimeSize();

        if (this.canEntityBeSeen(p_175451_1_) && this.getDistanceSqToEntity(p_175451_1_) < 0.6D * (double)i * 0.6D * (double)i && p_175451_1_.attackEntityFrom(DamageSource.causeMobDamage(this), (float)this.getAttackStrength()))
        {
            this.playSound(SoundEvents.entity_slime_attack, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            this.applyEnchantments(this, p_175451_1_);
        }
    }

    public float getEyeHeight()
    {
        return 0.625F * this.height;
    }

    /**
     * Indicates weather the slime is able to damage the player (based upon the slime's size)
     */
    protected boolean canDamagePlayer()
    {
        return !this.func_189101_db();
    }

    /**
     * Gets the amount of damage dealt to the player when "attacked" by the slime.
     */
    protected int getAttackStrength()
    {
        return this.getSlimeSize();
    }

    protected SoundEvent getHurtSound()
    {
        return this.func_189101_db() ? SoundEvents.entity_small_slime_hurt : SoundEvents.entity_slime_hurt;
    }

    protected SoundEvent getDeathSound()
    {
        return this.func_189101_db() ? SoundEvents.entity_small_slime_death : SoundEvents.entity_slime_death;
    }

    protected SoundEvent func_184709_cY()
    {
        return this.func_189101_db() ? SoundEvents.entity_small_slime_squish : SoundEvents.entity_slime_squish;
    }

    protected Item getDropItem()
    {
        return this.getSlimeSize() == 1 ? Items.slime_ball : null;
    }

    protected ResourceLocation getLootTable()
    {
        return this.getSlimeSize() == 1 ? LootTableList.ENTITIES_SLIME : LootTableList.EMPTY;
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this entity.
     */
    public boolean getCanSpawnHere()
    {
        BlockPos blockpos = new BlockPos(MathHelper.floor_double(this.posX), 0, MathHelper.floor_double(this.posZ));
        Chunk chunk = this.worldObj.getChunkFromBlockCoords(blockpos);

        if (this.worldObj.getWorldInfo().getTerrainType().handleSlimeSpawnReduction(rand, worldObj))
        {
            return false;
        }
        else
        {
            if (this.worldObj.getDifficulty() != EnumDifficulty.PEACEFUL)
            {
                BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(blockpos);

                if (biomegenbase == Biomes.swampland && this.posY > 50.0D && this.posY < 70.0D && this.rand.nextFloat() < 0.5F && this.rand.nextFloat() < this.worldObj.getCurrentMoonPhaseFactor() && this.worldObj.getLightFromNeighbors(new BlockPos(this)) <= this.rand.nextInt(8))
                {
                    return super.getCanSpawnHere();
                }

                if (this.rand.nextInt(10) == 0 && chunk.getRandomWithSeed(987234911L).nextInt(10) == 0 && this.posY < 40.0D)
                {
                    return super.getCanSpawnHere();
                }
            }

            return false;
        }
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    protected float getSoundVolume()
    {
        return 0.4F * (float)this.getSlimeSize();
    }

    /**
     * The speed it takes to move the entityliving's rotationPitch through the faceEntity method. This is only currently
     * use in wolves.
     */
    public int getVerticalFaceSpeed()
    {
        return 0;
    }

    /**
     * Returns true if the slime makes a sound when it jumps (based upon the slime's size)
     */
    protected boolean makesSoundOnJump()
    {
        return this.getSlimeSize() > 0;
    }

    /**
     * Causes this entity to do an upwards motion (jumping).
     */
    protected void jump()
    {
        this.motionY = 0.41999998688697815D;
        this.isAirBorne = true;
    }

    /**
     * Called only once on an entity when first time spawned, via egg, mob spawner, natural spawning etc, but not called
     * when entity is reloaded from nbt. Mainly used for initializing attributes and inventory
     */
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata)
    {
        int i = this.rand.nextInt(3);

        if (i < 2 && this.rand.nextFloat() < 0.5F * difficulty.getClampedAdditionalDifficulty())
        {
            ++i;
        }

        int j = 1 << i;
        this.setSlimeSize(j);
        return super.onInitialSpawn(difficulty, livingdata);
    }

    protected SoundEvent func_184710_cZ()
    {
        return this.func_189101_db() ? SoundEvents.entity_small_slime_jump : SoundEvents.entity_slime_jump;
    }

    /* ======================================== FORGE START =====================================*/
    /**
     * Called when the slime spawns particles on landing, see onUpdate.
     * Return true to prevent the spawning of the default particles.
     */
    protected boolean spawnCustomParticles() { return false; }
    /* ======================================== FORGE END   =====================================*/

    static class AISlimeAttack extends EntityAIBase
        {
            private EntitySlime slime;
            private int field_179465_b;

            public AISlimeAttack(EntitySlime slimeIn)
            {
                this.slime = slimeIn;
                this.setMutexBits(2);
            }

            /**
             * Returns whether the EntityAIBase should begin execution.
             */
            public boolean shouldExecute()
            {
                EntityLivingBase entitylivingbase = this.slime.getAttackTarget();
                return entitylivingbase == null ? false : (!entitylivingbase.isEntityAlive() ? false : !(entitylivingbase instanceof EntityPlayer) || !((EntityPlayer)entitylivingbase).capabilities.disableDamage);
            }

            /**
             * Execute a one shot task or start executing a continuous task
             */
            public void startExecuting()
            {
                this.field_179465_b = 300;
                super.startExecuting();
            }

            /**
             * Returns whether an in-progress EntityAIBase should continue executing
             */
            public boolean continueExecuting()
            {
                EntityLivingBase entitylivingbase = this.slime.getAttackTarget();
                return entitylivingbase == null ? false : (!entitylivingbase.isEntityAlive() ? false : (entitylivingbase instanceof EntityPlayer && ((EntityPlayer)entitylivingbase).capabilities.disableDamage ? false : --this.field_179465_b > 0));
            }

            /**
             * Updates the task
             */
            public void updateTask()
            {
                this.slime.faceEntity(this.slime.getAttackTarget(), 10.0F, 10.0F);
                ((EntitySlime.SlimeMoveHelper)this.slime.getMoveHelper()).func_179920_a(this.slime.rotationYaw, this.slime.canDamagePlayer());
            }
        }

    static class AISlimeFaceRandom extends EntityAIBase
        {
            private EntitySlime slime;
            private float field_179459_b;
            private int field_179460_c;

            public AISlimeFaceRandom(EntitySlime slimeIn)
            {
                this.slime = slimeIn;
                this.setMutexBits(2);
            }

            /**
             * Returns whether the EntityAIBase should begin execution.
             */
            public boolean shouldExecute()
            {
                return this.slime.getAttackTarget() == null && (this.slime.onGround || this.slime.isInWater() || this.slime.isInLava() || this.slime.isPotionActive(MobEffects.levitation));
            }

            /**
             * Updates the task
             */
            public void updateTask()
            {
                if (--this.field_179460_c <= 0)
                {
                    this.field_179460_c = 40 + this.slime.getRNG().nextInt(60);
                    this.field_179459_b = (float)this.slime.getRNG().nextInt(360);
                }

                ((EntitySlime.SlimeMoveHelper)this.slime.getMoveHelper()).func_179920_a(this.field_179459_b, false);
            }
        }

    static class AISlimeFloat extends EntityAIBase
        {
            private EntitySlime slime;

            public AISlimeFloat(EntitySlime slimeIn)
            {
                this.slime = slimeIn;
                this.setMutexBits(5);
                ((PathNavigateGround)slimeIn.getNavigator()).setCanSwim(true);
            }

            /**
             * Returns whether the EntityAIBase should begin execution.
             */
            public boolean shouldExecute()
            {
                return this.slime.isInWater() || this.slime.isInLava();
            }

            /**
             * Updates the task
             */
            public void updateTask()
            {
                if (this.slime.getRNG().nextFloat() < 0.8F)
                {
                    this.slime.getJumpHelper().setJumping();
                }

                ((EntitySlime.SlimeMoveHelper)this.slime.getMoveHelper()).setSpeed(1.2D);
            }
        }

    static class AISlimeHop extends EntityAIBase
        {
            private EntitySlime slime;

            public AISlimeHop(EntitySlime slimeIn)
            {
                this.slime = slimeIn;
                this.setMutexBits(5);
            }

            /**
             * Returns whether the EntityAIBase should begin execution.
             */
            public boolean shouldExecute()
            {
                return true;
            }

            /**
             * Updates the task
             */
            public void updateTask()
            {
                ((EntitySlime.SlimeMoveHelper)this.slime.getMoveHelper()).setSpeed(1.0D);
            }
        }

    static class SlimeMoveHelper extends EntityMoveHelper
        {
            private float field_179922_g;
            private int field_179924_h;
            private EntitySlime slime;
            private boolean field_179923_j;

            public SlimeMoveHelper(EntitySlime slimeIn)
            {
                super(slimeIn);
                this.slime = slimeIn;
                this.field_179922_g = 180.0F * slimeIn.rotationYaw / (float)Math.PI;
            }

            public void func_179920_a(float p_179920_1_, boolean p_179920_2_)
            {
                this.field_179922_g = p_179920_1_;
                this.field_179923_j = p_179920_2_;
            }

            public void setSpeed(double speedIn)
            {
                this.speed = speedIn;
                this.field_188491_h = EntityMoveHelper.Action.MOVE_TO;
            }

            public void onUpdateMoveHelper()
            {
                this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, this.field_179922_g, 90.0F);
                this.entity.rotationYawHead = this.entity.rotationYaw;
                this.entity.renderYawOffset = this.entity.rotationYaw;

                if (this.field_188491_h != EntityMoveHelper.Action.MOVE_TO)
                {
                    this.entity.setMoveForward(0.0F);
                }
                else
                {
                    this.field_188491_h = EntityMoveHelper.Action.WAIT;

                    if (this.entity.onGround)
                    {
                        this.entity.setAIMoveSpeed((float)(this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue()));

                        if (this.field_179924_h-- <= 0)
                        {
                            this.field_179924_h = this.slime.getJumpDelay();

                            if (this.field_179923_j)
                            {
                                this.field_179924_h /= 3;
                            }

                            this.slime.getJumpHelper().setJumping();

                            if (this.slime.makesSoundOnJump())
                            {
                                this.slime.playSound(this.slime.func_184710_cZ(), this.slime.getSoundVolume(), ((this.slime.getRNG().nextFloat() - this.slime.getRNG().nextFloat()) * 0.2F + 1.0F) * 0.8F);
                            }
                        }
                        else
                        {
                            this.slime.moveStrafing = this.slime.moveForward = 0.0F;
                            this.entity.setAIMoveSpeed(0.0F);
                        }
                    }
                    else
                    {
                        this.entity.setAIMoveSpeed((float)(this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue()));
                    }
                }
            }
        }
}
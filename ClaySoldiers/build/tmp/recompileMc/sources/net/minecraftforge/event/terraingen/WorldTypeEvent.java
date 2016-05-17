package net.minecraftforge.event.terraingen;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.WorldType;

/**
 * WorldTypeEvent is fired when an event involving the world occurs.<br>
 * If a method utilizes this {@link Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * {@link #worldType} contains the WorldType of the world this event is occurring in.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#TERRAIN_GEN_BUS}.<br>
 **/
public class WorldTypeEvent extends Event
{
    private final WorldType worldType;

    public WorldTypeEvent(WorldType worldType)
    {
        this.worldType = worldType;
    }

    public WorldType getWorldType()
    {
        return worldType;
    }

    /**
     * BiomeSize is fired when vanilla Minecraft attempts to generate biomes.<br>
     * This event is fired during biome generation in
     * GenLayer#initializeAllBiomeGenerators(long, WorldType). <br>
     * <br>
     * {@link #originalSize} the original size of the Biome. <br>
     * {@link #newSize} the new size of the biome. Initially set to the {@link #originalSize}. <br>
     * If {@link #newSize} is set to a new value, that value will be used for the Biome size. <br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#TERRAIN_GEN_BUS}.<br>
     **/
    public static class BiomeSize extends WorldTypeEvent
    {
        private final int originalSize;
        private int newSize;

        public BiomeSize(WorldType worldType, int original)
        {
            super(worldType);
            originalSize = original;
            setNewSize(original);
        }

        public int getOriginalSize()
        {
            return originalSize;
        }

        public int getNewSize()
        {
            return newSize;
        }

        public void setNewSize(int newSize)
        {
            this.newSize = newSize;
        }
    }

    /**
     * InitBiomeGens is fired when vanilla Minecraft attempts to initialize the biome generators.<br>
     * This event is fired just during biome generator initialization in
     * WorldChunkManager#WorldChunkManager(long, WorldType). <br>
     * <br>
     * {@link #seed} the seed of the world. <br>
     * {@link #originalBiomeGens} the array of GenLayers original intended for this Biome generation. <br>
     * {@link #newBiomeGens} the array of GenLayers that will now be used for this Biome generation. <br>
     * If {@link #newBiomeGens} is set to a new value, that value will be used for the Biome generator. <br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#TERRAIN_GEN_BUS}.<br>
     **/
    public static class InitBiomeGens extends WorldTypeEvent
    {
        private final long seed;
        private final GenLayer[] originalBiomeGens;
        private GenLayer[] newBiomeGens;

        public InitBiomeGens(WorldType worldType, long seed, GenLayer[] original)
        {
            super(worldType);
            this.seed = seed;
            originalBiomeGens = original;
            setNewBiomeGens(original.clone());
        }

        public long getSeed()
        {
            return seed;
        }

        public GenLayer[] getOriginalBiomeGens()
        {
            return originalBiomeGens;
        }

        public GenLayer[] getNewBiomeGens()
        {
            return newBiomeGens;
        }

        public void setNewBiomeGens(GenLayer[] newBiomeGens)
        {
            this.newBiomeGens = newBiomeGens;
        }
    }
}
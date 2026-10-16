package com.gtnewhorizons.angelica.compat.cubicchunks;

import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import com.cardinalstar.cubicchunks.api.IColumn;
import com.cardinalstar.cubicchunks.api.ICube;
import com.cardinalstar.cubicchunks.world.ICubicWorld;
import com.cardinalstar.cubicchunks.world.cube.Cube;
import com.cardinalstar.cubicchunks.world.cube.ICubeProviderInternal;
import com.gtnewhorizons.angelica.compat.mojang.ChunkSectionPos;

public class CubicChunksAPI {

    /** Fixed cubic render range: Y -1024 inclusive through +1024 exclusive, expressed in 16-block sections. */
    public static final int MIN_RENDER_SECTION_Y = -64;
    public static final int MAX_RENDER_SECTION_Y_EXCLUSIVE = 64;

    public static ExtendedBlockStorage getCubeStorage(World world, int x, int y, int z) {
        Cube cube = ((ICubeProviderInternal) world.getChunkProvider()).getLoadedCube(x, y, z);

        if (cube == null || cube.isEmpty()) return null;

        return cube.getStorage();
    }

    public static int[] getLoadedCubeLevelsInColumn(World world, int chunkX, int chunkZ) {
        IColumn column = (IColumn) world.getChunkFromChunkCoords(chunkX, chunkZ);

        return column.getLoadedCubes().stream().mapToInt(ICube::getY).toArray();
    }

    public static int getMinHeight(World world) {
        return ((ICubicWorld) world).getMinHeight();
    }

    public static int getMaxHeight(World world) {
        return ((ICubicWorld) world).getMaxHeight();
    }

    public static int getMinSectionY(World world) {
        return ChunkSectionPos.getSectionCoord(getMinHeight(world));
    }

    public static int getMaxSectionYExclusive(World world) {
        return ChunkSectionPos.getSectionCoord(getMaxHeight(world) - 1) + 1;
    }

    public static int getMinRenderSectionY(World world) {
        return Math.max(getMinSectionY(world), MIN_RENDER_SECTION_Y);
    }

    public static int getMaxRenderSectionYExclusive(World world) {
        return Math.min(getMaxSectionYExclusive(world), MAX_RENDER_SECTION_Y_EXCLUSIVE);
    }

    public static int getMinRenderHeight(World world) {
        return getMinRenderSectionY(world) << 4;
    }

    public static int getMaxRenderHeight(World world) {
        return getMaxRenderSectionYExclusive(world) << 4;
    }
}

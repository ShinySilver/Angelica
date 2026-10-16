package com.gtnewhorizons.angelica.rendering.celeritas;

import org.embeddedt.embeddium.impl.render.chunk.RenderSectionManager;
import org.embeddedt.embeddium.impl.render.chunk.map.ChunkTracker;

import com.cardinalstar.cubicchunks.util.HashMap3D;
import com.cardinalstar.cubicchunks.util.HashSet3D;

/** Tracks 3D cubes instead of columns; the inherited 2D state stays empty. */
public class CubeStatusTracker extends ChunkTracker {

    /// Cubes that have been synced to the client and are currently loaded.
    private final HashSet3D loadedCubes = new HashSet3D();
    /// Cubes that have been attached to the render section manager.
    private final HashSet3D attachedCubes = new HashSet3D();

    private final HashMap3D<PendingState> changedCubes = new HashMap3D<>();

    private enum PendingState {
        Add,
        Remove
    }

    public void onCubeLoaded(int cubeX, int cubeY, int cubeZ) {
        if (loadedCubes.add(cubeX, cubeY, cubeZ)) {
            pollCube(cubeX, cubeY, cubeZ);
        }
    }

    public void onCubeUnloaded(int cubeX, int cubeY, int cubeZ) {
        if (loadedCubes.remove(cubeX, cubeY, cubeZ)) {
            pollCube(cubeX, cubeY, cubeZ);
        }
    }

    private void pollCube(int cubeX, int cubeY, int cubeZ) {
        boolean renderable = loadedCubes.contains(cubeX, cubeY, cubeZ);
        boolean isRendering = attachedCubes.contains(cubeX, cubeY, cubeZ);

        if (renderable != isRendering) {
            changedCubes.put(cubeX, cubeY, cubeZ, renderable ? PendingState.Add : PendingState.Remove);
        } else {
            changedCubes.remove(cubeX, cubeY, cubeZ);
        }
    }

    @Override
    public void onChunkStatusAdded(int i, int i1, int i2) {
        // do nothing
    }

    @Override
    public void onChunkStatusRemoved(int i, int i1, int i2) {
        // do nothing
    }

    public void forEachReady(RenderSectionManager renderSectionManager) {
        changedCubes.forEach((x, y, z, state) -> {
            boolean ready = state == PendingState.Add;

            if (ready) {
                attachedCubes.add(x, y, z);
            } else {
                attachedCubes.remove(x, y, z);
            }
        });

        changedCubes.clear();

        for (var pos : attachedCubes.fastEntryIterable()) {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();

            renderSectionManager.onSectionAdded(x, y, z);
        }
    }

    public void forEachEvent(RenderSectionManager renderSectionManager) {
        changedCubes.forEach((x, y, z, state) -> {
            boolean ready = state == PendingState.Add;

            if (ready) {
                attachedCubes.add(x, y, z);
                renderSectionManager.onSectionAdded(x, y, z);
            } else {
                attachedCubes.remove(x, y, z);
                renderSectionManager.onSectionRemoved(x, y, z);
            }
        });

        changedCubes.clear();
    }
}

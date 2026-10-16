package com.gtnewhorizons.angelica.mixins.early.celeritas.terrain;

import org.embeddedt.embeddium.impl.render.chunk.occlusion.OcclusionCuller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** CubicChunks' sparse section lattice is not compatible with path-dependent frustum aperture clamping. */
@Mixin(value = OcclusionCuller.class, remap = false)
public class MixinOcclusionCuller_CubicChunks {

    @ModifyVariable(method = "findVisible", at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private boolean angelica$disableFrustumClamping(boolean allowFrustumClamping) {
        return false;
    }
}

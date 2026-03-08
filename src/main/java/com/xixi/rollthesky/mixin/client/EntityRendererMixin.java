package com.xixi.rollthesky.mixin.client;

import com.xixi.rollthesky.roll.RollRuntime;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
    @Group(name = "rollTheSky_turn0", min = 1, max = 1)
    @Redirect(
            method = {"updateCameraAndRender", "func_181560_a"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/entity/EntityPlayerSP;turn(FF)V",
                    ordinal = 0
            ),
            remap = false,
            require = 0
    )
    private void rollTheSky$turn0(EntityPlayerSP player, float yaw, float pitch) {
        RollRuntime.handleMouseTurn(player, yaw, pitch);
    }

    @Group(name = "rollTheSky_turn0", min = 1, max = 1)
    @Redirect(
            method = {"updateCameraAndRender", "func_181560_a"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/entity/EntityPlayerSP;func_70082_c(FF)V",
                    ordinal = 0
            ),
            remap = false,
            require = 0
    )
    private void rollTheSky$turn0Srg(EntityPlayerSP player, float yaw, float pitch) {
        RollRuntime.handleMouseTurn(player, yaw, pitch);
    }

    @Group(name = "rollTheSky_turn1", min = 1, max = 1)
    @Redirect(
            method = {"updateCameraAndRender", "func_181560_a"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/entity/EntityPlayerSP;turn(FF)V",
                    ordinal = 1
            ),
            remap = false,
            require = 0
    )
    private void rollTheSky$turn1(EntityPlayerSP player, float yaw, float pitch) {
        RollRuntime.handleMouseTurn(player, yaw, pitch);
    }

    @Group(name = "rollTheSky_turn1", min = 1, max = 1)
    @Redirect(
            method = {"updateCameraAndRender", "func_181560_a"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/entity/EntityPlayerSP;func_70082_c(FF)V",
                    ordinal = 1
            ),
            remap = false,
            require = 0
    )
    private void rollTheSky$turn1Srg(EntityPlayerSP player, float yaw, float pitch) {
        RollRuntime.handleMouseTurn(player, yaw, pitch);
    }
}

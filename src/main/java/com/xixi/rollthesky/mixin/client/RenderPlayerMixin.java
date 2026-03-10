package com.xixi.rollthesky.mixin.client;

import com.xixi.rollthesky.roll.PlayerRenderOrientation;
import com.xixi.rollthesky.roll.RollRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderPlayer.class)
public abstract class RenderPlayerMixin {
    @Unique
    private boolean rollTheSky$renderPoseOverridden;

    @Unique
    private float rollTheSky$prevRenderYawOffset;

    @Unique
    private float rollTheSky$renderYawOffset;

    @Unique
    private float rollTheSky$prevRotationYawHead;

    @Unique
    private float rollTheSky$rotationYawHead;

    @Inject(method = "doRender(Lnet/minecraft/client/entity/AbstractClientPlayer;DDDFF)V", at = @At("HEAD"))
    private void rollTheSky$alignRenderPose(AbstractClientPlayer entity, double x, double y, double z,
                                            float entityYaw, float partialTicks, CallbackInfo ci) {
        if (!rollTheSky$shouldUseCameraAlignedRender(entity)) {
            return;
        }

        rollTheSky$renderPoseOverridden = true;
        rollTheSky$prevRenderYawOffset = entity.prevRenderYawOffset;
        rollTheSky$renderYawOffset = entity.renderYawOffset;
        rollTheSky$prevRotationYawHead = entity.prevRotationYawHead;
        rollTheSky$rotationYawHead = entity.rotationYawHead;

        entity.prevRenderYawOffset = entity.prevRotationYaw;
        entity.renderYawOffset = entity.rotationYaw;
        entity.prevRotationYawHead = entity.prevRotationYaw;
        entity.rotationYawHead = entity.rotationYaw;
    }

    @Inject(method = "doRender(Lnet/minecraft/client/entity/AbstractClientPlayer;DDDFF)V", at = @At("TAIL"))
    private void rollTheSky$restoreRenderPose(AbstractClientPlayer entity, double x, double y, double z,
                                              float entityYaw, float partialTicks, CallbackInfo ci) {
        if (!rollTheSky$renderPoseOverridden) {
            return;
        }

        entity.prevRenderYawOffset = rollTheSky$prevRenderYawOffset;
        entity.renderYawOffset = rollTheSky$renderYawOffset;
        entity.prevRotationYawHead = rollTheSky$prevRotationYawHead;
        entity.rotationYawHead = rollTheSky$rotationYawHead;
        rollTheSky$renderPoseOverridden = false;
    }

    @Inject(method = "applyRotations(Lnet/minecraft/client/entity/AbstractClientPlayer;FFF)V", at = @At("HEAD"), cancellable = true)
    private void rollTheSky$applyCameraAlignedRotations(AbstractClientPlayer entityLiving, float ageInTicks,
                                                        float rotationYaw, float partialTicks, CallbackInfo ci) {
        if (!rollTheSky$shouldUseCameraAlignedRender(entityLiving)) {
            return;
        }

        PlayerRenderOrientation.Orientation orientation = PlayerRenderOrientation.resolve(
                entityLiving.prevRenderYawOffset,
                entityLiving.renderYawOffset,
                entityLiving.prevRotationYaw,
                entityLiving.rotationYaw,
                entityLiving.prevRotationPitch,
                entityLiving.rotationPitch,
                entityLiving.getTicksElytraFlying(),
                RollRuntime.getVisualRoll(partialTicks),
                partialTicks
        );

        GlStateManager.rotate(orientation.getBodyYawRotation(), 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(orientation.getFlightPitchRotation(), 1.0f, 0.0f, 0.0f);

        float visualRoll = orientation.getVisualRollRotation();
        float modelRoll = PlayerRenderOrientation.resolveModelRollRotation(visualRoll);
        if (modelRoll != 0.0f) {
            if (PlayerRenderOrientation.getModelRollAxis() == PlayerRenderOrientation.ModelRollAxis.BODY_Y) {
                GlStateManager.rotate(modelRoll, 0.0f, 1.0f, 0.0f);
            }
        }

        ci.cancel();
    }

    @Unique
    private boolean rollTheSky$shouldUseCameraAlignedRender(AbstractClientPlayer entityLiving) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        return player != null && entityLiving == player && RollRuntime.shouldRoll(player);
    }
}

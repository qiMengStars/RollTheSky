package com.xixi.rollthesky.mixin.client;

import com.xixi.rollthesky.roll.ConfigHandler;
import com.xixi.rollthesky.roll.RollRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderPlayer.class)
public abstract class RenderPlayerMixin {
    @Inject(method = "applyRotations(Lnet/minecraft/client/entity/AbstractClientPlayer;FFF)V", at = @At("TAIL"))
    private void rollTheSky$applyVisualRoll(AbstractClientPlayer entityLiving, float ageInTicks, float rotationYaw,
                                            float partialTicks, CallbackInfo ci) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null || entityLiving != player) {
            return;
        }
        if (!RollRuntime.shouldRoll(player)) {
            return;
        }

        float roll = RollRuntime.STATE.getRoll(partialTicks);
        if (ConfigHandler.invertVisualRoll) {
            roll = -roll;
        }
        if (roll != 0.0f) {
            GlStateManager.rotate(roll, 0.0f, 0.0f, 1.0f);
        }
    }
}

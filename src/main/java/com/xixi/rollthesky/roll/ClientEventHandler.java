package com.xixi.rollthesky.roll;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientEventHandler {
    private EntityPlayerSP lastPlayer;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            RollRuntime.STATE.updatePrev();
            return;
        }
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.player;
        if (player == null) {
            lastPlayer = null;
            RollRuntime.resetMouseState();
            return;
        }

        if (player != lastPlayer) {
            lastPlayer = player;
            RollRuntime.STATE.setRoll(0.0f);
            RollRuntime.STATE.setRollBack(0.0f);
            RollRuntime.STATE.setRolling(false);
            RollRuntime.clearSmoothers();
            RollRuntime.resetMouseState();
        }

        boolean rolling = RollRuntime.shouldRoll(player);
        RollRuntime.STATE.setRolling(rolling);

        if (!rolling) {
            float rollBack = RollRuntime.STATE.getRollBack(1.0f);
            rollBack *= ConfigHandler.rollReturnDamping;
            if (Math.abs(rollBack) < 0.01f) {
                rollBack = 0.0f;
            }
            RollRuntime.STATE.setRollBack(rollBack);
            RollRuntime.STATE.setRoll(0.0f);
            RollRuntime.clearSmoothers();
            RollRuntime.resetMouseState();
        }
    }

    @SubscribeEvent
    public void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.player;
        if (player == null) {
            return;
        }

        float partial = (float) event.getRenderPartialTicks();
        float roll;
        if (RollRuntime.shouldRoll(player)) {
            roll = -RollRuntime.STATE.getRoll(partial);
        } else {
            roll = -RollRuntime.STATE.getRollBack(partial);
        }

        if (ConfigHandler.invertVisualRoll) {
            roll = -roll;
        }
        event.setRoll(roll);
    }

    @SubscribeEvent
    public void onInputUpdate(InputUpdateEvent event) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null || !RollRuntime.shouldRoll(player)) {
            return;
        }
        // A/D are used for yaw by default; suppress strafing to avoid double-effect.
        event.getMovementInput().moveStrafe = 0.0f;
    }
}

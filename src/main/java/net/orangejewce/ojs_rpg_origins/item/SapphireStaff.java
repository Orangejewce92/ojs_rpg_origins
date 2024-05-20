package net.orangejewce.ojs_rpg_origins.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SapphireStaff extends Item {
    private static final int COOLDOWN_TICKS = 20 * 2; // 2 seconds cooldown
    private static final int MAX_CHARGE_TIME = 40; // 2 seconds for max charge

    public SapphireStaff(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity user, int timeLeft) {
        if (user instanceof Player) {
            Player player = (Player) user;
            int charge = this.getUseDuration(stack) - timeLeft;
            if (charge < 0 || player.getCooldowns().isOnCooldown(this)) {
                return;
            }

            float chargeRatio = Math.min((float) charge / MAX_CHARGE_TIME, 1.0f);
            double accelMultiplier = 0.1 * chargeRatio;
            double accelX = player.getLookAngle().x * accelMultiplier;
            double accelY = player.getLookAngle().y * accelMultiplier;
            double accelZ = player.getLookAngle().z * accelMultiplier;

            int explosionPower = Math.round(1 * chargeRatio); // Scales with charge time

            LargeFireball fireball = new LargeFireball(world, player, accelX, accelY, accelZ, explosionPower);
            fireball.setPos(player.getX() + player.getLookAngle().x * 1.5, player.getY(0.5D) + 0.5D, player.getZ() + player.getLookAngle().z * 1.5);

            world.addFreshEntity(fireball);

            if (!world.isClientSide) {
                for (int i = 0; i < 10; i++) {
                    double offsetX = (world.random.nextDouble() - 0.5) * 2.0;
                    double offsetY = (world.random.nextDouble() - 0.5) * 2.0;
                    double offsetZ = (world.random.nextDouble() - 0.5) * 2.0;
                    world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, player.getX() + offsetX, player.getY() + offsetY + 1.0, player.getZ() + offsetZ, 0, 0, 0);
                }
            }

            world.playSound(null, player.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);
            player.awardStat(Stats.ITEM_USED.get(this));
            stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));

            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000; // Allows charging for up to 72000 ticks (same as bows)
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW; // Display bow animation while charging
    }
    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("tooltip.staff").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00)).withBold(true)));
        // Additional information
        pTooltipComponents.add(Component.translatable("tooltip.infostaff")
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF)).withItalic(true)));

        // Unicode icon example
        pTooltipComponents.add(Component.translatable("tooltip.ability")
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFD700))));

        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}

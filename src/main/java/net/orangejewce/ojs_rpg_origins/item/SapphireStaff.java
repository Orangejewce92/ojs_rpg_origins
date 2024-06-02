package net.orangejewce.ojs_rpg_origins.item;

import net.minecraft.client.gui.screens.Screen;
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
import net.minecraft.world.damagesource.DamageSource;
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
    private static final int COOLDOWN_TICKS = 20 * 1; // 1 second cooldown
    private static final int MAX_CHARGE_TIME = 40; // 2 seconds for max charge
    private static final int USE_DURATION = 72000; // Max use duration (same as bows)
    private static final int PARTICLE_COUNT = 10; // Number of particles to spawn
    private static final float LIGHT_ATTACK_DAMAGE = 3.0f; // Light attack damage

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

            int explosionPower = Math.round(1 * chargeRatio);

            LargeFireball fireball = new LargeFireball(world, player, accelX, accelY, accelZ, explosionPower);
            fireball.setPos(player.getX() + player.getLookAngle().x * 1.5, player.getY(0.5D) + 0.5D, player.getZ() + player.getLookAngle().z * 1.5);

            world.addFreshEntity(fireball);

            if (!world.isClientSide) {
                for (int i = 0; i < PARTICLE_COUNT; i++) {
                    double offsetX = (world.random.nextDouble() - 0.5) * 2.0;
                    double offsetY = (world.random.nextDouble() - 0.5) * 2.0;
                    double offsetZ = (world.random.nextDouble() - 0.5) * 2.0;
                    world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, player.getX() + offsetX, player.getY() + offsetY + 1.0, player.getZ() + offsetZ, 0, 0, 0);
                }
            }

            world.playSound(null, player.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);
            player.awardStat(Stats.ITEM_USED.get(this));
            stack.hurtAndBreak(2, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand())); // Increased durability loss on right click

            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return USE_DURATION; // Allows charging for up to 72000 ticks (same as bows)
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW; // Display bow animation while charging
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("tooltip.balmung.details")
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00)).withItalic(true)));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.staff").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00)).withBold(true)));
            tooltipComponents.add(Component.translatable("tooltip.info_staff").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF)).withItalic(true)));
            tooltipComponents.add(Component.translatable("tooltip.ability").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFD700))));
            tooltipComponents.add(Component.translatable("tooltip.special_ability").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFF4500)).withItalic(true)));
            tooltipComponents.add(Component.translatable("tooltip.shift_info")
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF)).withItalic(true)));
            super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        }
    }
    @Override
    public void onUseTick(Level world, LivingEntity user, ItemStack stack, int count) {
        if (user instanceof Player player && world.isClientSide) {
            double d0 = player.getX() + player.getLookAngle().x * 1.2;
            double d1 = player.getY() + player.getEyeHeight() - 0.2;
            double d2 = player.getZ() + player.getLookAngle().z * 1.2;
            Vec3 motion = new Vec3(player.getRandom().nextDouble() - 0.5, player.getRandom().nextDouble() - 0.5, player.getRandom().nextDouble() - 0.5);
            world.addParticle(ParticleTypes.ENCHANT, d0, d1, d2, motion.x, motion.y, motion.z);
            if (count % 10 == 0) {
                world.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.BEACON_POWER_SELECT, SoundSource.PLAYERS, 0.5F, 1.0F + (count / 100.0F), false);
                stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand())); // Additional durability loss on hold
            }
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player player) {
            target.hurt(player.damageSources().playerAttack(player), LIGHT_ATTACK_DAMAGE);
            stack.hurtAndBreak(1, attacker, (entity) -> entity.broadcastBreakEvent(attacker.getUsedItemHand()));
            return super.hurtEnemy(stack, target, attacker);
        }
        return false;
    }
    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return repair.getItem() == ModItems.SAPPHIRE_RARE.get() || super.isValidRepairItem(toRepair, repair);
    }
}

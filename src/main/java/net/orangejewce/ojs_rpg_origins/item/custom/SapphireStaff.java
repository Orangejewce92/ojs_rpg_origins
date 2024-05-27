package net.orangejewce.ojs_rpg_origins.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SapphireStaff extends Item {
    private static final int COOLDOWN_TICKS = 20; // 1 second cooldown
    private static final int MAX_CHARGE_TIME = 40; // 2 seconds for max charge
    private static final int USE_DURATION = 72000; // Max use duration (same as bows)
    private static final int PARTICLE_COUNT = 10; // Number of particles to spawn
    private static final float LIGHT_ATTACK_DAMAGE = 3.0f; // Light attack damage

    public SapphireStaff(Rarity epic, Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        player.setCurrentHand(hand);
        return TypedActionResult.consume(stack);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int timeLeft) {
        if (user instanceof PlayerEntity player) {
            int charge = this.getMaxUseTime(stack) - timeLeft;
            if (charge < 0 || player.getItemCooldownManager().isCoolingDown(this)) {
                return;
            }

            float chargeRatio = Math.min((float) charge / MAX_CHARGE_TIME, 1.0f);
            double accelMultiplier = 0.1 * chargeRatio;
            Vec3d lookVec = player.getRotationVec(1.0F);
            double accelX = lookVec.x * accelMultiplier;
            double accelY = lookVec.y * accelMultiplier;
            double accelZ = lookVec.z * accelMultiplier;

            int explosionPower = Math.round(1 * chargeRatio);

            FireballEntity fireball = new FireballEntity(world, player, accelX, accelY, accelZ, explosionPower);
            fireball.setPos(player.getX() + lookVec.x * 1.5, player.getBodyY(0.5D) + 0.5D, player.getZ() + lookVec.z * 1.5);

            world.spawnEntity(fireball);

            if (!world.isClient) {
                for (int i = 0; i < PARTICLE_COUNT; i++) {
                    double offsetX = (world.random.nextDouble() - 0.5) * 2.0;
                    double offsetY = (world.random.nextDouble() - 0.5) * 2.0;
                    double offsetZ = (world.random.nextDouble() - 0.5) * 2.0;
                    world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, player.getX() + offsetX, player.getY() + offsetY + 1.0, player.getZ() + offsetZ, 0, 0, 0);
                }
            }

            world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F);
            player.incrementStat(Stats.USED.getOrCreateStat(this));
            stack.damage(2, player, (p) -> p.sendToolBreakStatus(player.getActiveHand())); // Increased durability loss on right click

            player.getItemCooldownManager().set(this, COOLDOWN_TICKS);
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return USE_DURATION; // Allows charging for up to 72000 ticks (same as bows)
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW; // Display bow animation while charging
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("tooltip.staff").setStyle(Style.EMPTY.withColor(Formatting.GREEN).withBold(true)));
        tooltip.add(Text.translatable("tooltip.info_staff").setStyle(Style.EMPTY.withColor(Formatting.WHITE).withItalic(true)));
        tooltip.add(Text.translatable("tooltip.ability").setStyle(Style.EMPTY.withColor(Formatting.GOLD)));
        tooltip.add(Text.translatable("tooltip.special_ability").setStyle(Style.EMPTY.withColor(Formatting.RED).withItalic(true)));
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int count) {
        if (user instanceof PlayerEntity player && world.isClient) {
            Vec3d lookVec = player.getRotationVec(1.0F);
            double d0 = player.getX() + lookVec.x * 1.2;
            double d1 = player.getEyeY() - 0.2;
            double d2 = player.getZ() + lookVec.z * 1.2;
            Vec3d motion = new Vec3d(player.getRandom().nextDouble() - 0.5, player.getRandom().nextDouble() - 0.5, player.getRandom().nextDouble() - 0.5);
            world.addParticle(ParticleTypes.ENCHANT, d0, d1, d2, motion.x, motion.y, motion.z);
            if (count % 10 == 0) {
                world.playSound(player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_BEACON_POWER_SELECT, SoundCategory.PLAYERS, 0.5F, 1.0F + (count / 100.0F), false);
                stack.damage(1, player, (p) -> p.sendToolBreakStatus(player.getActiveHand())); // Additional durability loss on hold
            }
        }
    }

//    @Override
//    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
//        if (attacker instanceof PlayerEntity player) {
//            target.damage(DamageSource.player(player), LIGHT_ATTACK_DAMAGE);
//            stack.damage(1, player, (entity) -> entity.sendToolBreakStatus(player.getActiveHand()));
//            return true;
//        }
//        return false;
//    }
}
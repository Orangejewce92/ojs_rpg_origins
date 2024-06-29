package net.orangejewce.ojs_rpg_origins.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = "ojs_rpg_origins")
public class HarmonicLuteItem extends Item {
    private static final int COOLDOWN_TICKS = 600; // Set your cooldown duration here

    public HarmonicLuteItem(Properties rarity) {
        super(new Item.Properties().stacksTo(1).durability(250));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            if (!player.getCooldowns().isOnCooldown(this)) {
                playMusic(level, player);
                itemstack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
            }
        } else {
            if (player.isCrouching()) {
                player.playSound(SoundEvents.NOTE_BLOCK_BANJO.get(), 1.0F, 1.0F);
            } else {
                player.playSound(SoundEvents.NOTE_BLOCK_GUITAR.get(), 1.0F, 1.0F);
            }
        }
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    private void playMusic(Level level, Player player) {
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, new AABB(player.blockPosition()).inflate(10));
        for (LivingEntity entity : entities) {
            if (entity instanceof Player && entity != player) {
                applyPassiveEffects((Player) entity);
            }
        }

        if (player.isCrouching()) {
            applyShieldingSong(player);
        } else {
            applySongOfInvulnerability(player);
        }
    }

    private void applyPassiveEffects(Player player) {
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0, true, true));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 0, true, true));
    }

    private void applyShieldingSong(Player player) {
        List<LivingEntity> entities = player.level().getEntitiesOfClass(LivingEntity.class, new AABB(player.blockPosition()).inflate(10));
        for (LivingEntity entity : entities) {
            if (entity instanceof Player) {
                entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 600, 1, true, true));
            }
        }
    }

    private void applySongOfInvulnerability(Player player) {
        List<LivingEntity> entities = player.level().getEntitiesOfClass(LivingEntity.class, new AABB(player.blockPosition()).inflate(5));
        for (LivingEntity entity : entities) {
            if (entity instanceof Player) {
                entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 3, true, true));
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.harmonic_lute").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00)).withBold(true)));
        tooltip.add(Component.translatable("tooltip.info_harmonic_lute").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF)).withItalic(true)));
        tooltip.add(Component.translatable("tooltip.passive_harmonic_lute").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFD700))));
        tooltip.add(Component.translatable("tooltip.active_harmonic_lute").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFF4500)).withItalic(true)));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (entity instanceof Player player && selected) {
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, new AABB(player.blockPosition()).inflate(10));
            for (LivingEntity e : entities) {
                if (e instanceof Player) {
                    applyPassiveEffects((Player) e);
                }
            }
        }
    }
}

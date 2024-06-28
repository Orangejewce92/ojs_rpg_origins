package net.orangejewce.ojs_rpg_origins.item.custom;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Targeter;
import net.minecraft.entity.ai.brain.task.LongJumpTask;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.orangejewce.ojs_rpg_origins.item.ModToolMaterial;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Target;
import java.util.List;

public class SapphireMiseryItem extends SwordItem {

    private static final int COOLDOWN = 200; // 10 seconds cooldown
    private static final int RESISTANCE_DURATION = 210; // Duration for resistance effect

    public SapphireMiseryItem() {
        super(ModToolMaterial.SAPPHIRE, 8, -3.0F, new Item.Settings().rarity(Rarity.EPIC));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (!world.isClient && !player.getItemCooldownManager().isCoolingDown(this)) {
            player.sendMessage(Text.literal("Ability Activated!"), true);
            player.heal(4.0F);
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, RESISTANCE_DURATION, 0, true, false, false));
            world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_RAVAGER_ROAR, SoundCategory.PLAYERS, 1.0F, 1.0F);
            player.getItemCooldownManager().set(this, COOLDOWN);
        }
        return TypedActionResult.success(itemStack);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) attacker;
            target.damage(target.getDamageSources().playerAttack(player), target.getHealth() * 2);
            stack.damage(1, player, (p) -> p.sendToolBreakStatus(attacker.getActiveHand()));
            player.incrementStat(Stats.USED.getOrCreateStat(this));
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("tooltip.balmung.details")
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00)).withItalic(true)));
        } else {
            tooltip.add(Text.translatable("tooltip.sapphire_misery").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00)).withBold(true)));
            tooltip.add(Text.translatable("tooltip.info_sapphire_misery").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF)).withItalic(true)));
            tooltip.add(Text.translatable("tooltip.ability_1").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFD700))));
            tooltip.add(Text.translatable("tooltip.special_ability_2").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFF4500)).withItalic(true)));
            tooltip.add(Text.translatable("tooltip.shift_info")
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF)).withItalic(true)));
            super.appendTooltip(stack, world, tooltip, context);
        }
    }

    public static void tickPlayer(PlayerEntity player) {
        if (player.getMainHandStack().getItem() instanceof SapphireMiseryItem) {
            if (player.age % 200 == 0) {
                player.heal(1.0F);
            }
        }
    }

    public static void onLivingHurt(LivingEntity entity, DamageSource source, float amount) {
        if (entity instanceof PlayerEntity player) {
            if (player.getMainHandStack().getItem() instanceof SapphireMiseryItem) {
                entity.damage(source, amount * 0.9F);
            }
        }
    }
}

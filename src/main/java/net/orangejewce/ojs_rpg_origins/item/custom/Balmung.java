package net.orangejewce.ojs_rpg_origins.item.custom;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.orangejewce.ojs_rpg_origins.item.ModItems;
import net.orangejewce.ojs_rpg_origins.item.ModToolMaterial;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

public class Balmung extends SwordItem {
    public Balmung() {
        super(ModToolMaterial.SAPPHIRE, 10, -2.4F, new Item.Settings().maxCount(1).rarity(Rarity.EPIC));
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity player) {
            World world = attacker.getWorld();
            BlockPos pos = target.getBlockPos();
            Box areaOfEffect = new Box(pos).expand(3);

            List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, areaOfEffect, entity -> entity != attacker && entity != target);
            for (LivingEntity entity : entities) {
                entity.damage(target.getDamageSources().playerAttack(player), 5.0F);
            }

            world.playSound(null, pos, SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }
        return super.postHit(stack, target, attacker);
    }

    public TypedActionResult<ItemStack> useOnEntity(World world, PlayerEntity player, LivingEntity target, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (target instanceof LivingEntity) {
            target.setOnFireFor(5);
            player.getItemCooldownManager().set(this, 100);
            world.playSound(null, target.getBlockPos(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 1.0F, 1.0F);
            return TypedActionResult.success(stack, world.isClient());
        }
        return TypedActionResult.pass(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("tooltip.balmung.details")
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00)).withItalic(true)));
        } else {
            tooltip.add(Text.translatable("tooltip.balmung")
                    .styled(style -> style.withColor(TextColor.fromRgb(0xFFD700)).withBold(true)));
            tooltip.add(Text.translatable("tooltip.info_balmung")
                    .styled(style -> style.withColor(TextColor.fromRgb(0xFFFFFF)).withItalic(true)));
            tooltip.add(Text.translatable("tooltip.special_attack")
                    .styled(style -> style.withColor(TextColor.fromRgb(0x00FF00))));
            super.appendTooltip(stack, world, tooltip, context);
            tooltip.add(Text.translatable("tooltip.shift_info")
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF)).withItalic(true)));
        }
    }
}

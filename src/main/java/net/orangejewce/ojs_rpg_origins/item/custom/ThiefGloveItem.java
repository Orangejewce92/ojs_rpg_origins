package net.orangejewce.ojs_rpg_origins.item.custom;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.orangejewce.ojs_rpg_origins.config.ThiefGloveConfig;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class ThiefGloveItem extends Item {
    public ThiefGloveItem() {
        super(new Item.Settings().maxCount(1).maxDamage(50).rarity(Rarity.EPIC));
    }

    public ActionResult interactMob(PlayerEntity player, LivingEntity target, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (!player.getWorld().isClient && target instanceof net.minecraft.entity.passive.VillagerEntity) {
            interactWithVillager(itemStack, player, target, hand);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    private void interactWithVillager(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand) {
        if (!player.getWorld().isClient) {
            if (player.getItemCooldownManager().isCoolingDown(this)) {
                return; // If on cooldown, do nothing
            }

            ThiefGloveConfig config = ThiefGloveConfig.getInstance();
            if (new Random().nextDouble() < config.stealChance) {
                List<ItemStack> possibleItems = ThiefGloveConfig.getStealableItems();
                ItemStack stolenItem = possibleItems.get(new Random().nextInt(possibleItems.size())).copy();
                player.getInventory().insertStack(stolenItem);
                player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.0F, 1.0F);
                stack.damage(1, player, (p) -> p.sendToolBreakStatus(hand)); // Reduce durability by 1 on success
                player.getItemCooldownManager().set(this, config.cooldownTicks); // Add cooldown
            } else {
                player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_VILLAGER_NO, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                player.getItemCooldownManager().set(this, config.cooldownTicks); // Add cooldown even if the steal fails
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("tooltip.thief_glove.details")
                    .styled(style -> style.withColor(TextColor.fromRgb(0x00FF00)).withItalic(true)));
        } else {
            tooltip.add(Text.translatable("tooltip.thief_glove")
                    .styled(style -> style.withColor(TextColor.fromRgb(0x00FF00)).withBold(true)));
            tooltip.add(Text.translatable("tooltip.info_thief_glove")
                    .styled(style -> style.withColor(TextColor.fromRgb(0xFFFFFF)).withItalic(true)));
            tooltip.add(Text.translatable("tooltip.steal_ability")
                    .styled(style -> style.withColor(TextColor.fromRgb(0xFFD700))));
            tooltip.add(Text.translatable("tooltip.steal_shift_right_click")
                    .styled(style -> style.withColor(TextColor.fromRgb(0xFF4500)).withItalic(true)));
            tooltip.add(Text.translatable("tooltip.shift_info")
                    .styled(style -> style.withColor(TextColor.fromRgb(0xFFFFFF)).withItalic(true)));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }
}

package net.orangejewce.ojs_rpg_origins.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = "ojs_rpg_origins")
public class ThiefGloveItem extends Item {
    private static final double STEAL_CHANCE = 0.1; // 10% chance to steal an item
    private static final int COOLDOWN_TICKS = 100; // 30 seconds cooldown (20 ticks per second * 30 seconds)

    private static final ItemStack[] POSSIBLE_STOLEN_ITEMS = new ItemStack[]{
            new ItemStack(Items.DIAMOND),
            new ItemStack(Items.GOLD_INGOT),
            new ItemStack(Items.EMERALD),
            new ItemStack(Items.IRON_INGOT),
            new ItemStack(Items.BOOK),
            new ItemStack(ForgeRegistries.ITEMS.getValue(new net.minecraft.resources.ResourceLocation("ojs_rpg_origins:sapphire")))
    };

    public ThiefGloveItem(Properties properties) {
        super(new Item.Properties().stacksTo(1).durability(50)); // Example durability
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!player.level().isClientSide && player.isShiftKeyDown() && target instanceof Villager) {
            if (player.getCooldowns().isOnCooldown(this)) {
                return InteractionResult.PASS; // If on cooldown, pass the interaction
            }

            Villager villager = (Villager) target;
            if (new Random().nextDouble() < STEAL_CHANCE) {
                ItemStack stolenItem = POSSIBLE_STOLEN_ITEMS[new Random().nextInt(POSSIBLE_STOLEN_ITEMS.length)].copy();
                player.getInventory().add(stolenItem);
                player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0F, 1.0F);
                stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                player.getCooldowns().addCooldown(this, COOLDOWN_TICKS); // Add cooldown
                return InteractionResult.SUCCESS;
            } else {
                player.level().playSound(null, player.blockPosition(), SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL, 1.0F, 1.0F);
                player.getCooldowns().addCooldown(this, COOLDOWN_TICKS); // Add cooldown even if the steal fails
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.translatable("tooltip.thief_glove").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00)).withBold(true)));
        tooltipComponents.add(Component.translatable("tooltip.info_thief_glove")
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF)).withItalic(true)));
        tooltipComponents.add(Component.translatable("tooltip.steal_ability")
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFD700))));
        tooltipComponents.add(Component.translatable("tooltip.steal_shift_right_click")
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFFFFF)).withItalic(true)));
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
}

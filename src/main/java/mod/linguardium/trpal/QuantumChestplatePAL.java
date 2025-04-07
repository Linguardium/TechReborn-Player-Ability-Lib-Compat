package mod.linguardium.trpal;

import io.github.ladysnake.pal.AbilitySource;
import io.github.ladysnake.pal.Pal;
import io.github.ladysnake.pal.VanillaAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import reborncore.common.powerSystem.RcEnergyItem;
import techreborn.config.TechRebornConfig;

public class QuantumChestplatePAL {

    private static final Identifier QUANTUM_ARMOR_FLIGHT_ABILITY_SOURCE_ID = Identifier.of("techreborn", "quantum_armor");
    private static final AbilitySource TECHREBORN_QUANTUM_ARMOR_ABILITY_SOURCE = Pal.getAbilitySource(QUANTUM_ARMOR_FLIGHT_ABILITY_SOURCE_ID, AbilitySource.CONSUMABLE);

    public static void flightTick(boolean flightConfigEnabled, PlayerEntity player, ItemStack stack, RcEnergyItem item) {
        // PAL abilities are only valid on serverside
        if (!(player instanceof ServerPlayerEntity serverPlayer)) return;

        if (!flightConfigEnabled) {
            dontAllowFlying(serverPlayer);
            return;
        }

        // This check does not allow for exact energy match. This is copied as-is to match TechReborn's implementation
        // This mod is not intended to fix bugs in TR
        if (item.getStoredEnergy(stack) > TechRebornConfig.quantumSuitFlyingCost) {
            allowFlying(serverPlayer);
        } else {
            dontAllowFlying(serverPlayer);
        }

        if (!isCurrentlyFlying(serverPlayer)) return;
        if (!isAllowingFlight(serverPlayer)) return;

        if (hasIndirectFlight(serverPlayer)) return;

        item.tryUseEnergy(stack, TechRebornConfig.quantumSuitFlyingCost);

    }

    // remove flight allowance when unequipped
    public static void onUnequip(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            dontAllowFlying(serverPlayer);
        }
    }

    // checks if the Quantum Chestplate's Ability Source is allowing flight
    // checks if the Quantum Chestplate is the highest priority flight allowance granter
    public static boolean isAllowingFlight(ServerPlayerEntity player) {
        return TECHREBORN_QUANTUM_ARMOR_ABILITY_SOURCE.grants(player, VanillaAbilities.ALLOW_FLYING) && TECHREBORN_QUANTUM_ARMOR_ABILITY_SOURCE.isActivelyGranting(player, VanillaAbilities.ALLOW_FLYING);
    }

    // checks if the player has creative mode or is a spectator to attempt to verify if the player should fly for free
    public static boolean hasIndirectFlight(PlayerEntity player) {
        return player.isCreative() || player.isSpectator();
    }

    // Enable Quantum Chestplate Ability Source to allow flight
    public static void allowFlying(ServerPlayerEntity playerEntity) {
        TECHREBORN_QUANTUM_ARMOR_ABILITY_SOURCE.grantTo(playerEntity, VanillaAbilities.ALLOW_FLYING);
        playerEntity.setOnGround(true);
    }

    // Disable Quantum Chestplate Ability Source from allowing flight
    public static void dontAllowFlying(ServerPlayerEntity playerEntity) {
        TECHREBORN_QUANTUM_ARMOR_ABILITY_SOURCE.revokeFrom(playerEntity, VanillaAbilities.ALLOW_FLYING);
    }

    public static boolean isCurrentlyFlying(ServerPlayerEntity player) {
        return VanillaAbilities.FLYING.isEnabledFor(player);
    }
}

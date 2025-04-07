package mod.linguardium.trpal.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import reborncore.common.powerSystem.RcEnergyItem;
import techreborn.items.armor.QuantumSuitItem;

import static mod.linguardium.trpal.QuantumChestplatePAL.flightTick;
import static mod.linguardium.trpal.QuantumChestplatePAL.onUnequip;

@Mixin(QuantumSuitItem.class)
public abstract class QuantumSuitArmorMixin {

	@ModifyExpressionValue(
			method="tickArmor",
			at= @At(value = "FIELD",
					opcode = Opcodes.GETSTATIC,
					target = "Ltechreborn/config/TechRebornConfig;quantumSuitEnableFlight:Z",
					remap = false)
	)
	private boolean redirectFlightLogic(boolean flightConfigurationEnabled, ItemStack stack, PlayerEntity player) {
		if (stack.getItem() instanceof RcEnergyItem energyItem) {
			flightTick(flightConfigurationEnabled, player, stack, energyItem);
		}
		// disable original code
		return false;
	}

	@ModifyExpressionValue(
			method="onRemoved",
			at= @At(value = "FIELD",
					opcode = Opcodes.GETSTATIC,
					target = "Ltechreborn/config/TechRebornConfig;quantumSuitEnableFlight:Z",
					remap = false)
	)
	private boolean removeFlightAbilityOnUnequip(boolean quantumSuitEnableFlight, PlayerEntity player, @Local EquipmentSlot slot) {
		if (slot == EquipmentSlot.CHEST) {
			onUnequip(player);
		}
		// disable original code
		return false;
	}
}
package mod.linguardium.trpal.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.ladysnake.pal.AbilitySource;
import io.github.ladysnake.pal.Pal;
import io.github.ladysnake.pal.VanillaAbilities;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import techreborn.items.armor.QuantumSuitItem;

@Mixin(QuantumSuitItem.class)
public abstract class QuantumSuitArmorMixin extends ArmorItem {
	@Unique
	private static final Identifier QUANTUM_ARMOR_FLIGHT_ABILITY_SOURCE_ID = new Identifier("techreborn", "quantum_armor");
	@Unique
	private static final AbilitySource TECHREBORN_QUANTUM_ARMOR_ABILITY_SOURCE = Pal.getAbilitySource(QUANTUM_ARMOR_FLIGHT_ABILITY_SOURCE_ID, AbilitySource.RENEWABLE);

	public QuantumSuitArmorMixin(ArmorMaterial material, Type type, Settings settings) {
		super(material, type, settings);
	}

	@WrapOperation(
			method="tickArmor",
			at=@At(value= "FIELD", opcode = Opcodes.PUTFIELD ,target = "Lnet/minecraft/entity/player/PlayerAbilities;allowFlying:Z")
	)
	private void usePALForFlight(PlayerAbilities instance, boolean allow, Operation<Void> original, ItemStack stack, PlayerEntity playerEntity) {
		if (allow) {
			allowFlying(playerEntity);
		}else{
			dontAllowFlying(playerEntity);
		}
	}

	@Inject(method="onRemoved", at=@At("HEAD"))
	private void removeFlightAbilityOnUnequip(PlayerEntity playerEntity, CallbackInfo ci) {
		if (this.getSlotType() == EquipmentSlot.CHEST) {
			dontAllowFlying(playerEntity);
		}
	}

	@Unique
	private static void allowFlying(PlayerEntity playerEntity) {
		if (!playerEntity.getWorld().isClient()) {
			TECHREBORN_QUANTUM_ARMOR_ABILITY_SOURCE.grantTo(playerEntity, VanillaAbilities.ALLOW_FLYING);
		}
	}

	@Unique
	private static void dontAllowFlying(PlayerEntity playerEntity) {
		if (!playerEntity.getWorld().isClient()) {
			TECHREBORN_QUANTUM_ARMOR_ABILITY_SOURCE.revokeFrom(playerEntity, VanillaAbilities.ALLOW_FLYING);
		}
	}

}
package net.kr1v.unlockedmouse.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.platform.Window;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Window.class)
public class WindowMixin {
	@WrapMethod(method = "updateWindowMouseGrab")
	private void change(Operation<Void> original) {
	}
}

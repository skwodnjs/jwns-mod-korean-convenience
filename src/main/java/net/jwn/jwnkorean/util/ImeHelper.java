package net.jwn.jwnkorean.util;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFWNativeWin32;

public class ImeHelper {
    public interface Imm32Library extends StdCallLibrary {
        Imm32Library INSTANCE = Native.load("imm32", Imm32Library.class);

        Pointer ImmGetContext(Pointer hwnd);
        boolean ImmSetConversionStatus(Pointer himc, int conversion, int sentence);
        boolean ImmSetOpenStatus(Pointer himc, boolean open);
        boolean ImmReleaseContext(Pointer hwnd, Pointer himc);
    }

    public static void setMode(int mode) {
        try {
            var window = Minecraft.getInstance().getWindow();
            long glfwHandle = window.handle();

            long hwndAddress = GLFWNativeWin32.glfwGetWin32Window(glfwHandle);
            Pointer hwnd = new Pointer(hwndAddress);

            Pointer himc = Imm32Library.INSTANCE.ImmGetContext(hwnd);
            if (himc != null && !himc.equals(Pointer.NULL)) {
                Imm32Library.INSTANCE.ImmSetOpenStatus(himc, true);
                // 1: 한글 모드, 0: 영문 모드
                if (mode == 0 || mode == 1) {
                    Imm32Library.INSTANCE.ImmSetConversionStatus(himc, mode, 0);
                    Imm32Library.INSTANCE.ImmReleaseContext(hwnd, himc);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
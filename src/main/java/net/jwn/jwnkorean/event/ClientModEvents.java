package net.jwn.jwnkorean.event;

import net.jwn.jwnkorean.JWNsKoreanMod;
import net.jwn.jwnkorean.util.ImeHelper;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.resources.language.LanguageManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ScreenEvent;

import java.lang.reflect.Field;

@Mod(value = JWNsKoreanMod.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = JWNsKoreanMod.MOD_ID, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void openScreen(ScreenEvent.Init.Post event) {
        LanguageManager languageManager = Minecraft.getInstance().getLanguageManager();
        String currentLanguage = languageManager.getSelected();
        boolean isKorean = "ko_kr".equals(currentLanguage);

        if (event.getScreen() instanceof InventoryScreen) {
            if (isKorean) {
                if (Util.getPlatform() == Util.OS.WINDOWS) {
                    Minecraft.getInstance().execute(() -> ImeHelper.setMode(1));
                }
            }
        } else if (event.getScreen() instanceof ChatScreen chatScreen) {
            try {
                // ChatScreen 클래스에서 "initial"이라는 이름의 필드를 찾습니다.
                Field initialField = ChatScreen.class.getDeclaredField("initial");
                initialField.setAccessible(true); // 접근 권한 부여

                // 실제 값을 꺼내옵니다.
                String initialText = (String) initialField.get(chatScreen);

                // "/"로 시작하지 않는 경우에만 한글 모드 실행
                if (initialText == null || !initialText.startsWith("/")) {
                    if (Util.getPlatform() == Util.OS.WINDOWS) {
                        Minecraft.getInstance().execute(() -> ImeHelper.setMode(1));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SubscribeEvent
    public static void closeScreen(ScreenEvent.Closing event) {
        if (event.getScreen() instanceof ChatScreen || event.getScreen() instanceof InventoryScreen) {
            if (Util.getPlatform() == Util.OS.WINDOWS) {
                Minecraft.getInstance().execute(() -> ImeHelper.setMode(0));
            }
        }
    }
}

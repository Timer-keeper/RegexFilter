package com.timer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ModConfig {
    public static boolean enabled = true;
    public static List<String> regexFilters = new ArrayList<>();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("regexfilter.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // 从文件加载配置
    public static void load() {
        try {
            if (!Files.exists(CONFIG_PATH)) {
                save(); // 如果文件不存在，创建默认配置
                return;
            }
            String json = Files.readString(CONFIG_PATH);
            ModConfig config = GSON.fromJson(json, ModConfig.class);
            enabled = config.enabled;
            regexFilters = new ArrayList<>(config.regexFilters); // 深拷贝防止外部修改
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 保存配置到文件
    public static void save() {
        try {
            // 创建临时实例保存当前状态
            ModConfig config = new ModConfig();
            config.enabled = enabled;
            config.regexFilters = new ArrayList<>(regexFilters);
            
            String json = GSON.toJson(config);
            Files.writeString(CONFIG_PATH, json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
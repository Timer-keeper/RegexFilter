package com.timer;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.jupiter.api.*;

public class ModConfigTest {
    private static Path tempConfig;

    @BeforeAll
    static void setup() throws IOException {
        tempConfig = Files.createTempFile("regexfilter-test", ".json");
        ModConfig.setConfigPathForTest(tempConfig); // 使用新方法设置路径
    }

    @AfterEach
    void reset() {
        ModConfig.load();
    }

    @Test
    void load_shouldHandleMissingFile() {
        // 删除临时文件模拟文件不存在的情况
        tempConfig.toFile().delete();

        ModConfig.load();
        assertThat(ModConfig.getInstance().enabled).isTrue();
        assertThat(ModConfig.getInstance().regexFilters).isEmpty();
    }

    @Test
    void save_shouldCleanInvalidRegex() {
        ModConfig.getInstance().regexFilters = List.of("valid.*", "[invalid");
        ModConfig.save();

        assertThat(ModConfig.getInstance().regexFilters).containsExactly("valid.*");
    }

    @Test
    void load_shouldHandleInvalidJson() throws IOException {
        Files.writeString(tempConfig, "{ invalid json }");

        ModConfig.load();
        assertThat(ModConfig.getInstance().enabled).isTrue(); // 使用默认值
    }

    @Test
    void shouldCompileValidPatterns() {
        ModConfig.getInstance().regexFilters = List.of("valid.*", "[a-z]+");
        ModConfig.getInstance().updateCompiledPatterns();

        List<Pattern> compiled = ModConfig.getInstance().getCompiledPatterns();
        assertThat(compiled).hasSize(2);
        assertThat(compiled.get(0).pattern()).isEqualTo("valid.*");
    }

    @Test
    void shouldSkipInvalidPatterns() {
        ModConfig.getInstance().regexFilters = List.of("valid.*", "[invalid[");
        ModConfig.getInstance().updateCompiledPatterns();

        List<Pattern> compiled = ModConfig.getInstance().getCompiledPatterns();
        assertThat(compiled).hasSize(1);
    }
}

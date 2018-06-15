package github.tartaricacid.controlmoving;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import github.tartaricacid.controlmoving.command.SelectList;
import github.tartaricacid.controlmoving.command.SelectNamed;
import github.tartaricacid.controlmoving.data.DataMapSelect;
import github.tartaricacid.controlmoving.event.RefuseInteract;
import github.tartaricacid.controlmoving.event.RefuseMoving;
import github.tartaricacid.controlmoving.event.SelectRange;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.UUID;

@Plugin(id = "controlmoving", name = "Control Moving", description = "一个控制玩家移动的插件")
public class ControlMoving {
    @Inject
    private Logger logger;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path defaultConfig;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader<CommentedConfigurationNode> configManager;

    // 存储玩家临时圈地范围的变量，重启后失效，故不存为文件
    public static HashMap<UUID, HashMap> tmpSelectMap = new HashMap<>();

    // 存储服务器圈地信息的变量
    public static HashMap<String, DataMapSelect> dataMap = new HashMap<>();

    public static ControlMoving INSTANCE;

    // 配置文件加载
    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        configManagerSetup();
        storageFileSetup();
    }


    // 事件监听注册
    @Listener
    public void onInit(GameInitializationEvent initEvent) {
        // 获取插件实例
        INSTANCE = this;

        Sponge.getEventManager().registerListeners(this, new SelectRange());
        Sponge.getEventManager().registerListeners(this, new RefuseMoving());
        Sponge.getEventManager().registerListeners(this, new RefuseInteract());
    }


    // 插件命令的注册
    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        // 插件的加载提示
        // 使用 UNIX 终端支持的颜色代码
        String ANSI_GREEN = "\u001B[32m";
        String ANSI_RESET = "\u001B[0m";
        logger.info(ANSI_GREEN + "+=============================+" + ANSI_RESET);
        logger.info(ANSI_GREEN + "|       Control Moving        |" + ANSI_RESET);
        logger.info(ANSI_GREEN + "+=============================+" + ANSI_RESET);

        // 命令注册部分
        // TODO：自定义样式和语言
        CommandSpec selectNamed = CommandSpec.builder()
                .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("name"))),
                        GenericArguments.onlyOne(GenericArguments.integer(Text.of("priority"))))
                .description(Text.of("给圈好的范围设定名称和优先级"))
                .executor(new SelectNamed())
                .build();

        // TODO：自定义样式和语言
        CommandSpec selectList = CommandSpec.builder()
                .description(Text.of("显示圈好的地皮列表"))
                .executor(new SelectList())
                .build();

        CommandSpec commandMain = CommandSpec.builder()
                .description(Text.of("Control Moving 插件主命令"))
                .permission("control_moving.command.main")
                .child(selectNamed, "name", "n")
                .child(selectList, "list", "l")
                .build();

        // 指令注册
        Sponge.getCommandManager().register(this, commandMain, "control_moving", "cm");
    }

    private void configManagerSetup() {
        configManager = HoconConfigurationLoader.builder().setPath(defaultConfig).build();

        // 如果文件不存在，我们需要创建一个全新的默认配置文件
        if (!Files.exists(defaultConfig)) {
            ConfigurationNode rootNode = configManager.createEmptyNode(ConfigurationOptions.defaults());
            // TODO：更详细的配置
            ConfigurationNode targetNode = rootNode.getNode("modules", "blockCheats", "enabled").setValue(false);

            try {
                configManager.save(rootNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void storageFileSetup() {
        Path storageFile = configDir.resolve("storage.json");

        // 文件不存在返回
        if (!storageFile.toFile().exists()) {
            return;
        }

        // 文件存在处理
        Gson gson = new Gson();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(storageFile.toFile()), "UTF-8"));
            dataMap = gson.fromJson(in, new TypeToken<HashMap<String, DataMapSelect>>() {
            }.getType());
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Path getConfigDirectory() {
        return configDir;
    }
}

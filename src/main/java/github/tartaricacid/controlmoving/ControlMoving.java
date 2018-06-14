package github.tartaricacid.controlmoving;

import com.google.inject.Inject;
import github.tartaricacid.controlmoving.command.SelectList;
import github.tartaricacid.controlmoving.command.SelectNamed;
import github.tartaricacid.controlmoving.event.RefuseMoving;
import github.tartaricacid.controlmoving.event.SelectRange;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.UUID;

@Plugin(id = "controlmoving", name = "Control Moving", description = "一个控制玩家移动的插件")
public class ControlMoving {
    @Inject
    public Logger logger;

    // 存储玩家临时圈地范围的变量，重启后失效，故不存为文件
    public static HashMap<UUID, HashMap> tmpSelectMap = new HashMap<>();

    // 存储服务器圈地信息的变量
    // TODO：开服时应当从 storage.json 文件中进行读取
    public static HashMap<String, HashMap> dataMap = new HashMap<>();

    // 命令注册部分
    // TODO：后续移动到单独的类里面？
    // TODO：自定义样式和语言
    private CommandSpec selectNamed = CommandSpec.builder()
            .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("name"))),
                    GenericArguments.onlyOne(GenericArguments.integer(Text.of("priority"))))
            .description(Text.of("给圈好的范围设定名称和优先级"))
            .permission("control_moving.command.name")
            .executor(new SelectNamed())
            .build();

    // TODO：后续移动到单独的类里面？
    // TODO：自定义样式和语言
    private CommandSpec selectList = CommandSpec.builder()
            .description(Text.of("显示圈好的地皮"))
            .permission("control_moving.command.list")
            .executor(new SelectList())
            .build();

    // 事件和命令的注册
    @Listener
    public void onInit(GameInitializationEvent initEvent) {
        Sponge.getEventManager().registerListeners(this, new SelectRange());
        Sponge.getEventManager().registerListeners(this, new RefuseMoving());

        // 这个缩写很简陋
        // TODO：后续修改缩写
        Sponge.getCommandManager().register(this, selectNamed, "cmn");
        Sponge.getCommandManager().register(this, selectList, "cml");
    }

    // 插件的加载提示
    // TODO：后续学习 nucleus 插件，实现彩色输出
    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        logger.info("+=============================+");
        logger.info("|       Control Moving        |");
        logger.info("+=============================+");
    }
}

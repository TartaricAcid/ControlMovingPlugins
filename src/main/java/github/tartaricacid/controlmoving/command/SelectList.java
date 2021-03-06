package github.tartaricacid.controlmoving.command;

import com.flowpowered.math.vector.Vector3i;
import github.tartaricacid.controlmoving.ControlMoving;
import github.tartaricacid.controlmoving.data.DataMapSelect;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.serializer.TextSerializers;

// 列出所有圈地的命令
public class SelectList implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        // 只有玩家执行指令才行
        if (src instanceof Player) {
            // 强制类型转换
            Player player = (Player) src;

            // 消息输出主题部分
            player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize("&r&l[&b&l保护墙插件&r&l] &e&l目前圈地情况"));

            // 循环打印出领地情况
            for (String name : ControlMoving.dataMap.keySet()) {
                DataMapSelect range = ControlMoving.dataMap.get(name);

                Vector3i pos1 = range.getPos1();
                Vector3i pos2 = range.getPos2();
                int priority = range.getPriority();
                String world = range.getWorld();

                String textInfo = String.format("%s: [%d] [%s] (%d,%d,%d)-(%d,%d,%d) ",
                        name, priority, world,
                        pos1.getX(), pos1.getY(), pos1.getZ(),
                        pos2.getX(), pos2.getY(), pos2.getZ());

                player.sendMessage(Text.builder(textInfo)
                        .color(TextColors.GOLD)
                        .style(TextStyles.BOLD)
                        .append(Text.builder("[删除]")
                                .color(TextColors.YELLOW)
                                .style(TextStyles.BOLD)
                                .onClick(TextActions.runCommand("/cm del " + name))
                                .onHover(TextActions.showText(Text.of("点击删除")))
                                .build())
                        .build());
            }

            // 提示：指令成功
            return CommandResult.success();
        }

        // 否则，指令只是执行过
        return CommandResult.empty();
    }
}

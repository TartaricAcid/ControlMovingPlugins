package github.tartaricacid.controlmoving.command;

import com.flowpowered.math.vector.Vector3i;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import github.tartaricacid.controlmoving.ControlMoving;
import github.tartaricacid.controlmoving.data.DataMapSelect;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.util.HashMap;

import static github.tartaricacid.controlmoving.ControlMoving.dataMap;
import static github.tartaricacid.controlmoving.ControlMoving.tmpSelectMap;

// 领地命名和优先级判定
public class SelectNamed implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        // 获取两个参数
        // name：领地名称
        // priority：优先级
        String name = args.<String>getOne("name").get();
        int priority = args.<Integer>getOne("priority").get();

        // 只有玩家才能执行这个指令
        if (src instanceof Player) {
            Player player = (Player) src;

            // 从临时数据中依据玩家 UUID 找到玩家划定的区域
            // 存储玩家数据可以让多个人同时圈地而不冲突
            if (tmpSelectMap.containsKey(player.getUniqueId())) {
                HashMap range = tmpSelectMap.get(player.getUniqueId());

                // 先判断画的区域是否为空
                if (range.containsKey("pos1") && range.containsKey("pos2") && range.containsKey("world")) {
                    // 数据参数
                    Vector3i pos1 = (Vector3i) range.get("pos1");
                    Vector3i pos2 = (Vector3i) range.get("pos2");
                    String world = (String) range.get("world");

                    // 圈地范围过小警告，考虑原版最大玩家可以延伸
                    Vector3i posTmp = pos1.sub(pos2).abs();
                    int min = (posTmp.getX() < posTmp.getY()) ? Math.min(posTmp.getX(), posTmp.getZ()) : Math.min(posTmp.getY(), posTmp.getZ());
                    if (min < 5) {
                        player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize("&r&l[&b&l保护墙插件&r&l] &c&l范围过小! x、y、z最小不能小于5"));
                        return CommandResult.empty();
                    }

                    // 存数据参数
                    dataMap.put(name, new DataMapSelect(pos1, pos2, world, priority));

                    // 发送信息
                    player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize("&r&l[&b&l保护墙插件&r&l] &e&l圈地成功！"));

                    // 进行一次文件存储，防止关服数据丢失
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();  // Json 进行一次格式化，看起来美观
                    Path storageFile = ControlMoving.INSTANCE.getConfigDirectory().resolve("storage.json");

                    // 不用 sponge api，因为那个 api 不识别 Vector3i 数据
                    try {
                        OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(storageFile.toFile()), "UTF-8");
                        out.write(gson.toJson(dataMap));
                        out.close();

                        // 指令成功
                        return CommandResult.success();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // 否则提示，没有圈地
                else {
                    player.sendMessage(TextSerializers.FORMATTING_CODE.deserialize("&r&l[&b&l保护墙插件&r&l] &c&l请先圈地！"));
                }
            }
        }
        // 指令执行过了
        return CommandResult.empty();
    }
}

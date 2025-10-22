package su.nightexpress.coinsengine.currency.operation;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface OperationExecutor {

    @NotNull String getName();

    @NotNull Optional<CommandSender> getBukkitSender();

    @NotNull
    static OperationExecutor of(@NotNull CommandSender sender) {
        return new OperationExecutor() {

            @Override
            @NotNull
            public String getName() {
                return sender.getName();
            }

            @Override
            @NotNull
            public Optional<CommandSender> getBukkitSender() {
                return Optional.of(sender);
            }
        };
    }

    @NotNull
    static OperationExecutor custom(@NotNull String name) {
        return new OperationExecutor() {

            @Override
            @NotNull
            public String getName() {
                return name;
            }

            @Override
            @NotNull
            public Optional<CommandSender> getBukkitSender() {
                return Optional.empty();
            }
        };
    }
}

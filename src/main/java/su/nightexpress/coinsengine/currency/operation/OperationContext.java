package su.nightexpress.coinsengine.currency.operation;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;

public class OperationContext {

    private final OperationExecutor           executor;
    private final EnumSet<NotificationTarget> notificationTargets;

    private OperationContext(@NotNull OperationExecutor executor) {
        this.executor = executor;
        this.notificationTargets = EnumSet.allOf(NotificationTarget.class);
    }

    @NotNull
    public static OperationContext of(@NotNull OperationExecutor sender) {
        return new OperationContext(sender);
    }

    @NotNull
    public static OperationContext of(@NotNull CommandSender sender) {
        return of(OperationExecutor.of(sender));
    }

    @NotNull
    public static OperationContext custom(@NotNull String name) {
        return of(OperationExecutor.custom(name));
    }

    @NotNull
    public static OperationContext console() {
        return of(Bukkit.getConsoleSender());
    }

    @NotNull
    public static OperationContext consoleQuiet() {
        return console().silent();
    }

    @NotNull
    public OperationContext silent() {
        return this.silentFor(NotificationTarget.values());
    }

    /**
     * Makes the operation silent for the specified targets.
     * @param targets The targets to disable notifications for.
     * @return The current context instance for method chaining.
     */
    @NotNull
    public OperationContext silentFor(@NotNull NotificationTarget... targets) {
        Arrays.asList(targets).forEach(this.notificationTargets::remove);
        return this;
    }

    @NotNull
    public OperationContext silentFor(@NotNull NotificationTarget target, boolean flag) {
        if (flag) this.notificationTargets.remove(target);
        else this.notificationTargets.add(target);
        return this;
    }

    public boolean shouldNotify(@NotNull NotificationTarget target) {
        return this.notificationTargets.contains(target);
    }

    public boolean shouldNotifyLogger() {
        return this.shouldNotify(NotificationTarget.CONSOLE_LOGGER) || this.shouldNotify(NotificationTarget.FILE_LOGGER);
    }

    @NotNull
    public OperationExecutor getExecutor() {
        return this.executor;
    }

    @NotNull
    public Optional<CommandSender> getBukkitSender() {
        return this.executor.getBukkitSender();
    }
}

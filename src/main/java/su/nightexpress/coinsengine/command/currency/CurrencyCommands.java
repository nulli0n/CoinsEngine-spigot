package su.nightexpress.coinsengine.command.currency;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.COEFiles;
import su.nightexpress.coinsengine.CoinsEnginePlugin;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.command.currency.provider.CommandProvider;
import su.nightexpress.coinsengine.command.currency.provider.impl.*;
import su.nightexpress.coinsengine.config.Lang;
import su.nightexpress.coinsengine.currency.CurrencyManager;
import su.nightexpress.coinsengine.currency.CurrencyRegistry;
import su.nightexpress.nightcore.commands.Commands;
import su.nightexpress.nightcore.commands.command.NightCommand;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.manager.SimpleManager;
import su.nightexpress.nightcore.util.LowerCase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CurrencyCommands extends SimpleManager<CoinsEnginePlugin> {

    private final CurrencyRegistry currencyRegistry;
    private final CurrencyManager currencyManager;

    private final Map<String, CommandProvider>   providerByNameMap;
    private final Map<String, CommandDefinition> definitionByNameMap;
    private final Map<String, Set<NightCommand>> currencyCommands;

    public CurrencyCommands(@NotNull CoinsEnginePlugin plugin, @NotNull CurrencyRegistry currencyRegistry, @NotNull CurrencyManager currencyManager) {
        super(plugin);
        this.currencyRegistry = currencyRegistry;
        this.currencyManager = currencyManager;
        this.providerByNameMap = new HashMap<>();
        this.definitionByNameMap = new HashMap<>();
        this.currencyCommands = new HashMap<>();

        this.registerDefaultProviders();
    }

    @Override
    protected void onLoad() {
        this.loadCommands();
    }

    @Override
    protected void onShutdown() {
        this.currencyRegistry.getCurrencies().forEach(this::unregisterCommands);
        this.currencyCommands.values().forEach(set -> set.forEach(NightCommand::unregister));
        this.currencyCommands.clear();

        this.definitionByNameMap.clear();
        this.providerByNameMap.clear();
    }

    private void registerDefaultProviders() {
        this.registerProvider(new BalanceProvider(this.plugin, this.currencyRegistry, this.currencyManager));
        this.registerProvider(new SendProvider(this.plugin, this.currencyRegistry, this.currencyManager));
        this.registerProvider(new PaymentsProvider(this.plugin, this.currencyRegistry, this.currencyManager));
        this.registerProvider(new GiveProvider(this.plugin, this.currencyRegistry, this.currencyManager));
        this.registerProvider(new GiveAllProvider(this.plugin, this.currencyRegistry, this.currencyManager));
        this.registerProvider(new SetProvider(this.plugin, this.currencyRegistry, this.currencyManager));
        this.registerProvider(new RemoveProvider(this.plugin, this.currencyRegistry, this.currencyManager));
        this.registerProvider(new ExchangeProvider(this.plugin, this.currencyRegistry, this.currencyManager));
    }

    public void registerProvider(@NotNull CommandProvider provider) {
        this.providerByNameMap.put(provider.getName(), provider);
    }

    private void loadCommands() {
        this.loadCommandDefinitions();

        this.currencyRegistry.getCurrencies().forEach(this::loadCommands);
    }

    private void loadCommandDefinitions() {
        FileConfig config = FileConfig.load(this.plugin.getDataFolder().getPath(), COEFiles.FILE_COMMANDS);
        String path = "Commands.";

        if (config.getSection(path).isEmpty()) {
            this.providerByNameMap.forEach((name, provider) -> config.set(path + "." + name, provider.getDefaultDefinition()));
        }

        config.getSection(path).forEach(sId -> {
            String name = LowerCase.INTERNAL.apply(sId);

            if (!this.providerByNameMap.containsKey(name)) {
                this.plugin.warn("Unknown command '" + sId + "' in '" + config.getPath() + "'.");
                return;
            }

            CommandDefinition definition = CommandDefinition.read(config, path + "." + sId);
            this.definitionByNameMap.put(name, definition);
        });

        config.saveChanges();
    }

    public void loadCommands(@NotNull Currency currency) {
        NightCommand currencyCommand = NightCommand.hub(this.plugin, currency.getCommandAliases(), rootBuilder -> {
            rootBuilder.permission(currency.isPermissionRequired() ? currency.getPermission() : null);
            rootBuilder.description(currency.replacePlaceholders().apply(Lang.COMMAND_CURRENCY_ROOT_DESC.text()));

            this.providerByNameMap.forEach((name, provider) -> {
                CommandDefinition balanceDef = this.definitionByNameMap.getOrDefault(name, provider.getDefaultDefinition());
                CommandVariant children = balanceDef.children();
                CommandVariant dedicated = balanceDef.dedicated();

                if (!children.enabled() && !dedicated.enabled()) return;
                if (!provider.isAvailable(currency)) return;

                provider.buildRoot(currency, rootBuilder);

                if (children.enabled()) {
                    rootBuilder.branch(Commands.literal(children.aliases()[0], builder -> provider.build(currency, builder)));
                }

                if (dedicated.enabled() && currency.isPrimary()) {
                    NightCommand command = NightCommand.literal(this.plugin, dedicated.aliases(), builder -> provider.build(currency, builder));
                    this.registerCommand(currency, command);
                }
            });
        });

        this.registerCommand(currency, currencyCommand);
    }

    private void registerCommand(@NotNull Currency currency, @NotNull NightCommand command) {
        if (command.register()) {
            this.currencyCommands.computeIfAbsent(currency.getId(), k -> new HashSet<>()).add(command);
        }
    }

    public void unregisterCommands(@NotNull Currency currency) {
        Set<NightCommand> commands = this.currencyCommands.remove(currency.getId());
        if (commands == null) return;

        commands.forEach(NightCommand::unregister);
    }
}

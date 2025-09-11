package su.nightexpress.coinsengine.currency.operation;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.coinsengine.api.currency.Currency;
import su.nightexpress.coinsengine.api.currency.OperationResult;
import su.nightexpress.coinsengine.data.impl.CoinsUser;

public abstract class ConsoleOperation<S extends CommandSender> extends AbstractOperation {

    protected final S sender;

    protected boolean feedback;
    protected boolean notify;

    public ConsoleOperation(@NotNull Currency currency, double amount, @NotNull CoinsUser user, @NotNull S sender) {
        super(currency, amount, user);
        this.sender = sender;
        this.setFeedback(true);
        this.setNotify(true);
    }

    @Override
    @NotNull
    public OperationResult perform() {
        OperationResult result = super.perform();

        if (result.isSuccess()) {
            if (this.feedback) this.sendFeedback();
            if (this.notify) this.notifyUser();
        }

        return result;
    }

    protected abstract void notifyUser();

    protected abstract void sendFeedback();

    @NotNull
    public S getSender() {
        return this.sender;
    }

    public boolean isFeedback() {
        return this.feedback;
    }

    public void setFeedback(boolean feedback) {
        this.feedback = feedback;
    }

    public boolean isNotify() {
        return this.notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }
}

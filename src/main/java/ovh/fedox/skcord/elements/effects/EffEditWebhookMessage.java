package ovh.fedox.skcord.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ovh.fedox.skcord.discord.DiscordWebhook;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name( "Webhook - Edit message content" )
@Description( "Edit the content of a webhook message quickly." )
@Examples( { "edit webhook message with id \"1234567890\" from \"https://discord.com/api/webhooks/1234567890/ABCDEFGHIJKLMN\" to \"New content\"", "edit webhook message with id \"1234567890\" from \"https://discord.com/api/webhooks/1234567890/ABCDEFGHIJKLMN\" to \"New content\" in thread \"9876543210\"" } )
@Since( "3.3-RELEASE" )
public class EffEditWebhookMessage extends Effect {

	static {
		Skript.registerEffect(EffEditWebhookMessage.class, "edit webhook message with id %string% from %string% to %string% [in [the] thread [with id] %-string%]");
	}

	private Expression<String> messageId;
	private Expression<String> webhook;
	private Expression<String> newContent;
	private Expression<String> threadId;

	@Override
	@SuppressWarnings( "unchecked" )
	public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
		this.messageId = (Expression<String>) expressions[0];
		this.webhook = (Expression<String>) expressions[1];
		this.newContent = (Expression<String>) expressions[2];
		this.threadId = (Expression<String>) expressions[3];
		return true;
	}

	@Override
	protected void execute(@NotNull org.bukkit.event.Event event) {
		String messageId = this.messageId.getSingle(event);
		String webhook = this.webhook.getSingle(event);
		String newContent = this.newContent.getSingle(event);
		String threadId = this.threadId != null ? this.threadId.getSingle(event) : null;

		if (messageId == null || webhook == null || newContent == null) {
			Skript.error("The message ID, webhook, and new content cannot be null.");
			return;
		}

		newContent = newContent.replaceAll("<@§(\\d+)>", "<@&$1>");

		try {
			Long threadIdLong = threadId != null ? Long.parseLong(threadId) : null;
			DiscordWebhook.editMessageAsync(webhook, messageId, newContent, null, null, threadIdLong,
				t -> Skript.error("Failed to edit webhook message: " + t.getMessage())
			);
		} catch (NumberFormatException e) {
			Skript.error("Invalid thread ID format: " + threadId);
		}
	}

	public @NotNull String toString(@Nullable org.bukkit.event.Event event, boolean b) {
		return "edit webhook message with id " + this.messageId.toString(event, b) + " from " + this.webhook.toString(event, b) + " to " + this.newContent.toString(event, b);
	}
}

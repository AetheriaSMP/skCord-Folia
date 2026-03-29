package ovh.fedox.skcord.discord;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.Bukkit;
import ovh.fedox.skcord.SkCord;
import ovh.fedox.skcord.discord.model.Embed;
import ovh.fedox.skcord.util.JsonUtil;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class DiscordWebhook {
    private final HttpClient httpClient;
    private String content;
    private String username;
    private String avatarUrl;
    private String threadName;
    private List<Embed> embeds;

    public DiscordWebhook() {
        this.embeds = new ArrayList<>();
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Deletes a webhook message
     *
     * @param webhookUrl The webhook URL
     * @param messageId  The ID of the message to delete
     * @throws IOException          If the request fails
     * @throws InterruptedException If the request is interrupted
     */
    public static void deleteMessage(String webhookUrl, String messageId) throws IOException, InterruptedException {
        deleteMessage(webhookUrl, messageId, null);
    }

    /**
     * Deletes a webhook message asynchronously
     *
     * @param webhookUrl The webhook URL
     * @param messageId  The ID of the message to delete
     * @param onError    Callback for errors (optional)
     */
    public static void deleteMessageAsync(String webhookUrl, String messageId, Consumer<Throwable> onError) {
        deleteMessageAsync(webhookUrl, messageId, null, onError);
    }

    /**
     * Deletes a webhook message asynchronously with optional thread ID
     *
     * @param webhookUrl The webhook URL
     * @param messageId  The ID of the message to delete
     * @param threadId   The ID of the thread (optional)
     * @param onError    Callback for errors (optional)
     */
    public static void deleteMessageAsync(String webhookUrl, String messageId, Long threadId, Consumer<Throwable> onError) {
        Bukkit.getAsyncScheduler().runNow(SkCord.getPlugin(), scheduledTask -> {
            try {
                deleteMessage(webhookUrl, messageId, threadId);
            } catch (Throwable t) {
                if (onError != null) {
                    onError.accept(t);
                }
            }
        });
    }

    /**
     * Deletes a webhook message with optional thread ID
     *
     * @param webhookUrl The webhook URL
     * @param messageId  The ID of the message to delete
     * @param threadId   The ID of the thread (optional)
     * @throws IOException          If the request fails
     * @throws InterruptedException If the request is interrupted
     */
    public static void deleteMessage(String webhookUrl, String messageId, Long threadId) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();

        String deleteUrl = webhookUrl + "/messages/" + messageId;
        if (threadId != null) {
            deleteUrl += "?thread_id=" + threadId;
        }

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(deleteUrl)).DELETE().build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 204) {
            throw new IOException("Failed to delete webhook message: HTTP " + response.statusCode() + " - " + response.body());
        }
    }

    /**
     * Edits a webhook message
     *
     * @param webhookUrl The webhook URL
     * @param messageId  The ID of the message to edit
     * @param content    The new content (optional)
     * @param embeds     The new embeds (optional)
     * @param avatarUrl  The new avatar URL (optional)
     * @throws IOException          If the request fails
     * @throws InterruptedException If the request is interrupted
     */
    public static void editMessage(String webhookUrl, String messageId, String content, List<Embed> embeds, String avatarUrl) throws IOException, InterruptedException {
        editMessage(webhookUrl, messageId, content, embeds, avatarUrl, null);
    }

    /**
     * Edits a webhook message asynchronously
     *
     * @param webhookUrl The webhook URL
     * @param messageId  The ID of the message to edit
     * @param content    The new content (optional)
     * @param embeds     The new embeds (optional)
     * @param avatarUrl  The new avatar URL (optional)
     * @param onError    Callback for errors (optional)
     */
    public static void editMessageAsync(String webhookUrl, String messageId, String content, List<Embed> embeds, String avatarUrl, Consumer<Throwable> onError) {
        editMessageAsync(webhookUrl, messageId, content, embeds, avatarUrl, null, onError);
    }

    /**
     * Edits a webhook message asynchronously with optional thread ID
     *
     * @param webhookUrl The webhook URL
     * @param messageId  The ID of the message to edit
     * @param content    The new content (optional)
     * @param embeds     The new embeds (optional)
     * @param avatarUrl  The new avatar URL (optional)
     * @param threadId   The ID of the thread (optional)
     * @param onError    Callback for errors (optional)
     */
    public static void editMessageAsync(String webhookUrl, String messageId, String content, List<Embed> embeds, String avatarUrl, Long threadId, Consumer<Throwable> onError) {
        Bukkit.getAsyncScheduler().runNow(SkCord.getPlugin(), scheduledTask -> {
            try {
                editMessage(webhookUrl, messageId, content, embeds, avatarUrl, threadId);
            } catch (Throwable t) {
                if (onError != null) {
                    onError.accept(t);
                }
            }
        });
    }

    /**
     * Edits a webhook message with optional thread ID
     *
     * @param webhookUrl The webhook URL
     * @param messageId  The ID of the message to edit
     * @param content    The new content (optional)
     * @param embeds     The new embeds (optional)
     * @param avatarUrl  The new avatar URL (optional)
     * @param threadId   The ID of the thread (optional)
     * @throws IOException          If the request fails
     * @throws InterruptedException If the request is interrupted
     */
    public static void editMessage(String webhookUrl, String messageId, String content, List<Embed> embeds, String avatarUrl, Long threadId) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();

        Map<String, Object> payload = new HashMap<>();
        if (content != null) {
            payload.put("content", content);
        }

        if (avatarUrl != null) {
            payload.put("avatar_url", avatarUrl);
        }
        if (embeds != null && !embeds.isEmpty()) {
            payload.put("embeds", embeds);
        }

        String jsonPayload = JsonUtil.toJson(payload);
        String editUrl = webhookUrl + "/messages/" + messageId;
        if (threadId != null) {
            editUrl += "?thread_id=" + threadId;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(editUrl))
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("Failed to edit webhook message: HTTP " + response.statusCode() + " - " + response.body());
        }
    }

    public DiscordWebhook content(String content) {
        this.content = content;
        return this;
    }

    public DiscordWebhook username(String username) {
        this.username = username;
        return this;
    }

    public DiscordWebhook avatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
    }

    public DiscordWebhook threadName(String threadName) {
        this.threadName = threadName;
        return this;
    }

    public DiscordWebhook addEmbed(Embed embed) {
        this.embeds.add(embed);
        return this;
    }

    public DiscordWebhook setEmbeds(List<Embed> embeds) {
        this.embeds = embeds;
        return this;
    }

    public String sendToDiscord(String webhookUrl) throws IOException, InterruptedException {
        return sendToDiscord(webhookUrl, null);
    }

    /**
     * Sends webhook message asynchronously
     *
     * @param webhookUrl The webhook URL
     * @param onSuccess  Callback with message ID on success (optional)
     * @param onError    Callback for errors (optional)
     */
    public void sendToDiscordAsync(String webhookUrl, Consumer<String> onSuccess, Consumer<Throwable> onError) {
        sendToDiscordAsync(webhookUrl, null, onSuccess, onError);
    }

    /**
     * Sends webhook message asynchronously with optional thread ID
     *
     * @param webhookUrl The webhook URL
     * @param threadId   The ID of the thread (optional)
     * @param onSuccess  Callback with message ID on success (optional)
     * @param onError    Callback for errors (optional)
     */
    public void sendToDiscordAsync(String webhookUrl, Long threadId, Consumer<String> onSuccess, Consumer<Throwable> onError) {
        Bukkit.getAsyncScheduler().runNow(SkCord.getPlugin(), scheduledTask -> {
            try {
                String messageId = sendToDiscord(webhookUrl, threadId);
                if (onSuccess != null) {
                    onSuccess.accept(messageId);
                }
            } catch (Throwable t) {
                if (onError != null) {
                    onError.accept(t);
                }
            }
        });
    }

    public String sendToDiscord(String webhookUrl, Long threadId) throws IOException, InterruptedException {
        Map<String, Object> payload = new HashMap<>();
        if (content != null) {
            payload.put("content", content);
        }
        if (username != null) {
            payload.put("username", username);
        }
        if (avatarUrl != null) {
            payload.put("avatar_url", avatarUrl);
        }
        if (threadName != null) {
            payload.put("thread_name", threadName);
        }
        if (!embeds.isEmpty()) {
            payload.put("embeds", embeds);
        }

        String jsonPayload = JsonUtil.toJson(payload);
        String finalUrl = webhookUrl + "?wait=true";
        if (threadId != null) {
            finalUrl += "&thread_id=" + threadId;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(finalUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("Failed to send webhook: HTTP " + response.statusCode() + " - " + response.body());
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(response.body());
            return jsonNode.get("id").asText();
        } catch (Exception e) {
            throw new IOException("Failed to parse message ID from response: " + e.getMessage());
        }
    }
}

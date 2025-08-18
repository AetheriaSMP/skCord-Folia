# skCord Tutorial: Complete Guide to Discord Integration in Skript

## Table of Contents

1. [Introduction](#introduction)
2. [Basic Webhook Messages](#basic-webhook-messages)
3. [Creating Embeds](#creating-embeds)
4. [Embed Components](#embed-components)
5. [Message Management](#message-management)
6. [Advanced Features](#advanced-features)
7. [Best Practices](#best-practices)
8. [Examples](#examples)

## Introduction

skCord is a powerful Skript addon that allows you to integrate Discord webhooks into your Minecraft server. With skCord,
you can send messages, create rich embeds, manage threads, and edit messages directly from your Skript code.

### Prerequisites

- Skript installed on your server
- skCord addon installed
- Discord webhook URL from your Discord server

## Basic Webhook Messages

### Simple Message Sending

The most basic way to send a message to Discord:

```plaintext
send "Hello, Discord!" as webhook to "https://discord.com/api/webhooks/your_webhook_url_here"
```

### Customized Webhook Messages

You can customize the webhook's appearance:

```plaintext
send "Server announcement!" as webhook to "https://discord.com/api/webhooks/your_webhook_url_here" with name "Server Bot" as "My Custom Bot" with picture "https://example.com/avatar.png"
```

### Thread Support

Send messages to specific threads:

```plaintext
# Send to existing thread
send "Thread message" as webhook to "https://discord.com/api/webhooks/your_webhook_url_here" in thread "123456789"
```

## Creating Embeds

Embeds are rich message formats that can display structured information with colors, images, and fields.

### Basic Embed Structure

```plaintext
create a new embed:
    title: "Server Status"
    description: "Current server information"
    color: "##00FF00"
    timestamp: true
    saveInto: {_embed}
```

### Complete Embed Example

```plaintext
on server start:
    create a new embed:
        title: "🚀 Server Started"
        description: "The Minecraft server is now online!"
        color: "##3437eb"
        timestamp: true
        footer: new footer named "Server Bot" and icon url "https://example.com/icon.png"
        thumbnail: "https://example.com/server-icon.png"
        image: "https://example.com/banner.png"
        author: new author with name "Server Admin" and url "https://example.com" with icon url "https://example.com/admin-avatar.png"
        fields: new inlined field with name "Players Online" and value "0/20"
        saveInto: {_embed}
    
    create a new webhook message:
        webhook: "https://discord.com/api/webhooks/your_webhook_url_here"
        embed: {_embed}
        username: "Server Monitor"
```

### Embed Properties

| Property      | Description                         | Required |
|---------------|-------------------------------------|----------|
| `title`       | The main title of the embed         | No       |
| `description` | Main text content                   | No       |
| `color`       | Hex color code (use `##` prefix)    | No       |
| `timestamp`   | Show current timestamp (true/false) | No       |
| `footer`      | Footer component                    | No       |
| `thumbnail`   | Small image in top-right            | No       |
| `image`       | Large image at bottom               | No       |
| `author`      | Author component at top             | No       |
| `fields`      | List of field components            | No       |
| `saveInto`    | Variable to store the embed         | **Yes**  |

## Embed Components

### Author Component

Creates an author section at the top of the embed:

```plaintext
set {_author} to new author with name "Player Name" and url "https://example.com/profile" with icon url "https://example.com/avatar.png"
```

**Parameters:**

- `name`: Author's display name (required)
- `url`: Clickable link (optional)
- `icon url`: Small icon next to name (optional)

### Field Component

Adds structured information to embeds:

```plaintext
# Regular field
set {_field1} to new field with name "Server IP" and value "play.example.com"

# Inline field (displays side-by-side with other inline fields)
set {_field2} to new inlined field with name "Version" and value "1.20.1"
```

**Parameters:**

- `name`: Field title (required)
- `value`: Field content (required)
- `inline`: Whether field displays inline (optional)

### Footer Component

Adds a footer at the bottom of the embed:

```plaintext
set {_footer} to new footer with name "Last updated" and icon url "https://example.com/clock-icon.png"
```

**Parameters:**

- `name`: Footer text (required)
- `icon url`: Small footer icon (optional)

## Message Management

### Advanced Webhook Messages

Use the section syntax for more control:

```plaintext
create a new webhook message:
    webhook: "https://discord.com/api/webhooks/your_webhook_url_here"
    message: "Hello, Discord!"
    username: "Custom Bot Name"
    avatar: "https://example.com/avatar.png"
    thread_id: 123456789
    saveMessageIdInto: {_messageId}
```

**Parameters:**

- `webhook`: Discord webhook URL (required)
- `message`: Text content (required if no embed)
- `embed`: Embed object (required if no message)
- `username`: Custom webhook name (optional)
- `avatar`: Custom webhook avatar URL (optional)
- `thread_id`: Send to specific thread ID (optional)
- `thread_name`: Create new thread with this name (optional)
- `saveMessageIdInto`: Store message ID for later editing (optional)

### Editing Messages

Edit previously sent webhook messages:

```plaintext
# Using the section syntax
edit webhook message:
    webhook: "https://discord.com/api/webhooks/your_webhook_url_here"
    message_id: {_savedMessageId}
    message: "Updated content"
    avatar: "https://example.com/new-avatar.png"

# Quick edit syntax
edit webhook message with id {_messageId} from "https://discord.com/api/webhooks/your_webhook_url_here" to "New content"
```

### Deleting Messages

Remove webhook messages:

```plaintext
# Delete from main channel
delete webhook message with id {_messageId} from "https://discord.com/api/webhooks/your_webhook_url_here"

# Delete from thread
delete webhook message with id {_messageId} from "https://discord.com/api/webhooks/your_webhook_url_here" in thread "123456789"
```

## Advanced Features

### Thread Management

```plaintext
# Create new thread and send message
create a new webhook message:
    webhook: "https://discord.com/api/webhooks/your_webhook_url_here"
    message: "Starting new discussion"
    thread_name: "Player Reports"

# Send to existing thread
create a new webhook message:
    webhook: "https://discord.com/api/webhooks/your_webhook_url_here"
    message: "Thread reply"
    thread_id: 987654321
```

### Multiple Fields in Embeds

```plaintext
on player join:
    set {_field1} to new inlined field with name "Player" and value "%player%"
    set {_field2} to new inlined field with name "Join Time" and value "%now%"
    set {_field3} to new field with name "Welcome Message" and value "Welcome to our server!"
    
    create a new embed:
        title: "Player Joined"
        color: "##00FF00"
        fields: {_field1}, {_field2}, {_field3}
        saveInto: {_embed}
    
    create a new webhook message:
        webhook: "https://discord.com/api/webhooks/your_webhook_url_here"
        embed: {_embed}
```

## Best Practices

### 1. Store Webhook URLs Safely

```plaintext
on script load:
    set {webhook::announcements} to "https://discord.com/api/webhooks/your_webhook_url_here"
    set {webhook::logs} to "https://discord.com/api/webhooks/another_webhook_url_here"
```

### 2. Error Handling

Always check if webhooks are configured:

```plaintext
command /announce <text>:
    trigger:
        if {webhook::announcements} is not set:
            send "Webhook not configured!" to player
            stop
        
        create a new webhook message:
            webhook: {webhook::announcements}
            message: "📢 %arg-1%"
            username: "Server Announcements"
```

### 3. Color Coding

Use consistent colors for different message types:

```plaintext
# Success: Green
set {color::success} to "##00FF00"
# Warning: Yellow  
set {color::warning} to "##FFFF00"
# Error: Red
set {color::error} to "##FF0000"
# Info: Blue
set {color::info} to "##0099FF"
```

### 4. Message ID Management

Store message IDs for editing capabilities:

```plaintext
command /status:
    trigger:
        create a new webhook message:
            webhook: {webhook::status}
            message: "Server Status: Online"
            saveMessageIdInto: {status::messageId}
        
        # Later, update the status
        wait 5 minutes
        edit webhook message:
            webhook: {webhook::status}
            message_id: {status::messageId}
            message: "Server Status: %tps% TPS"
```

## Examples

### Player Join/Leave Notifications

```plaintext
on join:
    create a new embed:
        title: "Player Joined"
        description: "%player% has joined the server!"
        color: "##00FF00"
        timestamp: true
        thumbnail: "https://minotar.net/avatar/%player%/64"
        footer: new footer named "Players Online: %number of all players%"
        saveInto: {_embed}
    
    create a new webhook message:
        webhook: {webhook::player-activity}
        embed: {_embed}
        username: "Server Monitor"

on quit:
    create a new embed:
        title: "Player Left"
        description: "%player% has left the server!"
        color: "##FF0000"
        timestamp: true
        thumbnail: "https://minotar.net/avatar/%player%/64"
        footer: new footer named "Players Online: %number of all players - 1%"
        saveInto: {_embed}
    
    create a new webhook message:
        webhook: {webhook::player-activity}
        embed: {_embed}
        username: "Server Monitor"
```

### Server Statistics Dashboard

```plaintext
every 10 minutes:
    set {_field1} to new inlined field with name "Players Online" and value "%number of all players%"
    set {_field2} to new inlined field with name "TPS" and value "%tps%"
    set {_field3} to new inlined field with name "Uptime" and value "%uptime%"
    
    create a new embed:
        title: "📊 Server Statistics"
        description: "Live server information"
        color: "##3437eb"
        timestamp: true
        fields: {_field1}, {_field2}, {_field3}
        footer: new footer named "Auto-updated every 10 minutes"
        saveInto: {_embed}
    
    if {stats::messageId} is set:
        edit webhook message:
            webhook: {webhook::stats}
            message_id: {stats::messageId}
            embed: {_embed}
    else:
        create a new webhook message:
            webhook: {webhook::stats}
            embed: {_embed}
            username: "Server Stats"
            saveMessageIdInto: {stats::messageId}
```

### Chat Bridge

```plaintext
on chat:
    create a new webhook message:
        webhook: {webhook::chat}
        message: "**%player%**: %message%"
        username: "%player%"
        avatar: "https://minotar.net/avatar/%player%/64"
```

### Admin Alert System

```plaintext
function adminAlert(message: text, severity: text):
    if {_severity} is "high":
        set {_color} to "##FF0000"
        set {_emoji} to "🚨"
    else if {_severity} is "medium":
        set {_color} to "##FFFF00"
        set {_emoji} to "⚠️"
    else:
        set {_color} to "##0099FF"
        set {_emoji} to "ℹ️"
    
    create a new embed:
        title: "%{_emoji}% Admin Alert"
        description: {_message}
        color: {_color}
        timestamp: true
        footer: new footer named "Severity: %{_severity}%"
        saveInto: {_embed}
    
    create a new webhook message:
        webhook: {webhook::admin-alerts}
        embed: {_embed}
        username: "Security System"

# Usage
on player command "/op":
    adminAlert("Player %player% attempted to use /op command!", "high")
```
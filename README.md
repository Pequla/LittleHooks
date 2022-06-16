# LittleHooks
Discord webhooks integration as a Spigot plugin. Extremely light weight.

> Requires plugin [LittleLink](https://github.com/Pequla/LittleLink)

## Configuration

The default configuration:
```yaml
webhook-url: url
color:
  system: 65535
  join: 65280
  leave: 16711680
  death: 8388736
```

- `webhook-url` is the webhook url. You can obtain it in the channel settings under the integrations/webhooks tab. Please note that this link allows you to send messages to that channel so keep it safe and secure
- `color` represents the decimal or hexadecimal color code of the discord message embed. All the sub options represent the name of the event the color will be used in

> `color.system` color code is used in the non player related events. ex: server loading or server stopping

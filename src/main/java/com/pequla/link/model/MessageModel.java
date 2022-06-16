package com.pequla.link.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MessageModel {
    private String username;
    private String content;
    private String avatar_url;
    private List<EmbedModel> embeds;
    private Boolean tts;
}

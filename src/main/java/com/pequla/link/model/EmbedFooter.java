package com.pequla.link.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmbedFooter {
    private String text;
    private String icon_url;
}

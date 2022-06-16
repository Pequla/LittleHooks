package com.pequla.link.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmbedAuthor {
    private String name;
    private String url;
    private String icon_url;
}

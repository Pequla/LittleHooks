package com.pequla.link.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EmbedModel {
    private Integer color;
    private String title;
    private String url;
    private EmbedAuthor author;
    private String description;
    private List<EmbedField> fields;
    private EmbedImage image;
    private EmbedImage thumbnail;
    private EmbedFooter footer;
    private String timestamp;
}

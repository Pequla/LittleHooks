package com.pequla.link.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmbedField {
    private String name;
    private String value;
    private Boolean inline;
}

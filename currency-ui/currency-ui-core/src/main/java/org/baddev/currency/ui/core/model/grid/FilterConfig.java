package org.baddev.currency.ui.core.model.grid;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collection;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Accessors(chain = true)
@EqualsAndHashCode
@ToString
public class FilterConfig {
    private String propId;
    private FilterKind kind;
    private Collection<String> selectOptions = new ArrayList<>();
    private boolean rangeDateOrDateTime;
    private boolean exactDateOrDateTime;
    private boolean textAutocomplete;
    private DateTimeResolution resolution;
}
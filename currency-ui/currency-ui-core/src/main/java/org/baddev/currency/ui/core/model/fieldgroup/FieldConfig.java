package org.baddev.currency.ui.core.model.fieldgroup;

import com.vaadin.data.Validator;
import com.vaadin.server.Resource;
import com.vaadin.ui.Field;
import lombok.*;
import lombok.experimental.Accessors;

@AllArgsConstructor
@RequiredArgsConstructor
@Setter
@Getter
@Accessors(chain = true)
@EqualsAndHashCode
@ToString
public class FieldConfig {
    private final String propId;
    private final String caption;
    private Resource icon;
    private Class<? extends Field> type;
    private Validator validator;
}
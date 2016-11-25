package org.baddev.currency.ui.util;

import com.vaadin.data.Validator;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.server.Resource;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;
import org.baddev.currency.ui.model.fieldgroup.FieldConfig;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by IPotapchuk on 11/21/2016.
 */
public final class FieldGroupUtils {

    private static final Class<? extends Field> DEFAULT_FIELD_TYPE = TextField.class;

    private FieldGroupUtils() {
    }

    public static <T> BeanFieldGroup<T> create(Class<T> beanClass,
                                               T formBean,
                                               Collection<FieldConfig> configs) {
        BeanFieldGroup<T> bfg = new BeanFieldGroup<>(beanClass);
        bfg.setItemDataSource(formBean);
        bind(bfg, configs);
        return bfg;
    }

    public static List<? extends Field> bind(BeanFieldGroup bfg, Collection<FieldConfig> configs) {
        return configs.stream().map(cfg -> {
            Field f = bfg.buildAndBind(cfg.getCaption(), cfg.getPropId(), cfg.getType() == null ? DEFAULT_FIELD_TYPE : cfg.getType());
            if (cfg.getIcon() != null) f.setIcon(cfg.getIcon());
            if (cfg.getValidator() != null) f.addValidator(cfg.getValidator());
            return f;
        }).collect(Collectors.toList());
    }

    public static FieldConfig fConf(String propId, String caption, Resource icon, Class<? extends Field> type, Validator validator) {
        return new FieldConfig(propId, caption, icon, type, validator);
    }

    public static FieldConfig fConf(String propId, String caption, Resource icon, Class<? extends Field> type) {
        return fConf(propId, caption, icon, type, null);
    }

    public static FieldConfig fConf(String propId, String caption, Resource icon) {
        return fConf(propId, caption, icon, DEFAULT_FIELD_TYPE);
    }

    public static FieldConfig fConf(String propId, String caption) {
        return fConf(propId, caption, DEFAULT_FIELD_TYPE);
    }

    public static FieldConfig fConf(String propId, String caption, Class<? extends Field> type) {
        return fConf(propId, caption, null, type);
    }

    public static FieldConfig fConf(String propId, String caption, Resource icon, Validator validator) {
        return fConf(propId, caption, icon, DEFAULT_FIELD_TYPE, validator);
    }

    public static FieldConfig fConf(String propId, String caption, Validator validator) {
        return fConf(propId, caption, DEFAULT_FIELD_TYPE, validator);
    }

    public static FieldConfig fConf(String propId, String caption, Class<? extends Field> type, Validator validator) {
        return fConf(propId, caption, null, type, validator);
    }

}

package org.baddev.currency.ui.component.window.form;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by IPotapchuk on 10/3/2016.
 */
public final class FormWindow {

    private static final Class DEFAULT_FIELD_TYPE = TextField.class;

    public enum Mode {
        NEW, EDIT, READONLY
    }

    private FormWindow() {
    }

    public static void show(Config config) {
        AbstractFormWindow formWindow = null;
        if (config.beanClass != null) {
            formWindow = new BindableFormWindow<>(config.mode,
                    createBeanFieldGroup(config.beanClass, config.formBean,
                            config.captionToPropertyIdMap,
                            config.propertyIdToFieldTypeMap));
        } else if (!config.lhs.isEmpty() || !config.rhs.isEmpty()){
            formWindow = new TwincolSelectWindow(config.lhs, config.rhs, config.itemCaptionProducer);
        }
        Objects.requireNonNull(formWindow);
        if (config.width != null) formWindow.withWidth(config.width);
        if (config.height != null) formWindow.withHeight(config.height);
        formWindow.withUIErrorHandling(config.uiErrorHandlingMode);
        if (config.onCommitError != null) {
            formWindow.withUIErrorHandling(false);
            formWindow.withErrorActionProvider(config.onCommitError);
        }
        if (config.onCommitSuccess != null) formWindow.withSuccessActionProvider(config.onCommitSuccess);
        formWindow.setResizable(config.resizable);
        formWindow.setModal(config.modal);
        formWindow.show(config.caption);
    }

    private static <T> BeanFieldGroup<T> createBeanFieldGroup(Class<T> beanClass,
                                                              T formBean,
                                                              Map<String, String> captionToPropertyMap,
                                                              Map<String, Class<? extends Field>> propertyToFieldTypeMap) {
        BeanFieldGroup<T> bfg = new BeanFieldGroup<>(beanClass);
        bfg.setItemDataSource(formBean);
        captionToPropertyMap.entrySet().forEach(en -> {
            Class<? extends Field> fieldType = propertyToFieldTypeMap.get(en.getValue());
            fieldType = (fieldType == null) ? DEFAULT_FIELD_TYPE : fieldType;
            bfg.buildAndBind(en.getKey(), en.getValue(), fieldType);
        });
        return bfg;
    }

    public static class Config {

        private Mode mode;
        private String caption = "";
        private Consumer onCommitSuccess;
        private Consumer onCommitError;
        private Float width = 600f;
        private Float height;
        private Class beanClass;
        private Object formBean;
        private Map<String, String> captionToPropertyIdMap = Collections.emptyMap();
        private Map<String, Class<? extends Field>> propertyIdToFieldTypeMap = Collections.emptyMap();
        private boolean resizable;
        private boolean modal = true;
        private Collection rhs = Collections.EMPTY_LIST;
        private Collection lhs = Collections.EMPTY_LIST;
        private Function itemCaptionProducer = Object::toString;
        private boolean uiErrorHandlingMode = true;

        public Config(Mode windowMode) {
            this.mode = windowMode;
        }

        public Config setCaption(String caption) {
            this.caption = caption;
            return this;
        }

        public Config setUiErrorHandlingMode(boolean uiErrorHandlingMode) {
            this.uiErrorHandlingMode = uiErrorHandlingMode;
            return this;
        }

        public <T> Config setOnCommitSuccess(Consumer<T> onCommitSuccess) {
            this.onCommitSuccess = onCommitSuccess;
            return this;
        }

        public <T extends Exception> Config setOnCommitError(Consumer<T> onCommitError) {
            this.onCommitError = onCommitError;
            return this;
        }

        public Config setWidth(Float width) {
            this.width = width;
            return this;
        }

        public Config setHeight(Float height) {
            this.height = height;
            return this;
        }

        public <T> Config setBeanClass(Class<T> beanClass) {
            this.beanClass = beanClass;
            return this;
        }

        public Config setFormBean(Object formBean) {
            this.formBean = formBean;
            return this;
        }

        public Config setCaptionToPropertyIdMap(Map<String, String> captionToPropertyIdMap) {
            this.captionToPropertyIdMap = captionToPropertyIdMap;
            return this;
        }

        public Config setPropertyIdToFieldTypeMap(Map<String, Class<? extends Field>> propertyIdToFieldTypeMap) {
            this.propertyIdToFieldTypeMap = propertyIdToFieldTypeMap;
            return this;
        }

        public Config setResizable(boolean resizable) {
            this.resizable = resizable;
            return this;
        }

        public Config setModal(boolean modal) {
            this.modal = modal;
            return this;
        }

        public <T> Config setRhs(Collection<T> rhs) {
            this.rhs = rhs;
            return this;
        }

        public <T> Config setLhs(Collection<T> lhs) {
            this.lhs = lhs;
            return this;
        }

        public <T> Config setItemCaptionProducer(Function<T, String> captionProducer) {
            this.itemCaptionProducer = captionProducer;
            return this;
        }
    }

}

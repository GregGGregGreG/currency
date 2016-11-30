package org.baddev.currency.ui.core.component.view;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.baddev.currency.ui.core.component.VerticalSpacedLayout;
import org.baddev.currency.ui.core.event.SafeProcessableBinderEvent;
import org.baddev.currency.ui.core.model.fieldgroup.FieldConfig;
import org.baddev.currency.ui.core.util.ButtonFactory;
import org.baddev.currency.ui.core.util.EventBus;
import org.baddev.currency.ui.core.util.FieldGroupUtils;
import org.baddev.currency.ui.core.util.UIUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by IPotapchuk on 6/29/2016.
 */
public abstract class AbstractFormView<T> extends AbstractView
        implements FieldGroup.CommitHandler, Button.ClickListener {

    private final Class<T> beanClass;
    protected T formBean;
    private BeanFieldGroup<T> binder;

    private String panelCaption = "";
    private Alignment panelAlignment = Alignment.MIDDLE_CENTER;
    private List<String> panelStyles;

    private static final String DEF_PANEL_STYLE = ValoTheme.PANEL_WELL;

    private Set<FormCommittedListener<T>> listeners = new LinkedHashSet<>();

    private FormLayout form;

    private boolean formChangedCheckEnabled = true;
    private boolean closeOnCommitSuccessIfHasParentWindow = true;

    public interface FormCommittedListener<T> {
        void afterSubmit(T formBean);
    }

    protected AbstractFormView(Class<T> beanClass, String... panelStyles) {
        this(beanClass, instantiate(beanClass), null, panelStyles);
    }

    protected AbstractFormView(Class<T> beanClass, T formBean, String... panelStyles) {
        this(beanClass, formBean, null, panelStyles);
    }

    protected AbstractFormView(Class<T> beanClass, Alignment panelAlignment, String... panelStyles) {
        this(beanClass, instantiate(beanClass), panelAlignment, panelStyles);
    }

    protected AbstractFormView(Class<T> beanClass, T formBean, Alignment panelAlignment, String... panelStyles) {
        this.formBean = formBean;
        this.beanClass = beanClass;
        if (panelAlignment != null) this.panelAlignment = panelAlignment;
        if (panelStyles != null && panelStyles.length > 0)
            this.panelStyles = new ArrayList<>(Arrays.asList(panelStyles));
    }

    private static <T> T instantiate(Class<T> clazz){
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected final void init(VerticalSpacedLayout rootLayout) {
        form = new FormLayout();
        form.setSizeUndefined(); //to center form

        binder = new BeanFieldGroup<>(beanClass);
        binder.setItemDataSource(formBean);

        binder.addCommitHandler(this);

        Button submitBtn;
        if (getSubmitButtonMode() == null) {
            submitBtn = ButtonFactory.createSubmitBtn();
        } else submitBtn = ButtonFactory.createFormButton(getSubmitButtonMode());
        bind(submitBtn);

        customizeForm(form, binder, submitBtn);

        if (!UIUtils.isComponentExists(form, submitBtn)) form.addComponent(submitBtn);

        Label caption = new Label(panelCaption, ContentMode.HTML);
        caption.addStyleName(ValoTheme.LABEL_COLORED);
        caption.addStyleName(ValoTheme.LABEL_BOLD);

        VerticalSpacedLayout wrapper = new VerticalSpacedLayout();
        if (!StringUtils.isEmpty(panelCaption)) wrapper.addComponent(caption);
        wrapper.addComponent(form);
        wrapper.setComponentAlignment(form, Alignment.MIDDLE_CENTER);
        wrapper.setSizeFull();

        Panel panel = new Panel(wrapper);
        if (panelStyles != null && !panelStyles.isEmpty()) {
            panelStyles.forEach(panel::addStyleName);
        } else {
            panel.setStyleName(DEF_PANEL_STYLE);
        }
        panel.setSizeUndefined(); //to center panel

        rootLayout.addComponent(panel);
        rootLayout.setComponentAlignment(panel, panelAlignment);

        submitBtn.focus();
    }

    @Override
    public final void buttonClick(Button.ClickEvent clickEvent) {
        try {
            if (!formChangedCheckEnabled || binder.isModified()) {
                binder.commit();
                if (getEventType() == null) commitSuccess(formBean);
                listeners.forEach(l -> l.afterSubmit(formBean));
                closeOnCommitSuccessIfHasParentWindow();
            }
        } catch (Exception e) {
            commitFailed(binder, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void postCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
        if (getEventType() != null) {
            BiConsumer<FieldGroup, Exception> commitFailed = this::commitFailed;
            Consumer<T> commitSuccess = this::commitSuccess;
            try {
                EventBus.post(ConstructorUtils.invokeConstructor(getEventType(),
                        new Object[]{this, formBean, commitFailed, commitSuccess, commitEvent.getFieldBinder()},
                        new Class[]{Object.class, beanClass, BiConsumer.class, Consumer.class, FieldGroup.class}));
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void preCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
    }

    public final void bind(Button button) {
        button.addClickListener(this);
        button.setClickShortcut(ShortcutAction.KeyCode.ENTER);
    }

    public final void setPanelCaption(String caption) {
        this.panelCaption = caption;
    }

    public final void setFormChangedCheckEnabled(boolean formChangedCheckEnabled) {
        this.formChangedCheckEnabled = formChangedCheckEnabled;
    }

    public void setCloseOnCommitSuccessIfHasParentWindow(boolean closeOnCommitSuccessIfHasParentWindow) {
        this.closeOnCommitSuccessIfHasParentWindow = closeOnCommitSuccessIfHasParentWindow;
    }

    public void addFormCommittedListener(FormCommittedListener<T> listener) {
        listeners.add(listener);
    }

    public boolean removeFormCommittedListener(FormCommittedListener<T> listener) {
        return listeners.remove(listener);
    }

    public final boolean isFormModified() {
        return formChangedCheckEnabled && binder.isModified();
    }

    public boolean isFormChangedCheckEnabled() {
        return formChangedCheckEnabled;
    }

    public boolean isCloseOnCommitSuccessIfHasParentWindow() {
        return closeOnCommitSuccessIfHasParentWindow;
    }

    protected abstract void customizeForm(final FormLayout formLayout, final BeanFieldGroup<T> binder, final Button submitBtn);

    protected ButtonFactory.Mode getSubmitButtonMode(){
        return null;
    }

    protected Class<? extends SafeProcessableBinderEvent<T>> getEventType() {
        return null;
    }

    protected void setItem(T formBean) {
        binder.setItemDataSource(new BeanItem<>(formBean));
        this.formBean = formBean;
    }

    protected void commitSuccess(T formBean) {
    }

    protected void commitFailed(FieldGroup binder, Exception e) {
    }

    protected void configureFieldsWithDefaults(final List<? extends Field> fields) {
        fields.forEach(f -> {
            if(f.isRequired()) f.setRequiredError("field is required");
            if(f instanceof AbstractComponent){
                ((AbstractComponent) f).setImmediate(true);
            }
            if(f instanceof AbstractTextField) {
                ((AbstractTextField) f).setNullRepresentation("");
            }
            if (f instanceof AbstractTextField || f instanceof ComboBox || f instanceof NativeSelect) {
                f.setWidth(300, Unit.PIXELS);
            }
        });
    }

    protected final List<? extends Field> map(FieldConfig... fieldConfigs) {
        return map(true, fieldConfigs);
    }

    protected final List<? extends Field> map(boolean configureWithDefaults, FieldConfig... fieldConfigs) {
        List<? extends Field> fields = FieldGroupUtils.bind(binder, Arrays.asList(fieldConfigs));
        if (configureWithDefaults) configureFieldsWithDefaults(fields);
        fields.forEach(f -> form.addComponent(f));
        return fields;
    }

    private void closeOnCommitSuccessIfHasParentWindow() {
        if (closeOnCommitSuccessIfHasParentWindow)
            Optional.ofNullable(findAncestor(Window.class)).ifPresent(Window::close);
    }
}

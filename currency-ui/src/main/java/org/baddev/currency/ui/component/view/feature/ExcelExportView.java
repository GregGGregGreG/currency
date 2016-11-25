package org.baddev.currency.ui.component.view.feature;

import com.vaadin.addon.tableexport.DefaultTableHolder;
import com.vaadin.addon.tableexport.ExcelExport;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.themes.ValoTheme;
import lombok.NonNull;
import org.baddev.currency.core.security.RoleEnum;
import org.baddev.currency.ui.component.view.base.AbstractFormView;
import org.baddev.currency.ui.component.view.base.AbstractGridView;
import org.baddev.currency.ui.util.NotificationUtils;
import org.springframework.context.annotation.Scope;

import javax.annotation.security.RolesAllowed;
import java.time.format.DateTimeFormatter;

import static org.baddev.currency.ui.util.FieldGroupUtils.fConf;

/**
 * Created by IPotapchuk on 11/23/2016.
 */
@SpringComponent
@Scope("prototype")
@RolesAllowed({RoleEnum.ADMIN, RoleEnum.USER})
public class ExcelExportView extends AbstractFormView<ExcelExport> {

    public ExcelExportView(@NonNull AbstractGridView gridView) {
        super(ExcelExport.class,
                new ExcelExport(new DefaultTableHolder(gridView.getGrid()),
                        gridView.getNameCaption(),
                        gridView.getNameCaption(),
                        gridView.getNameCaption() + "-report-" +
                                java.time.LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + ".xls"),
                ValoTheme.PANEL_BORDERLESS);
        setFormChangedCheckEnabled(false);
    }

    @Override
    public String getNameCaption() {
        return "Export To Excel";
    }

    @Override
    protected void customizeForm(FormLayout formLayout, BeanFieldGroup<ExcelExport> binder, Button submitBtn) {
        map(fConf("exportFileName", "File Name", FontAwesome.FILE),
                fConf("reportTitle", "Report Title", FontAwesome.HEADER),
                fConf("displayTotals", "Totals Displayed", CheckBox.class));
    }

    @Override
    public void preCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
        formBean.excludeCollapsedColumns();
    }

    @Override
    public void postCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
        formBean.export();
    }

    @Override
    protected void commitFailed(FieldGroup binder, Exception e) {
        NotificationUtils.notifyFailure("Export Failure", "Failed to export ");
    }

}

package com.github.maplepro.kotsuite.dialogs;

import com.github.maplepro.kotsuite.util.IntelliJNotifier;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.LinkedHashMap;
import java.util.Map;

public class KotStartDialog extends DialogWrapper {

    public static Map<String, TextFieldWithBrowseButton> label2field = new LinkedHashMap<>();

    public static Map<String, String> label2Text = new LinkedHashMap<>() {{
        put("EXPORT_FOLDER", "Export Folder: ");
        put("KOTSUITE_LOCATION", "KotSuite Location: ");
        put("JAVA_HOME", "Java Home: ");
        put("AS_LOCATION", "Android Studio Location: ");
        put("APK_LOCATION", "Android APK Location: ");
    }};

    private final Project project;
    private final ToolWindow toolWindow;
    private final IntelliJNotifier notifier;

    private CustomOKAction okAction;
    private DialogWrapperExitAction exitAction;

    public KotStartDialog(Project project) {
        super(true);
        setTitle("KotSuite Options");
        this.project = project;
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        this.toolWindow = toolWindowManager.getToolWindow("kotsuite");
        this.notifier = IntelliJNotifier.getNotifier(project);
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel();
        dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));

        for (Map.Entry<String, String> entry: label2Text.entrySet()) {
            dialogPanel.add(createParameterPanel(entry.getKey(), entry.getValue()));
        }

        return dialogPanel;
    }

    /**
     * Override the default OK/Cancel actions
     */
    @Override
    protected Action @NotNull [] createActions() {
        this.okAction = new CustomOKAction("OK");
        this.exitAction = new DialogWrapperExitAction("Cancle", CANCEL_EXIT_CODE);
        okAction.putValue(DialogWrapper.DEFAULT_ACTION, true);
        return new Action[]{ okAction, exitAction };
    }

    private JComponent createParameterPanel(String labelId, String labelText) {
        JPanel parameterPanel = new JPanel(new BorderLayout());

        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(100, 16));

        TextFieldWithBrowseButton textFieldWithBrowseButton = new TextFieldWithBrowseButton();
        textFieldWithBrowseButton.setPreferredSize(new Dimension(300, 30));
        textFieldWithBrowseButton.addBrowseFolderListener("Choose File...", "", project,
                FileChooserDescriptorFactory.createSingleFileDescriptor());

        parameterPanel.add(label, BorderLayout.WEST);
        parameterPanel.add(textFieldWithBrowseButton, BorderLayout.EAST);

        label2field.put(labelId, textFieldWithBrowseButton);

        return parameterPanel;
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        for (Map.Entry<String, TextFieldWithBrowseButton> entry: label2field.entrySet()) {
            String text = entry.getValue().getText();
            if (!StringUtils.isNotBlank(text)) {
                return new ValidationInfo("Validate failed.");
            }
        }

        return null;
    }

    protected class CustomOKAction extends DialogWrapperAction {

        protected CustomOKAction(@NotNull @NlsContexts.Button String name) {
            super(name);
        }

        @Override
        protected void doAction(ActionEvent actionEvent) {
            ValidationInfo validationInfo = doValidate();
            if (validationInfo != null) {
                close(CANCEL_EXIT_CODE);

                toolWindow.show();
                notifier.clearConsole();

                for (Map.Entry<String, TextFieldWithBrowseButton> entry: label2field.entrySet()) {
                    notifier.printOnConsole("\n" + entry.getValue().getText());
                }

                notifier.printOnConsole("Validate failed.");

            } else {
                close(OK_EXIT_CODE);

                // TODO: Start KotSuiteExecutor
            }
        }
    }

}

package com.phrase.intellij.ui;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.phrase.intellij.ClientDetection;
import com.phrase.intellij.PropertiesRepository;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Created by gfrey on 22/10/15.
 */
public class PhraseConfigurable implements SearchableConfigurable, Configurable.NoScroll {
    private JPanel rootPanel;
    private TextFieldWithBrowseButton clientPathFormattedTextField;
    private InfoPane infoPane;

    private boolean modified = false;


    @Override
    public void disposeUIResources() {
        if (rootPanel != null) {
            rootPanel.removeAll();
            rootPanel = null;
        }
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return "The Phrase plugin requires a installed Phrase CLI Client and a configuration file.";
    }

    @Override
    public void reset() {
        String clientPath = PropertiesRepository.getInstance().getClientPath();
        if (clientPath == null || clientPath.isEmpty()) {
            String guessedClientPath = ClientDetection.findClientInstallation();
            if (guessedClientPath != null) {
                clientPathFormattedTextField.setText(guessedClientPath);
                modified = true;
                JOptionPane.showMessageDialog(rootPanel, "A Phrase client was found at: " + guessedClientPath);
            }
        } else {
            clientPathFormattedTextField.setText(clientPath);
            modified = false;
        }
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        initializeActions();
        infoPane.setContent("<p>The Phrase plugin requires the <b>Phrase CLI client</b> and a configuration file. <a href=https://help.phrase.com/help/android-android-studio>Learn more</a>.</p>");
        return rootPanel;
    }

    private void initializeActions() {
        final FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(true, false, false, false, false, false) {
            public boolean isFileSelectable(VirtualFile file) {
                return file.getName().startsWith("phrase");
            }
        };
        clientPathFormattedTextField.addBrowseFolderListener("Choose Phrase client", "", null, fileChooserDescriptor);

        final JTextField clientPathTextField = clientPathFormattedTextField.getTextField();
        clientPathTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                handleUpdate();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                handleUpdate();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {}

            private void handleUpdate() {
                modified = true;
            }
        });

        infoPane.initializeActions();
    }


    @Nls
    @Override
    public String getDisplayName() {
        return "Phrase";
    }

    @Override
    public void apply() {
        PropertiesRepository.getInstance().setClientPath(getClientPath());
        modified = false;
    }

    @Override
    public boolean isModified() {
        return modified;
    }


    @Nullable
    @Override
    public Runnable enableSearch(String option) {
        return null;
    }

    @NotNull
    @Override
    public String getId() {
        return "phrase";
    }

    @NotNull
    private String getClientPath() {
        return clientPathFormattedTextField.getText().trim();
    }
}



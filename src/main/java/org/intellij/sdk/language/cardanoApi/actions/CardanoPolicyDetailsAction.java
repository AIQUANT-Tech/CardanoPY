package org.intellij.sdk.language.cardanoApi.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import org.intellij.sdk.language.cardanoApi.CardanoScanFetcher;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CardanoPolicyDetailsAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            Messages.showErrorDialog("Project is not available.", "Error");
            return;
        }

        CardanoScanFetcher fetcher = new CardanoScanFetcher(project);

        // Check if API key exists
        String apiKey = fetcher.ensureApiKey();
        if (apiKey == null || apiKey.trim().isEmpty()) {
            Messages.showErrorDialog(project, "API key is missing. Please enter a valid API key.", "API Key Missing");
            return;
        }

        // Show the query parameter dialog
        QueryParameterDialog dialog = new QueryParameterDialog();
        if (dialog.showAndGet()) {
            Map<String, String> queryParams = dialog.getQueryParameters();

            if (queryParams != null && !queryParams.isEmpty()) {
                StringBuilder queryString = new StringBuilder();
                queryParams.forEach((key, value) -> queryString.append(key).append("=").append(value).append("&"));

                // Remove the trailing ampersand
                if (!queryString.isEmpty()) {
                    queryString.setLength(queryString.length() - 1);
                }

                fetcher.fetchPolicyDetails(queryString.toString());
            } else {
                Messages.showErrorDialog(project, "Please provide valid query parameters.", "Invalid Parameters");
            }
        }
    }

    private static class QueryParameterDialog extends DialogWrapper {
        private JTextField policyIdField;
        private JTextField pageNoField;
        private JTextField limitField;

        protected QueryParameterDialog() {
            super(true); // Use current window as parent
            setTitle("Enter Query Parameter");
            init();
        }

        @Override
        protected @Nullable JComponent createCenterPanel() {
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Input fields
            policyIdField = new JTextField();
            pageNoField = new JTextField();
            limitField = new JTextField();

            gbc.gridx = 0;
            gbc.gridy = 0;
            panel.add(new JLabel("Policy ID (56 characters):"), gbc);
            gbc.gridx = 1;
            panel.add(policyIdField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            panel.add(new JLabel("Page Number (1-10000):"), gbc);
            gbc.gridx = 1;
            panel.add(pageNoField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            panel.add(new JLabel("Limit (1-50, Default: 20):"), gbc);
            gbc.gridx = 1;
            panel.add(limitField, gbc);

            return panel;
        }

        public Map<String, String> getQueryParameters() {
            Map<String, String> params = new HashMap<>();
            try {
                // Policy ID validation
                String policyId = policyIdField.getText().trim();
                if (policyId.length() == 56) {
                    params.put("policyId", policyId);
                } else {
                    Messages.showErrorDialog("Policy ID must be exactly 56 characters.", "Invalid Input");
                    return null;
                }

                // Page number validation
                String pageNo = pageNoField.getText().trim();
                if (!pageNo.isEmpty()) {
                    int pageNumber = Integer.parseInt(pageNo);
                    if (pageNumber >= 1 && pageNumber <= 10000) {
                        params.put("pageNo", pageNo);
                    } else {
                        Messages.showErrorDialog("Page number must be between 1 and 10000.", "Invalid Input");
                        return null;
                    }
                } else {
                    Messages.showErrorDialog("Page number is required.", "Invalid Input");
                    return null;
                }

                // Limit validation
                String limit = limitField.getText().trim();
                if (!limit.isEmpty()) {
                    int limitValue = Integer.parseInt(limit);
                    if (limitValue >= 1 && limitValue <= 50) {
                        params.put("limit", limit);
                    } else {
                        Messages.showErrorDialog("Limit must be between 1 and 50.", "Invalid Input");
                        return null;
                    }
                } else {
                    // If limit is not provided, use the default value
                    params.put("limit", "20");
                }

            } catch (NumberFormatException ex) {
                Messages.showErrorDialog("Invalid numeric input.", "Invalid Input");
                return null;
            }

            return params;
        }
    }
}

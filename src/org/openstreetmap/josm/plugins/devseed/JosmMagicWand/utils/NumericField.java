package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils;

import javax.swing.*;
import javax.swing.text.DocumentFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class NumericField extends JTextField {
    public NumericField(boolean allowDecimal) {
        super();
        ((PlainDocument) this.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (validateInput(string, allowDecimal)) {
                    super.insertString(fb, offset, string, attr);
                    firePropertyChange("text", "", getText());
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (validateInput(text, allowDecimal)) {
                    super.replace(fb, offset, length, text, attrs);
                    firePropertyChange("text", "", getText());
                }
            }

            private boolean validateInput(String text, boolean allowDecimal) {
                if (allowDecimal) {
                    return text.matches("^\\d+(\\.\\d+)?$");
                } else {
                    return text.matches("\\d*");
                }
            }
        });
    }
}
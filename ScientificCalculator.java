import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ScientificCalculator extends JFrame implements ActionListener {
    private JTextField textField;

    public ScientificCalculator() {
        setTitle("Scientific Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        textField = new JTextField();
        textField.setEditable(false);
        textField.setFont(new Font("Arial", Font.PLAIN, 24));
        add(textField, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 4, 5, 5));

        String[] buttons = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", ".", "=", "+",
                "C", "sqrt", "sin", "cos"
        };

        for (String button : buttons) {
            JButton btn = new JButton(button);
            btn.setFont(new Font("Arial", Font.BOLD, 18));
            btn.addActionListener(this);
            buttonPanel.add(btn);
        }

        // Customize the "C" and "=" buttons
        JButton clearButton = (JButton) buttonPanel.getComponent(16);
        clearButton.setBackground(new Color(255, 51, 51)); // Red color
        clearButton.setForeground(Color.WHITE); // White text color
        clearButton.setFont(new Font("Arial", Font.BOLD, 18));
        clearButton.addActionListener(this);

        JButton equalButton = (JButton) buttonPanel.getComponent(14);
        equalButton.setBackground(new Color(0, 153, 0)); // Green color
        equalButton.setForeground(Color.WHITE); // White text color
        equalButton.setFont(new Font("Arial", Font.BOLD, 18));
        equalButton.addActionListener(this);

        add(buttonPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        String currentText = textField.getText();

        switch (command) {
            case "=":
                try {
                    double result = evaluateExpression(currentText);
                    textField.setText(Double.toString(result));
                } catch (NumberFormatException ex) {
                    textField.setText("Error");
                }
                break;

            case "C":
                textField.setText("");
                break;

            case "sqrt":
                try {
                    double sqrtResult = Math.sqrt(Double.parseDouble(currentText));
                    textField.setText(Double.toString(sqrtResult));
                } catch (NumberFormatException ex) {
                    textField.setText("Error");
                }
                break;

            case "sin":
                try {
                    double sinResult = Math.sin(Math.toRadians(Double.parseDouble(currentText)));
                    textField.setText(Double.toString(sinResult));
                } catch (NumberFormatException ex) {
                    textField.setText("Error");
                }
                break;

            case "cos":
                try {
                    double cosResult = Math.cos(Math.toRadians(Double.parseDouble(currentText)));
                    textField.setText(Double.toString(cosResult));
                } catch (NumberFormatException ex) {
                    textField.setText("Error");
                }
                break;

            default:
                textField.setText(currentText + command);
                break;
        }
    }

    private double evaluateExpression(String expression) {
        try {
            return (double) new Object() {
                int pos = -1;
                int ch;

                void nextChar() {
                    ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
                }

                boolean isDigit(char c) {
                    return Character.isDigit(c);
                }

                double parse() {
                    nextChar();
                    double x = parseExpression();
                    if (pos < expression.length()) {
                        throw new RuntimeException("Unexpected: " + (char) ch);
                    }
                    return x;
                }

                double parseExpression() {
                    double x = parseTerm();
                    while (true) {
                        if (eat('+')) {
                            x += parseTerm();
                        } else if (eat('-')) {
                            x -= parseTerm();
                        } else {
                            return x;
                        }
                    }
                }

                double parseTerm() {
                    double x = parseFactor();
                    while (true) {
                        if (eat('*')) {
                            x *= parseFactor();
                        } else if (eat('/')) {
                            x /= parseFactor();
                        } else {
                            return x;
                        }
                    }
                }

                double parseFactor() {
                    if (eat('+')) {
                        return parseFactor();
                    }
                    if (eat('-')) {
                        return -parseFactor();
                    }

                    double x;
                    int startPos = pos;
                    if (eat('(')) {
                        x = parseExpression();
                        eat(')');
                    } else if (isDigit((char) ch) || ch == '.') {
                        while (isDigit((char) ch) || ch == '.') {
                            nextChar();
                        }
                        x = Double.parseDouble(expression.substring(startPos, pos));
                    } else {
                        throw new RuntimeException("Unexpected: " + (char) ch);
                    }

                    return x;
                }

                boolean eat(int charToEat) {
                    while (ch == ' ') {
                        nextChar();
                    }
                    if (ch == charToEat) {
                        nextChar();
                        return true;
                    }
                    return false;
                }
            }.parse();
        } catch (Exception ex) {
            throw new RuntimeException("Error evaluating expression: " + expression);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ScientificCalculator();
            }
        });
    }
}






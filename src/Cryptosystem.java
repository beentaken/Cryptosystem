import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.text.DefaultEditorKit;

/**
 * A program that simulate some cryptography system and perform some algorithms
 * to encrypt and decrypt messages. This program have three types of algorithms,
 * ElGamal, RSA, and Knapsack algorithms. There are also a Prime Generator
 * tool and configuration for the maximum value for the keys used in the
 * program.
 * 
 * This program was influenced from the book, Discrete Mathematics and Its 
 * Applications 7th Edition (Chapter 4 Number Theory and Cryptography. This
 * program can only encrypt alphabetic characters, and it encrypts two
 * characters at once. The resulting cipher text will be in the format of only
 * numbers.
 * 
 * Alphabetic to number conversion for this program:
 * A: 00    G: 06    M: 12    S: 18    Y: 24
 * B: 01    H: 07    N: 13    T: 19    Z: 25
 * C: 02    I: 08    O: 14    U: 20
 * D: 03    J: 09    P: 15    V: 21
 * E: 04    K: 10    Q: 16    W: 22
 * F: 05    L: 11    R: 17    X: 23
 * 
 * @author Su Khai Koh
 */
public class Cryptosystem extends JFrame {

    private JMenuBar menuBar;
    private JPanel leftPanel, centerPanel, rightPanel, egPanel, rsaPanel, ksPanel;
    private JRadioButton encryptRadioButton, decryptRadioButton;
    private JTextArea inputTextArea, resultTextArea;
    private JButton newKeyButton, submitButton;
    private JLabel headerLabel;
    private JTextField textField1, textField2, textField3, textField4, textField5;
    private JComboBox<String> algorithmSelectionList;
    private String message, egCipherText, nonEGCipherText;
    private int selectedAlgorithm;
    private ElGamal eg;
    private RSA rsa;
    private Knapsack ks;

    private static final int ELGAMAL  = 1,
                             RSA      = 2,
                             KNAPSACK = 3;
    
    private GridBagConstraints c = new GridBagConstraints();
    
    /**
     * Default constructor. Initiate ElGamal, RSA, and Knapsack objects, and
     * also setup all other components to the frame.
     */
    public Cryptosystem() {
        
        eg = new ElGamal();
        rsa = new RSA();
        ks = new Knapsack();
        
        // Setup menu bar and sub-panels
        setupMenuBar();
        setupLeftPanel();        
        setupCenterPanel();
        setupRightPanel();
        
        // Setup layout and add components to the frame
        setLayout(new BorderLayout());
        setJMenuBar(menuBar);
        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }
    
    /**
     * Setup a menu bar for the program.
     */
    private void setupMenuBar() {
        
        menuBar = new JMenuBar();
        
        // Add a function menu
        JMenu menu = new JMenu("Menu");
        menu.setMnemonic(KeyEvent.VK_M);
        menuBar.add(menu);
        
        // Allow user to reset the maximum value for all algorithms
        JMenuItem maxValueMenuItem = new JMenuItem("Change Max Value");
        maxValueMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayMaxValueDialog();
            }
        });
        menu.add(maxValueMenuItem);
        
        // Allow user to generate a random prime number within given range
        JMenuItem primeMenuItem = new JMenuItem("Prime Generator");
        primeMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayPrimeDialog();
            }
        });
        menu.add(primeMenuItem);
        
        // Exit button
        menu.addSeparator();
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.ALT_MASK));
        exitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        menu.add(exitMenuItem);
        
        // Add space between two menu items
        JMenu empty = new JMenu(" ");
        empty.setEnabled(false);
        menuBar.add(empty);
    }
    
    /**
     * Display window for user to set the maximum value for all the algorithms.
     * The maximum value is the value that control the values for certain keys
     * in the algorithms. These keys included ElGamal a and p keys, RSA p and q
     * keys, and Knapsack a key.
     */
    private void displayMaxValueDialog() {
        // Show current max value
        String displayStr = "Current Max Value: "+eg.getMaxValue()+
                "\nEnter new max value:";
        
        // A text field that receive a new max value from user
        String newMaxValue = 
                JOptionPane.showInputDialog(null, displayStr, 
                        "Maximum Value", JOptionPane.QUESTION_MESSAGE);

        // Reformat the input text
        newMaxValue = newMaxValue == null ? "" : newMaxValue.trim();

        // Display error message if user inputed invalid value
        if (!newMaxValue.matches("[0-9]+") && !newMaxValue.isEmpty()) {

            JOptionPane.showMessageDialog(null, "Invalid input", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (newMaxValue.isEmpty())
            return;

        // Set new max value to all algorithms
        eg.setMaxValue(newMaxValue);
        rsa.setMaxValue(newMaxValue);
        ks.setMaxValue(newMaxValue);

        // Regenerate all keys for all algorithms
        eg.generateAllKeys();
        rsa.generateAllKeys();
        ks.generateAllKeys();
    }
    
    /**
     * Display a window for user to get a random prime number within the given
     * range.
     */
    private void displayPrimeDialog() {
        
        // Create a custom panel
        JPanel panel = new JPanel(new GridLayout(2, 1));
        
        JLabel label = new JLabel(" Enter the range");
        panel.add(label);
        
        JPanel subpanel = new JPanel(new FlowLayout());
        
        JLabel labelFrom = new JLabel("From");
        JTextField tfFrom = new JTextField(5);
        subpanel.add(labelFrom);
        subpanel.add(tfFrom);
        
        JLabel labelTo = new JLabel("To");
        JTextField tfTo = new JTextField(5);
        subpanel.add(labelTo);
        subpanel.add(tfTo);
        
        panel.add(subpanel);
        
        // Display the custom panel inside a dialog
        JOptionPane.showMessageDialog(null, panel, "Prime Number Generator", 
                JOptionPane.QUESTION_MESSAGE);
        
        // Reformat the input text
        String from = tfFrom.getText();
        from = from == null ? "" : from.trim();
        
        String to = tfTo.getText();
        to = to == null ? "" : to.trim();
             
        // If user entered valid values
        if (from.matches("[0-9]+") && to.matches("[0-9]+")) {
            long low = Long.parseLong(from);
            long high = Long.parseLong(to);
            
            String prime = Prime.getPrime(low, high).toString();
            
            JTextField tfPrime = new JTextField(prime);
            tfPrime.setEditable(false);
            
            // Show the prime in text field
            JOptionPane.showMessageDialog(null, tfPrime, "Prime Number",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Setup the left panel, which holds the keys for the algorithms.
     */
    private void setupLeftPanel() {
        
        leftPanel = new JPanel(new GridBagLayout());

        egPanel = new JPanel(new GridBagLayout());
        rsaPanel = new JPanel(new GridBagLayout());
        ksPanel = new JPanel(new GridBagLayout());
        
        buildAlgorithmSelectionList();
        buildNewKeyButton();
        buildEGPanel();
    }
    
    /**
     * Build a button that allows user to generate new keys for the selected
     * algorithm.
     */
    private void buildNewKeyButton() {
        
        newKeyButton = new JButton("New Keys");
        newKeyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                switch (selectedAlgorithm) {
                    case ELGAMAL:
                        eg.generateAllKeys();
                        textField1.setText(eg.getA());
                        textField2.setText(eg.getK());
                        textField3.setText(eg.getP());
                        textField4.setText(eg.getG());
                        textField5.setText(eg.getR());
                        break;
                        
                    case RSA:
                        rsa.generateAllKeys();
                        textField1.setText(rsa.getP());
                        textField2.setText(rsa.getQ());
                        textField3.setText(rsa.getD());
                        textField4.setText(rsa.getN());
                        textField5.setText(rsa.getE());
                        break;
                        
                    case KNAPSACK:
                        ks.generateAllKeys();
                        textField1.setText(ks.getM());
                        textField2.setText(ks.getA());
                        textField3.setText(ks.getS());
                        textField4.setText(ks.getW());
                        break;
                        
                    default: break;
                }
                
                // Change the font's color to back to black
                textField1.setForeground(Color.black);
                textField2.setForeground(Color.black);
                textField3.setForeground(Color.black);
                textField4.setForeground(Color.black);
                textField5.setForeground(Color.black);
            }
        });
    }
    
    /**
     * Build a selection list which allows user to select the algorithm she 
     * wants to perform encryption or decryption.
     */
    private void buildAlgorithmSelectionList() {
        
        // Encrypt radio button
        encryptRadioButton = new JRadioButton("Encrypt");
        encryptRadioButton.setSelected(true);
        encryptRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                encryptRadioButton.setSelected(true);
                decryptRadioButton.setSelected(false);
                submitButton.setText("Encrypt");
                headerLabel.setText("Message");
                displaySampleText();
            }
        });

        // Decrypt radio button
        decryptRadioButton = new JRadioButton("Decrypt");
        decryptRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                encryptRadioButton.setSelected(false);
                decryptRadioButton.setSelected(true);
                submitButton.setText("Decrypt");
                headerLabel.setText("Cipher Text");
                displaySampleText();
            }
        });
        
        // Algorithm list
        String[] algorithms = {"ElGamal", "RSA", "Knapsack"};

        algorithmSelectionList = new JComboBox<String>(algorithms);
        algorithmSelectionList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                @SuppressWarnings("unchecked")
                JComboBox<String> list = (JComboBox<String>)e.getSource();
                String selected = (String) list.getSelectedItem();

                switch (selected) {
                    case "ElGamal" : buildEGPanel();    break;
                    case "RSA"     : buildRSAPanel();   break;
                    case "Knapsack": buildKSPanel();    break;
                    default: break;
                }
                
                displaySampleText();
                
                repaint();
                pack();
            }
        });
        
        // Create a sub-panel and add all the components into it
        JPanel subPanel = new JPanel();
        subPanel.setLayout(new GridLayout(1, 3));
        subPanel.add(encryptRadioButton);
        subPanel.add(decryptRadioButton);
        subPanel.add(algorithmSelectionList);
        
        setComponent(leftPanel, subPanel, 0, 0, 3, 1, 1, 1, 0, new Insets(5,0,0,0));
    }
    
    /**
     * Build a panel to hold all keys value for RSA algorithm.
     */
    private void buildRSAPanel() {
        
        // Do nothing if the current algorithm is already an RSA
        if (selectedAlgorithm == RSA) return;
        
        selectedAlgorithm = RSA;
        
        leftPanel.remove(egPanel);
        leftPanel.remove(ksPanel);
        
        // Rebuild all components
        rsaPanel.removeAll();
        rsaPanel.setBorder(BorderFactory.createTitledBorder("RSA"));
        rsaPanel.setPreferredSize(new Dimension(278, 200));
        
        // Create labels
        JLabel privateKeysLabel = new JLabel("Private Keys");
        JLabel publicKeysLabel = new JLabel("Public Keys");
        JLabel pLabel = new JLabel("p");
        JLabel qLabel = new JLabel("q");
        JLabel dLabel = new JLabel("d");
        JLabel nLabel = new JLabel("n");
        JLabel eLabel = new JLabel("e");
        
        // Instantiate text fields
        textField1 = new JTextField(4);
        textField1.setText(rsa.getP());
        
        textField2 = new JTextField(4);
        textField2.setText(rsa.getQ());
        
        textField3 = new JTextField(4);
        textField3.setText(rsa.getD());
        
        textField4 = new JTextField(4);
        textField4.setText(rsa.getN());
        
        textField5 = new JTextField(4);
        textField5.setText(rsa.getE());
        
        // Place the components into the ElGamal panel
        setComponent(rsaPanel, privateKeysLabel, 0, 0, 13, 1, 1, 2, 2, new Insets(5,15,5,15));
        setComponent(rsaPanel, pLabel, 0, 1, 1, 1, 0, 0, 0, new Insets(0,15,5,0));
        setComponent(rsaPanel, textField1, 1, 1, 3, 1, 0, 0, 0, new Insets(0,1,5,10));
        setComponent(rsaPanel, qLabel, 5, 1, 1, 1, 0, 0, 0, new Insets(0,5,5,0));
        setComponent(rsaPanel, textField2, 6, 1, 3, 1, 0, 0, 0, new Insets(0,1,5,10));
        setComponent(rsaPanel, dLabel, 9, 1, 1, 1, 0, 0, 0, new Insets(0,5,5,0));
        setComponent(rsaPanel, textField3, 10, 1, 3, 1, 0, 0, 0, new Insets(0,1,5,10));
        setComponent(rsaPanel, new JSeparator(), 0, 2, 13, 1, 1, 2, 2, new Insets(0,7,0,7));
        setComponent(rsaPanel, publicKeysLabel, 0, 3, 13, 1, 1, 2, 2, new Insets(5,15,5,15));
        setComponent(rsaPanel, nLabel, 0, 4, 1, 1, 0, 0, 0, new Insets(0,15,5,0));
        setComponent(rsaPanel, textField4, 1, 4, 3, 1, 0, 0, 0, new Insets(0,1,5,10));
        setComponent(rsaPanel, eLabel, 5, 4, 1, 1, 0, 0, 0, new Insets(0,5,5,0));
        setComponent(rsaPanel, textField5, 6, 4, 3, 1, 0, 0, 0, new Insets(0,1,5,10));

        setComponent(leftPanel, rsaPanel, 0, 1, 4, 3, 1, 1, 1, new Insets(10,5,0,5));
        setComponent(leftPanel, newKeyButton, 0, 4, 1, 1, 0, 0, 0, new Insets(5,10,10,15));
    }
    
    /**
     * Build a panel to hold all keys value for Knapsack algorithm.
     */
    private void buildKSPanel() {
        
        // Do nothing if the selected algorithm is already a Knapsack
        if (selectedAlgorithm == KNAPSACK) return;
        
        selectedAlgorithm = KNAPSACK;
        
        leftPanel.remove(egPanel);
        leftPanel.remove(rsaPanel);
        
        // Rebuild all components
        ksPanel.removeAll();
        ksPanel.setBorder(BorderFactory.createTitledBorder("Knapsack"));
        ksPanel.setPreferredSize(new Dimension(278, 200));
        
        // Create labels
        JLabel privateKeysLabel = new JLabel("Private Keys");
        JLabel publicKeysLabel = new JLabel("Public Keys");
        JLabel mLabel = new JLabel("m");
        JLabel aLabel = new JLabel("a");
        JLabel sLabel = new JLabel("S");
        JLabel wLabel = new JLabel("W");
        
        // Instantiate text fields
        textField1 = new JTextField(4);
        textField1.setText(ks.getM());
        
        textField2 = new JTextField(4);
        textField2.setText(ks.getA());
        
        textField3 = new JTextField(18);
        textField3.setText(ks.getS());
        
        textField4 = new JTextField(18);
        textField4.setText(ks.getW());
        
        // Place the components into the ElGamal panel
        setComponent(ksPanel, privateKeysLabel, 0, 0, 13, 1, 1, 2, 2, new Insets(5,15,5,15));
        setComponent(ksPanel, mLabel, 0, 1, 1, 1, 0, 0, 0, new Insets(0,15,5,0));
        setComponent(ksPanel, textField1, 1, 1, 3, 1, 0, 0, 0, new Insets(0,1,5,10));
        setComponent(ksPanel, aLabel, 5, 1, 1, 1, 0, 0, 0, new Insets(0,5,5,0));
        setComponent(ksPanel, textField2, 6, 1, 3, 1, 0, 0, 0, new Insets(0,1,5,10));
        setComponent(ksPanel, sLabel, 0, 2, 1, 1, 0, 0, 0, new Insets(0,15,5,0));
        setComponent(ksPanel, textField3, 1, 2, 12, 1, 1, 2, 2, new Insets(0,1,5,10));
        setComponent(ksPanel, new JSeparator(), 0, 3, 13, 1, 1, 2, 2, new Insets(0,7,0,7));
        setComponent(ksPanel, publicKeysLabel, 0, 4, 13, 1, 1, 2, 2, new Insets(5,15,5,15));
        setComponent(ksPanel, wLabel, 0, 5, 1, 1, 0, 0, 0, new Insets(0,15,5,0));
        setComponent(ksPanel, textField4, 1, 5, 12, 1, 1, 2, 2, new Insets(0,1,5,10));

        setComponent(leftPanel, ksPanel, 0, 1, 4, 3, 1, 1, 1, new Insets(10,5,0,5));
        setComponent(leftPanel, newKeyButton, 0, 4, 1, 1, 0, 0, 0, new Insets(5,10,10,15));
    }
    
    /**
     * Build a panel to hold all keys value for ElGamal algorithm.
     */
    private void buildEGPanel() {
        
        if (selectedAlgorithm == ELGAMAL) return;
        
        selectedAlgorithm = ELGAMAL;
        
        leftPanel.remove(rsaPanel);
        leftPanel.remove(ksPanel);
        
        // Rebuild all components
        egPanel.removeAll();
        egPanel.setBorder(BorderFactory.createTitledBorder("ElGamal"));
        egPanel.setPreferredSize(new Dimension(278, 200));
        
        // Create labels
        JLabel privateKeysLabel = new JLabel("Private Keys");
        JLabel publicKeysLabel = new JLabel("Public Keys");
        JLabel aLabel = new JLabel("a");
        JLabel kLabel = new JLabel("k");
        JLabel pLabel = new JLabel("p");
        JLabel gLabel = new JLabel("g");
        JLabel rLabel = new JLabel("r");
        
        // Instantiate text fields
        textField1 = new JTextField(4);
        textField1.setText(eg.getA());
        
        textField2 = new JTextField(4);
        textField2.setText(eg.getK());
        
        textField3 = new JTextField(4);
        textField3.setText(eg.getP());
        
        textField4 = new JTextField(4);
        textField4.setText(eg.getG());
        
        textField5 = new JTextField(4);
        textField5.setText(eg.getR());
        
        // Place the components into the ElGamal panel
        setComponent(egPanel, privateKeysLabel, 0, 0, 13, 1, 1, 2, 2, new Insets(5,15,5,15));
        setComponent(egPanel, aLabel, 0, 1, 1, 1, 0, 0, 0, new Insets(0,15,5,0));
        setComponent(egPanel, textField1, 1, 1, 3, 1, 0, 0, 0, new Insets(0,1,5,10));
        setComponent(egPanel, kLabel, 5, 1, 1, 1, 0, 0, 0, new Insets(0,5,5,0));
        setComponent(egPanel, textField2, 6, 1, 3, 1, 0, 0, 0, new Insets(0,1,5,10));
        setComponent(egPanel, new JSeparator(), 0, 2, 13, 1, 1, 2, 2, new Insets(0,7,0,7));
        setComponent(egPanel, publicKeysLabel, 0, 3, 13, 1, 1, 2, 2, new Insets(5,15,5,15));
        setComponent(egPanel, pLabel, 0, 4, 1, 1, 0, 0, 0, new Insets(0,15,5,0));
        setComponent(egPanel, textField3, 1, 4, 3, 1, 0, 0, 0, new Insets(0,1,5,10));
        setComponent(egPanel, gLabel, 5, 4, 1, 1, 0, 0, 0, new Insets(0,5,5,0));
        setComponent(egPanel, textField4, 6, 4, 3, 1, 0, 0, 0, new Insets(0,1,5,10));
        setComponent(egPanel, rLabel, 9, 4, 1, 1, 0, 0, 0, new Insets(0,5,5,0));
        setComponent(egPanel, textField5, 10, 4, 3, 1, 0, 0, 0, new Insets(0,1,5,10));

        setComponent(leftPanel, egPanel, 0, 1, 4, 3, 1, 1, 1, new Insets(10,5,0,5));
        setComponent(leftPanel, newKeyButton, 0, 4, 1, 1, 0, 0, 0, new Insets(5,10,10,15));
    }
    
    /**
     * Setup a center panel to hold a text area for user input.
     */
    private void setupCenterPanel() {
        
        // Assign example texts
        message = "Example";
        egCipherText = "(1234, 5678)\n(1122 3344)\n5566, 7788\n9900 0011";
        nonEGCipherText = "(1234 5678 1122 3344)\n5566 7788 9900 0011";
        
        centerPanel = new JPanel(new GridBagLayout());
        
        headerLabel = new JLabel("Message");
        
        // Add input text area into sub-panel
        inputTextArea = new JTextArea();
        inputTextArea.addFocusListener(new FocusListener() {

            // When user clicked on the text area, clear example text and
            // change the font color back to black
            public void focusGained(FocusEvent e) {
                if (inputTextArea.getForeground() == Color.gray) {
                    inputTextArea.setText("");
                    inputTextArea.setForeground(Color.black);
                }
            }

            // When user clicked outside the text area, call displaySampleText()
            // method
            public void focusLost(FocusEvent e) {
                displaySampleText();
            }
        });
        
        JScrollPane scrollpane = new JScrollPane(inputTextArea);
        scrollpane.setPreferredSize(new Dimension(150, 184));
        
        displaySampleText();
        
        JPanel subPanel = new JPanel(new GridBagLayout());
        setComponent(subPanel, headerLabel, 0, 0, 1, 1, 0, 0, 0, new Insets(0,5,0,0));
        setComponent(subPanel, scrollpane, 0, 1, 5, 3, 1, 2, 2, null);
        
        submitButton = new JButton("Encrypt");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                boolean validInput = checkUserInputs();
                
                if (validInput)
                    performAlgorithm();
            }
        });
        
        // Add sub-panel and submit button into center panel
        setComponent(centerPanel, new JLabel(" "), 0, 0, 5, 1, 1, 2, 2, null);
        setComponent(centerPanel, subPanel, 0, 1, 5, 3, 1, 2, 2, new Insets(15,0,0,0));
        setComponent(centerPanel, submitButton, 0, 4, 2, 1, 0, 0, 0, new Insets(5,5,10,15));
    }
    
    /**
     * Setup right panel, which hold the result text area.
     */
    private void setupRightPanel() {
        
        rightPanel = new JPanel(new GridBagLayout());
               
        JLabel resultLabel = new JLabel("Result");
        
        resultTextArea = new JTextArea();
        resultTextArea.setEditable(false);
        resultTextArea.setLineWrap(true);
        resultTextArea.setWrapStyleWord(true);
        JScrollPane scrollpane = new JScrollPane(resultTextArea);
        scrollpane.setPreferredSize(new Dimension(150, 184));
        
        // Add result text area into sub-panel
        JPanel subPanel = new JPanel(new GridBagLayout());
        setComponent(subPanel, resultLabel, 0, 0, 1, 1, 0, 0, 0, new Insets(0,5,0,0));
        setComponent(subPanel, scrollpane, 0, 1, 5, 3, 1, 2, 2, null);
        
        // Add sub-panel and submit button into center panel
        setComponent(rightPanel, new JLabel(" "), 0, 0, 5, 1, 1, 2, 2, null);
        setComponent(rightPanel, subPanel, 0, 1, 5, 3, 1, 2, 2, new Insets(0,0,0,5));
        setComponent(rightPanel, new JLabel(" "), 0, 4, 2, 1, 0, 0, 0, new Insets(12,5,10,15));
    }
    
    /**
     * Perform the selected algorithm, either encryption or decryption based on 
     * user selected.
     */
    private void performAlgorithm() {
        
        // Get user input
        String userInput = inputTextArea.getText();
        
        String result;
        
        Algorithm algorithm;
        
        // Determine which algorithm and then pass all the keys to it
        switch (selectedAlgorithm) {
            case RSA:
                rsa.setPrivateKeys(textField1.getText(), textField2.getText(), textField3.getText());
                rsa.setPublicKeys(textField4.getText(), textField5.getText());
                algorithm = rsa;
                break;
                
            case KNAPSACK:
                ks.setPrivateKeys(textField1.getText(), textField2.getText(), textField3.getText());
                ks.setPublicKeys(textField4.getText());
                algorithm = ks;
                break;
            default:
                eg.setPrivateKeys(textField1.getText(), textField2.getText());
                eg.setPublicKeys(textField3.getText(), textField4.getText(), textField5.getText());
                algorithm = eg;
                break;
        }
        
        // Perform either encrypt or decrypt the message
        result = encryptRadioButton.isSelected() ? 
                algorithm.encrypt(userInput) : algorithm.decrypt(userInput);
      
        // Show the result on the text area
        resultTextArea.setText(result == null ? "Invalid input" : result);
    }
    
    /**
     * Check all the keys value inputed by the user are valid, such as they
     * have to be all numeric.
     * @return true if user inputed valid keys, otherwise false
     */
    private boolean checkUserInputs() {
        
        boolean valid = true;
        
        if (!textField1.getText().trim().matches("[0-9]+")) {
            textField1.setForeground(Color.red);;
            valid = false;
        } else {
           textField1.setForeground(Color.black);
        }
        
        if (!textField2.getText().trim().matches("[0-9]+")) {
            // For ElGamal k value, user can leave k value blank for random
            // generate k value in each block of the message
            if (selectedAlgorithm == ELGAMAL && textField2.getText().trim().isEmpty())
                textField2.setForeground(Color.black);
            else {
                textField2.setForeground(Color.red);
                valid = false;
            }
        } else {
           textField2.setForeground(Color.black);
        }
        
        if (!textField3.getText().trim().matches("[0-9]+")) {
            // For Knapsack S key, the input can contain anything except
            // alphabet characters
            if (selectedAlgorithm == KNAPSACK && 
                    !textField3.getText().trim().matches(".*[a-zA-Z].*") &&
                    !textField3.getText().trim().isEmpty())
                textField3.setForeground(Color.black);
            else {
                textField3.setForeground(Color.red);;
                valid = false;
            }
        } else {
           textField3.setForeground(Color.black);
        }
                
        if (!textField4.getText().trim().matches("[0-9]+")) {
            // For Knapsack W key, the input can contain anything except
            // alphabet characters
            if (selectedAlgorithm == KNAPSACK && 
                    !textField4.getText().trim().matches(".*[a-zA-Z].*") &&
                    !textField4.getText().trim().isEmpty())
                textField4.setForeground(Color.black);
            else {
                textField4.setForeground(Color.red);;
                valid = false;
            }
        } else {
           textField4.setForeground(Color.black);
        }
        
        if (!textField5.getText().trim().matches("[0-9]+")) {
            textField5.setForeground(Color.red);;
            valid = false;
        } else {
           textField5.setForeground(Color.black);
        }
        
        return valid;
    }
    
    /**
     * Display sample text on the input text area to let user know what should
     * be the input like.
     */
    private void displaySampleText() {
       
        String cipherText;
        
        switch (selectedAlgorithm) {
            case ELGAMAL:
                cipherText = egCipherText;
                break;
            default:
                cipherText = nonEGCipherText;
                break;
        }
                
        String text = inputTextArea.getText().trim();
        
        // Do nothing if the text was entered by the user
        if (!text.isEmpty() && !(text.equals(message) || 
                                 text.equals(egCipherText) || 
                                 text.equals(nonEGCipherText)))
            return;
        
        inputTextArea.setText(encryptRadioButton.isSelected()? message : cipherText);
        
        inputTextArea.setForeground(Color.gray);
    }

    /**
     * A helper method that help to layout the given child component on the
     * parent component with the GridBagConstraints values.
     * @param parent the parent JComponent
     * @param children the child JComponent
     * @param gridx specifies the cell containing the leading edge of the 
     *        component's display area, where the first cell in a row has gridx=0
     * @param gridy specifies the cell at the top of the component's display 
     *        area, where the topmost cell has gridy=0
     * @param gridwidth specifies the number of cells in a row for the 
     *        component's display area
     * @param gridheight specifies the number of cells in a column for the 
     *        component's display area
     * @param weightx specifies how to distribute extra horizontal space
     * @param weighty Specifies how to distribute extra vertical space
     * @param fill this field is used when the component's display area is 
     *        larger than the component's requested size. It determines whether to resize the component, and if so, how
     * @param insets this field specifies the external padding of the component, 
     *        the minimum amount of space between the component and the edges of its display area
     */
    private void setComponent(JComponent parent, JComponent child,
            int gridx, int gridy, int gridwidth, int gridheight, 
            int weightx, int weighty, int fill, Insets insets) {
        
        c.gridx = gridx;
        c.gridy = gridy;
        c.gridwidth = gridwidth;
        c.gridheight = gridheight;
        c.weightx = weightx;
        c.weighty = weighty;
        c.fill = fill;
        c.insets = insets == null ? new Insets(0,0,0,0) : insets;  
        
        parent.add(child, c);
    }

    private static final int MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

    public static void main(String[] args) {

        // Change the Look And Feel of the program and set the shortcut keys
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());

                    // Set some shortcut keys for text fields
                    String[] components = {"TextField.focusInputMap",
                    "TextArea.focusInputMap"};

                    for (int i = 0; i < components.length; i++) {
                        InputMap im = (InputMap) UIManager.get(components[i]);
                        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, MASK), 
                                DefaultEditorKit.copyAction);
                        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, MASK), 
                                DefaultEditorKit.pasteAction);
                        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, MASK), 
                                DefaultEditorKit.cutAction);
                        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, MASK), 
                                DefaultEditorKit.selectAllAction);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, fall back to cross-platform
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                // not doing anything
            }
        }

        JFrame f = new Cryptosystem();
        f.setTitle("Cryptosystem");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setResizable(false);
        f.pack();
        f.setVisible(true);
    }
}

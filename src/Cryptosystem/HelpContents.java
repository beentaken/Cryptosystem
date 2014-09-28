package Cryptography;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

/**
 * A class that setup all the information for the help window.
 * 
 * @author Su Khai Koh
 */
public class HelpContents extends JDialog {

    private JPanel leftPanel, rightPanel;
    private JEditorPane editorPane;
    private JTree tree;
    
    public HelpContents(JFrame frame, boolean modal) {
        
        super(frame, modal);
        
        setupLeftPanel();
        setupRightPanel();
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);
        
        getContentPane().add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    /**
     * Setup left panel, which hold a JTree and showing all the help contents.
     */
    private void setupLeftPanel() {
        
        // Root of the node for tree
        Page contents = new Page("Contents", "./src/Help/help_main.html");
        DefaultMutableTreeNode contentPage = new DefaultMutableTreeNode(contents);
        
        // Build the rest of the nodes for the tree
        buildTree(contentPage);
        
        // Build the tree
        tree = new JTree(contentPage);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            
            // Show the correct help file when user clicked on a tree item
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = 
                        (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                
                if (node == null)   // nothing is selected
                    return; 
                
                Object nodeInfo = node.getUserObject();
                Page page = (Page) nodeInfo;
                
                File f = new File(page.getPathToPage());
                
                try {
                    editorPane.setPage(f.toURI().toURL());
                } catch (IOException x) {
                    String str = "File Not Found!";
                    editorPane.setText(str);
                }
            }
            
        });
        JScrollPane treeView = new JScrollPane(tree);
        treeView.setPreferredSize(new Dimension(200, 600));
        
        leftPanel = new JPanel();
        leftPanel.add(treeView);
    }
    
    /**
     * Build the tree (contents of the help information)
     * @param contentPage content page
     */
    private void buildTree(DefaultMutableTreeNode contentPage) {
        
        Page eg = new Page("ElGamal", "./src/Help/help_elgamal.html");
        DefaultMutableTreeNode egPage = new DefaultMutableTreeNode(eg);
        contentPage.add(egPage);
                
        Page rsa = new Page("RSA", "./src/Help/help_rsa.html");
        DefaultMutableTreeNode rsaPage = new DefaultMutableTreeNode(rsa);
        contentPage.add(rsaPage);
        
        Page ks = new Page("Knapsack", "./src/Help/help_knapsack.html");
        DefaultMutableTreeNode ksPage = new DefaultMutableTreeNode(ks);
        contentPage.add(ksPage);
        
        Page prime = new Page("Prime Generator", "./src/Help/help_prime_generator.html");
        DefaultMutableTreeNode primePage = new DefaultMutableTreeNode(prime);
        contentPage.add(primePage);
        
        Page mv = new Page("Maximum Value", "./src/Help/help_max_value.html");
        DefaultMutableTreeNode mvPage = new DefaultMutableTreeNode(mv);
        contentPage.add(mvPage);
    }
    
    /**
     * Setup right panel, which display the text from the selected html file.
     */
    private void setupRightPanel() {
        
        editorPane = new JEditorPane();
        editorPane.setEditable(false);

        JScrollPane sp = new JScrollPane(editorPane);
        sp.setPreferredSize(new Dimension(600, 600));
        
        rightPanel = new JPanel();
        rightPanel.add(sp);
    }
    
    /**
     * A private class that create a page for the help contents. The page will
     * have the name of the content and a path to the html file.
     * @author Su Khai Koh
     */
    private class Page {
        private String pageName;        // Name of the page
        private String pathToPage;      // Path to the page
        
        /**
         * Constructor that assign a page name and path to the html file.
         * @param name name of the page
         * @param path path to the html file of the page
         */
        private Page(String name, String path) {
            pageName = name;
            pathToPage = path;
        }
        
        /**
         * Get the path of the page.
         * @return path of the page
         */
        private String getPathToPage() {
            return pathToPage;
        }
        
        /**
         * Get the name of the page.
         * @return the name of the page
         */
        public String toString() {
            return pageName;
        }
    }
}

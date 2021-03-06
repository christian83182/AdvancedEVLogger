import javax.swing.*;
import java.awt.*;

public class ScrollableTextWindow extends JDialog {

    /**
     * A basic, reusable class designed to display some text in a scrollable window with a close button.
     * @param windowContent The String to be displayed by the window.
     * @param size The preferred size of the window.
     */
    ScrollableTextWindow(String title, Dimension size, String windowContent) {
        init(title, windowContent, size);
    }

    private void init(String title, String windowContent, Dimension size){
        this.setTitle(title);
        this.setModal(false);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setPreferredSize(size);
        this.setLayout(new GridBagLayout());

        //Create a text area with the content given in the constructor.
        JTextArea textArea = new JTextArea(windowContent);
        //Setting various textArea properties.
        textArea.setEditable(false);
        textArea.setFont(Settings.DEFAULT_FONT);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        //Add the text area to a scroll pane to make it scrollable.
        JScrollPane scrollArea = new JScrollPane(textArea);

        //Add the scrollable text area
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0;
        c.fill = GridBagConstraints.BOTH; c.weightx = 1; c.weighty = 1;
        c.insets = new Insets(10,10,10,10);
        this.add(scrollArea,c);

        //Create and add a 'close' button.
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> this.dispose());
        c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0,10,10,10);
        this.add(closeButton,c);

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}

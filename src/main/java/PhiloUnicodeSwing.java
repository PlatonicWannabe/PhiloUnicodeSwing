/*


  Dec 10, 2000  This is pretty close to all that I need in the GUI.
	Don't really need the quantifier symbols for Philo, but can use
	them later.
	Need to hook this up with the logic code: JavaCC, the Parser,
	the Server, and finally, Amzi components.  I want to see how easy
	it is to connect with unicode symbols to Prolog.

	Maybe read Eckel pp. 796 ff 2nd ed. on separating the business logic
	from the UI logic.
*/
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;



public class PhiloUnicodeSwing extends JFrame{

    private JTextArea outputarea;
    private JTextArea examplearea, inputarea;
    private JScrollPane outputareaScrollPane, exampleareaScrollPane;

    //The next to simplify creating new buttons
    //JButton all, some, onlyif, and, iff, or, not;
    private JButton clear, quit;
    String  str, inputfile, outputfile;
    private JPanel p;
    private Choice n1;
    //Just testing
    private JButton sub, parse, answer;
    int width = 50;
    String errstr, n, strwidth, attempt, result;
    String theOS, getexample, example, translation, getanswer;
    private JLabel l;


    private Font thisFont=new Font("Arial Unicode MS", Font.PLAIN, 12);

    private boolean firstTime;

    InnerJButton and = new InnerJButton("\u2227");
    InnerJButton not = new InnerJButton("\u00ac");
    InnerJButton or  = new InnerJButton("\u2228");
    InnerJButton iff = new InnerJButton("\u2194");
    InnerJButton onlyif = new InnerJButton("\u2192");
    InnerJButton all  = new InnerJButton("\u2200");
    InnerJButton some  = new InnerJButton("\u2203");

    private PhiloUnicodeSwing() {
        super("Philo Swing User Interface");

        buildGUI();
    }

    private void buildGUI() {

        p = new JPanel();

        //They will type the symbols into this inputarea
        // Should I add an actionListener if they hit Enter?
        inputarea = new JTextArea(1, 15);
        //next needed to get onlyif and iff to appear
        inputarea.setFont(thisFont);
        inputarea.setBackground(Color.white);

        //A TextArea where the example will be displayed
        examplearea = new JTextArea(8, 50);
        examplearea.setLineWrap(true);
        examplearea.setEditable(false);
        examplearea.setBackground(Color.white);
        examplearea.setText("Welcome to Philo the Logician.\n\nGive me a chance to contact the server\nand get the first example.");

        //wrap it in a ScrollPane with a title
        exampleareaScrollPane = new JScrollPane(examplearea);
        exampleareaScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        exampleareaScrollPane.setPreferredSize(new Dimension(400, 200));
        //puts a title above the ScrollPane
        exampleareaScrollPane.setBorder(BorderFactory.createTitledBorder("Give me a formula equivalent to:"));


        //A TextArea where the server's analysis of their answer gets displayed
        outputarea = new JTextArea(8, 50);
        outputarea.setLineWrap(true); //otherwise horizontal bars appeared in ScrollPane
        outputarea.setFont(thisFont); //I do not need this in the final version
        outputarea.setEditable(false);
        outputarea.setBackground(Color.white); //a method of Component class

        // wrap it in a ScrollPane with a title
        outputareaScrollPane = new JScrollPane(outputarea);
        outputareaScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        outputareaScrollPane.setPreferredSize(new Dimension(400, 200));
        //puts a title above the ScrollPane
        outputareaScrollPane.setBorder(BorderFactory.createTitledBorder("Evaluating Your Answer"));




        clear = new JButton("Clear");
        clear.addActionListener(e -> {
                inputarea.setText("");
                outputarea.setText("");
                inputarea.requestFocus();
            }
        );

        sub = new JButton("Submit");
        sub.setToolTipText("Click this button to send your answer to be checked.");
        sub.addActionListener(e -> {
                outputarea.setText(inputarea.getText());
                inputarea.requestFocus();
            }
        );

        quit = new JButton("Quit");
        quit.addActionListener(e -> {
                inputarea.setText("You're finished!");
                setVisible(false);
                System.exit(0);
            }
        );

        answer = new JButton("Answer");
        answer.setToolTipText("Returns an answer; anything equivalent is just as good.");
        parse = new JButton("Parse");
        parse.setToolTipText("Click this first; it will check that the formula is well-formed.");
        l = new JLabel("Choose example #");
        //att = new Attempt();
        firstTime = true;

        n1 = new Choice();
        n1.setBackground(Color.white);
        //Note that addItem needs a string; one cannot add an int to a choice list

        for(int i = 1; i < 11; i++) {
            n1.addItem(String.valueOf(i));
        }

        //Not needed in Swing?
        //this.getContentPane().setBackground(Color.orange);
        //a panel for buttons
        JPanel pb = new JPanel();

        pb.add(all);
        pb.add(some);
        pb.add(not);
        pb.add(and);
        pb.add(or);
        pb.add(onlyif);
        pb.add(iff);

        //Create a panel to go on the north of the content pane.
        JPanel pn = new JPanel();
        pn.setLayout(new BorderLayout());

        // This will be a panel put to the left (west) side of the north panel
        // and contains the choice box
        JPanel pnw = new JPanel();
        pnw.setLayout(new FlowLayout(FlowLayout.LEFT));
        pnw.add(l);
        pnw.add(n1);

        // At the top of the screen put the panel with the choice box, put
        // the input area and underneath put the panel of symbol buttons
        pn.add("West", pnw);
        pn.add("Center",inputarea);
        pn.add("South", pb);

        // This will be the center panel of our screen, underneath the north
        // panel.  Put the example area above the outputarea.  Notice that we
        // add the ScrollPanes which contain the TextAreas
        JPanel pc = new JPanel();
/*     pc.setLayout(new BorderLayout());
     pc.add("North",exampleareaScrollPane);
     pc.add("South",outputareaScrollPane);
*/
        //this GridLayout eliminates the space coming from the Center
        //of the BorderLayout when the window was expanded.

        pc.setLayout(new GridLayout(2,1));
        pc.add(exampleareaScrollPane);
        pc.add(outputareaScrollPane);

        // We want general action buttons to the left of the text areas, so create
        // a panel which will go to the left (west) of the text areas.
        JPanel pw = new JPanel();
        pw.setLayout(new BorderLayout());

        // A new panel to go in the top (north) of the panel just created.
        // Doing this constricts the buttons to a smaller shape than you
        // get if you simply put this Grid of buttons directly into pw.
        JPanel pwn = new JPanel();
        pwn.setLayout(new GridLayout(5,1));
        pwn.add(parse);
        pwn.add(sub);
        pwn.add(clear);
        pwn.add(answer);
        pwn.add(quit);

        pw.add("North", pwn);


        this.getContentPane().setLayout(new BorderLayout());

        getContentPane().add("North", pn);
        getContentPane().add("Center", pc);
        getContentPane().add("West", pw);





        //parse.enable();
        //sub.disable();
        pack();
        setVisible(true);
    }//end buildGUI


    /**Note that any button created by this generic inner class has attached an
     anonymous inner class that listens for the event of the button
     being pushed and which then performs the appropriate action.
     Inner class code from Nutshell pp 154-155
     Since students will be editing, I changed append to insert.
     */

    class InnerJButton extends JButton {
        InnerJButton(String symbol) {
            super(symbol);
            //without next call, symbols coming from elsewhere
            // also, iff and onlyif do not appear
            this.setFont(thisFont);
            this.addActionListener(e -> {
                    String s = e.getActionCommand();
                    int pos = inputarea.getCaretPosition();
                    inputarea.insert(s, pos);
                    //inputarea.append(s);
                    inputarea.requestFocus();
                }
            );
        }
    }


    public static void main(String[] args) {
        PhiloUnicodeSwing ps = new PhiloUnicodeSwing();
    }

}




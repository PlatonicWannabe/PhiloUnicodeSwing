/*
4/19/2020 This is an application, with a main(), not an applet. This creates the
GUI and connects to JavaCC to parse formulas. It corrects badly formed formulas.

I now need to add the vulture server
to connect with an IIS server running the Prolog code.

If I can get all that running on my machine, then I will try to dockerize
the whole chebang.


*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.CharArrayReader;
import java.io.Reader;
import java.text.ParseException;
import amzi.ls.*;


public class PhiloUnicodeSwing extends JFrame{
    LogicServer ls = new LogicServer();
    public JTextArea outputarea;
    public JTextArea inputarea;
    public JTextArea examplearea;
    JComboBox<String> cb;
    private JButton sub;
    private JButton parse;

    private Font thisFont=new Font("Lucida Sans Unicode", Font.PLAIN, 12);
    //private Font thisFont=new Font("Arial Unicode MS", Font.PLAIN, 12);

    private boolean firstTime;

    InnerJButton and = new InnerJButton("\u2227");
    InnerJButton not = new InnerJButton("\u00ac");
    InnerJButton or  = new InnerJButton("\u2228");
    InnerJButton iff = new InnerJButton("\u2194");
    InnerJButton onlyif = new InnerJButton("\u2192");
    private String input;
    //InnerJButton all  = new InnerJButton("\u2200");
    //InnerJButton some  = new InnerJButton("\u2203");

    private PhiloUnicodeSwing() {
        super("Philo Swing User Interface");
        buildGUI();
        try{
            ls.Init("");
            ls.Load("C:/Users/Bob/MyProlog/Philo/Amzi/unithrm9.xpl");
            //outputarea.setText("Logic server and xpl file ready."); This showed.
        } catch (LSException e) {System.err.println("Exception loading unithrm9: "+ e);}
       // buildGUI();
    }

    private void buildGUI() {

        JPanel p = new JPanel();

        //They will type the symbols into this inputarea
        // Should I add an actionListener if they hit Enter?
       //JTextArea inputarea;
        inputarea = new JTextArea(1, 15);
        //next needed to get onlyif and iff to appear
        inputarea.setFont(thisFont);
        inputarea.setBackground(Color.white);

        //A TextArea where the example will be displayed
        //JTextArea examplearea;
        examplearea = new JTextArea(8, 50);
        examplearea.setLineWrap(true);
        examplearea.setWrapStyleWord(true);
        examplearea.setEditable(false);
        examplearea.setBackground(Color.white);
        examplearea.setText("Welcome to Philo the Logician.\n\nSelect a number for your first example to try.");

        //wrap it in a ScrollPane with a title
        JScrollPane exampleareaScrollPane = new JScrollPane(examplearea);
        exampleareaScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        exampleareaScrollPane.setPreferredSize(new Dimension(400, 200));
        //puts a title above the ScrollPane
        exampleareaScrollPane.setBorder(BorderFactory.createTitledBorder("Formalize an example sentence."));


        //A TextArea where the server's analysis of their answer gets displayed
        //JTextArea outputarea;
        outputarea = new JTextArea(8, 50);
        outputarea.setLineWrap(true); //otherwise horizontal bars appeared in ScrollPane
        outputarea.setWrapStyleWord(true);
        outputarea.setFont(thisFont); //I do not need this in the final version
        outputarea.setEditable(false);
        outputarea.setBackground(Color.white); //a method of Component class

        // wrap it in a ScrollPane with a title
        JScrollPane outputareaScrollPane = new JScrollPane(outputarea);
        outputareaScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        outputareaScrollPane.setPreferredSize(new Dimension(400, 200));
        //puts a title above the ScrollPane
        outputareaScrollPane.setBorder(BorderFactory.createTitledBorder("Evaluating Your Answer"));


        //The next to simplify creating new buttons
        //JButton all, some, onlyif, and, iff, or, not;
        JButton clear = new JButton("Clear");
        clear.addActionListener(e -> {
            parse.setEnabled(true);
            inputarea.setText("");
            examplearea.setText("");
            outputarea.setText("");
            inputarea.requestFocus();
            }
        );

        JButton quit = new JButton("Quit");
        quit.addActionListener(e -> {
                    inputarea.setText("You're finished!");
                    setVisible(false);
                    System.exit(0);
                }
        );


        sub = new JButton("Submit");
        sub.setToolTipText("Click this button to send your answer to be checked.");

        sub.addActionListener(e -> {
            String response = "";
                outputarea.setText("");
               String str = inputarea.getText();
               String n = (String)cb.getSelectedItem();
                String attempt = "try("+n+","+str+",T,X)";
            try{
                long term = ls.ExecStr(attempt);
                if (term != 0){
                    String translation = ls.GetStrArg(term, 3);
                    String analysis= ls.GetStrArg(term, 4);
                    response = translation+" \n"+analysis;
                }
                else{
                    response = "Sorry, I could not handle your answer; try again.\n";
                }
            }catch(LSException ex) {
                ex.printStackTrace();
            }
            outputarea.setText(response);
                }


        );

        JButton answer = new JButton("Answer");
        answer.setToolTipText("Returns an answer; anything equivalent is just as good.");

        answer.addActionListener(e -> {
               String n = (String)cb.getSelectedItem();
               String input = "getanswer("+n+", X)";
               String response = "";
                try{
                    long term = ls.ExecStr(input);
                    if (term != 0) {
                        response = ls.GetStrArg(term, 2);
                    }
                    else {
                        response = "There seems to be something messed up; try again. \n";
                    }
                } catch (LSException lsException) {
                    lsException.printStackTrace();
                }
                outputarea.setText("A correct answer is: " +response);
            });

        parse = new JButton("Parse");
        parse.setToolTipText("Click this first; it will check that the formula is well-formed.");
        parse.addActionListener(e -> {
            try {
                getParse();
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        });

        JLabel l = new JLabel("Choose example #");
         firstTime = true;
        cb = new JComboBox<String>();
        cb.setBackground(Color.white);
        //Note that addItem needs a string; one cannot add an int to a choice list
        for(int i = 1; i < 11; i++) {
            cb.addItem(String.valueOf(i));
        }
        cb.setSelectedIndex(1);
        cb.setSelectedIndex(0);
        cb.addItemListener(evt -> {
            String example = "";
            outputarea.setText("");//A new example; clear old response
            examplearea.setText("");
            inputarea.setText("");
            inputarea.requestFocus();

          try {
              input = "present("+cb.getSelectedItem()+",X)";
              long term = ls.ExecStr(input);
                if (term != 0) {
                   example = ls.GetStrArg(term, 2);
                }
                else {
                  example = "I have no example of that number; try again \n";
                }
            } catch (LSException e) {
                e.printStackTrace();
            }
            examplearea.setText(example);
        });
        //a panel for buttons
        JPanel pb = new JPanel();

       // pb.add(all);
        //pb.add(some);
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
        pnw.add(cb);

        // At the top of the screen put the panel with the choice box, put
        // the input area and underneath put the panel of symbol buttons
        pn.add("West", pnw);
        pn.add("Center",inputarea);
        pn.add("South", pb);

        /*
         This will be the center panel of our screen, underneath the north
         panel.  Put the example area above the outputarea.  Notice that we
         add the ScrollPanes which contain the TextAreas
        */
        JPanel pc = new JPanel();
/*     pc.setLayout(new BorderLayout());
     pc.add("North",exampleareaScrollPane);
     pc.add("South",outputareaScrollPane);
*/
        /*
        this GridLayout eliminates the space coming from the Center
        of the BorderLayout when the window was expanded.
        */

        pc.setLayout(new GridLayout(2,1));
        pc.add(exampleareaScrollPane);
        pc.add(outputareaScrollPane);

        /*
         We want general action buttons to the left of the text areas, so create
         a panel which will go to the left (west) of the text areas.
        */
        JPanel pw = new JPanel();
        pw.setLayout(new BorderLayout());

        /*
         A new panel to go in the top (north) of the panel just created.
         Doing this constrains the buttons to a smaller shape than you
         get if you simply put this Grid of buttons directly into pw.
        */
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

    public void getParse() throws ParseException {
        String str = this.inputarea.getText();
        String unistr = addUnicodeEscapes(str);
        String parsestr = unistr+"\n";
        char[] contents = parsestr.toCharArray();
        Reader in = new CharArrayReader(contents);
        if(firstTime){
            StrFormUni parser = new StrFormUni(in);
            firstTime = false;
        }else {
            StrFormUni.ReInit(in);
        }
        try {
            StrFormUni.input();
            String s = ((String) StrFormUni.argStack.pop());
            outputarea.setText("Canonical form = " + s + "\n\n");
            parse.setEnabled(false);
            sub.setEnabled(true);

        } catch (Exception ex) {
            outputarea.setText(ex.getMessage());
            sub.setEnabled(false);
            parse.setEnabled(true);
        }
    }

    public String addUnicodeEscapes(String str) {
        String retval = "";
        char ch;
        for (int i = 0; i < str.length(); i++) {
            ch = str.charAt(i);
            if ( ch != '\n' && ch != '\r') {
                String s = "0000" + Integer.toString(ch, 16);
                retval += "\\u" + s.substring(s.length() -4, s.length());
            } else {
                retval += ch;
            }
        }
        return retval;
    }



    /**Note that any button created by this generic inner class has attached a
     lambda expression that listens for the event of the button
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




/*
 The MIT License (MIT)

 Copyright (c) 2020 Martin Braun

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */
package com.github.s4ke.flixbus.quickconnect;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

/**
 * @author Martin Braun
 * @version 1.1.0
 * @since 1.1.0
 */
public class FlixBusQuickConnect {
    private JTextField wlanAdapterID;
    private JTextField wlanSettingsXML;
    private JTextField wlanAdapterName;
    private JTextField wlanInterfaceName;
    private JTextField macPrefix;
    private JTextField maxLoginTries;
    private JButton startButton;
    private JButton stopButton;
    private JTextArea console;
    private JPanel mainPanel;
    private JScrollPane consoleScrollPane;

    private ExecutorService executorService;
    private Future<?> future;


    public FlixBusQuickConnect() {
        this.executorService = Executors.newCachedThreadPool();
        startButton.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( future != null ) {
                    return;
                }
                ConnectionRunnable connectionRunnable = new ConnectionRunnable( executorService );
                connectionRunnable.wlanAdapterId = wlanAdapterID.getText().trim();
                connectionRunnable.wlanSettingsXml = wlanSettingsXML.getText().trim();
                connectionRunnable.wlanAdapterName = wlanAdapterName.getText().trim();
                connectionRunnable.interfaceName = wlanInterfaceName.getText().trim();
                connectionRunnable.macPrefix = macPrefix.getText().trim();
                connectionRunnable.maxWlanLoginTries = Integer.parseInt( maxLoginTries.getText().trim() );
                future = executorService.submit( connectionRunnable );
            }
        } );
        stopButton.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( future == null ) {
                    return;
                }
                future.cancel( true );
                future = null;
            }
        } );
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame( "FlixBusQuickConnect" );
        FlixBusQuickConnect flixBusQuickConnect = new FlixBusQuickConnect();
        frame.setContentPane( flixBusQuickConnect.mainPanel );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setMinimumSize( new Dimension( 700, 400 ) );
        frame.setLocationRelativeTo( null );
        frame.pack();
        frame.setVisible( true );
        frame.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println( "Closed" );
                e.getWindow().dispose();
                flixBusQuickConnect.executorService.shutdownNow();
            }
        } );

        final PrintStream origOut = System.err;
        System.setErr( new PrintStream( new OutputStream() {

            private StringBuilder buffer = new StringBuilder( 128 );

            @Override
            public void write(int b) throws IOException {
                origOut.write( b );
                char c = (char) b;
                String value = Character.toString( c );
                buffer.append( value );
                if ( value.equals( "\n" ) ) {
                    flixBusQuickConnect.console.append( buffer.toString() );
                    flixBusQuickConnect.console.invalidate();
                    JScrollBar scrollBar = flixBusQuickConnect.consoleScrollPane.getVerticalScrollBar();
                    scrollBar.setValue( scrollBar.getMaximum() );
                    flixBusQuickConnect.consoleScrollPane.invalidate();
                    buffer.delete( 0, buffer.length() );
                    buffer.append( "[" ).append( "STDERR" ).append( "] " );
                }
            }
        } ) );
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout( new GridLayoutManager( 10, 1, new Insets( 0, 0, 0, 0 ), -1, -1 ) );
        final Spacer spacer1 = new Spacer();
        mainPanel.add(
                spacer1,
                new GridConstraints(
                        0,
                        0,
                        1,
                        1,
                        GridConstraints.ANCHOR_CENTER,
                        GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_WANT_GROW,
                        1,
                        null,
                        null,
                        null,
                        0,
                        false
                )
        );
        final JPanel panel1 = new JPanel();
        panel1.setLayout( new GridLayoutManager( 1, 1, new Insets( 0, 0, 0, 0 ), -1, -1 ) );
        mainPanel.add(
                panel1,
                new GridConstraints(
                        1,
                        0,
                        1,
                        1,
                        GridConstraints.ANCHOR_CENTER,
                        GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        null,
                        null,
                        null,
                        0,
                        false
                )
        );
        final JPanel panel2 = new JPanel();
        panel2.setLayout( new GridLayoutManager( 1, 2, new Insets( 0, 0, 0, 0 ), -1, -1 ) );
        panel1.add(
                panel2,
                new GridConstraints(
                        0,
                        0,
                        1,
                        1,
                        GridConstraints.ANCHOR_CENTER,
                        GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        null,
                        null,
                        null,
                        0,
                        false
                )
        );
        wlanAdapterID = new JTextField();
        wlanAdapterID.setText( ConnectionRunnable.WLAN_ADAPTER_ID );
        panel2.add(
                wlanAdapterID,
                new GridConstraints(
                        0,
                        1,
                        1,
                        1,
                        GridConstraints.ANCHOR_WEST,
                        GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_WANT_GROW,
                        GridConstraints.SIZEPOLICY_FIXED,
                        null,
                        new Dimension( 150, -1 ),
                        null,
                        0,
                        false
                )
        );
        final JLabel label1 = new JLabel();
        label1.setText( "WLAN ADAPTER ID (Windows)" );
        panel2.add(
                label1,
                new GridConstraints(
                        0,
                        0,
                        1,
                        1,
                        GridConstraints.ANCHOR_WEST,
                        GridConstraints.FILL_NONE,
                        GridConstraints.SIZEPOLICY_FIXED,
                        GridConstraints.SIZEPOLICY_FIXED,
                        null,
                        null,
                        null,
                        0,
                        false
                )
        );
        final JPanel panel3 = new JPanel();
        panel3.setLayout( new GridLayoutManager( 1, 2, new Insets( 0, 0, 0, 0 ), -1, -1 ) );
        mainPanel.add(
                panel3,
                new GridConstraints(
                        5,
                        0,
                        1,
                        1,
                        GridConstraints.ANCHOR_CENTER,
                        GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        null,
                        null,
                        null,
                        0,
                        false
                )
        );
        maxLoginTries = new JTextField();
        maxLoginTries.setText( String.valueOf(ConnectionRunnable.MAX_WLAN_LOGIN_TRIES) );
        panel3.add(
                maxLoginTries,
                new GridConstraints(
                        0,
                        1,
                        1,
                        1,
                        GridConstraints.ANCHOR_WEST,
                        GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_WANT_GROW,
                        GridConstraints.SIZEPOLICY_FIXED,
                        null,
                        new Dimension( 150, -1 ),
                        null,
                        0,
                        false
                )
        );
        final JLabel label2 = new JLabel();
        label2.setText( "MAX WLAN LOGIN TRIES" );
        panel3.add(
                label2,
                new GridConstraints(
                        0,
                        0,
                        1,
                        1,
                        GridConstraints.ANCHOR_WEST,
                        GridConstraints.FILL_NONE,
                        GridConstraints.SIZEPOLICY_FIXED,
                        GridConstraints.SIZEPOLICY_FIXED,
                        null,
                        null,
                        null,
                        0,
                        false
                )
        );
        final JPanel panel4 = new JPanel();
        panel4.setLayout( new GridLayoutManager( 1, 2, new Insets( 0, 0, 0, 0 ), -1, -1 ) );
        mainPanel.add(
                panel4,
                new GridConstraints(
                        4,
                        0,
                        1,
                        1,
                        GridConstraints.ANCHOR_CENTER,
                        GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        null,
                        null,
                        null,
                        0,
                        false
                )
        );
        macPrefix = new JTextField();
        macPrefix.setText( ConnectionRunnable.MAC_PREFIX );
        panel4.add(
                macPrefix,
                new GridConstraints(
                        0,
                        1,
                        1,
                        1,
                        GridConstraints.ANCHOR_WEST,
                        GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_WANT_GROW,
                        GridConstraints.SIZEPOLICY_FIXED,
                        null,
                        new Dimension( 150, -1 ),
                        null,
                        0,
                        false
                )
        );
        final JLabel label3 = new JLabel();
        label3.setText( "GENERATED MAC PREFIX" );
        panel4.add(
                label3,
                new GridConstraints(
                        0,
                        0,
                        1,
                        1,
                        GridConstraints.ANCHOR_WEST,
                        GridConstraints.FILL_NONE,
                        GridConstraints.SIZEPOLICY_FIXED,
                        GridConstraints.SIZEPOLICY_FIXED,
                        null,
                        null,
                        null,
                        0,
                        false
                )
        );
        final JPanel panel5 = new JPanel();
        panel5.setLayout( new GridLayoutManager( 1, 2, new Insets( 0, 0, 0, 0 ), -1, -1 ) );
        mainPanel.add(
                panel5,
                new GridConstraints(
                        3,
                        0,
                        1,
                        1,
                        GridConstraints.ANCHOR_CENTER,
                        GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        null,
                        null,
                        null,
                        0,
                        false
                )
        );
        wlanInterfaceName = new JTextField();
        wlanInterfaceName.setText( ConnectionRunnable.INTERFACE_NAME );
        panel5.add(
                wlanInterfaceName,
                new GridConstraints(
                        0,
                        1,
                        1,
                        1,
                        GridConstraints.ANCHOR_WEST,
                        GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_WANT_GROW,
                        GridConstraints.SIZEPOLICY_FIXED,
                        null,
                        new Dimension( 150, -1 ),
                        null,
                        0,
                        false
                )
        );
        final JLabel label4 = new JLabel();
        label4.setText( "WLAN INTERFACE NAME (Windows) / NETWORK ADAPTER (Linux)" );
        panel5.add(
                label4,
                new GridConstraints(
                        0,
                        0,
                        1,
                        1,
                        GridConstraints.ANCHOR_WEST,
                        GridConstraints.FILL_NONE,
                        GridConstraints.SIZEPOLICY_FIXED,
                        GridConstraints.SIZEPOLICY_FIXED,
                        null,
                        null,
                        null,
                        0,
                        false
                )
        );
        final JPanel panel6 = new JPanel();
        panel6.setLayout( new GridLayoutManager( 1, 2, new Insets( 0, 0, 0, 0 ), -1, -1 ) );
        mainPanel.add(
                panel6,
                new GridConstraints(
                        2,
                        0,
                        1,
                        1,
                        GridConstraints.ANCHOR_CENTER,
                        GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        null,
                        null,
                        null,
                        0,
                        false
                )
        );
        wlanAdapterName = new JTextField();
        wlanAdapterName.setText( ConnectionRunnable.WLAN_ADAPTER_NAME );
        panel6.add(
                wlanAdapterName,
                new GridConstraints(
                        0,
                        1,
                        1,
                        1,
                        GridConstraints.ANCHOR_WEST,
                        GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_WANT_GROW,
                        GridConstraints.SIZEPOLICY_FIXED,
                        null,
                        new Dimension( 150, -1 ),
                        null,
                        0,
                        false
                )
        );
        final JLabel label5 = new JLabel();
        label5.setText( "WLAN ADAPTER NAME (Windows)" );
        panel6.add(
                label5,
                new GridConstraints(
                        0,
                        0,
                        1,
                        1,
                        GridConstraints.ANCHOR_WEST,
                        GridConstraints.FILL_NONE,
                        GridConstraints.SIZEPOLICY_FIXED,
                        GridConstraints.SIZEPOLICY_FIXED,
                        null,
                        null,
                        null,
                        0,
                        false
                )
        );
        final JPanel panel7 = new JPanel();
        panel7.setLayout( new GridLayoutManager( 1, 2, new Insets( 0, 0, 0, 0 ), -1, -1 ) );
        mainPanel.add(
                panel7,
                new GridConstraints(
                        6,
                        0,
                        1,
                        1,
                        GridConstraints.ANCHOR_CENTER,
                        GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        null,
                        null,
                        null,
                        0,
                        false
                )
        );
        wlanSettingsXML = new JTextField();
        wlanSettingsXML.setText( ConnectionRunnable.WLAN_SETTINGS_XML );
        panel7.add(
                wlanSettingsXML,
                new GridConstraints(
                        0,
                        1,
                        1,
                        1,
                        GridConstraints.ANCHOR_WEST,
                        GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_WANT_GROW,
                        GridConstraints.SIZEPOLICY_FIXED,
                        null,
                        new Dimension( 150, -1 ),
                        null,
                        0,
                        false
                )
        );
        final JLabel label6 = new JLabel();
        label6.setText( "WLAN SETTINGS XML (Windows)" );
        panel7.add(
                label6,
                new GridConstraints(
                        0,
                        0,
                        1,
                        1,
                        GridConstraints.ANCHOR_WEST,
                        GridConstraints.FILL_NONE,
                        GridConstraints.SIZEPOLICY_FIXED,
                        GridConstraints.SIZEPOLICY_FIXED,
                        null,
                        null,
                        null,
                        0,
                        false
                )
        );
        startButton = new JButton();
        startButton.setText( "Start" );
        mainPanel.add(
                startButton,
                new GridConstraints(
                        7,
                        0,
                        1,
                        1,
                        GridConstraints.ANCHOR_CENTER,
                        GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_FIXED,
                        null,
                        null,
                        null,
                        0,
                        false
                )
        );
        stopButton = new JButton();
        stopButton.setText( "Stop" );
        mainPanel.add(
                stopButton,
                new GridConstraints(
                        8,
                        0,
                        1,
                        1,
                        GridConstraints.ANCHOR_CENTER,
                        GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_FIXED,
                        null,
                        null,
                        null,
                        0,
                        false
                )
        );
        consoleScrollPane = new JScrollPane();
        mainPanel.add(
                consoleScrollPane,
                new GridConstraints(
                        9,
                        0,
                        1,
                        1,
                        GridConstraints.ANCHOR_CENTER,
                        GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                        null,
                        null,
                        null,
                        0,
                        false
                )
        );
        console = new JTextArea();
        console.setText( "" );
        consoleScrollPane.setViewportView( console );
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}

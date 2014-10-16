// -----------------------------------------------------------------------------
//  Copyright (c) Edifecs Inc. All Rights Reserved.
//
// This software is the confidential and proprietary information of Edifecs Inc.
// ("Confidential Information").  You shall not disclose such Confidential
// Information and shall use it only in accordance with the terms of the license
// agreement you entered into with Edifecs.
//
// EDIFECS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
// SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
// NON-INFRINGEMENT. EDIFECS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
// LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
// ITS DERIVATIVES.
// -----------------------------------------------------------------------------

package com.edifecs.servicemanager.test.services;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.edifecs.epp.isc.annotations.Akka;
import com.edifecs.epp.isc.annotations.Arg;
import com.edifecs.epp.isc.annotations.Command;
import com.edifecs.epp.isc.Address;
import com.edifecs.epp.isc.core.command.AbstractCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles the chat messages.
 * Users write messages in the <code>ChatWindow</code> and it is multicasted to all running applications 
 * 
 * @author willclem
 */
@Akka(enabled=true)
public class TestChatServiceHandler extends AbstractCommandHandler implements ITestChatServiceHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
        
    private String name;        
    private Address address;
    private ChatWindow app;
    private List<Address> services;
    
    public TestChatServiceHandler(Address addr, List<Address> chatAddresses, String name) {
            this.address = addr;                            
            this.services = chatAddresses;          
            this.name = name;
            
            this.logger.debug("Initial Chat Clients => {}", this.services.toString());
    }

    public void initializeChatWindow(int maxW, int maxH, int startW, int startH) {
            app = new ChatWindow(address.toString(), maxW, maxH, startW, startH);
            app.go();
    }

    @Command(name = "addChatServiceClientCommand")
    public Boolean addChatServiceClientCommand(
            @Arg(name = "addr", required = true, description = "Client Address") Address addr
        ) throws Exception {

        synchronized(services) {
            if (!address.equals(addr)) {
                services.add(addr);                             
                logger.debug("{} client added => {}", addr, services.toString());
            }
        }
        
        return true;
    }   

    @Command(name = "removeChatServiceClientCommand")
    public Boolean removeChatServiceClientCommand(
            @Arg(name = "addr", required = true, description = "Client Address"
        ) Address addr) throws Exception {

        synchronized(services) {
            services.remove(addr);
        }
                
        logger.debug("{} client removed => {}", addr, services.toString());
        return true;
    }   
        
    @Command(name = "sendChatMessageCommand")
    public Boolean sendChatMessageCommand(
            @Arg(name = "message", required = true, description = "Message to be sent") String msg,
            @Arg(name = "client", required = true, description = "Application Name")    String name,
            @Arg(name = "addr", required = true, description = "Source Address")        Address src
        ) throws Exception {

        String message = "[" + src + ":" + name + "] " + msg;
        app.addToHistory(message);
        
        logger.debug(message);
        return true;
    }   
                
    private void sendMessage(String msg) {
        String command = "sendChatMessageCommand";

        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        properties.put("message", msg);
        properties.put("client", name); 
        properties.put("addr", address);

        try {
            synchronized(services) {
                for (Address addr: services) {
                    Boolean rsp = (Boolean) getCommandCommunicator().sendSyncMessage(addr, command, properties);            
                    logger.debug(String.format("{0} => {1} ? {2}", msg, addr, rsp));
                }
            }
        } catch (Exception e) {                 
            e.printStackTrace();
        }
    }       

    private class ChatWindow implements ActionListener, ItemListener {

        private Frame frame;    
        private TextArea history;               
        private TextArea current;

        private String appName;

        private int maxHeight;
        private int maxWidth;

        private int startHeight;
        private int startWidth;

        public ChatWindow(String name, int maxW, int maxH, int startW, int startH) {
            appName = name;

            maxWidth = Math.max(200, maxW);
            maxHeight = Math.max(200, maxH);

            startWidth = Math.max(6, startW);
            startHeight = Math.max(9, startH);                      
        }

        public void go() {
            frame = new Frame(appName);

            history = new TextArea(" ", 2 * startHeight/3, startWidth);             
            history.setEditable(false);
            frame.add(history, BorderLayout.CENTER);

            current = new TextArea(" ", startHeight/3, startWidth);

            current.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        String msg = current.getText();                                         
                        sendMessage(msg);

                        current.replaceRange("", 0, msg.length());
                    }
                }
            });

            frame.add(current, BorderLayout.SOUTH);

            frame.setSize(maxHeight, maxWidth);
            frame.pack();
            frame.setVisible(true);

            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });             
        }

        public void addToHistory(String msg) {
            history.append(msg + "\n");
        }

        public void actionPerformed(ActionEvent ae) {
            logger.debug("Button \"" + ae.getActionCommand() + "\" Pressed.");              
        }

        public void itemStateChanged(ItemEvent ie) {
            String state = "Deselected";
            if (ie.getStateChange() == ItemEvent.SELECTED) {
                state = "Selected";
            }

            logger.debug(ie.getItem() + " " + state);
        }               
    }
}


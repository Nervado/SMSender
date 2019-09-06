/*
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.*;
import java.io.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import javax.swing.event.*;


/**
 * Demo app that shows a very simple Mail Client
 *
 * @author Christopher Cotton
 * @author Bill Shannon
 */
 
 /**
SimpleClient
------------

Notes:
======

This should not be taken as a demo of how to use the Swing API, but
rather a very simple graphical mail client. It shows how viewers can
be used to display the content from mail messages.  It also (like the
other demos) shows how to retrieve Folders from a Store, Messages
from a Folder, and content from Messages.


To run the demo:
================

    1.  If you're using JDK 1.1.x, download the latest version of the JFC
	(Swing) APIs from http://java.sun.com/products/jfc/download.html.
	The SimpleClient uses at least version 1.1 of Swing.

	If you're using JDK 1.2 (J2SE 1.2) or newer, Swing is included
	and no separate download is necessary.

	We *strongly* encourage you to use the latest version of J2SE,
	which you can download from http://java.sun.com/j2se/.

    2.  Set your CLASSPATH to include the "mail.jar", "activation.jar",
	and (if you're using JDK 1.1.x and downloaded Swing separately)
	"swingall.jar", and the current directory.  For example:

	For JDK 1.1 on UNIX:

	export CLASSPATH=/u/me/download/mail.jar:/u/me/download/activation.jar:/u/me/download/swingall.jar:.

	For JDK 1.2 and newer on UNIX:

	export CLASSPATH=/u/me/download/mail.jar:/u/me/download/activation.jar:.

    3.  Go to the demo/client directory

    4.  Compile all the files using your Java compiler.  For example:

	  javac *.java

    5.  Run the demo. For example:

	  java SimpleClient -L imap://username:password@hostname/

	Note that SimpleClient expects to read the "simple.mailcap"
	file from the current directory.  The simple.mailcap file
	contains configuration information about viewers needed by
	the SimpleClient demo program.



Overview of the Classes
=======================

Main Classes:

	SimpleClient   =    contains main().
			     Uses the parameters to the application to
			     locate the correct Store.  e.g.

				SimpleClient -L imap://cotton:secret@snow-goon/

			     It will create the main frame and
			     creates a tree.  The tree uses the
			     StoreTreeNodes and FolderTreeNodes.

	StoreTreeNode   =    subclass of Swing's DefaultMutableTreeNode.
			     This class shows how to get Folders from
			     the Store.

	FolderTreeNode  =    subclass of Swing's DefaultMutableTreeNode.
			     If the folder has messages, it will create
			     a FolderViewer.  Otherwise it will add the
			     subfolders to the tree.

	SimpleAuthenticator = subclass of javax.mail.Authenticator. If
			     the Store is missing the username or the
			     password, this authenticator will be used.
			     It displays a dialog requesting the
			     information from the user.
				

Viewing Folders:

	FolderViewer    =    Uses a Swing Table to display all of the
			     Message in a Folder.  The "model" of the
			     data for this Table is a FolderModel which
			     knows how to get displayable information
			     from a Message.

JAF Viewers:

	MessageViewer   =    Uses the content of the DataHandler.  The
			     content will be a javax.mail.Message
			     object.  Displays the headers and then
			     uses the JAF to find another viewer for
			     the content type of the Message.  (either
			     multipart/mixed, image/gif, or text/plain)

	MultipartViewer =    Uses the content of the DataHandler.  The
			     content will be a javax.mail.Multipart
			     object.  Uses the JAF to find another
			     viewer for the first BodyPart's content.
			     Also puts Buttons (as "attachments") for
			     the rest of the BodyParts.  When the
			     Button are pressed, it uses the JAF to
			     find a viewer for the BodyPart's content,
			     and displays it in a separate frame (using
			     ComponentFrame).

	TextViewer      =    Uses the content of the DataHandler.  The
			     content will be either a java.lang.String
			     object, or a java.io.InputStream object.
			     Creates a TextArea and sets the text using
			     the String or InputStream.

Support Classes:

	ComponentFrame  =    support class which takes a java.awt.Component
			     and displays it in a Frame.

 

public class SimpleClient {

    static Vector url = new Vector();
    static FolderViewer fv;
    static MessageViewer mv;

    public static void main(String argv[]) {
	boolean usage = false;

	for (int optind = 0; optind < argv.length; optind++) {
	    if (argv[optind].equals("-L")) {
		url.addElement(argv[++optind]);
	    } else if (argv[optind].startsWith("-")) {
		usage = true;
		break;
	    } else {
		usage = true;
		break;
	    }
	}

	if (usage || url.size() == 0) {
	    System.out.println("Usage: SimpleClient -L url");
	    System.out.println("   where url is protocol://username:password@hostname/");
	    System.exit(1);
	}

	try {
	    // Set up our Mailcap entries.  This will allow the JAF
	    // to locate our viewers.
	    File capfile = new File("simple.mailcap");
	    if (!capfile.isFile()) {
		System.out.println(
		    "Cannot locate the \"simple.mailcap\" file.");
		System.exit(1);
	    }
	    
	    CommandMap.setDefaultCommandMap( new MailcapCommandMap(
		new FileInputStream(capfile)));
		
	    JFrame frame = new JFrame("Simple JavaMail Client");
	    frame.addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) {System.exit(0);}});
	    //frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
	    // Get a Store object
	    SimpleAuthenticator auth = new SimpleAuthenticator(frame);
	    Session session = 
		Session.getDefaultInstance(System.getProperties(), auth);
	    //session.setDebug(true);

	    DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");

	    // create a node for each store we have
	    for (Enumeration e = url.elements() ; e.hasMoreElements() ;) {
		String urlstring = (String) e.nextElement();
		URLName urln = new URLName(urlstring);
		Store store = session.getStore(urln);
		
		StoreTreeNode storenode = new StoreTreeNode(store);
		root.add(storenode);
	    }	    

	    DefaultTreeModel treeModel = new DefaultTreeModel(root);
	    JTree tree = new JTree(treeModel);
	    tree.addTreeSelectionListener(new TreePress());
		
		**/

	    /* Put the Tree in a scroller. */
	    JScrollPane        sp = new JScrollPane();
	    sp.setPreferredSize(new Dimension(250, 300));
	    sp.getViewport().add(tree);

	    /* Create a double buffered JPanel */
	    JPanel sv = new JPanel(new BorderLayout());
	    sv.add("Center", sp);

	    fv = new FolderViewer(null);

	    JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				sv, fv);
	    jsp.setOneTouchExpandable(true);
	    mv = new MessageViewer();
	    JSplitPane jsp2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				jsp, mv);
	    jsp2.setOneTouchExpandable(true);

	    frame.getContentPane().add(jsp2);
	    frame.pack();
	    frame.show();

	} catch (Exception ex) {
	    System.out.println("SimpletClient caught exception");
	    ex.printStackTrace();
	    System.exit(1);
	}
    }

}

class TreePress implements TreeSelectionListener {
    
    public void valueChanged(TreeSelectionEvent e) {
	TreePath path = e.getNewLeadSelectionPath();
	if (path != null) {
	    Object o = path.getLastPathComponent();
	    if (o instanceof FolderTreeNode) {
		FolderTreeNode node = (FolderTreeNode)o;
		Folder folder = node.getFolder();

		try {
		    if ((folder.getType() & Folder.HOLDS_MESSAGES) != 0) {
			SimpleClient.fv.setFolder(folder);
		    }
		} catch (MessagingException me) { }
	    }
	}
    }
}

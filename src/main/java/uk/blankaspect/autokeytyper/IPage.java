/*====================================================================*\

IPage.java

Interface: page.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.autokeytyper;

//----------------------------------------------------------------------


// IMPORTS


import javafx.scene.layout.Pane;

import uk.blankaspect.common.basictree.MapNode;

//----------------------------------------------------------------------


// INTERFACE: PAGE


/**
 * This interface defines the methods that must be implemented by a page of the user interface of {@link
 * AutoKeyTyperApp}.
 */

public interface IPage
{

////////////////////////////////////////////////////////////////////////
//  Methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the outer pane of this page.
	 *
	 * @return the outer pane of this page.
	 */

	Pane pane();

	//------------------------------------------------------------------

	/**
	 * Encodes the state of this page as a {@link MapNode} and returns the result.
	 *
	 * @return the state of this page encoded as a {@code MapNode}.
	 */

	MapNode encodeState();

	//------------------------------------------------------------------

	/**
	 * Decodes the state of this page from the specified {@link MapNode}.
	 *
	 * @param rootNode
	 *          the {@code MapNode} from which the state of this page is to be decoded.
	 */

	void decodeState(
		MapNode	rootNode);

	//------------------------------------------------------------------

	/**
	 * Notifies this page that it is about to be exited.  The default implementation of this method does nothing.
	 */

	default void onExiting()
	{
		// do nothing
	}

	//------------------------------------------------------------------

	/**
	 * Notifies this page that the window that contains it is shown.  The default implementation of this method does
	 * nothing.
	 */

	default void onWindowShown()
	{
		// do nothing
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------

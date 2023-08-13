/*====================================================================*\

Preferences.java

Class: preferences.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.autokeytyper;

//----------------------------------------------------------------------


// IMPORTS


import uk.blankaspect.ui.jfx.style.StyleManager;

//----------------------------------------------------------------------


// CLASS: PREFERENCES


/**
 * This class implements a set of user preferences.
 */

public class Preferences
{

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The identifier of the theme. */
	private	String		themeId;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a default set of user preferences.
	 */

	public Preferences()
	{
		// Initialise instance variables
		themeId = StyleManager.DEFAULT_THEME_ID;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a set of user preferences with the specified values.
	 *
	 * @param themeId
	 *          the identifier of the theme.
	 */

	public Preferences(
		String	themeId)
	{
		// Initialise instance variables
		this.themeId = themeId;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public String getThemeId()
	{
		return themeId;
	}

	//------------------------------------------------------------------

	public void setThemeId(
		String	id)
	{
		themeId = id;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------

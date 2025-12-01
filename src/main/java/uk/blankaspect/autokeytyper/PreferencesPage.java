/*====================================================================*\

PreferencesPage.java

Class: 'preferences' page.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.autokeytyper;

//----------------------------------------------------------------------


// IMPORTS


import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import uk.blankaspect.common.basictree.MapNode;

import uk.blankaspect.ui.jfx.label.Labels;

import uk.blankaspect.ui.jfx.spinner.CollectionSpinner;

import uk.blankaspect.ui.jfx.style.StyleManager;

//----------------------------------------------------------------------


// CLASS: 'PREFERENCES' PAGE


public class PreferencesPage
	implements IPage
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The horizontal gap between adjacent components of the control pane. */
	private static final	double	CONTROL_PANE_H_GAP	= 6.0;

	/** The padding around the control pane. */
	private static final	Insets	CONTROL_PANE_PADDING	= new Insets(12.0, 20.0, 12.0, 20.0);

	/** Miscellaneous strings. */
	private static final	String	THEME_STR	= "Theme";

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The spinner for the style theme. */
	private	CollectionSpinner<String>	themeSpinner;

	/** The outer pane of this page. */
	private	StackPane					pane;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public PreferencesPage()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : IPage interface
////////////////////////////////////////////////////////////////////////

	@Override
	public StackPane pane()
	{
		return pane;
	}

	//------------------------------------------------------------------

	@Override
	public MapNode encodeState()
	{
		return null;
	}

	//------------------------------------------------------------------

	@Override
	public void decodeState(
		MapNode	rootNode)
	{
		// do nothing
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public void init()
	{
		// Create spinner: theme
		StyleManager styleManager = StyleManager.INSTANCE;
		String themeId = styleManager.getThemeId();
		themeSpinner = CollectionSpinner.leftRightH(HPos.CENTER, true, styleManager.getThemeIds(), themeId, null,
													id -> styleManager.findTheme(id).name());
		themeSpinner.itemProperty().addListener((observable, oldId, id) ->
		{
			if (id != null)
			{
				// Update theme
				styleManager.selectTheme(id);

				// Reapply style sheet to the scenes of all JavaFX windows
				styleManager.reapplyStylesheet();
			}
		});

		// Create control pane
		HBox controlPane = new HBox(CONTROL_PANE_H_GAP, Labels.hNoShrink(THEME_STR), themeSpinner);
		controlPane.setAlignment(Pos.CENTER);
		controlPane.setPadding(CONTROL_PANE_PADDING);

		// Create outer pane
		pane = new StackPane(controlPane);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------

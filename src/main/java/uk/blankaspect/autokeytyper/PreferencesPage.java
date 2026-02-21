/*====================================================================*\

PreferencesPage.java

Class: 'preferences' page.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.autokeytyper;

//----------------------------------------------------------------------


// IMPORTS


import java.util.List;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.control.Button;

import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import javafx.scene.paint.Color;

import uk.blankaspect.common.basictree.MapNode;

import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.ui.jfx.container.PaneStyle;

import uk.blankaspect.ui.jfx.label.Labels;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

import uk.blankaspect.ui.jfx.spinner.CollectionSpinner;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;

//----------------------------------------------------------------------


// CLASS: 'PREFERENCES' PAGE


public class PreferencesPage
	implements IPage
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The horizontal gap between adjacent components in a container. */
	private static final	double	CONTROL_H_GAP	= 6.0;

	/** The padding around the control pane. */
	private static final	Insets	CONTROL_PANE_PADDING	= new Insets(12.0, 20.0, 12.0, 20.0);

	/** Miscellaneous strings. */
	private static final	String	COLOUR_SCHEME_STR	= "Colour scheme";

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			PaneStyle.ColourKey.PANE_BORDER,
			CssSelector.builder()
					.cls(StyleClass.PREFERENCES_PAGE)
					.desc(StyleClass.CONTROL_PANE)
					.build()
		)
	);

	/** CSS style classes. */
	private interface StyleClass
	{
		String	CONTROL_PANE		= StyleConstants.CLASS_PREFIX + "control-pane";
		String	PREFERENCES_PAGE	= StyleConstants.CLASS_PREFIX + "preferences-page";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The spinner for the style theme. */
	private	CollectionSpinner<String>	themeSpinner;

	/** The outer pane of this page. */
	private	StackPane					pane;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Register the style properties of this class and its dependencies with the style manager
		StyleManager.INSTANCE.register(DelayedPage.class, COLOUR_PROPERTIES,
									   PaneStyle.class);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public PreferencesPage()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the colour that is associated with the specified key in the colour map of the current theme of the
	 * {@linkplain StyleManager style manager}.
	 *
	 * @param  key
	 *           the key of the desired colour.
	 * @return the colour that is associated with {@code key} in the colour map of the current theme of the style
	 *         manager, or {@link StyleManager#DEFAULT_COLOUR} if there is no such colour.
	 */

	private static Color getColour(
		String	key)
	{
		return StyleManager.INSTANCE.getColourOrDefault(key);
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
	public List<Button> buttons()
	{
		return List.of();
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
		HBox controlPane = new HBox(CONTROL_H_GAP, Labels.hNoShrink(COLOUR_SCHEME_STR), themeSpinner);
		controlPane.setAlignment(Pos.CENTER);
		controlPane.setPadding(CONTROL_PANE_PADDING);
		controlPane.setBorder(SceneUtils.createSolidBorder(getColour(PaneStyle.ColourKey.PANE_BORDER)));
		controlPane.getStyleClass().add(StyleClass.CONTROL_PANE);

		// Create outer pane
		pane = new StackPane(controlPane);
		pane.getStyleClass().add(StyleClass.PREFERENCES_PAGE);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------

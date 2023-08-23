/*====================================================================*\

PreferencesDialog.java

Class: preferences dialog.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.autokeytyper;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.layout.HBox;

import javafx.stage.Window;

import uk.blankaspect.common.function.IProcedure1;

import uk.blankaspect.ui.jfx.dialog.SimpleModalDialog;

import uk.blankaspect.ui.jfx.spinner.CollectionSpinner;

import uk.blankaspect.ui.jfx.style.StyleManager;

//----------------------------------------------------------------------


// CLASS: PREFERENCES DIALOG


/**
 * This class implements a modal dialog in which the user preferences of the application may be edited.
 */

public class PreferencesDialog
	extends SimpleModalDialog<Preferences>
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	double	CONTROL_PANE_H_GAP	= 6.0;

	/** The padding around a control pane. */
	private static final	Insets	CONTROL_PANE_PADDING	= new Insets(12.0, 20.0, 12.0, 20.0);

	/** Miscellaneous strings. */
	private static final	String	PREFERENCES_STR	= "Preferences";
	private static final	String	THEME_STR		= "Theme";

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The result of this dialog. */
	private	Preferences	result;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a modal dialog in which the user preferences of the application may be edited.
	 *
	 * @param owner
	 *          the window that will be the owner of this dialog, or {@code null} if the dialog has no owner.
	 * @param preferences
	 *          the initial preferences.
	 */

	private PreferencesDialog(
		Window		owner,
		Preferences	preferences)
	{
		// Call superclass constructor
		super(owner, MethodHandles.lookup().lookupClass().getCanonicalName(), null, PREFERENCES_STR);

		// Create procedure to select theme
		StyleManager styleManager = StyleManager.INSTANCE;
		IProcedure1<String> selectTheme = id ->
		{
			// Update theme
			styleManager.selectTheme(id);

			// Reapply style sheet to the scenes of all JavaFX windows
			styleManager.reapplyStylesheet();
		};

		// Create spinner: theme
		String themeId = styleManager.getThemeId();
		CollectionSpinner<String> themeSpinner =
				CollectionSpinner.leftRightH(HPos.CENTER, true, styleManager.getThemeIds(), themeId, null,
											 id -> styleManager.findTheme(id).getName());
		themeSpinner.itemProperty().addListener((observable, oldId, id) -> selectTheme.invoke(id));

		// Create control pane
		HBox controlPane = new HBox(CONTROL_PANE_H_GAP, new Label(THEME_STR), themeSpinner);
		controlPane.setMaxWidth(HBox.USE_PREF_SIZE);
		controlPane.setAlignment(Pos.CENTER_LEFT);
		controlPane.setPadding(CONTROL_PANE_PADDING);

		// Add control pane to content pane
		addContent(controlPane);

		// Create button: OK
		Button okButton = new Button(OK_STR);
		okButton.getProperties().put(BUTTON_GROUP_KEY, BUTTON_GROUP1);
		okButton.setOnAction(event ->
		{
			// Get result
			result = new Preferences(themeSpinner.getItem());

			// Close dialog
			requestClose();
		});
		addButton(okButton, HPos.RIGHT);

		// Create button: cancel
		Button cancelButton = new Button(CANCEL_STR);
		cancelButton.getProperties().put(BUTTON_GROUP_KEY, BUTTON_GROUP1);
		cancelButton.setOnAction(event -> requestClose());
		addButton(cancelButton, HPos.RIGHT);

		// Fire 'cancel' button if Escape key is pressed; fire 'OK' button if Ctrl+Enter is pressed
		setKeyFireButton(cancelButton, okButton);

		// When window is closed, restore old theme if dialog was not accepted
		setOnHiding(event ->
		{
			if ((result == null) && (themeId != null) && !themeId.equals(styleManager.getThemeId()))
				selectTheme.invoke(themeId);
		});

		// Apply new style sheet to scene
		applyStyleSheet();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static Preferences show(
		Window		owner,
		Preferences	preferences)
	{
		return new PreferencesDialog(owner, preferences).showDialog();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 */

	@Override
	protected Preferences getResult()
	{
		return result;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------

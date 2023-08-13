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
import javafx.geometry.Side;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javafx.scene.paint.Color;

import javafx.stage.Window;

import uk.blankaspect.common.function.IProcedure1;

import uk.blankaspect.ui.jfx.dialog.SimpleModalDialog;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

import uk.blankaspect.ui.jfx.spinner.CollectionSpinner;

import uk.blankaspect.ui.jfx.style.StyleManager;

import uk.blankaspect.ui.jfx.tabbedpane.TabPane2;
import uk.blankaspect.ui.jfx.tabbedpane.TabPaneUtils;

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

	private static final	Insets	TABBED_PANE_HEADER_PADDING	= new Insets(2.0, 2.0, 0.0, 2.0);

	private static final	Color	TABBED_PANE_BORDER_COLOUR	= Color.grayRgb(200);

	private static final	double	MIN_TAB_WIDTH	= 70.0;

	/** Miscellaneous strings. */
	private static final	String	PREFERENCES_STR	= "Preferences";
	private static final	String	THEME_STR		= "Theme";

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	private static	int	selectedTabIndex;

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The tabbed pane. */
	private	TabPane2	tabPane;

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

		// Create tabbed pane
		tabPane = new TabPane2();
		tabPane.setBorder(SceneUtils.createSolidBorder(TABBED_PANE_BORDER_COLOUR, Side.BOTTOM));
		tabPane.setTabMinWidth(MIN_TAB_WIDTH);

		// Set tabbed pane as content of dialog
		setContent(tabPane);

		// Set padding around header of tabbed pane
		tabPane.skinProperty().addListener(observable ->
				TabPaneUtils.setHeaderAreaPadding(tabPane, TABBED_PANE_HEADER_PADDING));

		// Add tabs to tabbed pane
		for (TabId tabId : TabId.values())
			tabPane.getTabs().add(tabId.createTab());

		// Select tab
		tabPane.getSelectionModel().select(selectedTabIndex);


		//----  Tab: appearance

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

		// Create pane: appearance
		HBox appearancePane = new HBox(CONTROL_PANE_H_GAP, new Label(THEME_STR), themeSpinner);
		appearancePane.setMaxWidth(VBox.USE_PREF_SIZE);
		appearancePane.setAlignment(Pos.CENTER_LEFT);
		appearancePane.setPadding(CONTROL_PANE_PADDING);

		// Set appearance pane as content of tab
		getTab(TabId.APPEARANCE).setContent(appearancePane);


		//----  Window

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

		// When window is closed, save index of selected tab; restore old theme if dialog was not accepted
		setOnHiding(event ->
		{
			// Save index of selected tab
			selectedTabIndex = tabPane.getSelectionModel().getSelectedIndex();

			// Restore old theme if dialog was not accepted
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
//  Instance methods
////////////////////////////////////////////////////////////////////////

	private Tab getTab(
		TabId	tabId)
	{
		return tabPane.getTabs().get(tabId.ordinal());
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

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// ENUMERATION: TAB IDENTIFIER


	private enum TabId
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		APPEARANCE
		(
			"Appearance"
		);

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	String	text;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private TabId(
			String	text)
		{
			this.text = text;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		private Tab createTab()
		{
			Tab tab = new Tab(text);
			tab.setClosable(false);
			return tab;
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------

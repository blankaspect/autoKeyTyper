/*====================================================================*\

AutoKeyTyperApp.java

Class: automatic key-typer application.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.autokeytyper;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.AWTException;
import java.awt.Robot;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javafx.application.Application;

import javafx.geometry.Dimension2D;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Side;

import javafx.scene.Node;
import javafx.scene.Scene;

import javafx.scene.control.Button;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import javafx.scene.paint.Color;

import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import uk.blankaspect.common.basictree.MapNode;

import uk.blankaspect.common.build.BuildUtils;

import uk.blankaspect.common.cls.ClassUtils;

import uk.blankaspect.common.config.AppAuxDirectory;
import uk.blankaspect.common.config.AppConfig;

import uk.blankaspect.common.css.CssRuleSet;
import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.common.exception2.BaseException;
import uk.blankaspect.common.exception2.FileException;
import uk.blankaspect.common.exception2.LocationException;

import uk.blankaspect.common.filesystem.PathnameUtils;

import uk.blankaspect.common.function.IProcedure1;

import uk.blankaspect.common.logging.ErrorLogger;

import uk.blankaspect.common.resource.ResourceProperties;
import uk.blankaspect.common.resource.ResourceUtils;

import uk.blankaspect.common.string.StringUtils;

import uk.blankaspect.ui.jfx.button.Buttons;
import uk.blankaspect.ui.jfx.button.ImageDataButton;

import uk.blankaspect.ui.jfx.container.PaneStyle;

import uk.blankaspect.ui.jfx.dialog.ErrorDialog;

import uk.blankaspect.ui.jfx.exec.ExecUtils;

import uk.blankaspect.ui.jfx.filler.FillerUtils;

import uk.blankaspect.ui.jfx.image.ImageData;

import uk.blankaspect.ui.jfx.label.Labels;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

import uk.blankaspect.ui.jfx.spinner.CollectionSpinner;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.RuleSetBuilder;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;

import uk.blankaspect.ui.jfx.window.WindowState;

//----------------------------------------------------------------------


// CLASS: AUTOMATIC KEY-TYPER APPLICATION


/**
 * This class implements a JavaFX application that has two abilities:
 * <ol>
 *   <li>
 *     It can generate a sequence of native <i>key typed</i> events for some chosen input text after a specified delay.
 *     The events are added to the platform's input queue, which is equivalent to typing the input text on a keyboard.
 *   </li>
 *   <li>
 *     It can periodically generate a pair of native <i>key pressed</i> and <i>key released</i> events for a specified
 *     key, with a specified interval between key presses.  This is equivalent to repeatedly pressing and releasing a
 *     key on a keyboard at regular intervals.
 *   </li>
 * </ol>
 * <p>
 * The first ability may be used to enter text into an input field (for example, a password field on a web page) that
 * doesn't allow text to be pasted into it from the system clipboard.  The second ability may be used to prevent a
 * computer from entering a power-saving sleep state while a non-interactive or unattended application is running.
 * </p>
 */

public class AutoKeyTyperApp
	extends Application
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The short name of the application. */
	private static final	String	SHORT_NAME	= "AutoKeyTyper";

	/** The long name of the application. */
	private static final	String	LONG_NAME	= "Automatic key typer";

	/** The name of the application when used as a key. */
	private static final	String	NAME_KEY	= StringUtils.firstCharToLowerCase(SHORT_NAME);

	/** The name of the file that contains the build properties of the application. */
	private static final	String	BUILD_PROPERTIES_FILENAME	= "build.properties";

	/** The filename of the CSS style sheet. */
	private static final	String	STYLE_SHEET_FILENAME	= NAME_KEY + "-%02d.css";

	/** A map from system-property keys to the default values of the corresponding delays (in milliseconds) in the
		<i>WINDOW_SHOWN</i> event handler of the main window. */
	private static final	Map<String, Integer>	MAIN_WINDOW_DELAYS	= Map.of
	(
		SystemPropertyKey.MAIN_WINDOW_DELAY_SIZE,     50,
		SystemPropertyKey.MAIN_WINDOW_DELAY_LOCATION, 25,
		SystemPropertyKey.MAIN_WINDOW_DELAY_OPACITY,  25
	);

	/** The margins that are applied to the visual bounds of each screen when determining whether the saved location of
		the main window is within a screen. */
	private static final	Insets	SCREEN_MARGINS	= new Insets(0.0, 32.0, 32.0, 0.0);

	/** The padding around the page pane. */
	private static final	Insets	PAGE_PANE_PADDING	= new Insets(4.0, 6.0, 4.0, 6.0);

	/** The horizontal gap between adjacent components of the control pane. */
	private static final	double	CONTROL_PANE_H_GAP	= 6.0;

	/** The padding around the control pane. */
	private static final	Insets	CONTROL_PANE_PADDING	= new Insets(6.0, 12.0, 6.0, 12.0);

	/** The padding around a button. */
	private static final	Insets	BUTTON_PADDING	= new Insets(4.0, 16.0, 4.0, 16.0);

	/** The name of the default key-map file. */
	private static final	String	KEY_MAP_FILENAME	= "keyMap.txt";

	/** Miscellaneous strings. */
	private static final	String	CONFIG_ERROR_STR	= "Configuration error";
	private static final	String	PAGE_STR			= "Page";
	private static final	String	EXIT_STR			= "Exit";

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			PaneStyle.ColourKey.PANE_BORDER,
			CssSelector.builder()
					.id(StyleConstants.NodeId.APP_MAIN_ROOT)
					.desc(StyleClass.MAIN_CONTROL_PANE)
					.build()
		)
	);

	/** CSS rule sets. */
	private static final	List<CssRuleSet>	RULE_SETS	= List.of
	(
		RuleSetBuilder.create()
				.selector(CssSelector.builder()
						.id(StyleConstants.NodeId.APP_MAIN_ROOT)
						.desc(StyleClass.MAIN_CONTROL_PANE)
						.build())
				.borders(Side.TOP)
				.build()
	);

	/** CSS style classes. */
	private interface StyleClass
	{
		String	MAIN_CONTROL_PANE	= StyleConstants.CLASS_PREFIX + "main-control-pane";
	}

	/** Keys of properties. */
	private interface PropertyKey
	{
		String	APPEARANCE	= "appearance";
		String	MAIN_WINDOW	= "mainWindow";
		String	PAGE		= "page";
		String	PAGES		= "pages";
		String	THEME		= "theme";
	}

	/** Keys of system properties. */
	private interface SystemPropertyKey
	{
		String	KEY_MAP						= "keyMap";
		String	MAIN_WINDOW_DELAY_LOCATION	= "mainWindowDelay.location";
		String	MAIN_WINDOW_DELAY_OPACITY	= "mainWindowDelay.opacity";
		String	MAIN_WINDOW_DELAY_SIZE		= "mainWindowDelay.size";
		String	USE_STYLE_SHEET_FILE		= "useStyleSheetFile";
	}

	/** Error messages. */
	private interface ErrorMsg
	{
		String	NO_AUXILIARY_DIRECTORY =
				"The location of the auxiliary directory could not be determined.";

		String	FAILED_TO_READ_KEY_MAP =
				"Failed to read the key map.";

		String	FAILED_TO_CREATE_ROBOT =
				"Failed to create a robot.";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The properties of the build of this application. */
	private	ResourceProperties			buildProperties;

	/** The string representation of the version of this application. */
	private	String						versionStr;

	/** The state of the main window. */
	private	WindowState					mainWindowState;

	/** The object that adds native key events to the platform's input queue. */
	private	Robot						robot;

	/** A map from characters to key codes. */
	private Map<Character, KeyMap.Key>	keyMap;

	/** A map of pages of the UI. */
	private Map<Page, IPage>			pages;

	/** The current page. */
	private	Page						page;

	/** The pane that contains the page spinner and the <i>exit</i> button. */
	private	HBox						controlPane;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of an automatic key-typer application.
	 */

	public AutoKeyTyperApp()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * This is the main method of the application.
	 *
	 * @param args
	 *          the command-line arguments of the application.
	 */

	public static void main(
		String[]	args)
	{
		launch(args);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the delay (in milliseconds) that is defined the system property with the specified key.
	 *
	 * @param  key
	 *           the key of the system property.
	 * @return the delay (in milliseconds) that is defined the system property whose key is {@code key}, or a default
	 *         value if there is no such property or the property value is not a valid integer.
	 */

	private static int getDelay(
		String	key)
	{
		int delay = MAIN_WINDOW_DELAYS.get(key);
		String value = System.getProperty(key);
		if (value != null)
		{
			try
			{
				delay = Integer.parseInt(value);
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
		}
		return delay;
	}

	//------------------------------------------------------------------

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
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 */

	@Override
	public void start(
		Stage	primaryStage)
	{
		// Make main window invisible until it is shown
		primaryStage.setOpacity(0.0);

		// Log stack trace of uncaught exception
		if (ClassUtils.isFromJar(getClass()))
		{
			Thread.setDefaultUncaughtExceptionHandler((thread, exception) ->
			{
				try
				{
					ErrorLogger.INSTANCE.write(exception);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			});
		}

		// Initialise instance variables
		mainWindowState = new WindowState(false, true);
		pages = new EnumMap<>(Page.class);
		for (Page page : Page.values())
			pages.put(page, page.createPage());
		page = Page.DELAYED;

		// Read build properties
		try
		{
			buildProperties =
					new ResourceProperties(ResourceUtils.normalisedPathname(getClass(), BUILD_PROPERTIES_FILENAME));
			versionStr = BuildUtils.versionString(getClass(), buildProperties);
		}
		catch (LocationException e)
		{
			e.printStackTrace();
		}

		// Create container for local variables
		class Vars
		{
			Configuration	config;
			BaseException	configException;
		}
		Vars vars = new Vars();

		// Read configuration file and decode configuration
		try
		{
			// Initialise configuration
			vars.config = new Configuration();

			// Read and decode configuration
			if (!AppConfig.noConfigFile())
			{
				// Read configuration file
				vars.config.read();

				// Decode configuration
				decodeConfig(vars.config.getConfig());
			}
		}
		catch (BaseException e)
		{
			vars.configException = e;
		}

		// Get style manager
		StyleManager styleManager = StyleManager.INSTANCE;

		// Select theme from system property
		String themeId = System.getProperty(StyleManager.SystemPropertyKey.THEME);
		if (!StringUtils.isNullOrEmpty(themeId))
			styleManager.selectThemeOrDefault(themeId);

		// Set ID and style-sheet filename on style manager
		if (Boolean.getBoolean(SystemPropertyKey.USE_STYLE_SHEET_FILE))
		{
			styleManager.setId(getClass().getSimpleName());
			styleManager.setStyleSheetFilename(STYLE_SHEET_FILENAME);
		}

		// Register the style properties of this class with the style manager
		styleManager.register(getClass(), COLOUR_PROPERTIES, RULE_SETS);

		// Create robot to generate key events
		try
		{
			robot = new Robot();
		}
		catch (AWTException e)
		{
			ErrorDialog.show(primaryStage, SHORT_NAME, new BaseException(ErrorMsg.FAILED_TO_CREATE_ROBOT, e));
			System.exit(1);
		}

		// Read and parse key-map file
		Path keyMapFile = null;
		try
		{
			// Get location of key-map file from system property
			String pathname = System.getProperty(SystemPropertyKey.KEY_MAP);
			keyMapFile = (pathname == null) ? null : Path.of(PathnameUtils.parsePathname(pathname));

			// Read lines of key-map file
			List<String> keyMapLines = null;
			try
			{
				keyMapLines = (keyMapFile == null) ? ResourceUtils.readLines(getClass(), KEY_MAP_FILENAME)
												   : Files.readAllLines(keyMapFile);
			}
			catch (IOException e)
			{
				throw new BaseException(ErrorMsg.FAILED_TO_READ_KEY_MAP, e);
			}

			// Parse key map
			keyMap = KeyMap.parse(keyMapLines);
		}
		catch (BaseException e)
		{
			e = (keyMapFile == null) ? new LocationException(e, KEY_MAP_FILENAME) : new FileException(e, keyMapFile);
			ErrorDialog.show(primaryStage, SHORT_NAME, e);
			System.exit(1);
		}

		// Initialise 'Images' class
		Images.init();

		// Create procedure to enable/disable control pane
		IProcedure1<Boolean> enableControlPane = enabled -> controlPane.setDisable(!enabled);

		// Initialise pages
		((DelayedPage)pages.get(Page.DELAYED)).init(robot, keyMap, enableControlPane);
		((PeriodicPage)pages.get(Page.PERIODIC)).init(robot, enableControlPane);
		((PreferencesPage)pages.get(Page.PREFERENCES)).init();

		// Create page pane
		StackPane pagePane = new StackPane();
		pagePane.setPadding(PAGE_PANE_PADDING);

		// Add pages to page pane
		for (Page page : pages.keySet())
		{
			Pane pane = pages.get(page).pane();
			pane.setUserData(page.key);
			pane.setVisible(false);
			pagePane.getChildren().add(pane);
		}

		// Create procedure to update page
		IProcedure1<Page> updatePage = page ->
		{
			// Notify current page that it is being deselected
			pages.get(this.page).onDeselecting();

			// Update current page
			this.page = page;

			// Show current page; hide other pages
			for (Node child : pagePane.getChildren())
				child.setVisible(page.key.equals(child.getUserData()));
		};

		// Spinner: page
		CollectionSpinner<Page> pageSpinner =
				CollectionSpinner.leftRightH(HPos.CENTER, true, Page.class, page, null, null);
		pageSpinner.itemProperty().addListener((observable, oldPage, page) -> updatePage.invoke(page));

		// Button: exit
		Button exitButton = Buttons.hNoShrink(EXIT_STR);
		exitButton.setPadding(BUTTON_PADDING);
		exitButton.setOnAction(event ->
				primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST)));

		// Create control pane
		controlPane = new HBox(CONTROL_PANE_H_GAP, Labels.hNoShrink(PAGE_STR), pageSpinner,
							   FillerUtils.hBoxFiller(10.0), exitButton);
		controlPane.setAlignment(Pos.CENTER);
		controlPane.setPadding(CONTROL_PANE_PADDING);
		controlPane.setBorder(SceneUtils.createSolidBorder(getColour(PaneStyle.ColourKey.PANE_BORDER), Side.TOP));
		controlPane.getStyleClass().add(StyleClass.MAIN_CONTROL_PANE);

		// Create main pane
		VBox mainPane = new VBox(4.0, pagePane, controlPane);
		mainPane.setId(StyleConstants.NodeId.APP_MAIN_ROOT);
		mainPane.setAlignment(Pos.CENTER);

		// Show initial page
		updatePage.invoke(page);

		// Create scene
		Scene scene = new Scene(mainPane);

		// Add style sheet to scene
		styleManager.addStyleSheet(scene);

		// Update images of image buttons when theme changes
		StyleManager.INSTANCE.themeProperty().addListener(observable ->
		{
			ImageData.updateImages();
			ImageDataButton.updateButtons();
		});

		// Set properties of main window
		primaryStage.setTitle(LONG_NAME + " " + versionStr);
		primaryStage.getIcons().addAll(Images.APP_ICON_IMAGES);

		// Set scene on main window
		primaryStage.setScene(scene);
		primaryStage.sizeToScene();

		// When main window is shown, set its width and location after a delay
		primaryStage.setOnShown(event ->
		{
			// Get preferred width
			double prefWidth = primaryStage.getWidth();

			// Set width of main window after a delay
			ExecUtils.afterDelay(getDelay(SystemPropertyKey.MAIN_WINDOW_DELAY_SIZE), () ->
			{
				// Get size of window from saved state
				Dimension2D size = mainWindowState.getSize();

				// Set width of window
				if (size != null)
				{
					primaryStage.setWidth(size.getWidth());
					primaryStage.setHeight(primaryStage.getHeight());	// required for Linux/GNOME
				}

				// Set location of main window after a delay
				ExecUtils.afterDelay(getDelay(SystemPropertyKey.MAIN_WINDOW_DELAY_LOCATION), () ->
				{
					// Get location of window from saved state
					Point2D location = mainWindowState.getLocation();

					// Invalidate location if top centre of window is not within a screen
					double width = primaryStage.getWidth();
					if ((location != null) && !SceneUtils.isWithinScreen(location.getX() + 0.5 * width, location.getY(),
																		 SCREEN_MARGINS))
						location = null;

					// If there is no location, centre window within primary screen
					if (location == null)
						location = SceneUtils.centreInScreen(width, primaryStage.getHeight());

					// Set location of window
					primaryStage.setX(location.getX());
					primaryStage.setY(location.getY());

					// Perform remaining initialisation after a delay
					ExecUtils.afterDelay(getDelay(SystemPropertyKey.MAIN_WINDOW_DELAY_OPACITY), () ->
					{
						// Make window visible
						primaryStage.setOpacity(1.0);

						// Set lower bound on width of window
						primaryStage.setMinWidth(prefWidth);

						// Prevent height of window from changing
						double height = primaryStage.getHeight();
						primaryStage.setMinHeight(height);
						primaryStage.setMaxHeight(height);

						// Report any configuration error
						if (vars.configException != null)
							ErrorDialog.show(primaryStage, SHORT_NAME + " : " + CONFIG_ERROR_STR, vars.configException);
					});
				});
			});
		});

		// Write configuration file when main window is closed
		if (vars.config != null)
		{
			primaryStage.setOnHiding(event ->
			{
				// Update state of main window
				mainWindowState.restoreAndUpdate(primaryStage);

				// Write configuration
				if (vars.config.canWrite())
				{
					try
					{
						// Encode configuration
						encodeConfig(vars.config.getConfig());

						// Write configuration file
						vars.config.write();
					}
					catch (FileException e)
					{
						ErrorDialog.show(primaryStage, SHORT_NAME + " : " + CONFIG_ERROR_STR, e);
					}
				}
			});
		}

		// Display main window
		primaryStage.show();

		// Notify pages that their containing window has been shown
		for (Page page : pages.keySet())
			pages.get(page).onWindowShown();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Encodes the configuration of this application to the specified root node.
	 *
	 * @param rootNode
	 *          the root node of the configuration.
	 */

	private void encodeConfig(
		MapNode	rootNode)
	{
		// Clear properties
		rootNode.clear();

		// Encode theme ID
		String themeId = StyleManager.INSTANCE.getThemeId();
		if (themeId != null)
			rootNode.addMap(PropertyKey.APPEARANCE).addString(PropertyKey.THEME, themeId);

		// Encode state of main window
		MapNode windowStateNode = mainWindowState.encodeTree();
		if (!windowStateNode.isEmpty())
			rootNode.add(PropertyKey.MAIN_WINDOW, windowStateNode);

		// Encode page
		rootNode.addString(PropertyKey.PAGE, page.key);

		// Encode state of pages
		MapNode pagesNode = rootNode.addMap(PropertyKey.PAGES);
		for (Page page : pages.keySet())
		{
			MapNode pageNode = pages.get(page).encodeState();
			if (pageNode != null)
				pagesNode.add(page.key, pageNode);
		}
	}

	//------------------------------------------------------------------

	/**
	 * Decodes the configuration of this application from the specified root node.
	 *
	 * @param rootNode
	 *          the root node of the configuration.
	 */

	private void decodeConfig(
		MapNode	rootNode)
	{
		// Decode theme ID
		String key = PropertyKey.APPEARANCE;
		if (rootNode.hasMap(key))
		{
			String themeId = rootNode.getMapNode(key).getString(PropertyKey.THEME, StyleManager.DEFAULT_THEME_ID);
			StyleManager.INSTANCE.selectThemeOrDefault(themeId);
		}

		// Decode state of main window
		key = PropertyKey.MAIN_WINDOW;
		if (rootNode.hasMap(key))
			mainWindowState.decodeTree(rootNode.getMapNode(key));

		// Decode page
		page = rootNode.getEnumValue(Page.class, PropertyKey.PAGE, p -> p.key, Page.DELAYED);

		// Decode state of pages
		key = PropertyKey.PAGES;
		if (rootNode.hasMap(key))
		{
			MapNode pagesNode = rootNode.getMapNode(key);
			for (Page page : pages.keySet())
			{
				if (pagesNode.hasMap(page.key))
					pages.get(page).decodeState(pagesNode.getMapNode(page.key));
			}
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// ENUMERATION: PAGE


	private enum Page
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		DELAYED
		(
			"Delayed"
		)
		{
			protected IPage createPage()
			{
				return new DelayedPage();
			}
		},

		PERIODIC
		(
			"Periodic"
		)
		{
			protected IPage createPage()
			{
				return new PeriodicPage();
			}
		},

		PREFERENCES
		(
			"Preferences"
		)
		{
			protected IPage createPage()
			{
				return new PreferencesPage();
			}
		};

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	String	key;
		private	String	text;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private Page(
			String	text)
		{
			// Initialise instance variables
			key = name().toLowerCase();
			this.text = text;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Abstract methods
	////////////////////////////////////////////////////////////////////

		protected abstract IPage createPage();

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public String toString()
		{
			return text;
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: CONFIGURATION


	/**
	 * This class implements the configuration of the application.
	 */

	private static class Configuration
		extends AppConfig
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		/** The identifier of a configuration file. */
		private static final	String	ID	= "AO0O5A1TSWTBGLQQ0AFJ46F5W";

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a new instance of the configuration of the application.
		 *
		 * @throws BaseException
		 *           if the configuration directory could not be determined.
		 */

		private Configuration()
			throws BaseException
		{
			// Call superclass constructor
			super(ID, NAME_KEY, SHORT_NAME, LONG_NAME);

			// Determine location of config file
			if (!noConfigFile())
			{
				// Get location of parent directory of config file
				AppAuxDirectory.Directory directory =
						AppAuxDirectory.getDirectory(NAME_KEY, getClass().getEnclosingClass());
				if (directory == null)
					throw new BaseException(ErrorMsg.NO_AUXILIARY_DIRECTORY);

				// Set parent directory of config file
				setDirectory(directory.location());
			}
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------

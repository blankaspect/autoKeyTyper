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

import java.lang.invoke.MethodHandles;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import javafx.application.Application;
import javafx.application.Platform;

import javafx.geometry.Dimension2D;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Side;

import javafx.scene.Group;
import javafx.scene.Scene;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import javafx.scene.paint.Color;

import javafx.stage.Stage;

import javafx.util.Duration;

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

import uk.blankaspect.common.function.IProcedure0;

import uk.blankaspect.common.logging.ErrorLogger;

import uk.blankaspect.common.os.OsUtils;

import uk.blankaspect.common.resource.ResourceProperties;
import uk.blankaspect.common.resource.ResourceUtils;

import uk.blankaspect.common.string.StringUtils;

import uk.blankaspect.ui.jfx.button.Buttons;
import uk.blankaspect.ui.jfx.button.GraphicButton;

import uk.blankaspect.ui.jfx.container.PaneStyle;

import uk.blankaspect.ui.jfx.dialog.ErrorDialog;

import uk.blankaspect.ui.jfx.exec.ExecUtils;

import uk.blankaspect.ui.jfx.filler.FillerUtils;

import uk.blankaspect.ui.jfx.font.Fonts;

import uk.blankaspect.ui.jfx.icon.Icons;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

import uk.blankaspect.ui.jfx.spinner.IntRangeSpinner;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.FxStyleClass;
import uk.blankaspect.ui.jfx.style.RuleSetBuilder;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;

import uk.blankaspect.ui.jfx.window.WindowState;

//----------------------------------------------------------------------


// CLASS: AUTOMATIC KEY-TYPER APPLICATION


/**
 * This class implements a JavaFX application that can generate a sequence of native key events for some chosen input
 * text.  The key events are added to the platform's input queue after a specified delay, which is equivalent to typing
 * the input text on a keyboard.
 * <p>
 * This application may be used to facilitate the entry of text into an input field (for example, a password field on a
 * web page) that does not allow text to be pasted from the system clipboard.
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

	/** The delay (in milliseconds) in a <i>WINDOW_SHOWN</i> event handler on platforms other than Windows. */
	private static final	int		WINDOW_SHOWN_DELAY	= 150;

	/** The delay (in milliseconds) in a <i>WINDOW_SHOWN</i> event handler on Windows. */
	private static final	int		WINDOW_SHOWN_DELAY_WINDOWS	= 50;

	/** The delay (in milliseconds) before making the main window visible by restoring its opacity. */
	private static final	int		WINDOW_VISIBLE_DELAY	= 50;

	/** The margins that are applied to the visual bounds of each screen when determining whether the saved location of
		the main window is within a screen. */
	private static final	Insets	SCREEN_MARGINS	= new Insets(0.0, 32.0, 32.0, 0.0);

	/** The minimum delay (in seconds) when generating key events. */
	private static final	int		MIN_DELAY		= 1;

	/** The maximum delay (in seconds) when generating key events. */
	private static final	int		MAX_DELAY		= 15;

	/** The default delay (in seconds) when generating key events. */
	private static final	int		DEFAULT_DELAY	= 3;

	/** The preferred number of columns of the input field. */
	private static final	int		INPUT_FIELD_NUM_COLUMNS	= 40;

	/** The horizontal gap between adjacent components of the control pane. */
	private static final	double	CONTROL_PANE_H_GAP	= 6.0;

	/** The vertical gap between adjacent components of the control pane. */
	private static final	double	CONTROL_PANE_V_GAP	= 6.0;

	/** The padding around the control pane. */
	private static final	Insets	CONTROL_PANE_PADDING	= new Insets(8.0, 8.0, 8.0, 12.0);

	/** The horizontal gap between adjacent buttons of the button pane. */
	private static final	double	BUTTON_PANE_H_GAP	= 12.0;

	/** The vertical gap between adjacent buttons of the button pane. */
	private static final	double	BUTTON_PANE_V_GAP	= 6.0;

	/** The padding around the button pane. */
	private static final	Insets	BUTTON_PANE_PADDING	= new Insets(4.0, 12.0, 4.0, 12.0);

	/** The name of the default key-map file. */
	private static final	String	KEY_MAP_FILENAME	= "keyMap.txt";

	/** Miscellaneous strings. */
	private static final	String	CONFIG_ERROR_STR	= "Configuration error";
	private static final	String	INPUT_STR			= "Input";
	private static final	String	CLEAR_INPUT_STR		= "Clear input";
	private static final	String	DELAY_STR			= "Delay";
	private static final	String	SECONDS_STR			= "seconds";
	private static final	String	PREFERENCES_STR		= "Preferences";
	private static final	String	ABORT_STR			= "Abort";
	private static final	String	GENERATE_STR		= "Generate";

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.CLEAR_INPUT_BUTTON_DISC,
			CssSelector.builder()
					.id(StyleConstants.NodeId.APP_MAIN_ROOT)
					.desc(Icons.StyleClass.CLEAR01_DISC)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.STROKE,
			ColourKey.CLEAR_INPUT_BUTTON_CROSS,
			CssSelector.builder()
					.id(StyleConstants.NodeId.APP_MAIN_ROOT)
					.desc(Icons.StyleClass.CLEAR01_CROSS)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			PaneStyle.ColourKey.PANE_BORDER,
			CssSelector.builder()
					.id(StyleConstants.NodeId.APP_MAIN_ROOT)
					.desc(StyleClass.CONTROL_PANE)
					.build()
		)
	);

	/** CSS rule sets. */
	private static final	List<CssRuleSet>	RULE_SETS	= List.of
	(
		RuleSetBuilder.create()
				.selector(CssSelector.builder()
						.id(StyleConstants.NodeId.APP_MAIN_ROOT)
						.desc(StyleClass.CONTROL_PANE)
						.build())
				.borders(Side.BOTTOM)
				.build(),
		RuleSetBuilder.create()
				.selector(CssSelector.builder()
						.id(StyleConstants.NodeId.APP_MAIN_ROOT)
						.desc(StyleClass.INPUT_FIELD)
						.desc(FxStyleClass.TEXT)
						.build())
				.grayFontSmoothing()
				.build()
	);

	/** CSS style classes. */
	private interface StyleClass
	{
		String	CONTROL_PANE	= StyleConstants.CLASS_PREFIX + "control-pane";
		String	INPUT_FIELD		= StyleConstants.CLASS_PREFIX + "input-field";
	}

	/** Keys of colours that are used in colour properties. */
	private interface ColourKey
	{
		String	PREFIX	= StyleManager.colourKeyPrefix(MethodHandles.lookup().lookupClass().getEnclosingClass());

		String	CLEAR_INPUT_BUTTON_CROSS	= PREFIX + "clearInputButton.cross";
		String	CLEAR_INPUT_BUTTON_DISC		= PREFIX + "clearInputButton.disc";
	}

	/** Keys of properties. */
	private interface PropertyKey
	{
		String	APPEARANCE	= "appearance";
		String	DELAY		= "delay";
		String	MAIN_WINDOW	= "mainWindow";
		String	THEME		= "theme";
	}

	/** Keys of system properties. */
	private interface SystemPropertyKey
	{
		String	KEY_MAP					= "keyMap";
		String	USE_STYLE_SHEET_FILE	= "useStyleSheetFile";
		String	WINDOW_SHOWN_DELAY		= "windowShownDelay";
	}

	/** Error messages. */
	private interface ErrorMsg
	{
		String	NO_AUXILIARY_DIRECTORY		= "The location of the auxiliary directory could not be determined.";
		String	FAILED_TO_READ_KEY_MAP		= "Failed to read the key map.";
		String	FAILED_TO_CREATE_ROBOT		= "Failed to create a robot.";
		String	CHARACTERS_NOT_IN_KEY_MAP	= "The following characters cannot be mapped to key codes:";
	}

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	private static	Map<Character, KeyMap.Key>	keyMap	= Collections.emptyMap();

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The properties of the build of this application. */
	private	ResourceProperties	buildProperties;

	/** The string representation of the version of this application. */
	private	String				versionStr;

	/** The state of the main window. */
	private	WindowState			mainWindowState;

	/** The delay (in seconds) when generating key events. */
	private	int					delay;

	/** The object that adds native key events to the platform's input queue. */
	private	Robot				robot;

	/** The <i>delay</i> spinner. */
	private	IntRangeSpinner		delaySpinner;

	/** The <i>abort</i> button. */
	private	Button				abortButton;

	/** The <i>generate</i> button. */
	private	Button				generateButton;

	/** The timer that provides a delay of the chosen length when generating key events. */
	private	Timeline			delayTimer;

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
	 * Returns the delay (in milliseconds) in a <i>WINDOW_SHOWN</i> event handler.
	 *
	 * @return the delay (in milliseconds) in a <i>WINDOW_SHOWN</i> event handler.
	 */

	private static int getWindowShownDelay()
	{
		int delay = OsUtils.isWindows() ? WINDOW_SHOWN_DELAY_WINDOWS : WINDOW_SHOWN_DELAY;
		String value = System.getProperty(SystemPropertyKey.WINDOW_SHOWN_DELAY);
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
		delay = DEFAULT_DELAY;

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

		// Register the style properties of this class and its dependencies with the style manager
		styleManager.register(getClass(), COLOUR_PROPERTIES, RULE_SETS, PaneStyle.class);

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

		// Create control pane
		GridPane controlPane = new GridPane();
		controlPane.setHgap(CONTROL_PANE_H_GAP);
		controlPane.setVgap(CONTROL_PANE_V_GAP);
		controlPane.setAlignment(Pos.CENTER);
		controlPane.setPadding(CONTROL_PANE_PADDING);
		controlPane.setBorder(SceneUtils.createSolidBorder(getColour(PaneStyle.ColourKey.PANE_BORDER), Side.BOTTOM));
		controlPane.getStyleClass().add(StyleClass.CONTROL_PANE);
		VBox.setVgrow(controlPane, Priority.ALWAYS);

		// Initialise column constraints
		ColumnConstraints column = new ColumnConstraints();
		column.setMinWidth(Region.USE_PREF_SIZE);
		column.setHalignment(HPos.RIGHT);
		controlPane.getColumnConstraints().add(column);

		column = new ColumnConstraints();
		column.setHalignment(HPos.LEFT);
		controlPane.getColumnConstraints().add(column);

		// Initialise row index
		int row = 0;

		// Create text field: input
		TextField inputField = new TextField();
		inputField.setFont(Fonts.monoFont());
		inputField.setPrefColumnCount(INPUT_FIELD_NUM_COLUMNS);
		inputField.setOnAction(event -> generateButton.fire());
		inputField.getStyleClass().add(StyleClass.INPUT_FIELD);
		HBox.setHgrow(inputField, Priority.ALWAYS);

		// Create button: clear input
		Group clearIcon = Icons.clear01(getColour(ColourKey.CLEAR_INPUT_BUTTON_DISC),
										getColour(ColourKey.CLEAR_INPUT_BUTTON_CROSS));
		GraphicButton clearInputButton = new GraphicButton(clearIcon, CLEAR_INPUT_STR);
		clearInputButton.setOnAction(event ->
		{
			Platform.runLater(() ->
			{
				inputField.clear();
				inputField.requestFocus();
			});
		});

		// Create pane: input
		HBox inputPane = new HBox(4.0, inputField, clearInputButton);
		inputPane.setAlignment(Pos.CENTER_LEFT);
		controlPane.addRow(row++, new Label(INPUT_STR), inputPane);
		GridPane.setHgrow(inputPane, Priority.ALWAYS);

		// Create spinner: delay
		delaySpinner = IntRangeSpinner.leftRightH(HPos.CENTER, false, MIN_DELAY, MAX_DELAY, delay, "000", null);

		// Create pane: delay
		HBox delayPane = new HBox(5.0, delaySpinner, new Label(SECONDS_STR));
		delayPane.setAlignment(Pos.CENTER_LEFT);
		controlPane.addRow(row++, new Label(DELAY_STR), delayPane);

		// Create button: preferences
		Button preferencesButton = Buttons.hNoShrink(PREFERENCES_STR);
		preferencesButton.setOnAction(event -> PreferencesDialog.show(primaryStage));

		// Create procedure to update buttons
		IProcedure0 updateButtons = () ->
		{
			boolean noInput = inputField.getText().isEmpty();
			clearInputButton.setDisable((delayTimer != null) || noInput);
			preferencesButton.setDisable(delayTimer != null);
			abortButton.setDisable(delayTimer == null);
			generateButton.setDisable((delayTimer != null) || noInput);
		};

		// Create button: abort
		abortButton = Buttons.hExpansive(ABORT_STR);
		abortButton.setOnAction(event ->
		{
			// Stop delay timer and invalidate it
			if (delayTimer != null)
			{
				// Stop delay timer
				delayTimer.stop();

				// Invalidate delay timer
				delayTimer = null;
			}

			// Update buttons
			updateButtons.invoke();
		});

		// Create button: generate
		generateButton = Buttons.hExpansive(GENERATE_STR);
		generateButton.setOnAction(event ->
		{
			// Get text from input field
			String text = inputField.getText();

			// If there is text, generate key presses and releases for its characters after a delay
			if (!text.isEmpty())
			{
				// Create list of characters of input text that do not appear in key map
				List<Character> unmappedChars = new ArrayList<>();
				for (int i = 0; i < text.length(); i++)
				{
					char ch = text.charAt(i);
					if (!keyMap.containsKey(ch))
						unmappedChars.add(ch);
				}

				// Report characters not in key map
				if (!unmappedChars.isEmpty())
				{
					StringBuilder buffer = new StringBuilder(256);
					buffer.append(ErrorMsg.CHARACTERS_NOT_IN_KEY_MAP);
					for (int i = 0; i < unmappedChars.size(); i++)
					{
						if (i % 10 == 0)
							buffer.append('\n');
						else
							buffer.append("  ");
						buffer.append(unmappedChars.get(i));
					}
					ErrorDialog.show(primaryStage, GENERATE_STR, buffer.toString());
					return;
				}

				// Generate key presses and releases after a delay
				delayTimer = new Timeline(new KeyFrame(Duration.seconds((double)delaySpinner.getValue()), event0 ->
				{
					// Invalidate delay timer
					delayTimer = null;

					// Generate key presses and releases
					for (int i = 0; i < text.length(); i++)
					{
						char ch = text.charAt(i);
						KeyMap.Key key = keyMap.get(ch);
						if (key != null)
							key.type(robot);
					}

					// Update buttons
					updateButtons.invoke();
				}));

				// Update buttons
				updateButtons.invoke();

				// Request focus on 'abort' button
				abortButton.requestFocus();

				// Start delay
				delayTimer.play();
			}
		});

		// Update buttons when content of input field changes
		inputField.textProperty().addListener(observable -> updateButtons.invoke());

		// Update buttons
		updateButtons.invoke();

		// Create right button pane
		TilePane rightButtonPane = new TilePane(BUTTON_PANE_H_GAP, BUTTON_PANE_V_GAP, abortButton, generateButton);
		rightButtonPane.setPrefColumns(rightButtonPane.getChildren().size());
		rightButtonPane.setAlignment(Pos.CENTER);

		// Create button pane
		HBox buttonPane = new HBox(preferencesButton, FillerUtils.hBoxFiller(16.0), rightButtonPane);
		buttonPane.setAlignment(Pos.CENTER);
		buttonPane.setPadding(BUTTON_PANE_PADDING);

		// Create main pane
		VBox mainPane = new VBox(controlPane, buttonPane);
		mainPane.setId(StyleConstants.NodeId.APP_MAIN_ROOT);
		mainPane.setAlignment(Pos.CENTER);

		// Create scene
		Scene scene = new Scene(mainPane);

		// Add style sheet to scene
		styleManager.addStyleSheet(scene);

		// Set properties of main window
		primaryStage.setTitle(LONG_NAME + " " + versionStr);
		primaryStage.getIcons().addAll(Images.APP_ICON_IMAGES);

		// Set scene on main window
		primaryStage.setScene(scene);
		primaryStage.sizeToScene();

		// When main window is shown, set its width and location after a delay
		primaryStage.setOnShown(event ->
		{
			// Set width and location of main window after a delay
			ExecUtils.afterDelay(getWindowShownDelay(), () ->
			{
				// Get size of window from saved state
				Dimension2D size = mainWindowState.getSize();

				// Set width of window
				if (size != null)
				{
					primaryStage.setWidth(size.getWidth());
					primaryStage.setHeight(primaryStage.getHeight());	// required for Linux/GNOME
				}

				// Get location of window from saved state
				Point2D location = mainWindowState.getLocation();

				// Invalidate location if top centre of window is not within a screen
				double width = primaryStage.getWidth();
				if ((location != null)
						&& !SceneUtils.isWithinScreen(location.getX() + 0.5 * width, location.getY(), SCREEN_MARGINS))
					location = null;

				// If there is no location, centre window within primary screen
				if (location == null)
					location = SceneUtils.centreInScreen(width, primaryStage.getHeight());

				// Set location of window
				primaryStage.setX(location.getX());
				primaryStage.setY(location.getY());

				// Prevent height of window from changing
				double height = Math.ceil(primaryStage.getHeight());
				primaryStage.setMinHeight(height);
				primaryStage.setMaxHeight(height);

				// Perform remaining initialisation after a delay
				ExecUtils.afterDelay(WINDOW_VISIBLE_DELAY, () ->
				{
					// Make window visible
					primaryStage.setOpacity(1.0);

					// Report any configuration error
					if (vars.configException != null)
						ErrorDialog.show(primaryStage, SHORT_NAME + " : " + CONFIG_ERROR_STR, vars.configException);
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

		// Encode delay
		if (delaySpinner != null)
			delay = delaySpinner.getValue();
		rootNode.addInt(PropertyKey.DELAY, delay);
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

		// Decode delay
		delay = rootNode.getInt(PropertyKey.DELAY, DEFAULT_DELAY);
	}

	//------------------------------------------------------------------

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

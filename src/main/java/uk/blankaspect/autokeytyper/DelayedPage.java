/*====================================================================*\

DelayedPage.java

Class: 'delayed' page.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.autokeytyper;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.Robot;

import java.lang.invoke.MethodHandles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import javafx.application.Platform;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Group;

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

import javafx.util.Duration;

import uk.blankaspect.common.basictree.MapNode;

import uk.blankaspect.common.css.CssRuleSet;
import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.common.function.IProcedure0;
import uk.blankaspect.common.function.IProcedure1;

import uk.blankaspect.ui.jfx.button.Buttons;
import uk.blankaspect.ui.jfx.button.GraphicButton;

import uk.blankaspect.ui.jfx.container.PaneStyle;

import uk.blankaspect.ui.jfx.dialog.ErrorDialog;

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

//----------------------------------------------------------------------


// CLASS: 'DELAYED' PAGE


public class DelayedPage
	implements IPage
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The minimum delay (in seconds) when generating key events. */
	private static final	int		MIN_DELAY		= 1;

	/** The maximum delay (in seconds) when generating key events. */
	private static final	int		MAX_DELAY		= 20;

	/** The default delay (in seconds) when generating key events. */
	private static final	int		DEFAULT_DELAY	= 3;

	/** The preferred number of columns of the input field. */
	private static final	int		INPUT_FIELD_NUM_COLUMNS	= 40;

	/** The horizontal gap between adjacent components of the control pane. */
	private static final	double	CONTROL_PANE_H_GAP	= 6.0;

	/** The vertical gap between adjacent components of the control pane. */
	private static final	double	CONTROL_PANE_V_GAP	= 8.0;

	/** The padding around the control pane. */
	private static final	Insets	CONTROL_PANE_PADDING	= new Insets(8.0, 8.0, 8.0, 12.0);

	/** The horizontal gap between adjacent buttons of the button pane. */
	private static final	double	BUTTON_PANE_H_GAP	= 12.0;

	/** The vertical gap between adjacent buttons of the button pane. */
	private static final	double	BUTTON_PANE_V_GAP	= 6.0;

	/** The padding around the button pane. */
	private static final	Insets	BUTTON_PANE_PADDING	= new Insets(4.0, 6.0, 0.0, 6.0);

	/** Miscellaneous strings. */
	private static final	String	INPUT_STR		= "Input";
	private static final	String	CLEAR_INPUT_STR	= "Clear input";
	private static final	String	DELAY_STR		= "Delay";
	private static final	String	SECONDS_STR		= "seconds";
	private static final	String	ABORT_STR		= "Abort";
	private static final	String	GENERATE_STR	= "Generate";

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.CLEAR_INPUT_BUTTON_DISC,
			CssSelector.builder()
					.cls(StyleClass.DELAYED_PAGE)
					.desc(Icons.StyleClass.CLEAR01_DISC)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.STROKE,
			ColourKey.CLEAR_INPUT_BUTTON_CROSS,
			CssSelector.builder()
					.cls(StyleClass.DELAYED_PAGE)
					.desc(Icons.StyleClass.CLEAR01_CROSS)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			PaneStyle.ColourKey.PANE_BORDER,
			CssSelector.builder()
					.cls(StyleClass.DELAYED_PAGE)
					.desc(StyleClass.CONTROL_PANE)
					.build()
		)
	);

	/** CSS rule sets. */
	private static final	List<CssRuleSet>	RULE_SETS	= List.of
	(
		RuleSetBuilder.create()
				.selector(CssSelector.builder()
						.cls(StyleClass.DELAYED_PAGE)
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
		String	DELAYED_PAGE	= StyleConstants.CLASS_PREFIX + "delayed-page";
		String	INPUT_FIELD		= StyleConstants.CLASS_PREFIX + "input-field";
	}

	/** Keys of colours that are used in colour properties. */
	private interface ColourKey
	{
		String	PREFIX	= StyleManager.colourKeyPrefix(MethodHandles.lookup().lookupClass().getEnclosingClass());

		String	CLEAR_INPUT_BUTTON_CROSS	= PREFIX + "clearInputButton.cross";
		String	CLEAR_INPUT_BUTTON_DISC		= PREFIX + "clearInputButton.disc";
	}

	/** Error messages. */
	private interface ErrorMsg
	{
		String	CHARACTERS_NOT_IN_KEY_MAP =
				"The following characters cannot be mapped to key codes:";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The saved state of this page. */
	private	State			state;

	/** The <i>delay</i> spinner. */
	private	IntRangeSpinner	delaySpinner;

	/** The <i>abort</i> button. */
	private	Button			abortButton;

	/** The <i>generate</i> button. */
	private	Button			generateButton;

	/** The outer pane of this page. */
	private	VBox			pane;

	/** The timer that provides a delay of the chosen length when generating key events. */
	private	Timeline		timer;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Register the style properties of this class with the style manager
		StyleManager.INSTANCE.register(DelayedPage.class, COLOUR_PROPERTIES, RULE_SETS);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public DelayedPage()
	{
		// Initialise instance variables
		state = new State();
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
	public VBox pane()
	{
		return pane;
	}

	//------------------------------------------------------------------

	@Override
	public MapNode encodeState()
	{
		// Update state
		state.delay = delaySpinner.value();

		// Encode state and return result
		return state.encodeTree();
	}

	//------------------------------------------------------------------

	@Override
	public void decodeState(
		MapNode	rootNode)
	{
		state.decodeTree(rootNode);
	}

	//------------------------------------------------------------------

	@Override
	public void onDeselecting()
	{
		abortButton.fire();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public void init(
		Robot						robot,
		Map<Character, KeyMap.Key>	keyMap,
		IProcedure1<Boolean>		enableExternalControls)
	{
		// Create control pane
		GridPane controlPane = new GridPane();
		controlPane.setHgap(CONTROL_PANE_H_GAP);
		controlPane.setVgap(CONTROL_PANE_V_GAP);
		controlPane.setAlignment(Pos.CENTER);
		controlPane.setPadding(CONTROL_PANE_PADDING);
		controlPane.setBorder(SceneUtils.createSolidBorder(getColour(PaneStyle.ColourKey.PANE_BORDER)));
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
		delaySpinner = IntRangeSpinner.leftRightH(HPos.CENTER, false, MIN_DELAY, MAX_DELAY, state.delay, "000", null);

		// Create pane: delay
		HBox delayPane = new HBox(5.0, delaySpinner, new Label(SECONDS_STR));
		delayPane.setAlignment(Pos.CENTER_LEFT);
		controlPane.addRow(row++, new Label(DELAY_STR), delayPane);

		// Create procedure to update buttons
		IProcedure0 updateButtons = () ->
		{
			boolean timerRunning = (timer != null);
			boolean noInput = inputField.getText().isEmpty();
			clearInputButton.setDisable(timerRunning || noInput);
			abortButton.setDisable(!timerRunning);
			generateButton.setDisable(timerRunning || noInput);
		};

		// Create button: abort
		abortButton = Buttons.hExpansive(ABORT_STR);
		abortButton.setOnAction(event ->
		{
			// Stop delay timer and invalidate it
			if (timer != null)
			{
				// Stop delay timer
				timer.stop();

				// Invalidate delay timer
				timer = null;
			}

			// Update buttons
			updateButtons.invoke();

			// Enable external controls
			enableExternalControls.invoke(true);
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
					ErrorDialog.show(SceneUtils.getWindow(generateButton), GENERATE_STR, buffer.toString());
					return;
				}

				// Create timer to generate key presses after a delay
				timer = new Timeline(new KeyFrame(Duration.seconds((double)delaySpinner.value()), event0 ->
				{
					// Invalidate delay timer
					timer = null;

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

					// Enable external controls
					enableExternalControls.invoke(true);
				}));

				// Update buttons
				updateButtons.invoke();

				// Disable external controls
				enableExternalControls.invoke(false);

				// Request focus on 'abort' button
				abortButton.requestFocus();

				// Start delay
				timer.play();
			}
		});

		// Update buttons when content of input field changes
		inputField.textProperty().addListener(observable -> updateButtons.invoke());

		// Update buttons
		updateButtons.invoke();

		// Create button pane
		TilePane buttonPane = new TilePane(BUTTON_PANE_H_GAP, BUTTON_PANE_V_GAP, abortButton, generateButton);
		buttonPane.setPrefColumns(buttonPane.getChildren().size());
		buttonPane.setAlignment(Pos.CENTER_RIGHT);
		buttonPane.setPadding(BUTTON_PANE_PADDING);

		// Create outer pane
		pane = new VBox(4.0, controlPane, buttonPane);
		pane.setAlignment(Pos.CENTER);
		pane.getStyleClass().add(StyleClass.DELAYED_PAGE);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: STATE


	private static class State
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		/** Keys of properties. */
		private interface PropertyKey
		{
			String	DELAY	= "delay";
		}

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private int	delay;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private State()
		{
			// Initialise instance variables
			delay = DEFAULT_DELAY;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		private MapNode encodeTree()
		{
			// Create root node
			MapNode rootNode = new MapNode();

			// Encode delay
			rootNode.addInt(PropertyKey.DELAY, delay);

			// Return root node
			return rootNode;
		}

		//--------------------------------------------------------------

		private void decodeTree(
			MapNode	rootNode)
		{
			// Decode delay
			delay = rootNode.getInt(PropertyKey.DELAY, DEFAULT_DELAY);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------

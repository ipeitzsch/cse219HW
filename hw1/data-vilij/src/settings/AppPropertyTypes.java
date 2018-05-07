package settings;

/**
 * This enumerable type lists the various application-specific property types listed in the initial set of properties to
 * be loaded from the workspace properties <code>xml</code> file specified by the initialization parameters.
 *
 * @author Ritwik Banerjee
 * @see vilij.settings.InitializationParams
 */
public enum AppPropertyTypes {

    /* resource files and folders */
    DATA_RESOURCE_PATH,

    /* user interface icon file names */
    SCREENSHOT_ICON,

    /* tooltips for user interface buttons */
    SCREENSHOT_TOOLTIP,

    /* error messages */
    RESOURCE_SUBDIR_NOT_FOUND,

    /* application-specific message titles */
    SAVE_UNSAVED_WORK_TITLE,

    /* application-specific messages */
    SAVE_UNSAVED_WORK,

    /* application-specific parameters */
    DATA_FILE_EXT,
    DATA_FILE_EXT_DESC,
    TEXT_AREA,
    SPECIFIED_FILE,
    LEFT_PANE_TITLE,
    LEFT_PANE_TITLEFONT,
    LEFT_PANE_TITLESIZE,
    CHART_TITLE,
    DISPLAY_BUTTON_TEXT,
    MAX_ITERATIONS,
    REFRESH,
    CONTINUOUS,
    NUM_LABELS,
    NUM_LABELS_ERROR,
    INTEGERS_ONLY,
    RUN_ICON,
    RUN_TOOLTIP,
    OPTIONS_TOOLTIP,
    OPTIONS_ICON,
    LOAD_WORK_TITLE,
    STILL_RUNNING_TITLE,
    STILL_RUNNING_MSG,
    DONE,
    ALG_PATH,
    ALG_PROP_PATH,
    INVALID_TEXT_FIELD,
    INVALID_NAME_ERROR,
    SAVE_SCRNSHOT_TITLE,
    SAVE_SCRNSHOT_DESC,
    SAVE_SCRNSHOT_EXT,
    SAVE_SCRNSHOT_FORMAT,
    OPTIONS,
    SUCCESSFUL_LOAD_TITLE,
    LINE,
    EDIT,
    READ_DONE,
    CLASSIFICATION,
    CLUSTER;
}

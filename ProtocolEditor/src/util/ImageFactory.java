package util;

import javax.swing.Icon;
import javax.swing.ImageIcon;

// for returning icons.
// Icons come from Nuvola. http://icon-king.com/?p=15

public class ImageFactory {
	
	// singleton
	private static ImageFactory uniqueInstance = new ImageFactory();
	// private constructor
	private ImageFactory() {};
	// return uniqueInstance
	public static ImageFactory getInstance() {
		return uniqueInstance;
	}
	
	public static final String ICONS_FILE = "../16x16/";
	public static final String ACTION_ICONS_FILE = ICONS_FILE + "actions/";
	
	public static final String OPEN_FILE_ICON = ICONS_FILE + "filesystems/folder_open.png";
	public static final String SAVE_ICON = ACTION_ICONS_FILE + "filesave.png";
	public static final String PRINT_ICON = ACTION_ICONS_FILE + "fileprint.png";
	public static final String LOAD_DEFAULTS_ICON = ACTION_ICONS_FILE + "bookmarks_list_add.png";
	public static final String NEW_FILE_ICON = ACTION_ICONS_FILE + "filenew.png";
	public static final String SAVE_FILE_AS_ICON = ACTION_ICONS_FILE + "filesaveas.png";
	public static final String EDIT_ICON = ICONS_FILE + "apps/package_editors.png";
	public static final String ADD_ICON = ACTION_ICONS_FILE + "edit_add.png";
	public static final String DELETE_ICON = ACTION_ICONS_FILE + "cancel.png";
	public static final String MOVE_UP_ICON = ACTION_ICONS_FILE + "up.png";
	public static final String MOVE_DOWN_ICON = ACTION_ICONS_FILE + "down.png";
	public static final String DEMOTE_ICON = ACTION_ICONS_FILE + "demote.png";
	public static final String PROMOTE_ICON = ACTION_ICONS_FILE + "promote.png";
	public static final String DUPLICATE_ICON = ACTION_ICONS_FILE + "editcopy.png";
	public static final String IMPORT_ICON = ACTION_ICONS_FILE + "compfile.png";
	public static final String BIG_PROTOCOL_ICON = ACTION_ICONS_FILE + "edu_science.png";
	public static final String COLLAPSED_ICON = ACTION_ICONS_FILE + "1rightarrow.png";
	public static final String NOT_COLLAPSED_ICON = ACTION_ICONS_FILE + "1downarrow.png";
	public static final String WWW_ICON = ACTION_ICONS_FILE + "www.png";
	public static final String INFO_ICON = ACTION_ICONS_FILE + "messagebox_info.png";
	public static final String SEARCH_ICON = ICONS_FILE + "apps/xmag.png";
	public static final String TWO_LEFT_ARROW = ACTION_ICONS_FILE + "2leftarrow.png";
	public static final String N0 = ACTION_ICONS_FILE + "no.png";
	public static final String EDU_MATHS = ICONS_FILE + "apps/edu_mathematics.png";
	public static final String NOTE_PAD = ICONS_FILE + "apps/kwrite.png";
	public static final String VALIDATION_ICON = ICONS_FILE + "apps/spellcheck.png";
	public static final String RED_BALL_ICON = ACTION_ICONS_FILE + "krec_record.png";
	public static final String UNDO_ICON = ACTION_ICONS_FILE + "undo.png";
	public static final String REDO_ICON = ACTION_ICONS_FILE + "redo.png";
	public static final String CLEAR_FIELDS_ICON = ACTION_ICONS_FILE + "news_unsubscribe.png";
	public static final String WWW_FILE_ICON = ICONS_FILE + "filesystems/folder_http.png";
	public static final String FIND_ICON = ACTION_ICONS_FILE + "find.png";
	public static final String FILE_CLOSE_ICON = ACTION_ICONS_FILE + "fileclose.png";
	public static final String PREVIOUS_UP_ICON = ACTION_ICONS_FILE + "previousUp.png";
	public static final String NEXT_DOWN_ICON = ACTION_ICONS_FILE + "nextDown.png";
	
	
	public Icon getIcon(String iconPathName) {
		try {
			return new ImageIcon(ImageFactory.class.getResource(iconPathName));
		} catch (NullPointerException ex) {
			System.out.println("Could not find Icon at " + iconPathName);
			return null;
		}
	}

}

package command;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.json.JSONArray;
import org.json.JSONObject;

import exception.PersistenceException;
import interfaces.ICommand;
import interfaces.IElement;
import widget.window.MainWindow;

public class OpenCommand implements ICommand {

	@Override
	public void execute() {
		FileDialog fd = new FileDialog(MainWindow.getInstance(), SWT.OPEN);
		fd.setText("Open");

		String[] filterExt = { "*.json", "*.*" };
		fd.setFilterExtensions(filterExt);
		String filename = fd.open();
		String json = null;

		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = reader.readLine()) != null) {
				json = line;
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (json != null) {
			JSONObject obj = new JSONObject(json);
			try {
				beginDecoding(obj);
			} catch (PersistenceException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	public void beginDecoding(JSONObject obj) {
		JSONArray elements = obj.getJSONArray("elements");
		for (int i = 0; i < elements.length(); i++) {
			JSONObject item = elements.getJSONObject(i);
			String className = item.getString("class");
			IElement elem = null;
			try {
				elem = (IElement) Class.forName(className).getConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException 
					| InvocationTargetException | NoSuchMethodException 
					| SecurityException | ClassNotFoundException e) {
				e.printStackTrace();
			}
			if (elem == null) {
				throw new PersistenceException("Wrong JSON format: wrong class name.");
			}
			elem.setId(item.getString("id"));
			elem.jsonDecode(item);
		}
	}

}
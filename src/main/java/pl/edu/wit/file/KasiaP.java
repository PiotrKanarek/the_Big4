package pl.edu.wit.file;

import javax.swing.JTextField;

public class KasiaP {
	
	public boolean walidacja(JTextField field) {
			return !field.getText().isEmpty();
	}
}
